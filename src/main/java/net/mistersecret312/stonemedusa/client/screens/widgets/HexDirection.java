package net.mistersecret312.stonemedusa.client.screens.widgets;

public enum HexDirection
{
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(0, -1);

    public final int dc;
    public final int dr;

    HexDirection(int dc, int dr)
    {
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
}