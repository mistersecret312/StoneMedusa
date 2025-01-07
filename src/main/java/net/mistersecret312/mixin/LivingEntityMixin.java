package net.mistersecret312.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
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
        entity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(
        cap ->
        {
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f, 1f);
            if(PetrificationConfig.petrified_entity_damage.get())
            {
                cap.setBreakStage(cap.getBreakStage() + (pAmount > 5f ? 1 : 0));
                if(cap.getBreakStage() >= 9 && PetrificationConfig.petrified_entity_destroy.get())
                {
                    entity.discard();
                    entity.level().addDestroyBlockEffect(entity.blockPosition(), Blocks.STONE.defaultBlockState());
                }
            }
            cir.setReturnValue(false);
        });
    }
}
