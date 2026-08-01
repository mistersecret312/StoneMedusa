package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import org.joml.Vector2d;

public class StartHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_START =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_start.png");

	public final int startSignal;
	public StartHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen,
						int startSignal)
	{
		super(x, y, row, column, radius, screen);
		this.startSignal = startSignal;
	}

	@Override
	public int getMaxOutputs()
	{
		return 6;
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return GRID_START;
	}

	public TileType getType()
	{
		return TileType.START;
	}

	@Override
	public void updateSignal()
	{
		this.signal = startSignal;
		for (HexDirection outDir : outputs)
		{
			BaseHexTile target = screen.tiles.get(new Vector2d(r + outDir.dr, c + outDir.dc));
			if (target != null)
				target.updateSignal();
		}
	}

	@Override
	public boolean handlesSignal()
	{
		return true;
	}
}
