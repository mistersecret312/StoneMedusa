package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import net.mistersecret312.stonemedusa.init.NetworkInit;
import net.mistersecret312.stonemedusa.network.ServerboundConsumeItemPacket;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class TileTypeItemWidget extends AbstractWidget implements Renderable
{
	public final EngineeringScreen screen;
	public final Item item;
	public final TilePlacement type;

	public TileTypeItemWidget(int x, int y, Item item, TilePlacement type, EngineeringScreen screen)
	{
		super(x, y, 16, 16, Component.empty());
		this.item = item;
		this.type = type;
		this.screen = screen;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(!isMouseOver(mouseX, mouseY))
			return false;

		if(this.screen.activeTileType.equals(type))
			this.screen.activeTileType = TilePlacement.BLANK;
		else this.screen.activeTileType = type;

		return true;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int x, int y, float partial)
	{
		graphics.renderItem(new ItemStack(item), getX(), getY());
		if(this.screen.activeTileType.equals(type))
			graphics.drawCenteredString(Minecraft.getInstance().font,
					"" + countItem(), getX()+16, getY()+16, -1);
	}

	public int countItem()
	{
		int amount = 0;
		IItemHandler handler = this.screen.getMenu().blockEntity.getItemHandler();
		for(int i = 0; i < handler.getSlots(); i++)
		{
			if(handler.getStackInSlot(i).getItem().equals(item))
				amount += handler.getStackInSlot(i).getCount();
		}

		return amount;
	}

	public void removeItem()
	{
		IItemHandler handler = this.screen.getMenu().blockEntity.getItemHandler();
		for(int i = 0; i < handler.getSlots(); i++)
		{
			if(handler.getStackInSlot(i).getItem().equals(item))
			{
				handler.extractItem(i, 1, false);
				PacketDistributor.sendToServer(new ServerboundConsumeItemPacket(i, 1));
				return;
			}
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{

	}
}
