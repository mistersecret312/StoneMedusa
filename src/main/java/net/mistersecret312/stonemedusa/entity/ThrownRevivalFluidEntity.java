package net.mistersecret312.stonemedusa.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.mistersecret312.stonemedusa.config.RevivalConfig;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.EntityInit;
import net.mistersecret312.stonemedusa.init.ItemInit;

import java.util.List;

public class ThrownRevivalFluidEntity extends ThrowableItemProjectile
{

    public ThrownRevivalFluidEntity(Level pLevel)
    {
        super(EntityInit.REVIVAL_FLUIID.get(), pLevel);
    }

    public ThrownRevivalFluidEntity(Level level, double x, double y, double z)
    {
        super(EntityInit.REVIVAL_FLUIID.get(), x, y, z, level);
    }

    public ThrownRevivalFluidEntity(Level level, LivingEntity living)
    {
        super(EntityInit.REVIVAL_FLUIID.get(), living, level);
    }

    @Override
    protected Item getDefaultItem()
    {
        return ItemInit.REVIVAL_FLUID.get();
    }

    @Override
    protected void onHit(HitResult pResult)
    {
        AABB aabb = this.getBoundingBox().inflate(2.0D, 2.0D, 2.0D);
        this.level().levelEvent(2002, this.blockPosition(), 13409380);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity living : list)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), RevivalConfig.revival_time.get(), 0, false, false, true));
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if(result.getEntity() instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), RevivalConfig.revival_time.get(), 0, false, false, true));
    }
}
