package net.mistersecret312.stonemedusa.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.init.EffectInit;

public class PetrificationEffect extends MobEffect
{
    public PetrificationEffect(MobEffectCategory pCategory, int pColor)
    {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier)
    {
        pLivingEntity.setInvulnerable(true);
        pLivingEntity.setAirSupply(pLivingEntity.getMaxAirSupply());

        MobEffectInstance petrification = pLivingEntity.getActiveEffectsMap().get(EffectInit.PETRIFICATION.get());

        if(petrification.endsWithin(80))
        {
            MobEffectInstance depetrification = new MobEffectInstance(MobEffects.HEAL,40, 1, false, false, false);
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

        if(petrification.endsWithin(10))
            pLivingEntity.setInvulnerable(false);


        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return true;
    }
}
