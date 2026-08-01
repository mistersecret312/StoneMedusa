package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.util.StringRepresentable;

public enum HexDirection implements StringRepresentable
{
    NORTH_EAST("north_east", 1, -1),
    EAST("east", 1, 0),
    SOUTH_EAST("south_east", 0, 1),
    SOUTH_WEST("south_west", -1, 1),
    WEST("west", -1, 0),
    NORTH_WEST("north_west", 0, -1);

    public static final EnumCodec<HexDirection> CODEC = StringRepresentable.fromEnum(HexDirection::values);

    public final String name;
    public final int dc;
    public final int dr;

    HexDirection(String name, int dc, int dr)
    {
        this.name = name;
        this.dc = dc;
        this.dr = dr;
    }

    public HexDirection getOpposite()
    {
        return switch (this) {
            case NORTH_EAST -> SOUTH_WEST;
            case EAST -> WEST;
            case SOUTH_EAST -> NORTH_WEST;
            case SOUTH_WEST -> NORTH_EAST;
            case WEST -> EAST;
            case NORTH_WEST -> SOUTH_EAST;
        };
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}