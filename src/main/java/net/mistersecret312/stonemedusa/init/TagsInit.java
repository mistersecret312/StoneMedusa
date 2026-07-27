package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.mistersecret312.stonemedusa.StoneMedusa;

public class TagsInit
{
	public static class Entity
	{
		public static final TagKey<EntityType<?>> PETRIFICATION_IMMUNE = tag("petrification_immune");

		private static TagKey<EntityType<?>> tag(String name)
		{
			return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, name));
		}

	}
}
