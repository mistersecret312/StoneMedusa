package net.mistersecret312.stonemedusa.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MedusaFragmentItem extends Item implements IBorderCustom
{
	public MedusaFragmentItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack)
	{
		MutableComponent name = Component.translatable(this.getDescriptionId(stack));
		int color = getNameColor(stack);
		Style style = Style.EMPTY.withColor(color);
		return name.withStyle(style);
	}

	@Override
	public int getNameColor(ItemStack stack)
	{
		return 0xff00aeff;
	}

	@Override
	public Pair<Integer, Integer> getBorderColors(ItemStack stack)
	{
		return Pair.of(0xff00aeff, 0xff00628c);
	}
}
