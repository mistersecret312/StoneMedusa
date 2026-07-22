package net.mistersecret312.stonemedusa.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.items.MedusaItem;

public class DiamondBatteryTooltipRenderer implements ClientTooltipComponent
{
	public ItemStack stack;
	public DiamondBatteryTooltipRenderer(MedusaItem.DiamondBatteryTooltip tooltip)
	{
		this.stack = tooltip.stack().batteryStack();
	}

	@Override
	public int getHeight()
	{
		return 18;
	}

	@Override
	public int getWidth(Font font)
	{
		return 16;
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics)
	{
		guiGraphics.renderItem(this.stack, x, y);
	}
}
