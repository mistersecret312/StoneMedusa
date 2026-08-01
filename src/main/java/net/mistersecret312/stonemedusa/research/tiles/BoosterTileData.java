package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mistersecret312.stonemedusa.client.screens.widgets.TileType;

public record BoosterTileData(int amount) implements TileData
{
	public static final MapCodec<BoosterTileData> CODEC =
			RecordCodecBuilder.mapCodec(instance -> instance.group(
					Codec.INT.fieldOf("boost_amount").forGetter(BoosterTileData::amount)
			).apply(instance, BoosterTileData::new)
	);

	@Override
	public TileType getType()
	{
		return TileType.BOOSTER;
	}
}

