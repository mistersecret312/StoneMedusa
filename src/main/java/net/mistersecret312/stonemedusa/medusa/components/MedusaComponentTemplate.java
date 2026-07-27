package net.mistersecret312.stonemedusa.medusa.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;

import java.util.List;

public record MedusaComponentTemplate(String componentID, MedusaComponentType type,
									  double maxIntegrity, List<MedusaModifier> modifiers)
{
	public static final ResourceLocation COMPONENT_LOCATION =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "medusa_component");
	public static final ResourceKey<Registry<MedusaComponentTemplate>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(COMPONENT_LOCATION);

	public static final Codec<MedusaComponentTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(MedusaComponentTemplate::componentID),
			MedusaComponentType.CODEC.fieldOf("type").forGetter(MedusaComponentTemplate::type),
			Codec.DOUBLE.fieldOf("max_integrity").forGetter(MedusaComponentTemplate::maxIntegrity),
			MedusaModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(MedusaComponentTemplate::modifiers)
	).apply(instance, MedusaComponentTemplate::new));

}
