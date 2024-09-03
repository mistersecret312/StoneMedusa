package net.mistersecret312.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
    @WrapOperation(method = "onMove(JDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;turnPlayer()V"))
    public void turnPlayer(MouseHandler instance, Operation<Void> original)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null || !minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            original.call(instance);
    }

}
