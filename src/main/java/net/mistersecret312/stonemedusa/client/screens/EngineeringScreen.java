package net.mistersecret312.stonemedusa.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.mistersecret312.stonemedusa.client.screens.widgets.*;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.Map;

public class EngineeringScreen extends AbstractContainerScreen<EngineeringTableMenu>
{
	public Map<Vector2d, BaseHexTile> tiles = new HashMap<>();
	public Map<TileType, TileTypeItemWidget> types = new HashMap<>();
	public boolean solved = false;

	public TileType activeTileType = TileType.BLANK;

	public EngineeringScreen(EngineeringTableMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}

	@Override
	protected void init()
	{
		int x = (width) / 2;
		int y = (height) / 2;
		int radius = 3;
		for (int c = -radius; c <= radius; c++)
		{
			int rStart = Math.max(-radius, -c - radius);
			int rEnd = Math.min(radius, -c + radius);

			for (int r = rStart; r <= rEnd; r++)
			{
				BaseHexTile widget = new BaseHexTile(x, y, r, c, radius, this);
				placeTile(r, c, widget);
			}
		}

		placeTypeButton(new TileTypeItemWidget(100, 100, Items.COPPER_INGOT,
				TileType.WIRE, this));

		placeTile(-2, -1, new StartHexTile(x,y,-2,-1, radius,this, 15));
		placeTile(2, -3, new StartHexTile(x,y,2,-3, radius,this, 15));

		placeTile(2, 1, new EndHexTile(x,y,2,1, radius,this, 10));
		placeTile(-2, 3, new EndHexTile(x,y,-2,3, radius,this, 10));
	}

	public void placeTypeButton(TileTypeItemWidget typeWidget)
	{
		types.put(typeWidget.type, typeWidget);
		addRenderableWidget(typeWidget);
	}

	public void placeTile(int row, int column, BaseHexTile newTile)
	{
		Vector2d coord = new Vector2d(row, column);
		BaseHexTile oldTile = this.tiles.get(coord);

		if (oldTile != null)
		{
			oldTile.disconnectFromNeighbors();
			this.removeWidget(oldTile);
		}

		this.tiles.put(coord, newTile);
		this.addRenderableWidget(newTile);
		newTile.autoConnectToNeighbors();

		this.solved = this.tiles.values().stream().filter(tile -> tile instanceof EndHexTile).allMatch(tile -> tile.signal == ((EndHexTile) tile).requiredSignal);
	}

	public void placeTile(int row, int column, int x, int y, int radius, TileType type)
	{
		if(type == null)
			return;

		BaseHexTile tile = new BaseHexTile(x, y, row, column, radius, this);
		if(type.equals(TileType.WIRE))
			tile = new WireHexTile(x, y, row, column, radius, this);

		if(!tile.canConnectOnSpot() && !(tile.getType().equals(TileType.BLANK)))
			return;

		TileTypeItemWidget typeWidget = this.types.get(type);
		if(typeWidget == null || typeWidget.countItem() == 0)
			return;

		typeWidget.removeItem();
		placeTile(row, column, tile);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
	{
		int x = (width) / 2;
		int y = (height) / 3;

		if(solved)
			graphics.drawCenteredString(Minecraft.getInstance().font, "Solved!", x, y, 0x00FFF00);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
//		super.renderLabels(guiGraphics, mouseX, mouseY);
	}
}
