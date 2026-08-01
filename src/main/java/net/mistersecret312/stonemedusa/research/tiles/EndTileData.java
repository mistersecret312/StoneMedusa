package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mistersecret312.stonemedusa.client.screens.widgets.TileType;

public record EndTileData(int required) implements TileData
{
	public static final MapCodec<EndTileData> CODEC =
			RecordCodecBuilder.mapCodec(instance -> instance.group(
					Codec.INT.fieldOf("required_signal").forGetter(EndTileData::required)
			).apply(instance, EndTileData::new)
	);

	@Override
	public TileType getType()
	{
		return TileType.END;
	}
}

