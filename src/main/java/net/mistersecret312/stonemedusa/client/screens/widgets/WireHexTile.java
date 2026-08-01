package net.mistersecret312.stonemedusa.client.screens.widgets;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

import java.util.HashSet;
import java.util.Set;

public class WireHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_WIRE =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_wire.png");

	public WireHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen, CompoundTag tag)
	{
		super(x, y, row, column, radius, screen, tag);
	}

	@Override
	public int getMaxInputs()
	{
		return 1;
	}

	@Override
	public int getMaxOutputs()
	{
		return 1;
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return GRID_WIRE;
	}

	@Override
	public boolean handlesSignal()
	{
		return true;
	}

	public TileType getType()
	{
		return TileType.WIRE;
	}
}
