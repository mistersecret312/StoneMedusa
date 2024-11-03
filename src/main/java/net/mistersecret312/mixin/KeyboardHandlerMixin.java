package net.mistersecret312.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.settings.KeyMappingLookup;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin
{
    @Final
    @Shadow
    private Minecraft minecraft;

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
            if(InputConstants.getKey(pKey, pScanCode).getValue() != InputConstants.KEY_ESCAPE && InputConstants.getKey(pKey, pScanCode).getValue() != InputConstants.KEY_F5
                    && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            {
                return true;
            } else return false;
        } else return false;
    }

}
