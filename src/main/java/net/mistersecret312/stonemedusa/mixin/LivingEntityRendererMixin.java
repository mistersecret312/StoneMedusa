package net.mistersecret312.stonemedusa.mixin;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.client.StoneRenderTypes;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity>
{
	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	protected void injectPetrificationRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir)
	{
		float progress = entity.getData(AttachmentTypeInit.PETRIFICATION.get()).getPetrificationProgress();
		if (progress > 0.0f)
		{
			LivingEntityRenderer<T, ?> renderer = (LivingEntityRenderer<T, ?>) (Object) this;
			ResourceLocation texture = renderer.getTextureLocation(entity);

			cir.setReturnValue(StoneRenderTypes.PETRIFICATION.apply(texture));
		}
	}
}
