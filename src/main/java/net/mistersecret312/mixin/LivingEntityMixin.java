package net.mistersecret312.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.LazyOptional;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
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

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity entity = ((LivingEntity) (Object) this);
        LazyOptional<PetrifiedCapability> cap = entity.getCapability(CapabilitiesInit.PETRIFIED);
        if(cap.isPresent())
        {
            Optional<PetrifiedCapability> capOp = cap.resolve();
            if(capOp.isPresent())
            {
                PetrifiedCapability capability = capOp.get();
                if (capability.isPetrified() && PetrificationConfig.petrified_entity_damage.get())
                {
                    capability.setBreakStage(capability.getBreakStage() + (pAmount > 5f ? 1 : 0));
                    if(entity.level().isClientSide)
                    {
                        NetworkInit.sendToServer(new BreakingEntityPetrifiedPacket(entity.getId()));
                    }
                    if (capability.getBreakStage() >= 9 && PetrificationConfig.petrified_entity_destroy.get())
                    {
                        entity.level().playLocalSound(entity.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.3f, 1f, false);
                        entity.level().playSound(entity, entity.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f, 1f);
                        entity.level().addDestroyBlockEffect(entity.blockPosition(), Blocks.STONE.defaultBlockState());
                        pAmount = Float.MAX_VALUE;
                        return;
                    }
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
