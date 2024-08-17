package net.mistersecret312.stonemedusa.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.mistersecret312.stonemedusa.init.EntityInit;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.item.MedusaItem;

public class MedusaProjectile extends ThrowableItemProjectile
{
    public static final String ENERGY = "energy";
    public static final String TARGET_RADIUS = "targetRadius";
    public static final String CURRENT_RADIUS = "currentRadius";
    public static final String DELAY = "delay";
    public static final String IS_ACTIVE = "isActive";

    private static final EntityDataAccessor<Boolean> ACTIVE =
            SynchedEntityData.defineId(MedusaProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> RADIUS =
            SynchedEntityData.defineId(MedusaProjectile.class, EntityDataSerializers.FLOAT);


    private int energy = 0;
    private float targetRadius = 0;
    private int delay = 0;

    private int activeTicker = 0;
    private int expansionTicker = 0;
    private int shrinkingTicker = 0;


    public MedusaProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public MedusaProjectile(Level level)
    {
        super(EntityInit.MEDUSA.get(), level);
    }

    public MedusaProjectile(Level level, LivingEntity living)
    {
        super(EntityInit.MEDUSA.get(), living, level);
    }

    public MedusaProjectile(Level level, LivingEntity living, int energy, float radius, int delay)
    {
        super(EntityInit.MEDUSA.get(), living, level);
        this.energy = energy;
        this.targetRadius = radius;
        this.delay = delay;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(RADIUS, 0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        this.energy = tag.getInt(ENERGY);
        this.targetRadius = tag.getFloat(TARGET_RADIUS);
        this.entityData.set(RADIUS, tag.getFloat(CURRENT_RADIUS));
        this.delay = tag.getInt(DELAY);
        this.entityData.set(ACTIVE, tag.getBoolean(IS_ACTIVE));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt(ENERGY, this.energy);
        tag.putFloat(TARGET_RADIUS, this.targetRadius);
        tag.putFloat(CURRENT_RADIUS, this.entityData.get(RADIUS));
        tag.putInt(DELAY, this.delay);
        tag.putBoolean(IS_ACTIVE, this.entityData.get(ACTIVE));
    }

    @Override
    public void tick()
    {
        super.tick();
        if(delay > 0)
            countdown();
        if(isActive())
            activeTick();

    }

    @Override
    public boolean isInvulnerable()
    {
        return true;
    }

    public void countdown()
    {
        this.delay--;
        if(delay == 0)
            activate();
    }

    public void activate()
    {
        this.setActive(true);
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public void activeTick()
    {

        if(activeTicker >= this.targetRadius*100)
        {
            deactivate();
            activeTicker = 0;
        }
        else
        {
            if(this.energy > 100)
                this.energy -= 100;
            else this.energy = 0;

            activeTicker++;
            if(activeTicker <= this.targetRadius*40 && !(activeTicker > this.targetRadius*40))
                expansionTicker++;
            if(activeTicker > this.targetRadius*60 && activeTicker <= this.targetRadius*100)
                shrinkingTicker++;

            if(expansionTicker > 0 && activeTicker <= this.targetRadius*40)
                this.setCurrentRadius(Mth.lerp(expansionTicker/(this.targetRadius*40), 0, this.targetRadius));
            if(shrinkingTicker > 0 && activeTicker > this.targetRadius*60 && activeTicker <= this.targetRadius*100)
                this.setCurrentRadius(Mth.lerp(shrinkingTicker/(this.targetRadius*40), this.targetRadius, 0));
        }
    }

    public void deactivate()
    {
        this.setActive(false);
        this.setNoGravity(false);
    }

    @Override
    protected Item getDefaultItem()
    {
        return null;
    }

    @Override
    protected void onHit(HitResult pResult)
    {
        ItemStack stack = MedusaItem.getMedusa(ItemInit.MEDUSA.get(), this.getEnergy(), this.getTargetRadius(), this.getDelay());

        this.level().addFreshEntity(new ItemEntity(this.level(), this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ(), stack));

        this.discard();
    }

    public int getEnergy()
    {
        return this.energy;
    }

    public float getTargetRadius()
    {
        return targetRadius;
    }

    public float getCurrentRadius()
    {
        return this.entityData.get(RADIUS);
    }

    public int getDelay()
    {
        return delay;
    }

    public boolean isActive()
    {
        return this.entityData.get(ACTIVE);
    }

    public void setEnergy(int energy)
    {
        this.energy = energy;
    }

    public void setTargetRadius(float radius)
    {
        this.targetRadius = radius;
    }

    public void setCurrentRadius(float currentRadius)
    {
        this.entityData.set(RADIUS, currentRadius);
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    public void setActive(boolean active)
    {
        this.entityData.set(ACTIVE, active);
    }
}
