package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

public class AoEBlockTile extends BaseHexTile
{
	public static final ResourceLocation GRID_AOE_BLOCKER =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_aoe_blocker.png");

	public AoEBlockTile(int x, int y, int row, int column, int radius, EngineeringScreen screen)
	{
		super(x, y, row, column, radius, screen);

		for(HexDirection direction : HexDirection.values())
		{
			int tileRow = row+direction.dr;
			int tileColumn = column+direction.dc;
			screen.placeTile(tileRow, tileColumn,
					new BlockerHexTile(x, y, tileRow, tileColumn, radius, screen));
		}
	}

	@Override
	public TileType getType()
	{
		return TileType.AOE_BLOCKER;
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return GRID_AOE_BLOCKER;
	}
}
