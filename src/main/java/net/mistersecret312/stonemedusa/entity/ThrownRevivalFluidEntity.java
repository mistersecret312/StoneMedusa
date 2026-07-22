package net.mistersecret312.stonemedusa.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.config.RevivalConfig;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.init.EntityInit;
import net.mistersecret312.stonemedusa.init.ItemInit;

import java.util.List;
import java.util.UUID;

public class ThrownRevivalFluidEntity extends ThrowableItemProjectile
{
	public ThrownRevivalFluidEntity(EntityType<ThrownRevivalFluidEntity> type, Level level)
	{
		super(type, level);
	}

	public ThrownRevivalFluidEntity(Level level)
	{
		super(EntityInit.THROWN_REVIVAL_FLUID.get(), level);
	}

	public ThrownRevivalFluidEntity(Level level, double x, double y, double z)
	{
		super(EntityInit.THROWN_REVIVAL_FLUID.get(), x, y, z, level);
	}

	public ThrownRevivalFluidEntity(Level level, LivingEntity living)
	{
		super(EntityInit.THROWN_REVIVAL_FLUID.get(), living, level);
	}

	@Override
	protected Item getDefaultItem()
	{
		return ItemInit.REVIVAL_FLUID_FLASK.get();
	}
	@Override
	public boolean isPickable()
	{
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount)
	{
		AABB aabb = this.getBoundingBox().inflate(2.0D, 2.0D, 2.0D);
		this.level().levelEvent(2002, this.blockPosition(), 13409380);
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
		for (LivingEntity living : list)
			living.getData(AttachmentTypeInit.PETRIFICATION.get()).startDepetrification(living);
		discard();
		return true;
	}

	@Override
	protected void onHit(HitResult pResult)
	{
		AABB aabb = this.getBoundingBox().inflate(2.0D, 2.0D, 2.0D);
		this.level().levelEvent(2002, this.blockPosition(), 13409380);
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
		for (LivingEntity living : list)
			living.getData(AttachmentTypeInit.PETRIFICATION.get()).startDepetrification(living);
		discard();
	}

	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		AABB aabb = this.getBoundingBox().inflate(2.0D, 2.0D, 2.0D);
		this.level().levelEvent(2002, this.blockPosition(), 13409380);
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
		for (LivingEntity living : list)
			living.getData(AttachmentTypeInit.PETRIFICATION.get()).startDepetrification(living);
		discard();
	}
}
