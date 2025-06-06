package net.mistersecret312.stonemedusa.entity;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.capability.WorldCapability;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
import net.mistersecret312.stonemedusa.init.*;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.network.packets.EntityPetrifiedPacket;
import net.mistersecret312.stonemedusa.network.packets.MedusaActivatedPacket;
import net.mistersecret312.stonemedusa.network.packets.MedusaSoundTickPacket;
import net.mistersecret312.stonemedusa.network.packets.MedusaTextureUpdatePacket;

import java.util.List;
import java.util.Random;

public class MedusaProjectile extends ThrowableItemProjectile
{
    public static final String ENERGY = "energy";
    public static final String TARGET_RADIUS = "targetRadius";
    public static final String CURRENT_RADIUS = "currentRadius";
    public static final String FADE = "fade";
    public static final String DELAY = "delay";
    public static final String IS_ACTIVE = "isActive";
    public static final String IS_COUNTINDOWN_ACTIVE = "isCountdownActive";
    public static final String IS_GENERATED = "isGenerated";
    public static final String SPEED = "speed";

    private static final EntityDataAccessor<Boolean> ACTIVE =
            SynchedEntityData.defineId(MedusaProjectile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> RADIUS =
            SynchedEntityData.defineId(MedusaProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FADING =
            SynchedEntityData.defineId(MedusaProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> TARGET_TYPE =
            SynchedEntityData.defineId(MedusaProjectile.class, EntityDataSerializers.STRING);

    public static int IDLE_TIME = 200;
    
    private int energy = 0;
    private float targetRadius = 0;
    private int delay = 0;
    private boolean countingDown = false;
    private double speed = MedusaConfig.base_speed.get();
    private boolean generated = false;

    private int activeTicker = 0;
    private int expansionTicker = 0;


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

    public MedusaProjectile(Level level, LivingEntity living, int energy, float radius, int delay, boolean countingDown, boolean isActive, String targetType, boolean generated)
    {
        super(EntityInit.MEDUSA.get(), living, level);
        this.energy = energy;
        this.targetRadius = radius;
        this.delay = delay;
        this.countingDown = countingDown;
        this.setActive(isActive);
        this.setTargetType(targetType);
        this.generated = generated;
    }

    public MedusaProjectile(Level level, int energy, float radius, int delay, boolean countingDown, boolean isActive, String targetType, boolean generated)
    {
        super(EntityInit.MEDUSA.get(), level);
        this.energy = energy;
        this.targetRadius = radius;
        this.delay = delay;
        this.countingDown = countingDown;
        this.setActive(isActive);
        this.setTargetType(targetType);
        this.generated = generated;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ACTIVE, false);
        this.entityData.define(RADIUS, 0F);
        this.entityData.define(TARGET_TYPE, "");
        this.entityData.define(FADING, 0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        this.energy = tag.getInt(ENERGY);
        this.targetRadius = tag.getFloat(TARGET_RADIUS);
        this.entityData.set(RADIUS, tag.getFloat(CURRENT_RADIUS));
        this.entityData.set(FADING, tag.getFloat(FADE));
        this.delay = tag.getInt(DELAY);
        this.entityData.set(ACTIVE, tag.getBoolean(IS_ACTIVE));
        this.entityData.set(TARGET_TYPE, tag.getString("targetType"));
        this.countingDown = tag.getBoolean(IS_COUNTINDOWN_ACTIVE);
        this.generated = tag.getBoolean(IS_GENERATED);
        this.speed = tag.getInt(SPEED);
        this.activeTicker = tag.getInt("activeTicker");
        this.expansionTicker = tag.getInt("expansionTicker");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt(ENERGY, this.energy);
        tag.putFloat(TARGET_RADIUS, this.targetRadius);
        tag.putFloat(CURRENT_RADIUS, this.entityData.get(RADIUS));
        tag.putFloat(FADE, this.entityData.get(FADING));
        tag.putInt(DELAY, this.delay);
        tag.putBoolean(IS_ACTIVE, this.entityData.get(ACTIVE));
        tag.putString("targetType", this.entityData.get(TARGET_TYPE));
        tag.putBoolean(IS_COUNTINDOWN_ACTIVE, this.countingDown);
        tag.putBoolean(IS_GENERATED, this.generated);
        tag.putDouble(SPEED, this.speed);
        tag.putInt("activeTicker", activeTicker);
        tag.putInt("expansionTicker", expansionTicker);
    }

    @Override
    public void tick()
    {
        if(this.level().isClientSide())
            return;

        if(delay > 0 && this.isCountingDown())
            countdown();

        if(isActive())
        {
            activeTick();
        }
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
        this.setCountingDown(false);
        ((MedusaItem) this.getDefaultItem()).setCountdownActive(this.getItem(), false);
        MedusaItem.setActive(this.getItem(), true);
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);

        int demandedEnergy = (int) ((this.targetRadius*this.speed*2+IDLE_TIME)*100);
        double energyPercentage = (double) this.energy /MedusaConfig.max_energy.get();
        if(energyPercentage < 0.05)
            this.speed = Math.max(3, (100-(energyPercentage*100))-65);
        if(demandedEnergy > this.energy)
            this.targetRadius = Math.max(1f, (float) ((this.energy - 20000) / (25 * this.speed * 2)));

        //this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1F, 1F);

    }

    @Override
    public boolean isNoGravity()
    {
        return this.isActive();
    }

    public void activeTick()
    {

        if(activeTicker >= this.targetRadius*this.speed+2*IDLE_TIME)
        {
            deactivate();

            this.level().playSound(null, this.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.AMBIENT, 1f, 1f);

            activeTicker = 0;
            expansionTicker = 0;
        }
        else
        {
            if(activeTicker == 0)
            {
                LevelChunk chunk = this.level().getChunkAt(this.blockPosition());
                if(level() instanceof ServerLevel serverLevel)
                    ForgeChunkManager.forceChunk(serverLevel, StoneMedusa.MOD_ID, this.blockPosition(), chunk.getPos().x, chunk.getPos().z, true, true);


                this.energy -= MedusaConfig.flat_activation_cast.get();
                this.energy -= (int) (MedusaConfig.cost_per_meter.get()*this.getTargetRadius());

                if(energy < 0)
                    energy = 0;

                NetworkInit.sendToTracking(this, new MedusaActivatedPacket(this.getId()));
                NetworkInit.sendToTracking(this, new MedusaTextureUpdatePacket(this.getId(), this.isActive(), this.isCountingDown()));
            }

            if (activeTicker % 85 == 0)
            {
                NetworkInit.sendToTracking(this, new MedusaSoundTickPacket(this.getId()));
            }

            activeTicker++;
            if(activeTicker <= this.targetRadius*this.speed && !(activeTicker > this.targetRadius*this.speed))
                expansionTicker++;
            if(activeTicker > (this.targetRadius*this.speed)+IDLE_TIME && !(activeTicker > this.targetRadius*this.speed+2*IDLE_TIME))
                this.setFading(this.getFading()+0.0048f);


            if(expansionTicker > 0 && activeTicker <= this.targetRadius*this.speed)
                this.setCurrentRadius((float) Mth.lerp(expansionTicker/(this.targetRadius*this.speed), 0, this.targetRadius));

            if(this.getCurrentRadius() > 0f && this.getFading() < 0.4f)
                petrify(this.getCurrentRadius()*1.5F+2.5f);

            level().getCapability(CapabilitiesInit.WORLD).ifPresent(cap -> {
                WorldCapability.MedusaData data = new WorldCapability.MedusaData(this.getCurrentRadius(), this.getFading(), this.blockPosition(), ResourceKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), ResourceLocation.parse(this.getTargetType())));
                cap.getMedusaData().put(this.position(), data);
            });
        }
    }

