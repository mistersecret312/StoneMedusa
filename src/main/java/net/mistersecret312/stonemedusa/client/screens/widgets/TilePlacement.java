package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.util.StringRepresentable;

public enum TilePlacement implements StringRepresentable
{
	BLANK("blank", TileType.BLANK),
	BOOSTER("booster", TileType.BOOSTER),
	SPLITTER("splitter", TileType.SPLITTER),
	WIRE("wire", TileType.WIRE),
	PLATE("plate", TileType.BLANK);

	public static final EnumCodec<TilePlacement> CODEC = StringRepresentable.fromEnum(TilePlacement::values);

	final String name;
	final TileType tileType;
	TilePlacement(String name, TileType tileType)
	{
		this.name = name;
		this.tileType = tileType;
	}

	@Override
	public String getSerializedName()
	{
		return name;
	}

	public TileType getTileType()
	{
		return tileType;
	}
}
