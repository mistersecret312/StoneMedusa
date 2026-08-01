package net.mistersecret312.stonemedusa.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.components.MedusaComponentTemplate;
import net.mistersecret312.stonemedusa.research.tiles.Grid;

import java.util.List;

public record ResearchEntry(String name, ResourceLocation icon,
							List<ResourceLocation> prerequisites, Grid grid)
{
	public static final ResourceLocation RESEARCH_ENTRY_LOCATION =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "research_entry");
	public static final ResourceKey<Registry<ResearchEntry>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(RESEARCH_ENTRY_LOCATION);

	public static final Codec<ResearchEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(ResearchEntry::name),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(ResearchEntry::icon),
			ResourceLocation.CODEC.listOf().optionalFieldOf("prerequisites", List.of()).forGetter(ResearchEntry::prerequisites),
			Grid.CODEC.fieldOf("grid").forGetter(ResearchEntry::grid)
	).apply(instance, ResearchEntry::new));
}
