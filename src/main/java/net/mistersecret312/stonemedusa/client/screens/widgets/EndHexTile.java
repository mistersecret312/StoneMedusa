package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import org.joml.Vector2d;

public class EndHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_END =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_end.png");

	public int requiredSignal;
	public EndHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen,
					  int requiredSignal)
	{
		super(x, y, row, column, radius, screen, new CompoundTag());
		this.requiredSignal = requiredSignal;
	}

	public EndHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen, CompoundTag tag)
	{
		this(x, y, row, column, radius, screen, 0);
		loadAdditional(tag);
	}

	@Override
	public CompoundTag saveAdditional()
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt("required_signal", requiredSignal);
		return tag;
	}

	@Override
	public void loadAdditional(CompoundTag tag)
	{
		this.requiredSignal = tag.getInt("required_signal");
		super.loadAdditional(tag);
	}

	@Override
	public int getMaxInputs()
	{
		return 1;
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return GRID_END;
	}

	public TileType getType()
	{
		return TileType.END;
	}

	@Override
	public boolean handlesSignal()
	{
		return true;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int i, int i1, float v)
	{
		super.renderWidget(graphics, i, i1, v);

		Font font = Minecraft.getInstance().font;
		String string = "Required Signal : " + requiredSignal;
		graphics.drawCenteredString(Minecraft.getInstance().font, string,
				getX()+font.width(string)-32, getY()-4, -1);
	}
}
