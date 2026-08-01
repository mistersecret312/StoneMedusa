package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.nbt.CompoundTag;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

@FunctionalInterface
public interface HexTileFactory
{
	BaseHexTile create(int x, int y, int row, int column, int radius,
					   EngineeringScreen screen, CompoundTag tag);
}
