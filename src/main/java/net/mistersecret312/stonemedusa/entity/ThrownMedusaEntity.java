package net.mistersecret312.stonemedusa.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.data_components.DiamondBatteryComponent;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.init.DataComponentInit;
import net.mistersecret312.stonemedusa.init.EntityInit;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.items.DiamondBatteryItem;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.source.EntitySource;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.UUID;

public class ThrownMedusaEntity extends ThrowableItemProjectile implements IMedusa
{
	public ThrownMedusaEntity(EntityType<ThrownMedusaEntity> type, Level level)
	{
		super(type, level);
	}

	public ThrownMedusaEntity(Level level)
	{
		super(EntityInit.MEDUSA.get(), level);
	}

	public ThrownMedusaEntity(Level level, double x, double y, double z)
	{
		super(EntityInit.MEDUSA.get(), x, y, z, level);
	}

	public ThrownMedusaEntity(Level level, LivingEntity living)
	{
		super(EntityInit.MEDUSA.get(), living, level);
	}

	@Override
	protected Item getDefaultItem()
	{
		return ItemInit.MEDUSA.get();
	}

	@Override
	public void tick()
	{
		super.tick();
	}

	@Override
	public void beamStart(MedusaBeam beam, Level level)
	{
		this.setNoGravity(true);
		this.setDeltaMovement(new Vec3(0d, 0d, 0d));
	}

	@Override
	public void beamTick(MedusaBeam beam, Level level)
	{
		if(beam.getExpansionTick() != 0)
			this.setDeltaMovement(getDeltaMovement().multiply(0.95, 0.95, 0.95));
	}

	@Override
	public void beamEnd(MedusaBeam beam, Level level)
	{
		this.setNoGravity(false);
	}

	@Override
	public boolean isPickable()
	{
		return true;
	}

	@Override
	public boolean canCollideWith(Entity entity)
	{
		return false;
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	@Override
	public boolean canBeHitByProjectile()
	{
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount)
	{
		Entity direct = source.getDirectEntity();
		if(direct instanceof Player && !source.is(Tags.DamageTypes.IS_MAGIC))
		{
			this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), getItem().getBreakingSound(),
					this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F,
					false);
			for(int i = 0; i < 15; ++i) {
				Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - (double)0.5F) * 0.1, Math.random() * 0.1 + 0.1, (double)0.0F);
				vec3 = vec3.xRot(-this.getXRot() * ((float)Math.PI / 180F));
				vec3 = vec3.yRot(-this.getYRot() * ((float)Math.PI / 180F));
				double d0 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
				Vec3 vec31 = new Vec3(((double)this.random.nextFloat() - (double)0.5F) * 0.3, d0, 0.6);
				vec31 = vec31.xRot(-this.getXRot() * ((float)Math.PI / 180F));
				vec31 = vec31.yRot(-this.getYRot() * ((float)Math.PI / 180F));
				vec31 = vec31.add(this.getX(), this.getY(), this.getZ());
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()),
						vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05, vec3.z);
			}
			if(!level().isClientSide())
			{
				discard();
				ResourceKey<LootTable> table = ResourceKey.create(Registries.LOOT_TABLE,
						ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "broken_medusa"));
				LootParams lootParams = new LootParams.Builder((ServerLevel) level()).create(LootContextParamSets.EMPTY);
				LootTable lootTable = level().getServer().reloadableRegistries().getLootTable(table);
				List<ItemStack> stacks = lootTable.getRandomItems(lootParams);
				for(ItemStack stack : stacks)
				{
					Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - (double)0.5F) * 0.1, Math.random() * 0.1 + 0.1, (double)0.0F);
					vec3 = vec3.xRot(-this.getXRot() * ((float)Math.PI / 180F));
					vec3 = vec3.yRot(-this.getYRot() * ((float)Math.PI / 180F));
					double d0 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
					Vec3 vec31 = new Vec3(((double)this.random.nextFloat() - (double)0.5F) * 0.3, d0, 0.6);
					vec31 = vec31.xRot(-this.getXRot() * ((float)Math.PI / 180F));
					vec31 = vec31.yRot(-this.getYRot() * ((float)Math.PI / 180F));
					vec31 = vec31.add(this.getX(), this.getY(), this.getZ());


					ItemEntity fragment = new ItemEntity(level(), vec31.x, vec31.y, vec31.z, stack);
					fragment.setDeltaMovement(vec3);
					level().addFreshEntity(fragment);
				}
			}
			return true;
		}
		if(direct != null)
		{
			Vec3 speed = direct.getDeltaMovement();
			this.setDeltaMovement(speed.multiply(0.25, 0.25, 0.25));
			this.hasImpulse = true;
		}

		return false;
	}

	@Override
	protected void onHit(HitResult pResult)
	{
		discard();
		ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y, position().z, getItem());
		UUID deviceId = MedusaItem.getDeviceId(getItem());
		MedusaBeam beam = level().getData(AttachmentTypeInit.MEDUSA).getMedusa(deviceId);
		if(beam != null)
		{
			beam.setSource(new EntitySource(itemEntity.getUUID(), itemEntity.getId()));
			level().syncData(AttachmentTypeInit.MEDUSA);
		}
		level().addFreshEntity(itemEntity);
	}

	@Override
	protected void onHitEntity(EntityHitResult result)
	{

	}

	@Override
	public void consumeActivationEnergy(MedusaBeam beam, Level level, int energy)
	{
		ItemStack stack = getItem();
		if(stack.has(DataComponentInit.BATTERY))
		{
			DiamondBatteryComponent batteryComponent = stack.get(DataComponentInit.BATTERY);
			if(batteryComponent == null)
				return;
			ItemStack battery = batteryComponent.batteryStack();
			int newEnergy = DiamondBatteryItem.getEnergy(battery)-energy;
			if(newEnergy < 0)
			{
				newEnergy = 0;
				beam.end(level);
			}
			DiamondBatteryItem.setEnergy(battery, newEnergy);
			stack.set(DataComponentInit.BATTERY, new DiamondBatteryComponent(battery));
		}

	}

	@Override
	public int getAvailableEnergy(MedusaBeam beam, Level level)
	{
		ItemStack stack = this.getItem();
		if(stack != null && stack.has(DataComponentInit.BATTERY))
		{
			DiamondBatteryComponent component = stack.get(DataComponentInit.BATTERY);
			if(component != null)
				return DiamondBatteryItem.getEnergy(component.batteryStack());
		}
		return 0;
	}
}
