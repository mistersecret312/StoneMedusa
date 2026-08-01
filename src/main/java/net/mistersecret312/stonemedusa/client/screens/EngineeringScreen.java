package net.mistersecret312.stonemedusa.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.widgets.*;
import net.mistersecret312.stonemedusa.data_attachment.ResearchAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import net.mistersecret312.stonemedusa.network.ServerboundResearchProgressPacket;
import net.mistersecret312.stonemedusa.research.ResearchEntry;
import net.mistersecret312.stonemedusa.research.tiles.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EngineeringScreen extends AbstractContainerScreen<EngineeringTableMenu>
{
	public Player player;
	public Map<Vector2d, BaseHexTile> tiles = new HashMap<>();
	public Map<TileType, TileTypeItemWidget> types = new HashMap<>();
	public boolean solved = false;
	public ResearchEntry activeEntry;

	public TilePlacement activeTileType = TilePlacement.BLANK;

	public EngineeringScreen(EngineeringTableMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.player = playerInventory.player;

		ResearchAttachment attachment = player.getData(AttachmentTypeInit.RESEARCH);
		if(attachment.latestResearch == null)
			attachment.latestResearch = ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "basic");
	}

	@Override
	protected void init()
	{
		tiles.clear();
		types.clear();
		solved = false;
		activeTileType = TilePlacement.BLANK;

		int x = (width) / 2;
		int y = (height) / 2;

		placeTypeButton(new TileTypeItemWidget(100, 100, Items.COPPER_INGOT,
				TilePlacement.WIRE, this));
		placeTypeButton(new TileTypeItemWidget(100, 126, Items.IRON_INGOT,
				TilePlacement.PLATE, this));
		placeTypeButton(new TileTypeItemWidget(100, 152, Items.GOLD_INGOT,
				TilePlacement.BOOSTER, this));
		placeTypeButton(new TileTypeItemWidget(100, 178, Items.DIAMOND,
				TilePlacement.SPLITTER, this));

		ResourceLocation latest = this.player.getData(AttachmentTypeInit.RESEARCH.get()).latestResearch;
		if(latest != null)
			loadFromPlayer(latest, x, y);
	}

	public void loadFromPlayer(ResourceLocation key, int x, int y)
	{
		ResearchAttachment attachment = player.getData(AttachmentTypeInit.RESEARCH.get());

		RegistryAccess registryAccess = menu.blockEntity.getLevel().registryAccess();
		Registry<ResearchEntry> registry = registryAccess.registryOrThrow(ResearchEntry.REGISTRY_KEY);
		ResearchEntry entry = registry.get(key);
		if(entry == null)
			return;

		int radius = entry.grid().radius();
		CompoundTag tag = attachment.researches.getOrDefault(key, new CompoundTag());
		this.activeEntry = entry;

		for (int c = -radius; c <= radius; c++)
		{
			int rStart = Math.max(-radius, -c - radius);
			int rEnd = Math.min(radius, -c + radius);

			for (int r = rStart; r <= rEnd; r++)
			{
				BaseHexTile widget = new BaseHexTile(x, y, r, c, radius, this, new CompoundTag());
				placeTile(r, c, widget, true);
			}
		}

		if(tag.isEmpty())
		{
			for(GridTile tile : entry.grid().tiles())
				placeTile(tile.row(), tile.column(), x, y, radius, tile.tileData());
			return;
		}

		ListTag listTag = tag.getList("tiles", Tag.TAG_COMPOUND);
		for(int i = 0; i < listTag.size(); i++)
		{
			CompoundTag tileTag = listTag.getCompound(i);
			int row = tileTag.getInt("row");
			int column = tileTag.getInt("column");
			int signal = tileTag.getInt("signal");
			CompoundTag extraData = tileTag.getCompound("extra_data");
			TileType type = TileType.CODEC.byName(tileTag.get("type").getAsString());
			Set<HexDirection> inputs = new HashSet<>();
			Set<HexDirection> outputs = new HashSet<>();
			ListTag inputsTag = tileTag.getList("inputs", Tag.TAG_STRING);
			for(Tag stringTag : inputsTag)
				inputs.add(HexDirection.CODEC.byName(stringTag.getAsString()));
			ListTag outputsTag = tileTag.getList("outputs", Tag.TAG_STRING);
			for(Tag stringTag : outputsTag)
				outputs.add(HexDirection.CODEC.byName(stringTag.getAsString()));

			BaseHexTile tile = HexTileFactoryManager.FACTORIES.get(type).create(x, y, row, column, radius, this, extraData);
			tile.inputs = inputs;
			tile.outputs = outputs;
			tile.signal = signal;

			this.placeTile(row, column, tile, false);
		}
	}

	public void placeTypeButton(TileTypeItemWidget typeWidget)
	{
		types.put(typeWidget.type.getTileType(), typeWidget);
		addRenderableWidget(typeWidget);
	}

	public void placeTile(int row, int column, BaseHexTile newTile, boolean autoConnect)
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
		if(autoConnect)
			newTile.autoConnectToNeighbors();

		this.solved = this.tiles.values().stream().filter(tile -> tile instanceof EndHexTile).allMatch(tile -> tile.signal == ((EndHexTile) tile).requiredSignal);
	}

	@Override
	public void onClose()
	{
		super.onClose();
		CompoundTag tag = new CompoundTag();

		RegistryAccess registryAccess = menu.blockEntity.getLevel().registryAccess();
		Registry<ResearchEntry> registry = registryAccess.registryOrThrow(ResearchEntry.REGISTRY_KEY);
		ResourceLocation resourceLocation = registry.getKey(activeEntry);
		if(resourceLocation == null)
			return;

		ListTag tilesTag = new ListTag();
		tiles.forEach((pos, tile) ->
			{
				CompoundTag tileTag = new CompoundTag();
				tileTag.putInt("row", tile.getRow());
				tileTag.putInt("column", tile.getColumn());
				tileTag.putInt("signal", tile.signal);
				tileTag.putString("type", tile.getType().getSerializedName());
				tileTag.put("extra_data", tile.saveAdditional());

				ListTag inputs = new ListTag();
				ListTag outputs = new ListTag();

				for(HexDirection direction : tile.getInputs())
					inputs.add(StringTag.valueOf(direction.getSerializedName()));
				for(HexDirection direction : tile.getOutputs())
					outputs.add(StringTag.valueOf(direction.getSerializedName()));

				tileTag.put("inputs", inputs);
				tileTag.put("outputs", outputs);
				tilesTag.add(tileTag);
			});
		tag.put("tiles", tilesTag);
		PacketDistributor.sendToServer(new ServerboundResearchProgressPacket(resourceLocation, tag));
	}

	public void placeTile(int row, int column, int x, int y, int radius, TileData data)
	{
		switch(data)
		{
			case StartTileData(int strength) ->
			{
				StartHexTile tile = new StartHexTile(x, y, row, column, radius, this, strength);
				placeTile(row, column, tile, true);
			}
			case EndTileData(int required) ->
			{
				EndHexTile tile = new EndHexTile(x, y, row, column, radius, this, required);
				placeTile(row, column, tile, true);
			}
			case BoosterTileData(int amount) ->
			{
				BoosterHexTile tile = new BoosterHexTile(x, y, row, column, radius, this, amount);
				placeTile(row, column, tile, true);
			}
			default -> placeTile(row, column, x, y, radius, data.getType());
		}
	}

	public void placeTile(int row, int column, int x, int y, int radius, TileType type)
	{
		if(type == null)
			return;

		BaseHexTile tile = new BaseHexTile(x, y, row, column, radius, this, new CompoundTag());
		if(type.equals(TileType.WIRE))
			tile = new WireHexTile(x, y, row, column, radius, this, new CompoundTag());
		if(type.equals(TileType.BOOSTER))
			tile = new BoosterHexTile(x, y, row, column, radius, this, 5);
		if(type.equals(TileType.SPLITTER))
			tile = new SplitterHexTile(x, y, row, column, radius, this, new CompoundTag());
		if(type.equals(TileType.AOE_BLOCKER))
			tile = new AoEBlockTile(x, y, row, column, radius, this, new CompoundTag());
		if(type.equals(TileType.BLOCKER))
			tile = new BlockerHexTile(x, y, row, column, radius, this, new CompoundTag());
		if(type.equals(TileType.REMOVED))
			tile = new RemoveHexTile(x, y, row, column, radius, this, new CompoundTag());

		if(!tile.canConnectOnSpot() && !(tile.getType().equals(TileType.BLANK)))
			return;

		TileTypeItemWidget typeWidget = this.types.get(type);
		if(type != TileType.BLANK && (typeWidget == null || typeWidget.countItem() == 0))
			return;

		if(type != TileType.BLANK)
			typeWidget.removeItem();
		placeTile(row, column, tile, true);
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
