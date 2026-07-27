package net.mistersecret312.stonemedusa.medusa;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record PetrifiedArea(Vec2 epicenter, ResourceKey<Level> dimension, double radius,
							long removalTimeStamp, Set<UUID> entities, boolean petrifyLoad)
{
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		tag.putDouble("radius", radius);
		tag.putLong("removal_timestamp", removalTimeStamp);
		tag.putString("dimension", dimension.location().toString());
		tag.putBoolean("petrify_load", petrifyLoad);

		ListTag entitiesTag = new ListTag();
		for(UUID uuid : entities)
			entitiesTag.add(StringTag.valueOf(uuid.toString()));
		tag.put("entities", entitiesTag);

		CompoundTag posTag = new CompoundTag();
		posTag.putFloat("x", epicenter.x);
		posTag.putFloat("z", epicenter.y);

		tag.put("pos", posTag);
		return tag;
	}

	public static PetrifiedArea deserializeNBT(CompoundTag tag)
	{
		double radius = tag.getDouble("radius");
		long timestamp = tag.getLong("removal_timestamp");
		boolean petrifyLoad = tag.getBoolean("petrify_load");

		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION,
				ResourceLocation.parse(tag.getString("dimension")));

		Set<UUID> entities = new HashSet<>();
		ListTag entitiesTag = tag.getList("entities", StringTag.TAG_STRING);
		for(Tag stringTag : entitiesTag)
			entities.add(UUID.fromString(stringTag.getAsString()));

		CompoundTag posTag = tag.getCompound("pos");
		float x = posTag.getFloat("x");
		float z = posTag.getFloat("z");

		return new PetrifiedArea(new Vec2(x, z), dimension, radius, timestamp, entities, petrifyLoad);
	}
}
