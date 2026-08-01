package net.mistersecret312.stonemedusa.research.tiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record Grid(int radius, List<GridTile> tiles)
{
	public static final Codec<Grid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("radius").forGetter(Grid::radius),
			GridTile.CODEC.listOf().optionalFieldOf("tiles", List.of()).forGetter(Grid::tiles)
	).apply(instance, Grid::new));
}
