package net.mistersecret312.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin
{
    @Final
    @Shadow
    private Minecraft minecraft;
    private static final ArrayList<Integer> keyWhitelist = Lists.newArrayList(InputConstants.KEY_SLASH, InputConstants.KEY_F5, InputConstants.KEY_F3,
            65, 66, 67, 68, 71, 72, 73, 76, 78, 80, 81, 83, 84, 293, InputConstants.KEY_F1, InputConstants.KEY_F2, InputConstants.KEY_F4,
            InputConstants.KEY_F6, InputConstants.KEY_F7, InputConstants.KEY_F8, InputConstants.KEY_F9, InputConstants.KEY_ESCAPE);

    @Inject(method = "keyPress(JIIII)V", at = @At("HEAD"), cancellable = true)
    public void keyPress(long pWindowPointer, int pKey, int pScanCode, int pAction, int pModifiers, CallbackInfo ci)
    {
        if(mixinMethod(pKey, pScanCode))
            ci.cancel();
    }

    public boolean mixinMethod(int pKey, int pScanCode)
    {
        if(this.minecraft.screen == null)
        {
            if(InputConstants.getKey(pKey, pScanCode).getValue() == InputConstants.KEY_SLASH && Minecraft.getInstance().player.hasPermissions(3))
                return false;
            return keyWhitelist.stream().noneMatch(key -> key == InputConstants.getKey(pKey, pScanCode).getValue()) && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get());
        } else return false;
    }

}
