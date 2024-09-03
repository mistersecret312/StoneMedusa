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
    public static final String BREAK_STAGE = "";
    public static final String AGE = "age";

    public boolean petrified = false;
    public int breakStage = -1;
    public float age = 0f;

    public void tick(Level level, LivingEntity living)
    {
        if(level.isClientSide())
            return;

        petrified = living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get());

        NetworkInit.sendToTracking(living, new PetrifiedEntityUpdatePacket(petrified, breakStage, age, living.getId()));
    }

    public boolean isPetrified()
    {
        return petrified;
    }

    public int getBreakStage()
    {
        return breakStage;
    }

    public float getAge()
    {
        return age;
    }

    public void setPetrified(boolean petrified)
    {
        this.petrified = petrified;
    }

    public void setBreakStage(int breakStage)
    {
        if(breakStage < -1)
            breakStage = -1;
        if(breakStage > 9)
            breakStage = 9;
        this.breakStage = breakStage;
    }

    public void setAge(float age)
    {
        this.age = age;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean(PETRIFIED, petrified);
        tag.putInt(BREAK_STAGE, breakStage);
        tag.putFloat(AGE, age);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.petrified = nbt.getBoolean(PETRIFIED);
        this.breakStage = nbt.getInt(BREAK_STAGE);
        this.age = nbt.getFloat(AGE);
    }
}
