package net.mistersecret312.stonemedusa.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.NetworkInit;
import net.mistersecret312.stonemedusa.network.packets.PetrifiedEntityUpdatePacket;

public class PetrifiedCapability implements INBTSerializable<CompoundTag>
{
    public static final String PETRIFIED = "petrified";

    public boolean petrified = false;

    public void tick(Level level, LivingEntity living)
    {
        if(level.isClientSide())
            return;

        petrified = living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get());

        NetworkInit.sendToTracking(living, new PetrifiedEntityUpdatePacket(petrified, living.getId()));
    }

    public boolean isPetrified()
    {
        return petrified;
    }

    public void setPetrified(boolean petrified)
    {
        this.petrified = petrified;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean(PETRIFIED, petrified);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.petrified = nbt.getBoolean(PETRIFIED);
    }
}
