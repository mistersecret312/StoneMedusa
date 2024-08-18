package net.mistersecret312.stonemedusa.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.EntityInit;

import java.util.List;

public class ThrownRevivalFluidEntity extends ThrowableItemProjectile
{

    public ThrownRevivalFluidEntity(Level pLevel)
    {
        super(EntityInit.REVIVAL_FLUIID.get(), pLevel);
    }

    public ThrownRevivalFluidEntity(Level level, LivingEntity living)
    {
        super(EntityInit.REVIVAL_FLUIID.get(), living, level);
    }

    @Override
    protected Item getDefaultItem()
    {
        return null;
    }

    @Override
    protected void onHit(HitResult pResult)
    {
        this.level().levelEvent(2007, this.blockPosition(), 14024585);

        AABB aabb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity living : list)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), 100, 0, false, false, true));
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if(result.getEntity() instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), 100, 0, false, false, true));
    }
}
