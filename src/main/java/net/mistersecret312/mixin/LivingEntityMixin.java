package net.mistersecret312.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.NetworkInit;
import net.mistersecret312.stonemedusa.network.packets.BreakingEntityPetrifiedPacket;
import net.mistersecret312.stonemedusa.network.packets.EntityPetrifiedBrokenPacket;
import net.mistersecret312.stonemedusa.network.packets.EntityPetrifiedPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity entity = ((LivingEntity) (Object) this);
        entity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
        {
            if (cap.isPetrified() && PetrificationConfig.petrified_entity_damage.get())
            {
                cap.setBreakStage(cap.getBreakStage() + (pAmount > 5f ? 1 : 0));
                if(entity.level().isClientSide)
                {
                    NetworkInit.sendToServer(new BreakingEntityPetrifiedPacket(entity.getId()));
                }
                if (cap.getBreakStage() >= 9 && PetrificationConfig.petrified_entity_destroy.get())
                {
                    if(entity.level().isClientSide)
                        NetworkInit.sendToTracking(entity, new EntityPetrifiedBrokenPacket(entity.getId()));

                    entity.discard();
                }
                cir.setReturnValue(false);
            }

        });
    }
}
