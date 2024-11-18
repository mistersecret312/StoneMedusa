package net.mistersecret312.stonemedusa.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.util.SphereUtils;

public class MedusaProjectileRenderer extends EntityRenderer<MedusaProjectile>
{
    private final ItemRenderer itemRenderer;

    public MedusaProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MedusaProjectile medusa, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight)
    {
        pMatrixStack.pushPose();
        if(!medusa.getTargetType().isBlank() && medusa.isActive())
            if(Minecraft.getInstance().getCameraEntity() != null && Minecraft.getInstance().getCameraEntity().getType() == ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(medusa.getTargetType())))
                SphereUtils.drawTexturedSphere(pMatrixStack, pBuffer,
                    new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrification_beam.png"),
                    medusa.getCurrentRadius()/1300f, 32, 0.0F, 0.0F, pPackedLight, false,
                    OverlayTexture.NO_OVERLAY, new float[]{0.0f, 1.0f, 0.0f, 0.25f});
        if(medusa.getTargetType().isBlank() && medusa.isActive())
            SphereUtils.drawTexturedSphere(pMatrixStack, pBuffer,
                    new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrification_beam.png"),
                    medusa.getCurrentRadius()/1300f, 32, 0.0F, 0.0F, pPackedLight, false,
                    OverlayTexture.NO_OVERLAY, new float[]{0.0f, 1.0f, 0.0f, 0.25f});

        pMatrixStack.popPose();
        this.itemRenderer.renderStatic(medusa.getItem(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pMatrixStack, pBuffer, medusa.level(), medusa.getId());
        super.render(medusa, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MedusaProjectile entity)
    {
        if(entity.isActive())
            return new ResourceLocation(StoneMedusa.MOD_ID, "textures/item/medusa_activated.png");
        else return new ResourceLocation(StoneMedusa.MOD_ID, "textures/item/medusa.png");
    }

    @Override
    public boolean shouldRender(MedusaProjectile pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ)
    {
        return true;
    }

}
