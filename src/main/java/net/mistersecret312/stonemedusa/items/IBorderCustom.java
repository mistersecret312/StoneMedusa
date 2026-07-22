package net.mistersecret312.stonemedusa.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;

public interface IBorderCustom
{
	int getNameColor(ItemStack stack);
	Pair<Integer, Integer> getBorderColors(ItemStack stack);
}
