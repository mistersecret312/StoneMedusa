package net.mistersecret312.stonemedusa.effects;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.init.EffectInit;

import java.util.List;

public class PetrificationEffect extends MobEffect
{
    public PetrificationEffect(MobEffectCategory pCategory, int pColor)
    {
        super(pCategory, pColor);
    }

    @Override
    public List<ItemStack> getCurativeItems()
    {
        return List.of();
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier)
    {
        pLivingEntity.setInvulnerable(true);
        pLivingEntity.setAirSupply(pLivingEntity.getMaxAirSupply());

        MobEffectInstance petrification = pLivingEntity.getActiveEffectsMap().get(EffectInit.PETRIFICATION.get());

        if(petrification.endsWithin(20))
        {
            MobEffectInstance depetrification = new MobEffectInstance(MobEffects.REGENERATION,20, 9, false, false, false);
            pLivingEntity.addEffect(depetrification);
        }
        else
        {
            pLivingEntity.getActiveEffectsMap().forEach((effect, instance) ->
            {
                if(!effect.equals(EffectInit.PETRIFICATION.get()))
                    pLivingEntity.removeEffect(effect);
            });
        }

        if(petrification.endsWithin(5))
        {
            pLivingEntity.setInvulnerable(false);
            pLivingEntity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap -> cap.setPetrified(false));
        }
        if(petrification.endsWithin(1))
        {
            pLivingEntity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap -> cap.setBreakStage(-1));
            pLivingEntity.level().playSound(null, pLivingEntity.blockPosition(), SoundEvents.DEEPSLATE_BREAK, SoundSource.NEUTRAL, 1f, 1f);
        }

        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return true;
    }
}
