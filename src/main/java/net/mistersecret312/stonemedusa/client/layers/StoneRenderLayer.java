package net.mistersecret312.stonemedusa.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;

public class StoneRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M>
{
    public StoneRenderLayer(RenderLayerParent<T, M> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int pPackedLight, T livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        livingEntity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(
        cap -> {
            if(cap.isPetrified())
            {
                poseStack.pushPose();
                poseStack.scale(1.3f, 1.3f, 1.3f);
                poseStack.popPose();
                this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrified_entity.png"))), pPackedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 0.65F);
                if(cap.getBreakStage() >= 0)
                {
                    poseStack.pushPose();
                    poseStack.scale(1.31f, 1.31f, 1.31f);
                    poseStack.popPose();
                    this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrification_destruction/destroy_stage_" + cap.getBreakStage() + ".png"))), pPackedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
                }
            }
        });
    }


}
