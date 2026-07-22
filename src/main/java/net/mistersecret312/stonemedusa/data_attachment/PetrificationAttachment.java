package net.mistersecret312.stonemedusa.data_attachment;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.util.MedusaUtil;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector3f;

public class PetrificationAttachment implements INBTSerializable<CompoundTag>
{
	public static final StreamCodec<RegistryFriendlyByteBuf, PetrificationAttachment> STREAM_CODEC = StreamCodec.of(
			(buf, attach) -> buf.writeNbt(attach.serializeNBT(buf.registryAccess())),
			(buf) -> {
				PetrificationAttachment attachment = new PetrificationAttachment();
				CompoundTag tag = buf.readNbt();
				if(tag != null)
					attachment.deserializeNBT(buf.registryAccess(), tag);
				return attachment;
			}
	);

	private int petrificationProgress = 0;
	private double beamRadius = 0;
	private Vec3 beamPosition;

	private int depetrificationProgress = 0;

	private float lockedYaw;
	private float lockedPitch;
	private float lockedHeadYaw;
	private float lockedBodyYaw;

	private boolean hadNoAiBefore;
	private boolean hadNoGravityBefore;
	private boolean isFullyPetrified;
	private boolean locked;

	public boolean wasCrouching;
	public int crackStage = 0;

	public void tick(Level level, LivingEntity living)
	{
		if(crackStage > 9 && !level.isClientSide())
			living.hurt(MedusaUtil.getPetrificationSource(living.damageSources()), Float.MAX_VALUE);

		if(depetrificationProgress > petrificationProgress)
			petrificationProgress = 0;
		if(depetrificationProgress != 0)
		{
			if(!level.isClientSide())
				living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1, 999,
					false, false, false));

			if(depetrificationProgress < 100)
				depetrificationProgress++;
			else
			{
				if(!level.isClientSide())
				{
					if(living instanceof Mob mob)
						mob.setNoAi(hadNoAiBefore);
					living.setNoGravity(hadNoGravityBefore);
				}
				depetrify();
			}
			living.syncData(AttachmentTypeInit.PETRIFICATION);
		}
		if(petrificationProgress != 0)
		{
			if(!level.isClientSide())
			{
				living.clearFire();
				if(!living.onGround())
				{
					Vec3 delta = living.getDeltaMovement();
					living.setDeltaMovement(0, (delta.y - 0.08D) * 0.98D, 0);
				}
				living.move(MoverType.SELF, living.getDeltaMovement());
			}
			petrificationProgress++;
			if(petrificationProgress == 100 && !isFullyPetrified)
			{
				this.isFullyPetrified = true;
				if(!locked)
					this.lockTransform(living);
			}
			living.syncData(AttachmentTypeInit.PETRIFICATION);
		}
	}

	public void startPetrification(LivingEntity living, Vector3f beamPos, double beamSize)
	{
		if(beamPosition != null || petrificationProgress != 0)
			return;
		if(living instanceof Player player)
			wasCrouching = player.isCrouching();

		beamPosition = new Vec3(beamPos);
		beamRadius = beamSize;
		petrificationProgress = 1;
		living.syncData(AttachmentTypeInit.PETRIFICATION);
	}

	public void startDepetrification(LivingEntity living)
	{
		if(depetrificationProgress != 0 || petrificationProgress == 0)
			return;

		depetrificationProgress = 1;
		living.syncData(AttachmentTypeInit.PETRIFICATION);
	}

	public void lockTransform(LivingEntity entity) {
		this.lockedYaw = entity.getYRot();
		this.lockedPitch = entity.getXRot();
		this.lockedHeadYaw = entity.yHeadRot;
		this.lockedBodyYaw = entity.yBodyRot;

		if (entity instanceof Mob mob)
			this.hadNoAiBefore = mob.isNoAi();
		this.hadNoGravityBefore = entity.isNoGravity();
		this.locked = true;
		entity.syncData(AttachmentTypeInit.PETRIFICATION);
	}

	public void depetrify()
	{
		petrificationProgress = 0;
		depetrificationProgress = 0;
		beamPosition = null;
		beamRadius = 0;
		crackStage = 0;
		isFullyPetrified = false;
		wasCrouching = false;
		locked = false;

		lockedBodyYaw = 0;
		lockedPitch = 0;
		lockedHeadYaw = 0;
		lockedYaw = 0;
	}

	public boolean shouldInteract()
	{
		return petrificationProgress < 75 || depetrificationProgress >= 100;
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("progress", petrificationProgress);
		tag.putInt("depetrification_progress", depetrificationProgress);
		tag.putDouble("beam_radius", beamRadius);

		tag.putFloat("locked_yaw", lockedYaw);
		tag.putFloat("locked_body_yaw", lockedBodyYaw);
		tag.putFloat("locked_pitch", lockedPitch);
		tag.putFloat("locked_head_yaw", lockedHeadYaw);

		tag.putBoolean("had_ai", hadNoAiBefore);
		tag.putBoolean("had_no_gravity", hadNoGravityBefore);
		tag.putBoolean("was_crouching", wasCrouching);
		tag.putInt("crack_stage", crackStage);
		tag.putBoolean("locked", locked);

		if(beamPosition != null)
		{
			CompoundTag posTag = new CompoundTag();
			posTag.putDouble("x", beamPosition.x);
			posTag.putDouble("y", beamPosition.y);
			posTag.putDouble("z", beamPosition.z);
			tag.put("beam_position", posTag);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
	{
		this.petrificationProgress = tag.getInt("progress");
		this.depetrificationProgress = tag.getInt("depetrification_progress");
		this.beamRadius = tag.getDouble("beam_radius");
		this.hadNoAiBefore = tag.getBoolean("had_ai");
		this.hadNoGravityBefore = tag.getBoolean("had_no_gravity");
		this.wasCrouching = tag.getBoolean("was_crouching");
		this.crackStage = tag.getInt("crack_stage");
		this.locked = tag.getBoolean("locked");

		this.lockedYaw = tag.getFloat("locked_yaw");
		this.lockedPitch = tag.getFloat("locked_pitch");
		this.lockedHeadYaw = tag.getFloat("locked_head_yaw");
		this.lockedBodyYaw = tag.getFloat("locked_body_yaw");

		if(tag.contains("beam_position"))
		{
			CompoundTag posTag = tag.getCompound("beam_position");
			double x = posTag.getDouble("x");
			double y = posTag.getDouble("y");
			double z = posTag.getDouble("z");

			this.beamPosition = new Vec3(x, y, z);
		}
	}

	public int getPetrificationProgress()
	{
		return petrificationProgress;
	}

	public void setPetrificationProgress(int progress)
	{
		this.petrificationProgress = progress;
	}

	public double getBeamRadius()
	{
		return beamRadius;
	}

	public Vec3 getBeamPosition()
	{
		return beamPosition;
	}

	public int getDepetrificationProgress()
	{
		return depetrificationProgress;
	}

	public void setDepetrificationProgress(int progress)
	{
		this.depetrificationProgress = progress;
	}

	public float getLockedBodyYaw()
	{
		return lockedBodyYaw;
	}

	public float getLockedHeadYaw()
	{
		return lockedHeadYaw;
	}

	public float getLockedPitch()
	{
		return lockedPitch;
	}

	public float getLockedYaw()
	{
		return lockedYaw;
	}

	public boolean isFullyPetrified()
	{
		return isFullyPetrified;
	}

	public boolean hadNoAiBefore()
	{
		return hadNoAiBefore;
	}
}
