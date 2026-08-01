package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mistersecret312.stonemedusa.client.screens.widgets.TileType;

public record StartTileData(int strength) implements TileData
{
	public static final MapCodec<StartTileData> CODEC =
			RecordCodecBuilder.mapCodec(instance -> instance.group(
					Codec.INT.fieldOf("signal_strength").forGetter(StartTileData::strength)
			).apply(instance, StartTileData::new)
	);

	@Override
	public TileType getType()
	{
		return TileType.START;
	}
}

