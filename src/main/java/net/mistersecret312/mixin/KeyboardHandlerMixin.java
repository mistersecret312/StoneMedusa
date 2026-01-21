package net.mistersecret312.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.EndPortalBlock;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;

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
        ArrayList<Integer> keyBlacklist = Lists.newArrayList(minecraft.options.keyJump.getKey().getValue(),
                minecraft.options.keyAttack.getKey().getValue(), minecraft.options.keySwapOffhand.getKey().getValue(),
                minecraft.options.keyDrop.getKey().getValue(), minecraft.options.keySprint.getKey().getValue(),
                minecraft.options.keyLeft.getKey().getValue(), minecraft.options.keyRight.getKey().getValue(),
                minecraft.options.keyShift.getKey().getValue(), minecraft.options.keyUp.getKey().getValue(),
                minecraft.options.keyDown.getKey().getValue());

        if(this.minecraft.screen == null)
            return keyBlacklist.stream().anyMatch(key -> key == InputConstants.getKey(pKey, pScanCode).getValue()) && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get());
        else return false;
    }

}
