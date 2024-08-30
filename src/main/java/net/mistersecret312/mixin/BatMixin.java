package net.mistersecret312.mixin;

import net.minecraft.world.entity.ambient.Bat;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bat.class)
public class BatMixin
{
    @Inject(method = "isFlapping()Z", at = @At("RETURN"), cancellable = true)
    public void notFlip(CallbackInfoReturnable<Boolean> cir)
    {
        Bat bat = ((Bat) (Object) this);
        if(bat.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            cir.setReturnValue(false);
    }
}
