package net.mistersecret312.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.FluidTypeInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "baseTick()V", at = @At("HEAD"))
    public void tick(CallbackInfo ci)
    {
        Entity entity = ((Entity) (Object) this);
        DamageSource source = new DamageSource(Holder.direct(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(StoneMedusa.MOD_ID, "nitric_acid")))),
                null, null, null);
        if(entity.isInFluidType(FluidTypeInit.REVIVAL_FLUID_TYPE.get()) && entity.level().getGameTime() % 20 == 0)
            entity.hurt(source, 1);
    }

    @Inject(method = "onFlap()V", at = @At("HEAD"), cancellable = true)
    public void dontFlap(CallbackInfo ci)
    {
        if(((Entity) (Object) this) instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                ci.cancel();
    }

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

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    public void dontHurtAnimate(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir)
    {
        if(((Entity) (Object) this) instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                cir.setReturnValue(false);
    }
}
