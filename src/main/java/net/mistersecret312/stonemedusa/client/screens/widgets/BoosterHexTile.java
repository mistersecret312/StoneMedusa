package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;

public class BoosterHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_BOOSTER =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_booster.png");

	public int boostAmount;
	public BoosterHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen, int amount)
	{
		super(x, y, row, column, radius, screen, new CompoundTag());
		boostAmount = amount;
	}

	public BoosterHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen, CompoundTag tag)
	{
		this(x, y, row, column, radius, screen, 0);
		loadAdditional(tag);
	}

	@Override
	public CompoundTag saveAdditional()
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("boost_amount", boostAmount);
		return tag;
	}

	@Override
	public void loadAdditional(CompoundTag tag)
	{
		this.boostAmount = tag.getInt("boost_amount");
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
		return GRID_BOOSTER;
	}

	@Override
	public int modifySignal(int signal)
	{
		return signal+5;
	}

	@Override
	public TileType getType()
	{
		return TileType.BOOSTER;
	}

	@Override
	public boolean handlesSignal()
	{
		return true;
	}
}
