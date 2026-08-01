package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import org.joml.Vector2d;

public class EndHexTile extends BaseHexTile
{
	public static final ResourceLocation GRID_END =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_end.png");

	public final int requiredSignal;
	public EndHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen,
					  int requiredSignal)
	{
		super(x, y, row, column, radius, screen);
		this.requiredSignal = requiredSignal;
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
