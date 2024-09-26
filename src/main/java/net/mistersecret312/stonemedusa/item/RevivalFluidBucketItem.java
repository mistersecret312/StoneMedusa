package net.mistersecret312.stonemedusa.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.mistersecret312.stonemedusa.config.RevivalConfig;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RevivalFluidBucketItem extends BucketItem
{
    public RevivalFluidBucketItem(Supplier<? extends Fluid> supplier, Properties builder)
    {
        super(supplier, builder);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
    {
        return RevivalConfig.revival_fluid_bucket_burntime.get();
    }
}
