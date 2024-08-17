package net.mistersecret312.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "getBlockJumpFactor()F", at = @At("HEAD"), cancellable = true)
    public void jumpPetrified(CallbackInfoReturnable<Float> cir)
    {
        if(((Entity) (Object) this) instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                cir.setReturnValue(0f);
    }

    @Inject(method = "turn(DD)V", at = @At("HEAD"), cancellable = true)
    public void dontTurnIfPetrified(double pYRot, double pXRot, CallbackInfo ci)
    {
        if(((Entity) (Object) this) instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                ci.cancel();
    }

    @Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"))
    public void dontMovePetrified(Vec3 pDeltaMovement, CallbackInfo ci)
    {
        if(((Entity) (Object) this) instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                pDeltaMovement = new Vec3(0, 0,0);
    }
}
