package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GridTile(int row, int column, TileData tileData)
{
	public static final Codec<GridTile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("row").forGetter(GridTile::row),
			Codec.INT.fieldOf("column").forGetter(GridTile::column),
			TileData.CODEC.fieldOf("data").forGetter(GridTile::tileData)
	).apply(instance, GridTile::new));
}
