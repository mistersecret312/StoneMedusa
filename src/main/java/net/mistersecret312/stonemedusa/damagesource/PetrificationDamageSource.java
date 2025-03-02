package net.mistersecret312.stonemedusa.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.LevelReader;

public class PetrificationDamageSource extends DamageSource
{

    public PetrificationDamageSource(Holder<DamageType> type)
    {
        super(type);
    }

    public static DamageSource source(LevelReader level, ResourceKey<DamageType> resourceKey)
    {
        Holder.Reference<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(resourceKey);
        return new PetrificationDamageSource(holder);
    }
}
