package net.mistersecret312.stonemedusa.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
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
        SphereUtils.drawTexturedSphere(pMatrixStack, pBuffer, new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrification_beam.png"), medusa.getCurrentRadius()/1300f, 32, 0.0F, 0.0F, pPackedLight, false, OverlayTexture.NO_OVERLAY, new float[]{0.0f, 1.0f, 0.0f, 0.25f});
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
