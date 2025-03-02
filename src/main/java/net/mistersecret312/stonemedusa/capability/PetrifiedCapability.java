package net.mistersecret312.stonemedusa.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.NetworkInit;
import net.mistersecret312.stonemedusa.network.packets.PetrifiedEntityUpdatePacket;

public class PetrifiedCapability implements INBTSerializable<CompoundTag>
{
    public static final String PETRIFIED = "petrified";
    public static final String BREAK_STAGE = "breakStage";
    public static final String AGE = "age";
    public static final String TIME_PETRIFIED = "timePetrified";
    public static final String BROKEN = "broken";

    public boolean petrified = false;
    public int breakStage = -1;
    public boolean broken = false;
    public float age = 0f;
    public int timePetrified = 0;

    public void tick(Level level, LivingEntity living)
    {
        if(level.isClientSide())
            return;

        petrified = living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get());
        if(isPetrified())
            timePetrified++;
        else if(timePetrified != 0)
            timePetrified = 0;

        NetworkInit.sendToTracking(living, new PetrifiedEntityUpdatePacket(petrified, breakStage, age, broken, living.getId()));
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

    public boolean isBroken()
    {
        return broken;
    }

    public int getTimePetrified()
    {
        return timePetrified;
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

    public void setBroken(boolean broken)
    {
        this.broken = broken;
    }

    public void setTimePetrified(int timePetrified)
    {
        if(timePetrified < 0)
            timePetrified = 0;
        this.timePetrified = timePetrified;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean(PETRIFIED, petrified);
        tag.putInt(BREAK_STAGE, breakStage);
        tag.putFloat(AGE, age);
        tag.putInt(TIME_PETRIFIED, timePetrified);
        tag.putBoolean(BROKEN, broken);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.petrified = nbt.getBoolean(PETRIFIED);
        this.breakStage = nbt.getInt(BREAK_STAGE);
        this.age = nbt.getFloat(AGE);
        this.timePetrified = nbt.getInt(TIME_PETRIFIED);
        this.broken = nbt.getBoolean(BROKEN);
    }
}
