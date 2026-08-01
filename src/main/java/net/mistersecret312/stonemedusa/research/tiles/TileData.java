package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.Codec;
import net.mistersecret312.stonemedusa.client.screens.widgets.TileType;

public interface TileData
{
	TileType getType();

	Codec<TileData> CODEC = TileType.CODEC.dispatch(TileData::getType, TileType::getCodec);
}