    public void petrify(float radius)
    {
        List<Entity> targets = this.level().getEntities(this,
                new AABB(this.blockPosition().getX()- radius, this.blockPosition().getY()- radius,
                        this.blockPosition().getZ()- radius, this.blockPosition().getX()+ radius,
                        this.blockPosition().getY()+ radius, this.blockPosition().getZ()+ radius),
                entity ->
                {
                    if(entity instanceof LivingEntity living)
                    {
                        if(living.getEffect(EffectInit.PETRIFICATION.get()) != null)
                            return false;

                        double distance = this.distanceTo(living);
                        return distance-2 < radius && distance+2 >= radius;
                    }
                    return false;
                });
        for(Entity entity : targets)
        {
            ResourceLocation targetType = new ResourceLocation(this.getTargetType());
            if(ForgeRegistries.ENTITY_TYPES.containsKey(targetType) && ForgeRegistries.ENTITY_TYPES.getValue(targetType) != entity.getType())
                continue;

            if(entity.isEyeInFluidType(FluidTypeInit.REVIVAL_FLUID_TYPE.get()))
                continue;

            if(entity.getType().is(StoneMedusa.PETRIFICATION_IMMUNE))
                continue;

            if(entity instanceof Player player)
                if(player.isCreative() || player.isSpectator())
                    continue;

            if (entity instanceof LivingEntity living)
                if (!living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()) && Math.sqrt(living.blockPosition().distSqr(new Vec3i(this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ()))) < radius)
                {
                    living.addEffect(new MobEffectInstance(EffectInit.PETRIFICATION.get(), living instanceof Player ? PetrificationConfig.player_petrification_time.get() : PetrificationConfig.entity_petrification_time.get(), 0, false, false, true), living);
                    living.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
                    {
                        cap.setPetrified(true);

                        living.level().playSound(null, living.blockPosition(), SoundEvents.DRIPSTONE_BLOCK_PLACE, SoundSource.MASTER, 1F, 0.5F);
                        NetworkInit.sendToTracking(living, new EntityPetrifiedPacket(living.getId()));

                        cap.setAge(Integer.valueOf(living.tickCount).floatValue());
                    });
                }
        }
    }

    public void deactivate()
    {
        this.setActive(false);
        this.setCountingDown(false);
        ((MedusaItem) this.getDefaultItem()).setCountdownActive(this.getItem(), false);
        MedusaItem.setActive(this.getItem(), false);
        this.level().getCapability(CapabilitiesInit.WORLD).ifPresent(cap -> cap.getMedusaData().remove(this.position()));
        this.setNoGravity(false);
        this.noPhysics = false;
        this.setDeltaMovement(Vec3.ZERO);
        this.setSpeed(MedusaConfig.base_speed.get());

        NetworkInit.sendToTracking(this, new MedusaTextureUpdatePacket(this.getId(), this.isActive(), this.isCountingDown()));

        LevelChunk chunk = this.level().getChunkAt(this.blockPosition());
        if(level() instanceof ServerLevel serverLevel)
            ForgeChunkManager.forceChunk(serverLevel, StoneMedusa.MOD_ID, this.blockPosition(), chunk.getPos().x, chunk.getPos().z, true, true);

    }

    @Override
    protected Item getDefaultItem()
    {
        return ItemInit.MEDUSA.get();
    }

    @Override
    protected void onHit(HitResult pResult)
    {
        Random random = new Random();
        if(this.isGenerated())
        {
            if(random.nextFloat() > MedusaConfig.break_chance.get())
            {
                this.discard();
                return;
            }

            this.teleportRelative(0, 1.5, 0);
            this.setTargetRadius((float) random.nextDouble(MedusaConfig.min_generated_radius.get(), MedusaConfig.max_generated_radius.get()));
            this.setEnergy(random.nextInt((int) (MedusaConfig.min_generated_energy.get()*MedusaConfig.max_energy.get()), (int) (MedusaConfig.max_generated_energy.get()*MedusaConfig.max_energy.get())));
            if(random.nextFloat() > MedusaConfig.player_target_chance.get())
                this.setTargetType("minecraft:player");
            this.activate();
            this.setGenerated(false);
            return;
        }
        
        ItemStack stack = MedusaItem.getMedusa(ItemInit.MEDUSA.get(), this.getEnergy(), this.getTargetRadius(), this.getDelay(), ((MedusaItem) this.getItem().getItem()).getStartDelay(this.getItem()), this.isActive(), this.isCountingDown(), this.getTargetType());

        this.level().addFreshEntity(new ItemEntity(this.level(), this.position().x, this.position().y, this.position().z, stack));

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

    public float getFading()
    {
        return this.entityData.get(FADING);
    }

    public int getDelay()
    {
        return delay;
    }

    public boolean isActive()
    {
        return this.entityData.get(ACTIVE);
    }

    public boolean isCountingDown()
    {
        return countingDown;
    }

    public String getTargetType()
    {
        String type = this.entityData.get(TARGET_TYPE);

        return type;
    }

    public double getSpeed()
    {
        return speed;
    }

    public boolean isGenerated()
    {
        return generated;
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

    public void setFading(float fading)
    {
        this.entityData.set(FADING, fading);
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    public void setActive(boolean active)
    {
        this.entityData.set(ACTIVE, active);
    }

    public void setCountingDown(boolean countingDown)
    {
        this.countingDown = countingDown;
    }

    public void setTargetType(String type)
    {
        this.entityData.set(TARGET_TYPE, type);
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public void setGenerated(boolean generated)
    {
        this.generated = generated;
    }
}
