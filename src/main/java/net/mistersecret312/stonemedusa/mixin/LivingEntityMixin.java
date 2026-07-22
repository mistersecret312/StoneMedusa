package net.mistersecret312.stonemedusa.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.util.MedusaUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@Inject(method = "isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true)
	public void invulnerabilityFix(DamageSource source, CallbackInfoReturnable<Boolean> cir)
	{
		if(source.is(MedusaUtil.PETRIFICATION_DAMAGE_TYPE))
			cir.setReturnValue(false);
	}
}
