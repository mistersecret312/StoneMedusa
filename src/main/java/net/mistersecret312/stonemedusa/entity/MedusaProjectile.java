package net.mistersecret312.stonemedusa.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.EntityInit;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.item.MedusaItem;

import java.util.List;

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
        if(this.level().isClientSide())
            return;
        if(delay > 0)
            countdown();
        if(isActive())
            activeTick();
        if(!isActive())
            super.tick();
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
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);
    }

    @Override
    public boolean isNoGravity()
    {
        return this.isActive();
    }

    public void activeTick()
    {

        if(activeTicker >= this.targetRadius*45)
        {
            deactivate();
            activeTicker = 0;
            expansionTicker = 0;
            shrinkingTicker = 0;
        }
        else
        {
            if(this.energy > 100)
                this.energy -= 100;
            else this.energy = 0;

            activeTicker++;
            if(activeTicker <= this.targetRadius*5 && !(activeTicker > this.targetRadius*5))
                expansionTicker++;
            if(activeTicker > this.targetRadius*40 && activeTicker <= this.targetRadius*45)
                shrinkingTicker++;

            if(expansionTicker > 0 && activeTicker <= this.targetRadius*5)
                this.setCurrentRadius(Mth.lerp(expansionTicker/(this.targetRadius*5), 0, this.targetRadius));
            if(shrinkingTicker > 0 && activeTicker > this.targetRadius*40 && activeTicker <= this.targetRadius*45)
                this.setCurrentRadius(Mth.lerp(shrinkingTicker/(this.targetRadius*5), this.targetRadius, 0));

            if(this.getCurrentRadius() > 0f)
                petrify();
        }
    }

    public void petrify()
    {
        List<Entity> targets = this.level().getEntities(this,
                new AABB(this.blockPosition().getX()-(this.getCurrentRadius()*1.3f), this.blockPosition().getY()-(this.getCurrentRadius()*1.3f),
                        this.blockPosition().getZ()-(this.getCurrentRadius()*1.3f), this.blockPosition().getX()+(this.getCurrentRadius()*1.3f),
                        this.blockPosition().getY()+(this.getCurrentRadius()*1.3f), this.blockPosition().getZ()+(this.getCurrentRadius()*1.3f)),
                entity -> entity instanceof LivingEntity);

        for(Entity entity : targets)
        {
            if(entity instanceof LivingEntity living)
                if(!living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                    living.addEffect(new MobEffectInstance(EffectInit.PETRIFICATION.get(), 4000, 0, false, false, true));
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
        if(this.isActive())
            return;

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
