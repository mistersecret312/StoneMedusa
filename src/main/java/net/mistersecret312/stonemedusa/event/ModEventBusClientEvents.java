package net.mistersecret312.stonemedusa.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.util.SphereUtils;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEventBusClientEvents
{
    @SubscribeEvent
    public static void renderLast(RenderLevelStageEvent event)
    {
        if(event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER))
        {
            LevelRenderer renderer = event.getLevelRenderer();
            Frustum frustum = event.getFrustum();
            Camera camera = event.getCamera();
            PoseStack stack = event.getPoseStack();
            float partialTick = event.getPartialTick();
            Matrix4f projectionMatrix = event.getProjectionMatrix();

            MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

            ClientLevel level = Minecraft.getInstance().level;
            if(level != null)
            {
                stack.pushPose();
                boolean flag1 = Minecraft.getInstance().level.effects().isFoggyAt(Mth.floor(camera.getPosition().x), Mth.floor(camera.getPosition().y)) || Minecraft.getInstance().gui.getBossOverlay().shouldCreateWorldFog();
                FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_TERRAIN, MedusaConfig.max_radius.get().floatValue(), flag1, partialTick);
                stack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
                level.getCapability(CapabilitiesInit.WORLD).ifPresent(cap -> {
                    cap.getMedusaData().forEach((vec, data) -> {
                        stack.pushPose();
                        stack.translate(vec.x, vec.y, vec.z);

                        SphereUtils.drawTexturedSphere(stack, buffer,
                                new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrification_beam.png"),
                                data.getRadius()/1300f, 32, 0.0F, 0.0F, 15728880, false,
                                OverlayTexture.NO_OVERLAY, new float[]{0.0f, 1.0f, 0.0f, (0.25f*(1-Math.min(data.getTransparency(), 1)))});

                        stack.popPose();
                    });
                });
                stack.popPose();
            }


        }
    }

    @SubscribeEvent
    public static void dontScroll(InputEvent.MouseScrollingEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen == null && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void dontClick(InputEvent.MouseButton.Pre event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen == null && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void dontPress(InputEvent.Key.InteractionKeyMappingTriggered event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null && !event.getKeyMapping().getKey().equals(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_ESCAPE)) && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);

    }
}
