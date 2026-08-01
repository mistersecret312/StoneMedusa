package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

public class BlockerHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_BLOCKER =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_blocker.png");

	public BlockerHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen)
	{
		super(x, y, row, column, radius, screen);
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return GRID_BLOCKER;
	}

	@Override
	public TileType getType()
	{
		return TileType.BLOCKER;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(!isMouseOver(mouseX, mouseY))
			return false;

		if(screen.activeTileType.equals(TilePlacement.PLATE))
		{
			screen.placeTile(r, c, x, y, gridRadius, TileType.BLANK);
			return true;
		}

		return false;
	}
}