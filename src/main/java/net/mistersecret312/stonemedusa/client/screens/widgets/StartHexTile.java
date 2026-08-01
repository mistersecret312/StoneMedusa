package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import org.joml.Vector2d;

public class StartHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_START =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_start.png");

	public int startSignal;
	public StartHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen,
						int startSignal)
	{
		super(x, y, row, column, radius, screen, new CompoundTag());
		this.startSignal = startSignal;
	}

	public StartHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen, CompoundTag tag)
	{
		this(x, y, row, column, radius, screen, 0);
		loadAdditional(tag);
	}

	@Override
	public CompoundTag saveAdditional()
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("start_signal", startSignal);
		return tag;
	}

	@Override
	public void loadAdditional(CompoundTag tag)
	{
		this.startSignal = tag.getInt("start_signal");
		super.loadAdditional(tag);
	}

	@Override
	public int getMaxOutputs()
	{
		return 1;
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
