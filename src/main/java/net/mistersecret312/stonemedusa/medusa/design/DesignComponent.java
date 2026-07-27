package net.mistersecret312.stonemedusa.medusa.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record DesignComponent(ResourceLocation id, List<RepairIngredient> repairIngredients)
{
	public static final Codec<DesignComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(DesignComponent::id),
			RepairIngredient.CODEC.listOf().optionalFieldOf("repair_ingredients", List.of()).forGetter(DesignComponent::repairIngredients)
	).apply(instance, DesignComponent::new));
}
