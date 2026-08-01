package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mistersecret312.stonemedusa.client.screens.widgets.TileType;

public record BasicTileData(TileType type) implements TileData
{
	public static final MapCodec<BasicTileData> CODEC =
			RecordCodecBuilder.mapCodec(instance -> instance.point(new BasicTileData(TileType.BLANK))
	);

	public static MapCodec<BasicTileData> codecFor(TileType type) {
		return RecordCodecBuilder.mapCodec(instance -> instance.point(new BasicTileData(type)));
	}

	@Override
	public TileType getType()
	{
		return type;
	}
}

