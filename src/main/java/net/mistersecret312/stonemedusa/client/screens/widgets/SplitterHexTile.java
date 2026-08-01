package net.mistersecret312.stonemedusa.client.screens.widgets;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

import java.util.HashSet;
import java.util.Set;

public class SplitterHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_SPLITTER =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_splitter.png");

	public SplitterHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen)
	{
		super(x, y, row, column, radius, screen);
	}

	@Override
	public int getMaxInputs()
	{
		return 1;
	}

	@Override
	public int getMaxOutputs()
	{
		return 2;
	}

	@Override
	public boolean handlesSignal()
	{
		return true;
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return GRID_SPLITTER;
	}

	@Override
	public TileType getType()
	{
		return TileType.SPLITTER;
	}
}
