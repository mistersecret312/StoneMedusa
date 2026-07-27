package net.mistersecret312.stonemedusa.medusa;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.medusa.components.*;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import net.mistersecret312.stonemedusa.util.MedusaUtil;

import java.util.*;

public class MedusaHandler
{
	public static final StreamCodec<RegistryFriendlyByteBuf, MedusaHandler> STREAM_CODEC = StreamCodec.of(
			(buf, attach) -> buf.writeNbt(attach.serializeNBT(buf.registryAccess())),
			(buf) -> deserializeNBT(buf.readNbt(), buf.registryAccess())
	);

	public final UUID medusaID;
	public MedusaSource source;

	public Map<MedusaComponentType, MedusaComponent> components = new HashMap<>();
	public Map<MedusaAttribute, Double> attributes = new HashMap<>();

	public boolean markRemove = false;
	public boolean initialized = false;

	public MedusaHandler(UUID medusaID, MedusaSource source, MedusaComponentTemplate hull, MedusaComponentTemplate wiring,
						 MedusaComponentTemplate batterySlot, MedusaComponentTemplate focalPoint)
	{
		this.medusaID = medusaID;
		this.source = source;

		MedusaComponent hullComponent = new MedusaComponent(hull.componentID(), hull.type(),
				hull.maxIntegrity(), hull.modifiers());
		MedusaComponent wiringComponent = new MedusaComponent(wiring.componentID(), wiring.type(),
				wiring.maxIntegrity(), wiring.modifiers());
		MedusaComponent batteryComponent = new MedusaComponent(batterySlot.componentID(), batterySlot.type(),
				batterySlot.maxIntegrity(), batterySlot.modifiers());
		MedusaComponent focalComponent = new MedusaComponent(focalPoint.componentID(), focalPoint.type(),
				focalPoint.maxIntegrity(), focalPoint.modifiers());


		this.components.put(MedusaComponentType.HULL, hullComponent);
		this.components.put(MedusaComponentType.WIRING, wiringComponent);
		this.components.put(MedusaComponentType.BATTERY_SLOT, batteryComponent);
		this.components.put(MedusaComponentType.FOCAL_POINT, focalComponent);

		calculateAttributes();
	}

	public MedusaHandler(UUID medusaID, MedusaSource source, MedusaComponent hull, MedusaComponent wiring,
						 MedusaComponent batterySlot, MedusaComponent focalPoint)
	{
		this.medusaID = medusaID;
		this.source = source;

		this.components.put(MedusaComponentType.HULL, hull);
		this.components.put(MedusaComponentType.WIRING, wiring);
		this.components.put(MedusaComponentType.BATTERY_SLOT, batterySlot);
		this.components.put(MedusaComponentType.FOCAL_POINT, focalPoint);

		calculateAttributes();
	}

	public void tick(Level level)
	{
		if(!initialized)
			calculateAttributes();
		if(source == null)
			return;

		IMedusa medusa = getTrackedMedusa(level);
		if(medusa == null)
		{
			source = null;
			return;
		}

		double energyDrain = attributes.get(MedusaAttribute.ENERGY_DRAIN);
		if(energyDrain > 0)
			medusa.consumeEnergy(level, source, (int) energyDrain);

		double wearOverTime = attributes.get(MedusaAttribute.WEAR_OVER_TIME);
		if(wearOverTime > 0)
		{
			MedusaComponent hull = components.get(MedusaComponentType.HULL);
			double percentage = hull.getIntegrityPercentage();
			if(percentage >= 50d)
				damageComponent(MedusaComponentType.HULL, wearOverTime);
			else
			{
				for(MedusaComponentType type : MedusaComponentType.values())
					damageComponent(type, wearOverTime);
			}
		}

		level.syncData(AttachmentTypeInit.MEDUSA);
	}

