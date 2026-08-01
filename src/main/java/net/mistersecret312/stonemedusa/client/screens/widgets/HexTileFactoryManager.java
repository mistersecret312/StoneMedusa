package net.mistersecret312.stonemedusa.client.screens.widgets;

import java.util.HashMap;
import java.util.Map;

public class HexTileFactoryManager
{
	public static final Map<TileType, HexTileFactory> FACTORIES = new HashMap<>();

	static
	{
		FACTORIES.put(TileType.BLANK, BaseHexTile::new);
		FACTORIES.put(TileType.AOE_BLOCKER, AoEBlockTile::new);
		FACTORIES.put(TileType.BLOCKER, BlockerHexTile::new);
		FACTORIES.put(TileType.END, EndHexTile::new);
		FACTORIES.put(TileType.REMOVED, RemoveHexTile::new);
		FACTORIES.put(TileType.SPLITTER, SplitterHexTile::new);
		FACTORIES.put(TileType.START, StartHexTile::new);
		FACTORIES.put(TileType.WIRE, WireHexTile::new);
	}
}
