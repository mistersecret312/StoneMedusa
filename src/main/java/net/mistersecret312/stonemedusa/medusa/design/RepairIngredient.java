package net.mistersecret312.stonemedusa.medusa.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public record RepairIngredient(Ingredient ingredient, double repairPercentage)
{
	public static final Codec<RepairIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RepairIngredient::ingredient),
			Codec.DOUBLE.fieldOf("percentage").forGetter(RepairIngredient::repairPercentage)
	).apply(instance, RepairIngredient::new));
}

