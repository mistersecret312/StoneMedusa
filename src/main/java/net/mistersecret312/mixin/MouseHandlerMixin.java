package net.mistersecret312.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
    @Inject(method = "onMove(JDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;turnPlayer()V"), cancellable = true)
    public void moveMouse(long pWindowPointer, double pXpos, double pYpos, CallbackInfo ci)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            ci.cancel();
    }

}
