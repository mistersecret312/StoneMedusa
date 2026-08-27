package net.mistersecret312.stonemedusa.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.*;
import net.mistersecret312.stonemedusa.client.beams.MedusaBeamRenderer;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.data_attachment.PetrificationAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.items.IBorderCustom;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import static net.mistersecret312.stonemedusa.medusa.MedusaChatHandler.MEDUSA_COMMAND;

@EventBusSubscriber(modid = StoneMedusa.MODID, value = Dist.CLIENT)
public class ClientEvents
{
	@SubscribeEvent
	public static void renderBeam(RenderLevelStageEvent event)
	{
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		Camera camera = event.getCamera();
		PoseStack poseStack = event.getPoseStack();
		Level level = Minecraft.getInstance().level;
		if(level == null) return;

		MedusaLevelAttachment medusaAttachment = level.getData(AttachmentTypeInit.MEDUSA);
		if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL)
		{
			for(Map.Entry<UUID, MedusaBeam> entry : medusaAttachment.getActiveBeams().entrySet())
			{
				MedusaBeam beam = entry.getValue();
				if(beam.getSettings().location() == null)
					continue;
				if(!beam.getSettings().dimension().equals(level.dimension()))
					continue;

				MedusaBeamRenderer<MedusaBeam> renderer = MedusaBeamRenderers.getRenderer(beam);
				if(renderer != null)
					renderer.render(beam, poseStack, buffer, camera, event.getPartialTick().getGameTimeDeltaPartialTick(true));
			}
		}
	}

	@SubscribeEvent
	public static void renderEntity(RenderLivingEvent.Pre<?, ?> event)
	{
		LivingEntity entity = event.getEntity();
		PetrificationAttachment petrification = entity.getData(AttachmentTypeInit.PETRIFICATION.get());
		float progress = petrification.getPetrificationProgress();

		if(progress >= 100f && !PetrifiedEntityRenderer.isRenderingDummy() && !(entity instanceof Player)
		&& !entity.isDeadOrDying())
		{
			event.setCanceled(true);
			PetrifiedEntityRenderer.renderFrozenDummy(entity, event);
		}

		if(progress <= 0 || petrification.getBeamPosition() == null)
			return;

		Vec3 medusaLocalPos = petrification.getBeamPosition();
		Vec3 directionToMedusa = medusaLocalPos.normalize();

		double halfWidth = entity.getBbWidth() / 2.0;
		double halfHeight = entity.getBbHeight() / 2.0;
		Vec3 impactPointLocal = new Vec3(directionToMedusa.x * halfWidth,
				(directionToMedusa.y * halfHeight) + halfHeight,
				directionToMedusa.z * halfWidth);

		double entityDiagonal = Math.sqrt(
				Math.pow(entity.getBbWidth(), 2) + Math.pow(entity.getBbHeight(), 2) + Math.pow(entity.getBbWidth(),
						2));
		float trueRadius = (float) (entityDiagonal * 1.3f);

		ShaderInstance shader = StoneMedusa.ClientModEvents.petrificationInstance;
		if(shader != null)
		{
			Matrix4f inversePose = new Matrix4f(event.getPoseStack().last().pose());
			inversePose.invert();

			shader.safeGetUniform("InversePoseMat").set(inversePose);
			shader.safeGetUniform("Progress").set(progress);
			shader.safeGetUniform("MaxRadius").set(trueRadius);
			shader.safeGetUniform("EpicenterLocal")
				  .set((float) impactPointLocal.x, (float) impactPointLocal.y, (float) impactPointLocal.z);

			float depetriProgress = petrification.getDepetrificationProgress();
			shader.safeGetUniform("DepetriProgress").set(depetriProgress);
			shader.safeGetUniform("DepetriMaxRadius").set(trueRadius);
			shader.safeGetUniform("EntityYaw").set((float) Math.toRadians(entity.yBodyRot));
			if(depetriProgress > 0.0f)
			{
				float headY = entity.getBbHeight();
				shader.safeGetUniform("DepetriEpicenterLocal").set(0.0f, headY, 0.0f);
			}

			int crackStage = petrification.crackStage;
			if (crackStage > 0)
			{
				crackStage = Math.clamp(crackStage, 0, 9);

				ResourceLocation crackTexture = ResourceLocation.withDefaultNamespace(
						"textures/block/destroy_stage_" + crackStage + ".png");
				RenderSystem.setShaderTexture(3, crackTexture);
				shader.safeGetUniform("Sampler3").set(3);
				shader.safeGetUniform("CrackOpacity").set(1.0f);
			} else shader.safeGetUniform("CrackOpacity").set(0.0f);
		}
	}

	@SubscribeEvent
	public static void renderEntityPost(RenderLivingEvent.Post<?, ?> event)
	{
		LivingEntity entity = event.getEntity();
		float currentProgress = entity.getData(AttachmentTypeInit.PETRIFICATION.get()).getPetrificationProgress();

		if(currentProgress > 0)
		{
			LivingEntityRenderer<LivingEntity, ?> renderer = (LivingEntityRenderer<LivingEntity, ?>) event.getRenderer();
			ResourceLocation texture = renderer.getTextureLocation(entity);
			RenderType customType = StoneRenderTypes.PETRIFICATION.apply(texture);

			if(event.getMultiBufferSource() instanceof MultiBufferSource.BufferSource bufferSource)
				bufferSource.endBatch(customType);
		}
	}

	@SubscribeEvent
	public static void onRenderArm(RenderArmEvent event) {
		AbstractClientPlayer player = event.getPlayer();
		PetrificationAttachment cap = player.getData(AttachmentTypeInit.PETRIFICATION.get());
		float progress = cap.getPetrificationProgress();

		if (progress <= 0.0f || cap.getBeamPosition() == null)
			return;

		event.setCanceled(true);

		Vec3 medusaLocalPos = cap.getBeamPosition();
		Vec3 directionToMedusa = medusaLocalPos.normalize();

		double halfWidth = player.getBbWidth() / 2.0;
		double halfHeight = player.getBbHeight() / 2.0;
		Vec3 impactPointLocal = new Vec3(
				directionToMedusa.x * halfWidth,
				(directionToMedusa.y * halfHeight) + halfHeight,
				directionToMedusa.z * halfWidth);

		double playerDiagonal = Math.sqrt(Math.pow(player.getBbWidth(), 2) + Math.pow(player.getBbHeight(), 2) + Math.pow(player.getBbWidth(), 2));
		float trueRadius = (float) (playerDiagonal * 1.2f);

		ShaderInstance shader = StoneMedusa.ClientModEvents.petrificationInstance;
		if (shader != null)
		{
			Matrix4f inversePose = new Matrix4f(event.getPoseStack().last().pose());
			inversePose.invert();

			shader.safeGetUniform("InversePoseMat").set(inversePose);
			shader.safeGetUniform("Progress").set(progress);
			shader.safeGetUniform("MaxRadius").set(trueRadius);
			shader.safeGetUniform("EpicenterLocal")
				  .set((float) impactPointLocal.x, (float) impactPointLocal.y, (float) impactPointLocal.z);

			float depetriProgress = cap.getDepetrificationProgress();
			shader.safeGetUniform("DepetriProgress").set(depetriProgress);
			shader.safeGetUniform("DepetriMaxRadius").set(trueRadius);
			if(depetriProgress > 0.0f)
			{
				float headY = player.getBbHeight();
				shader.safeGetUniform("DepetriEpicenterLocal").set(0.0f, headY, 0.0f);
			}
			int crackStage = cap.crackStage;
			if (crackStage > 0)
			{
				crackStage = Math.clamp(crackStage, 0, 9);

				ResourceLocation crackTexture = ResourceLocation.withDefaultNamespace(
						"textures/block/destroy_stage_" + crackStage + ".png");
				RenderSystem.setShaderTexture(3, crackTexture);
				shader.safeGetUniform("Sampler3").set(3);
				shader.safeGetUniform("CrackOpacity").set(1.0f);
			} else shader.safeGetUniform("CrackOpacity").set(0.0f);
		}

		RenderType customType = StoneRenderTypes.PETRIFICATION.apply(player.getSkin().texture());
		VertexConsumer buffer = event.getMultiBufferSource().getBuffer(customType);

		PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();

		model.attackTime = 0.0F;
		model.crouching = false;
		model.swimAmount = 0.0F;
		model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

		if (event.getArm() == HumanoidArm.RIGHT)
		{
			model.rightArm.xRot = 0.0F;
			model.rightSleeve.xRot = 0.0F;

			model.rightSleeve.copyFrom(model.rightArm);

			model.rightArm.render(event.getPoseStack(), buffer, event.getPackedLight(), OverlayTexture.NO_OVERLAY);
			model.rightSleeve.render(event.getPoseStack(), buffer, event.getPackedLight(), OverlayTexture.NO_OVERLAY);
		}
		else
		{
			model.leftArm.xRot = 0.0F;
			model.leftSleeve.xRot = 0.0F;

			model.leftSleeve.copyFrom(model.leftArm);

			model.leftArm.render(event.getPoseStack(), buffer, event.getPackedLight(), OverlayTexture.NO_OVERLAY);
			model.leftSleeve.render(event.getPoseStack(), buffer, event.getPackedLight(), OverlayTexture.NO_OVERLAY);
		}

		if (event.getMultiBufferSource() instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch(customType);
	}

	@SubscribeEvent
	public static void onMovementInput(MovementInputUpdateEvent event)
	{
		if(event.getEntity().getAbilities().instabuild)
			return;
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		if(cap.getPetrificationProgress() >= 50)
		{
			Input input = event.getInput();

			input.up = false;
			input.down = false;
			input.left = false;
			input.right = false;
			input.forwardImpulse = 0.0f;
			input.leftImpulse = 0.0f;
			input.jumping = false;
			input.shiftKeyDown = cap.wasCrouching;
		}
	}

	@SubscribeEvent
	public static void onTurnInput(CalculatePlayerTurnEvent event)
	{
		Player player = Minecraft.getInstance().player;
		if(player == null)
			return;
		if(player.getAbilities().instabuild)
			return;
		PetrificationAttachment cap = player.getData(AttachmentTypeInit.PETRIFICATION);
		if(!cap.shouldInteract())
			event.setMouseSensitivity(-0.35);
	}

	@SubscribeEvent
	public static void renderTooltip(RenderTooltipEvent.Color event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.getItem() instanceof IBorderCustom custom)
		{
			event.setBorderStart(custom.getBorderColors(stack).getFirst());
			event.setBorderEnd(custom.getBorderColors(stack).getSecond());
		}
	}

//	@SubscribeEvent
//	public static void receiveMedusaCommand(ClientChatReceivedEvent.Player event)
//	{
//		UUID senderUUID = event.getSender();
//		String message = event.getMessage().getString();
//		Matcher matcher = MEDUSA_COMMAND.matcher(message);
//		Level level = Minecraft.getInstance().level;
//		Player player = Minecraft.getInstance().player;
//		if (matcher.matches() && level != null && player != null)
//		{
//			Player sender = level.getPlayerByUUID(senderUUID);
//			if(sender == null)
//				return;
//
//			if(player.position().distanceTo(sender.position()) > 50)
//				event.setCanceled(true);
//		}
//	}
}
