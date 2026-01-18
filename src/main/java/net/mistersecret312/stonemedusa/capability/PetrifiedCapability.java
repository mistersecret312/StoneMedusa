package net.mistersecret312.stonemedusa.capability;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.config.RevivalConfig;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.FluidTypeInit;
import net.mistersecret312.stonemedusa.init.NetworkInit;
import net.mistersecret312.stonemedusa.network.packets.PetrifiedEntityUpdatePacket;

public class PetrifiedCapability implements INBTSerializable<CompoundTag>
{
    public static final String PETRIFIED = "petrified";
    public static final String BREAK_STAGE = "breakStage";
    public static final String AGE = "age";
    public static final String TIME_PETRIFIED = "timePetrified";
    public static final String TICK_COUNT = "tickCount";
    public static final String BROKEN = "broken";

    public boolean petrified = false;
    public int breakStage = -1;
    public boolean broken = false;
    public float age = 0f;
    public int timePetrified = 0;

    public float limbSwing = 0;
    public float limbSwingAmount = 0;
    public float headYaw = 0;
    public float headPitch = 0;

    public void tick(Level level, LivingEntity living)
    {
        if(level.isClientSide())
            return;

        this.petrified = living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get());
        if(isPetrified())
        {
            timePetrified++;
            living.setTicksFrozen(0);
            living.setNoActionTime(0);
            living.clearFire();
            Vec3 movement = new Vec3(0d, -0.04d*living.getBbHeight()*living.getBbWidth(), 0d);
            living.move(MoverType.SELF, movement);
        }
        else if(timePetrified != 0)
            timePetrified = 0;

        DamageSource source = new DamageSource(Holder.direct(living.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(StoneMedusa.MOD_ID, "nitric_acid")))),
                null, null, null);
        if((living.isInFluidType(FluidTypeInit.REVIVAL_FLUID_TYPE.get()) || living.isInFluidType(FluidTypeInit.NITRIC_ACID_TYPE.get())) && living.level().getGameTime() % 20 == 0)
        {
            living.hurt(source, living.isInFluidType(FluidTypeInit.REVIVAL_FLUID_TYPE.get()) ? RevivalConfig.revival_damage.get() : RevivalConfig.nitric_damage.get());

            if (living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()) && !living.getActiveEffectsMap().get(EffectInit.PETRIFICATION.get()).endsWithin(RevivalConfig.revival_time.get()))
                living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), RevivalConfig.revival_time.get(), 0, false, false, true));
        }

        NetworkInit.sendToTracking(living, new PetrifiedEntityUpdatePacket(petrified, breakStage, age, broken, limbSwing, limbSwingAmount, headYaw, headPitch, living.getId()));
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
        tag.putFloat("limbSwing", limbSwing);
        tag.putFloat("limbSwingAmount", limbSwingAmount);
        tag.putFloat("headYaw", headYaw);
        tag.putFloat("headPitch", headPitch);

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
        this.limbSwing = nbt.getFloat("limbSwing");
        this.limbSwingAmount = nbt.getFloat("limbSwingAmount");
        this.headYaw = nbt.getFloat("headYaw");
        this.headPitch = nbt.getFloat("headPitch");
    }

    public void setHeadPitch(float pitch)
    {
        this.headPitch = pitch;
    }

    public void setHeadYaw(float yaw)
    {
        this.headYaw = yaw;
    }

    public void setLimbSwingAmount(float position)
    {
        this.limbSwing = position;
    }

    public void setLimbSwing(float speed)
    {
        this.limbSwing = speed;
    }

}
