package net.mistersecret312.stonemedusa.events;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.data_attachment.PetrificationAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.PetrifiedArea;
import net.mistersecret312.stonemedusa.medusa.source.EntitySource;
import net.mistersecret312.stonemedusa.medusa.source.InventorySource;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import net.mistersecret312.stonemedusa.medusa.source.PlayerSource;
import net.mistersecret312.stonemedusa.util.MedusaUtil;
import net.mistersecret312.stonemedusa.util.StructureUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Vector3f;

import java.util.*;

@EventBusSubscriber(modid = StoneMedusa.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents
{
	@SubscribeEvent
	public static void levelTick(LevelTickEvent.Pre event)
	{
		Level level = event.getLevel();
		if(!level.tickRateManager().runsNormally())
			return;

		MedusaLevelAttachment medusaAttachment = level.getData(AttachmentTypeInit.MEDUSA);
		medusaAttachment.tickBeams(level);
		medusaAttachment.tickAreas(level);
		medusaAttachment.tickHandlers(level);
	}

	@SubscribeEvent
	public static void entityTick(EntityTickEvent.Post event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity living)
			living.getData(AttachmentTypeInit.PETRIFICATION.get()).tick(living.level(), living);
	}

	@SubscribeEvent
	public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event)
	{
		Player player = event.getEntity();
		if(player instanceof ServerPlayer)
		{
			Level level = player.level();
			level.syncData(AttachmentTypeInit.MEDUSA);
		}
	}

	@SubscribeEvent
	public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event)
	{

	}

	@SubscribeEvent
	public static void onLivingDamage(LivingIncomingDamageEvent event)
	{
		LivingEntity entity = event.getEntity();
		PetrificationAttachment cap = entity.getData(AttachmentTypeInit.PETRIFICATION);
		if(!cap.shouldInteract())
		{
			cap.crackStage++;
			entity.syncData(AttachmentTypeInit.PETRIFICATION);

			DamageSource source = event.getSource();
			if(!source.is(MedusaUtil.PETRIFICATION_DAMAGE_TYPE))
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Pre event) {
		if(!(event.getEntity() instanceof LivingEntity entity))
			return;

		PetrificationAttachment cap = entity.getData(AttachmentTypeInit.PETRIFICATION);
		if (cap.getPetrificationProgress() > 100)
		{
			if (entity instanceof Mob mob && !mob.isNoAi())
				mob.setNoAi(true);

			entity.setNoGravity(false);

			entity.setYRot(cap.getLockedYaw());
			entity.setXRot(cap.getLockedPitch());
			entity.yHeadRot = cap.getLockedHeadYaw();
			entity.yBodyRot = cap.getLockedBodyYaw();

			entity.yRotO = cap.getLockedYaw();
			entity.xRotO = cap.getLockedPitch();
		}
	}

	@SubscribeEvent
	public static void onEntitySpawn(FinalizeSpawnEvent event)
	{
		MobSpawnType type = event.getSpawnType();
		Mob mob = event.getEntity();


		if(type.equals(MobSpawnType.CHUNK_GENERATION) || type.equals(MobSpawnType.NATURAL) || type.equals(MobSpawnType.STRUCTURE))
			handleEntityPetrificationInArea(mob.level(), mob, false);
	}

	@SubscribeEvent
	public static void levelLoad(LevelEvent.Load event)
	{
		if(event.getLevel().getServer() == null)
			return;

		ServerLevel level = event.getLevel().getServer().overworld();

		long seed = level.getSeed();
		int chunkX = StructureUtil.getChunkX(seed, 15524351, MedusaConfig.pyramid_generation_x_chunk_offset.get(),
				MedusaConfig.pyramid_generation_x_chunk_bounds.get(), 0);
		int chunkZ = StructureUtil.getChunkZ(seed, 15524351, MedusaConfig.pyramid_generation_z_chunk_offset.get(),
				MedusaConfig.pyramid_generation_z_chunk_bounds.get(), 0);

		MedusaLevelAttachment attachment = level.getData(AttachmentTypeInit.MEDUSA);
		if(!attachment.loadedPyramid)
		{
			PetrifiedArea area = new PetrifiedArea(new Vec2(chunkX * 16, chunkZ * 16), level.dimension(), 250, -1,
					new HashSet<>(), false);
			attachment.addPetrifiedArea(level, area);
		}
	}

	@SubscribeEvent
	public static void chunkLoad(ChunkEvent.Load event)
	{
		MinecraftServer server = event.getLevel().getServer();
		if(!event.isNewChunk() || server == null)
			return;

		ServerLevel serverLevel = server.overworld();
		long seed = serverLevel.getSeed();
		int chunkX = StructureUtil.getChunkX(seed, 15524351, MedusaConfig.pyramid_generation_x_chunk_offset.get(),
				MedusaConfig.pyramid_generation_x_chunk_bounds.get(), 0);
		int chunkZ = StructureUtil.getChunkZ(seed, 15524351, MedusaConfig.pyramid_generation_z_chunk_offset.get(),
				MedusaConfig.pyramid_generation_z_chunk_bounds.get(), 0);

		ChunkPos pos = event.getChunk().getPos();
		if(pos.x == chunkX && pos.z == chunkZ)
			serverLevel.getData(AttachmentTypeInit.MEDUSA.get()).loadedPyramid = true;

	}

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		Level level = event.getLevel();
		Entity entity = event.getEntity();

		if(event.loadedFromDisk())
			handleEntityPetrificationInArea(level, entity, true);
	}

	public static void handleEntityPetrificationInArea(Level level, Entity entity, boolean load)
	{
		MedusaLevelAttachment medusaAttachment = level.getData(AttachmentTypeInit.MEDUSA);
		List<PetrifiedArea> areas = new ArrayList<>(medusaAttachment.getPetrifiedAreas());
		areas.stream()
			 .filter(area ->
				 {
					 if(!area.petrifyLoad() && load)
						 return false;

					 Vec2 entityPos = new Vec2((float) entity.getX(), (float) entity.getZ());
					 return entityPos.distanceToSqr(area.epicenter()) <= area.radius() * area.radius() &&
							 level.dimension().equals(area.dimension());
				 })
			 .forEach(area -> {
				 if(area.entities().contains(entity.getUUID()) || !(entity instanceof LivingEntity living))
					 return;

				 area.entities().add(entity.getUUID());
				 PetrificationAttachment petrification = entity.getData(AttachmentTypeInit.PETRIFICATION.get());
				 petrification.startPetrification(living, new Vector3f(), area.radius());
				 petrification.setPetrificationProgress(100);
			 });

	}

	@SubscribeEvent
	public static void onPlayerToss(ItemTossEvent event)
	{
		Player player = event.getPlayer();
		ItemEntity itemEntity = event.getEntity();
		ItemStack stack = itemEntity.getItem();
		if(stack.getItem() instanceof MedusaItem)
		{
			UUID deviceId = MedusaItem.getDeviceId(stack);
			MedusaBeam beam = player.level().getData(AttachmentTypeInit.MEDUSA).getMedusa(deviceId);
			if(beam == null)
				return;
			beam.setSource(new EntitySource(itemEntity.getUUID(), itemEntity.getId()));
			player.level().syncData(AttachmentTypeInit.MEDUSA);
		}
	}

	@SubscribeEvent
	public static void onItemPickUp(ItemEntityPickupEvent.Post event)
	{
		Player player = event.getPlayer();
		ItemStack stack = event.getOriginalStack();
		if(stack.getItem() instanceof MedusaItem)
		{
			int slot = player.getInventory().findSlotMatchingItem(stack);
			UUID deviceId = MedusaItem.getDeviceId(stack);
			MedusaBeam beam = player.level().getData(AttachmentTypeInit.MEDUSA).getMedusa(deviceId);
			if(beam == null)
				return;
			beam.setSource(new InventorySource(new PlayerSource(player.getUUID()), slot));
			player.level().syncData(AttachmentTypeInit.MEDUSA);
		}
	}

	@SubscribeEvent
	public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		PetrificationAttachment otherCap = event.getTarget().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract() || !otherCap.shouldInteract())
			event.setCanceled(true);
	}
	@SubscribeEvent
	public static void onPlayerEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		PetrificationAttachment otherCap = event.getTarget().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract() || !otherCap.shouldInteract())
			event.setCanceled(true);
	}
	@SubscribeEvent
	public static void onPlayerRBInteract(PlayerInteractEvent.RightClickBlock event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract())
			event.setCanceled(true);
	}
	@SubscribeEvent
	public static void onPlayerRIInteract(PlayerInteractEvent.RightClickItem event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract())
			event.setCanceled(false);
	}
	@SubscribeEvent
	public static void onPlayerLBInteract(PlayerInteractEvent.LeftClickBlock event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onAttack(AttackEntityEvent event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		PetrificationAttachment otherCap = event.getTarget().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract() || !otherCap.shouldInteract())
		{
			if(!otherCap.shouldInteract())
			{
				otherCap.crackStage++;
				event.getTarget().syncData(AttachmentTypeInit.PETRIFICATION);
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onUseItem(LivingEntityUseItemEvent.Start event)
	{
		PetrificationAttachment cap = event.getEntity().getData(AttachmentTypeInit.PETRIFICATION);
		if (!cap.shouldInteract())
			event.setCanceled(true);
	}
}
