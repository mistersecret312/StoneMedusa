package net.mistersecret312.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Mob.class)
public abstract class MobMixin
{
    @Inject(method = "checkDespawn()V", at = @At("HEAD"), cancellable = true)
    public void dontDespawnPetrified(CallbackInfo ci)
    {
        Mob mob = (Mob) (Object) this;
        LazyOptional<PetrifiedCapability> capabilityLazyOptional = mob.getCapability(CapabilitiesInit.PETRIFIED);
        Optional<PetrifiedCapability> capabilityOptional = capabilityLazyOptional.resolve();
        PetrifiedCapability capability = capabilityOptional.orElse(null);

        if(capability != null && capability.isPetrified())
            ci.cancel();
    }

    @Inject(method = "serverAiStep()V", at = @At("HEAD"), cancellable = true)
    public void dontThinkPetrified(CallbackInfo ci)
    {
        Mob mob = (Mob) (Object) this;
        LazyOptional<PetrifiedCapability> capabilityLazyOptional = mob.getCapability(CapabilitiesInit.PETRIFIED);
        Optional<PetrifiedCapability> capabilityOptional = capabilityLazyOptional.resolve();
        PetrifiedCapability capability = capabilityOptional.orElse(null);

        if(capability != null && capability.isPetrified())
            ci.cancel();
    }

    @Inject(method = "playAmbientSound()V", at = @At("HEAD"), cancellable = true)
    public void dontPlayAmbientSound(CallbackInfo ci)
    {
        Mob mob = (Mob) (Object) this;
        LazyOptional<PetrifiedCapability> capabilityLazyOptional = mob.getCapability(CapabilitiesInit.PETRIFIED);
        Optional<PetrifiedCapability> capabilityOptional = capabilityLazyOptional.resolve();
        PetrifiedCapability capability = capabilityOptional.orElse(null);

        if(capability != null && capability.isPetrified())
            ci.cancel();
    }
}