	public void calculateAttributes()
	{
		attributes.put(MedusaAttribute.ENERGY_DRAIN, 0d);
		attributes.put(MedusaAttribute.ENERGY_EFFICIENCY, 100d);
		attributes.put(MedusaAttribute.MAX_ENERGY_FLUX, 100d);
		attributes.put(MedusaAttribute.WEAR_OVER_TIME, 0d);

		for(MedusaComponent component : components.values())
		{
			for(MedusaModifier modifier : component.getActiveModifiers())
				if(modifier.operation().equals(AttributeOperation.MULTIPLY_BASE))
					attributes.put(modifier.attribute(), attributes.get(modifier.attribute()) * modifier.amount());

			for(MedusaModifier modifier : component.getActiveModifiers())
				if(modifier.operation().equals(AttributeOperation.ADD))
					attributes.put(modifier.attribute(), attributes.get(modifier.attribute()) + modifier.amount());


			for(MedusaModifier modifier : component.getActiveModifiers())
				if(modifier.operation().equals(AttributeOperation.MULTIPLY_TOTAL))
					attributes.put(modifier.attribute(), attributes.get(modifier.attribute()) * modifier.amount());

		}

		attributes.put(MedusaAttribute.ENERGY_EFFICIENCY, Math.max(0d, attributes.get(MedusaAttribute.ENERGY_EFFICIENCY)));
		attributes.put(MedusaAttribute.MAX_ENERGY_FLUX, Math.max(0d, Math.min(100d, attributes.get(MedusaAttribute.MAX_ENERGY_FLUX))));
		initialized = true;
	}

	public void damageComponent(MedusaComponentType type, double damage)
	{
		MedusaComponent component = components.get(type);
		if(component == null)
			return;

		component.damage(damage);
		calculateAttributes();
	}

	public double getEfficiency()
	{
		return attributes.get(MedusaAttribute.ENERGY_EFFICIENCY);
	}

	public double getMaxFlux()
	{
		return attributes.get(MedusaAttribute.MAX_ENERGY_FLUX);
	}

	public double getAttribute(MedusaAttribute attribute)
	{
		return attributes.get(attribute);
	}

	public IMedusa getTrackedMedusa(Level level)
	{
		return source.resolve(level);
	}

	public void markForRemoval()
	{
		markRemove = true;
	}

	public boolean shouldRemove()
	{
		return markRemove;
	}

	public CompoundTag serializeNBT(RegistryAccess registryAccess)
	{
		return serializeNBT(((HolderLookup.Provider) registryAccess));
	}

	public static MedusaHandler deserializeNBT(CompoundTag tag, RegistryAccess registryAccess)
	{
		return MedusaHandler.deserializeNBT(tag, ((HolderLookup.Provider) registryAccess));
	}

	public CompoundTag serializeNBT(HolderLookup.Provider registryAccess)
	{
		CompoundTag tag = new CompoundTag();
		tag.putUUID("medusa", medusaID);
		tag.putBoolean("initialized", initialized);
		tag.putBoolean("removed", markRemove);
		if(source != null)
			tag.put("source", source.save());


		MedusaComponent hull = components.get(MedusaComponentType.HULL);
		tag.put("hull", hull.serializeNBT());

		MedusaComponent wiring = components.get(MedusaComponentType.WIRING);
		tag.put("wiring", wiring.serializeNBT());

		MedusaComponent batterySlot = components.get(MedusaComponentType.BATTERY_SLOT);
		tag.put("battery_slot", batterySlot.serializeNBT());

		MedusaComponent focalPoint = components.get(MedusaComponentType.FOCAL_POINT);
		tag.put("focal_point", focalPoint.serializeNBT());

		return tag;
	}

	public static MedusaHandler deserializeNBT(CompoundTag tag, HolderLookup.Provider registryAccess)
	{
		UUID uuid = tag.getUUID("medusa");

		MedusaSource source = null;
		if(tag.contains("source"))
			source = MedusaSource.load(tag.getCompound("source"));

		MedusaComponent hull = MedusaComponent.deserializeNBT(tag.getCompound("hull"));
		MedusaComponent wiring = MedusaComponent.deserializeNBT(tag.getCompound("wiring"));
		MedusaComponent batterySlot = MedusaComponent.deserializeNBT(tag.getCompound("battery_slot"));
		MedusaComponent focalPoint = MedusaComponent.deserializeNBT(tag.getCompound("focal_point"));

		MedusaHandler handler = new MedusaHandler(uuid, source, hull, wiring, batterySlot, focalPoint);

		handler.markRemove = tag.getBoolean("removed");
		handler.initialized = tag.getBoolean("initialized");
		return handler;
	}
}
