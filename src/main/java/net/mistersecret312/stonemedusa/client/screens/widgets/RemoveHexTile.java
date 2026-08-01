package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

public class RemoveHexTile extends BaseHexTile
{
	public RemoveHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen)
	{
		super(x, y, row, column, radius, screen);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int i, int i1, float v)
	{

	}

	@Override
	public TileType getType()
	{
		return TileType.REMOVE;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		return false;
	}
}
