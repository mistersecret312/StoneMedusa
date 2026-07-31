package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.util.StringRepresentable;

public enum TileType implements StringRepresentable
{
	BLANK("blank"),
	START("start"),
	END("end"),
	WIRE("wire");

	final String name;
	TileType(String name)
	{
		this.name = name;
	}

	@Override
	public String getSerializedName()
	{
		return name;
	}
}
