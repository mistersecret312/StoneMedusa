package net.mistersecret312.mixin;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.init.EffectInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity>
{
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    public void setupAnimations(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci)
    {
        if(pEntity.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
        {
            pLimbSwing = 0F;
            pLimbSwingAmount = 0F;
        }
    }
}
