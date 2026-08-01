package net.mistersecret312.stonemedusa.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.widgets.*;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import net.mistersecret312.stonemedusa.research.ResearchEntry;
import net.mistersecret312.stonemedusa.research.tiles.*;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.Map;

public class EngineeringScreen extends AbstractContainerScreen<EngineeringTableMenu>
{
	public Map<Vector2d, BaseHexTile> tiles = new HashMap<>();
	public Map<TileType, TileTypeItemWidget> types = new HashMap<>();
	public boolean solved = false;
	public ResearchEntry activeEntry;

	public TilePlacement activeTileType = TilePlacement.BLANK;

	public EngineeringScreen(EngineeringTableMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		RegistryAccess registryAccess = menu.blockEntity.getLevel().registryAccess();
		Registry<ResearchEntry> registry = registryAccess.registryOrThrow(ResearchEntry.REGISTRY_KEY);
		ResearchEntry entry = registry.get(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "basic"));
		if(entry != null)
			activeEntry = entry;
	}

	@Override
	protected void init()
	{
		tiles.clear();
		types.clear();
		solved = false;
		activeTileType = TilePlacement.BLANK;

		RegistryAccess registryAccess = menu.blockEntity.getLevel().registryAccess();
		Registry<ResearchEntry> registry = registryAccess.registryOrThrow(ResearchEntry.REGISTRY_KEY);
		ResearchEntry entry = registry.get(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "basic"));
		if(entry != null)
		{
			int x = (width) / 2;
			int y = (height) / 2;
			int radius = entry.grid().radius();
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

			for(GridTile tile : entry.grid().tiles())
				placeTile(tile.row(), tile.column(), x, y, radius, tile.tileData());
		}


		placeTypeButton(new TileTypeItemWidget(100, 100, Items.COPPER_INGOT,
				TilePlacement.WIRE, this));
		placeTypeButton(new TileTypeItemWidget(100, 126, Items.IRON_INGOT,
				TilePlacement.PLATE, this));
		placeTypeButton(new TileTypeItemWidget(100, 152, Items.GOLD_INGOT,
				TilePlacement.BOOSTER, this));
		placeTypeButton(new TileTypeItemWidget(100, 178, Items.DIAMOND,
				TilePlacement.SPLITTER, this));
	}

	public void placeTypeButton(TileTypeItemWidget typeWidget)
	{
		types.put(typeWidget.type.getTileType(), typeWidget);
		addRenderableWidget(typeWidget);
	}

	public void placeTile(int row, int column, BaseHexTile newTile)
	{
		Vector2d coord = new Vector2d(row, column);
		BaseHexTile oldTile = this.tiles.get(coord);

		if(oldTile == null && !newTile.getType().equals(TileType.BLANK))
			return;

		if (oldTile != null)
		{
			if(oldTile.getType().equals(newTile.getType()))
				return;

			oldTile.disconnectFromNeighbors();
			this.removeWidget(oldTile);
		}

		this.tiles.put(coord, newTile);
		this.addRenderableWidget(newTile);
		newTile.autoConnectToNeighbors();

		this.solved = this.tiles.values().stream().filter(tile -> tile instanceof EndHexTile).allMatch(tile -> tile.signal == ((EndHexTile) tile).requiredSignal);
	}

	public void placeTile(int row, int column, int x, int y, int radius, TileData data)
	{
		switch(data)
		{
			case StartTileData(int strength) ->
			{
				StartHexTile tile = new StartHexTile(x, y, row, column, radius, this, strength);
				placeTile(row, column, tile);
			}
			case EndTileData(int required) ->
			{
				EndHexTile tile = new EndHexTile(x, y, row, column, radius, this, required);
				placeTile(row, column, tile);
			}
			case BoosterTileData(int amount) ->
			{
				BoosterHexTile tile = new BoosterHexTile(x, y, row, column, radius, this, amount);
				placeTile(row, column, tile);
			}
			default -> placeTile(row, column, x, y, radius, data.getType());
		}
	}

	public void placeTile(int row, int column, int x, int y, int radius, TileType type)
	{
		if(type == null)
			return;

		BaseHexTile tile = new BaseHexTile(x, y, row, column, radius, this);
		if(type.equals(TileType.WIRE))
			tile = new WireHexTile(x, y, row, column, radius, this);
		if(type.equals(TileType.BOOSTER))
			tile = new BoosterHexTile(x, y, row, column, radius, this, 5);
		if(type.equals(TileType.SPLITTER))
			tile = new SplitterHexTile(x, y, row, column, radius, this);

		if(!tile.canConnectOnSpot() && !(tile.getType().equals(TileType.BLANK)))
			return;

		TileTypeItemWidget typeWidget = this.types.get(type);
		if(type != TileType.BLANK && (typeWidget == null || typeWidget.countItem() == 0))
			return;

		if(type != TileType.BLANK)
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

	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
}
