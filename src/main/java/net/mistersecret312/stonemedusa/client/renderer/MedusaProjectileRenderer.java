package net.mistersecret312.stonemedusa.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.util.SphereUtils;

public class MedusaProjectileRenderer extends ThrownItemRenderer<MedusaProjectile>
{
    public MedusaProjectileRenderer(EntityRendererProvider.Context pContext, float pScale, boolean pFullBright)
    {
        super(pContext, pScale, pFullBright);
    }

    public MedusaProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context, 1.0F, false);
    }

    @Override
    public void render(MedusaProjectile medusa, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight)
    {
        pMatrixStack.pushPose();
        if(medusa.isActive())
            SphereUtils.drawTexturedSphere(pMatrixStack, pBuffer, new ResourceLocation(StoneMedusa.MOD_ID, "textures/entity/petrification_beam.png"), medusa.getCurrentRadius(), 16, 0.0F, 0.0F, pPackedLight, true, OverlayTexture.NO_OVERLAY);
        pMatrixStack.popPose();
        super.render(medusa, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);

    }
}
