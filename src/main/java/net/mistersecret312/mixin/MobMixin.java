package net.mistersecret312.mixin;

import net.minecraft.world.entity.Mob;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin
{
    @Inject(method = "checkDespawn()V", at = @At("HEAD"), cancellable = true)
    public void dontDespawnPetrified(CallbackInfo ci)
    {
        if(((Mob) (Object) this).getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            ci.cancel();
    }

    @Inject(method = "serverAiStep()V", at = @At("HEAD"), cancellable = true)
    public void dontThinkPetrified(CallbackInfo ci)
    {
        if(((Mob) (Object) this).getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            ci.cancel();
    }

    @Inject(method = "playAmbientSound()V", at = @At("HEAD"), cancellable = true)
    public void dontPlayAmbientSound(CallbackInfo ci)
    {
        if(((Mob) (Object) this).getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            ci.cancel();
    }
}
