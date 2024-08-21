package net.mistersecret312.stonemedusa.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;
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
            MobEffectInstance depetrification = new MobEffectInstance(MobEffects.HEAL,20, 0, false, false, false);
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


        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return true;
    }
}
