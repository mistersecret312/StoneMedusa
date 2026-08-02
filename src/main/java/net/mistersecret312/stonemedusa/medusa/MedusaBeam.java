package net.mistersecret312.stonemedusa.medusa;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.data_attachment.PetrificationAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.init.BeamTypeInit;
import net.mistersecret312.stonemedusa.init.TagsInit;
import net.mistersecret312.stonemedusa.medusa.components.MedusaAttribute;
import net.mistersecret312.stonemedusa.medusa.components.MedusaComponentType;
import net.mistersecret312.stonemedusa.medusa.source.InventorySource;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import net.mistersecret312.stonemedusa.util.MedusaUtil;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MedusaBeam
{
	public static final StreamCodec<RegistryFriendlyByteBuf, MedusaBeam> STREAM_CODEC = StreamCodec.of(
			(buf, attach) -> buf.writeNbt(attach.serializeNBT()),
			(buf) -> deserializeNBT(buf.readNbt())
	);

	private int tick = 0;
	private int expansionTick = 0;
	private int shrinkingTick = 0;

	private double currentRadius = 0;
	private double prevRadius = 0;
	private boolean reachedMaxRadius = false;

	private int maxDelay = -1;
	private int delayTicker = -1;

	private boolean markForRemoval;
	public int attemptedScans = 0;

	public boolean serverApproved = true;

	private MedusaSettings settings;
	private Vector3f prevPosition;

	private List<UUID> petrifiedBefore = new ArrayList<>();

	public MedusaBeam(MedusaSettings settings)
	{
		this.settings = settings;
		this.prevPosition = settings.position();
	}

	public MedusaBeamType<?> getType()
	{
		return BeamTypeInit.DEFAULT.get();
	}

	public void tick(Level level)
	{
		if (level == null || shouldRemove())
			return;
		if (!level.dimension().equals(getSettings().dimension()))
			return;

		IMedusa medusa = settings.source().resolve(level);
		if ((medusa != null) || (level.isClientSide() && serverApproved))
		{
			if (!level.isClientSide() && medusa != null)
				medusa.beamTick(this, level);

			this.setPosition(level, settings.source().providePosition(level));
		}
		else if (!level.isClientSide())
		{
			if(attemptedScans < 5)
			{
				AABB area = new AABB(new BlockPos((int) settings.position().x, (int) settings.position().y,
						(int) settings.position().z)).inflate(10);
				MedusaSource newSource = MedusaUtil.getSpecificSource(level, area, settings.uuid());
				if(newSource != null)
				{
					setSource(newSource);
					attemptedScans = 0;
					serverApproved = true;
					level.syncData(AttachmentTypeInit.MEDUSA);
					return;
				}
				serverApproved = false;
				attemptedScans++;
				return;
			}

			markForRemoval(level);
			return;
		}

		if (this.delayTicker >= 0)
		{
			this.delayTicker--;
			return;
		}

		if (tick == 0 && !level.isClientSide())
			start(level);
		tick++;

		int finalExpansionTick = (int) (settings.radius() / settings.speed());
		if (finalExpansionTick == Integer.MAX_VALUE && !level.isClientSide())
			end(level);

		if(!level.isClientSide())
		{
			MedusaHandler handler = getHandler(level);
			if(handler != null)
				handler.damageComponent(MedusaComponentType.HULL, 1);
		}
		if (expansionTick <= finalExpansionTick + MedusaConfig.medusa_idle_time.get())
		{
			if(!level.isClientSide())
			{
				MedusaHandler handler = getHandler(level);
				if(handler != null) handler.damageComponent(MedusaComponentType.WIRING, 1);
			}
			expand(settings.speed());
			expansionTick++;
		}
		else if (shrinkingTick == 0)
		{
			shrinkingTick = expansionTick - MedusaConfig.medusa_idle_time.get();
			this.reachedMaxRadius = true;
			PetrifiedArea area = new PetrifiedArea(new Vec2(settings.position().x, settings.position().z),
					settings.dimension(), settings.radius(),
					level.getGameTime()+expansionTick+(long) (240*settings.radius()),
					new HashSet<>(), true);
			level.getData(AttachmentTypeInit.MEDUSA).addPetrifiedArea(level, area);
		}

		if (shrinkingTick != 0)
		{
			expand(-settings.speed());
			shrinkingTick--;

			if ((shrinkingTick == 0 || Double.compare(currentRadius, 0D) <= 0) && !level.isClientSide())
				end(level);

		} else if (!level.isClientSide())
				affectEntities(level);
	}

	public void expand(double rate)
	{
		prevRadius = currentRadius;
		currentRadius += rate;

		if(currentRadius > settings.radius())
			currentRadius = settings.radius();
		if(currentRadius < 0)
			currentRadius = 0;
	}

	public void start(Level level)
	{
		IMedusa medusa = settings.source().resolve(level);
		if(medusa != null)
		{
			if(!level.isClientSide())
			{
				MedusaHandler handler = getHandler(level);
				if(handler != null)
				{
					handler.damageComponent(MedusaComponentType.FOCAL_POINT, 1);
					handler.damageComponent(MedusaComponentType.BATTERY_SLOT, 10);
				}
				this.consumeEnergy(level, medusa);
			}

			medusa.beamStart(this, level);
		}
	}

	public void end(Level level)
	{
		markForRemoval(level);
		IMedusa medusa = settings.source().resolve(level);
		if(medusa != null)
			medusa.beamEnd(this, level);
	}

	public void affectEntities(Level level)
	{
		AABB box = new AABB(new BlockPos(0, 0, 0));
		box = box.inflate(currentRadius).move(settings.position());
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);

		for(LivingEntity living : entities)
		{
			if(living instanceof Player player)
			{
				if(player.isSpectator())
					continue;
				if(player.getAbilities().invulnerable)
					continue;
			}
			if(petrifiedBefore.contains(living.getUUID()))
				continue;
			if(living.getType().is(TagsInit.Entity.PETRIFICATION_IMMUNE))
				continue;

			Vector3f pos = new Vector3f(settings.position());
			double distance = living.position().distanceTo(new Vec3(settings.position()));
			if(distance > settings.radius())
				continue;
			PetrificationAttachment petrification = living.getData(AttachmentTypeInit.PETRIFICATION);
			if(petrification.getPetrificationProgress() == 0)
			{
				petrification.startPetrification(living, pos.sub(living.position().toVector3f()), currentRadius);
				petrifiedBefore.add(living.getUUID());
			}
		}
	}

	public<T extends IMedusa> void consumeEnergy(Level level, T medusa)
	{
		MedusaHandler handler = getHandler(level);
		if(handler == null)
			return;

		int energy = medusa.getAvailableEnergy(this, level);
		double energyFactor = 5d*handler.getAttribute(MedusaAttribute.ENERGY_EFFICIENCY);
		int energyToUse = Math.max(1, (int) (energyFactor*settings.radius()));

		double maxEnergyUsage = handler.getAttribute(MedusaAttribute.MAX_ENERGY_FLUX)*medusa.getMaximumEnergy(settings.source(), level);
		if(energyToUse > maxEnergyUsage)
		{
			if(maxEnergyUsage == 0)
				end(level);

			settings = new MedusaSettings(maxEnergyUsage/energyFactor, settings.speed(), settings.color(),
					settings.location(), settings.uuid(), settings.source());
		}

		if(energy < energyToUse)
		{
			settings = new MedusaSettings(energy/(energyFactor*1.25d), settings.speed(), settings.color(),
					settings.location(), settings.uuid(), settings.source());
		}

		level.syncData(AttachmentTypeInit.MEDUSA);
		medusa.consumeActivationEnergy(this, level, energyToUse);
	}

	public MedusaSettings getSettings()
	{
		return settings;
	}

	public MedusaHandler getHandler(Level level)
	{
		UUID uuid = settings.source().getMedusaUUID(level);
		if(uuid != null)
			return level.getData(AttachmentTypeInit.MEDUSA).getMedusaHandlers().get(uuid);
		else return null;
	}

	public void setPosition(Level level, MedusaSettings.MedusaPosition position)
	{
		if(position == null)
			return;

		this.prevPosition = settings.position();
		this.settings = new MedusaSettings(settings.radius(), settings.speed(),
				settings.color(), position, settings.uuid(), settings.source());
	}

	public void setSource(MedusaSource source)
	{
		this.settings = new MedusaSettings(settings.radius(), settings.speed(),
				settings.color(), settings.location(), settings.uuid(), source);
	}

	public double getCurrentRadius()
	{
		return currentRadius;
	}

	public void setCurrentRadius(double currentRadius)
	{
		this.currentRadius = currentRadius;
	}

	public double getPreviousRadius()
	{
		return prevRadius;
	}

	public void setPreviousRadius(double prevRadius)
	{
		this.prevRadius = prevRadius;
	}

	public Vector3f getPreviousPosition()
	{
		return prevPosition;
	}

	public int getDelayTicker()
	{
		return delayTicker;
	}

	public int getMaxDelay()
	{
		return maxDelay;
	}

	public void setDelayTicker(int delayTicker)
	{
		this.delayTicker = delayTicker;
	}

	public void setMaxDelay(int maxDelay)
	{
		this.maxDelay = maxDelay;
	}

	public boolean hasReachedMaxRadius()
	{
		return reachedMaxRadius;
	}

	public int getExpansionTick()
	{
		return expansionTick;
	}

	public int getShrinkingTick()
	{
		return shrinkingTick;
	}

	public int getColor()
	{
		return settings.color();
	}

	public void markForRemoval(Level level)
	{
		this.markForRemoval = true;
		level.syncData(AttachmentTypeInit.MEDUSA);
	}

	public boolean shouldRemove()
	{
		return markForRemoval;
	}

	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();

		ResourceLocation type = BeamTypeInit.REGISTRY.getKey(getType());
		if(type != null)
			tag.putString("type", type.toString());

		tag.put("source", this.settings.source().save());

		CompoundTag settingsTag = new CompoundTag();
		settingsTag.putUUID("uuid", settings.uuid());
		settingsTag.putInt("color", settings.color());
		settingsTag.putDouble("speed", settings.speed());
		settingsTag.putDouble("radius", settings.radius());

		CompoundTag posTag = new CompoundTag();
		posTag.putFloat("x", settings.position().x);
		posTag.putFloat("y", settings.position().y);
		posTag.putFloat("z", settings.position().z);
		settingsTag.put("pos", posTag);

		settingsTag.putString("dimension", settings.dimension().location().toString());

		tag.put("settings", settingsTag);

		tag.putBoolean("removal", shouldRemove());
		tag.putInt("tick", tick);
		tag.putInt("expansion_tick", expansionTick);
		tag.putInt("shrinking_tick", shrinkingTick);
		tag.putInt("delay_tick", delayTicker);
		tag.putInt("delay_max", maxDelay);
		tag.putDouble("current_radius", currentRadius);
		tag.putDouble("previous_radius", prevRadius);
		tag.putBoolean("reached_max_radius", reachedMaxRadius);
		tag.putBoolean("approved", serverApproved);

		ListTag listTag = new ListTag();
		for(UUID uuid : petrifiedBefore)
		{
			StringTag uuidTag = StringTag.valueOf(uuid.toString());
			listTag.add(uuidTag);
		}
		tag.put("petrified_before", listTag);

		return tag;
	}

	public static MedusaBeam deserializeNBT(CompoundTag tag)
	{
		CompoundTag settingsTag = tag.getCompound("settings");

		MedusaSource source = MedusaSource.load(tag.getCompound("source"));

		CompoundTag posTag = settingsTag.getCompound("pos");
		float x = posTag.getFloat("x");
		float y = posTag.getFloat("y");
		float z = posTag.getFloat("z");
		Vector3f pos = new Vector3f(x, y, z);

		ResourceLocation keyLoc = ResourceLocation.parse(settingsTag.getString("dimension"));
		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, keyLoc);

		MedusaSettings.MedusaPosition position = new MedusaSettings.MedusaPosition(pos, dimension);

		UUID uuid = settingsTag.getUUID("uuid");
		int color = settingsTag.getInt("color");
		double speed = settingsTag.getDouble("speed");
		double radius = settingsTag.getDouble("radius");

		MedusaSettings settings = new MedusaSettings(radius, speed, color, position, uuid, source);
		MedusaBeam beam = new MedusaBeam(settings);

		beam.markForRemoval = tag.getBoolean("removal");
		beam.tick = tag.getInt("tick");
		beam.expansionTick = tag.getInt("expansion_tick");
		beam.shrinkingTick = tag.getInt("shrinking_tick");
		beam.delayTicker = tag.getInt("delay_tick");
		beam.maxDelay = tag.getInt("delay_max");
		beam.currentRadius = tag.getDouble("current_radius");
		beam.prevRadius = tag.getDouble("previous_radius");
		beam.reachedMaxRadius = tag.getBoolean("reached_max_radius");
		beam.serverApproved = tag.getBoolean("approved");

		ListTag listTag = tag.getList("petrified_before", StringTag.TAG_STRING);
		for(Tag stringtag : listTag)
			beam.petrifiedBefore.add(UUID.fromString(stringtag.getAsString()));

		return beam;
	}
}
