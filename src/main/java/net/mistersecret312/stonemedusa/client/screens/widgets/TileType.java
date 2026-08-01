package net.mistersecret312.stonemedusa.client.screens.widgets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;
import net.mistersecret312.stonemedusa.research.tiles.*;

public enum TileType implements StringRepresentable
{
	BLANK("blank"),
	START("start", StartTileData.CODEC),
	END("end", EndTileData.CODEC),
	REMOVED("removed"),
	BLOCKER("blocker"),
	AOE_BLOCKER("aoe_blocker"),
	BOOSTER("booster", BoosterTileData.CODEC),
	SPLITTER("splitter"),
	WIRE("wire");

	public static final Codec<TileType> CODEC = StringRepresentable.fromEnum(TileType::values);

	final String name;
	final MapCodec<? extends TileData> codec;
	TileType(String name)
	{
		this.name = name;
		this.codec = BasicTileData.codecFor(this);
	}

	TileType(String name, MapCodec<? extends TileData> codec)
	{
		this.name = name;
		this.codec = codec;
	}

	@Override
	public String getSerializedName()
	{
		return name;
	}

	public MapCodec<? extends TileData> getCodec()
	{
		return codec;
	}
}
