package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import org.joml.Vector2d;

import java.util.EnumSet;
import java.util.Set;

public class BaseHexTile extends AbstractWidget implements Renderable
{
	public static final ResourceLocation GRID =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_1.png");
	public final EngineeringScreen screen;

	public int signal = 0;
	int x,y;
	int r,c;
	int gridRadius;

	protected Set<HexDirection> inputs = EnumSet.noneOf(HexDirection.class);
	protected Set<HexDirection> outputs = EnumSet.noneOf(HexDirection.class);

	public BaseHexTile(int x, int y, int row, int column, int radius, EngineeringScreen screen)
	{
		super(x, y, 16, 16, Component.empty());
		this.x = x;
		this.y = y;
		this.r = row;
		this.c = column;
		this.screen = screen;
		this.gridRadius = radius;
	}

	@Override
	public int getX()
	{
		return x + (c * 16) + (r * 8);
	}

	@Override
	public int getY()
	{
		return y + (r * 12);
	}

	public int getMaxInputs()
	{
		return 0;
	}

	public int getMaxOutputs()
	{
		return 0;
	}

	public boolean canAcceptInput()
	{
		return inputs.size() < getMaxInputs();
	}

	public boolean canAcceptOutput()
	{
		return outputs.size() < getMaxOutputs();
	}

	public void addInput(HexDirection dir)
	{
		inputs.add(dir);
	}

	public void addOutput(HexDirection dir)
	{
		outputs.add(dir);
	}

	public void clearConnections()
	{
		inputs.clear();
		outputs.clear();
		signal = 0;
	}

	public void disconnectFromNeighbors()
	{
		for (HexDirection inDir : this.inputs)
		{
			BaseHexTile source = screen.tiles.get(new Vector2d(r + inDir.dr, c + inDir.dc));
			if (source != null)
				source.outputs.remove(inDir.getOpposite());
		}

		for (HexDirection outDir : this.outputs)
		{
			BaseHexTile target = screen.tiles.get(new Vector2d(r + outDir.dr, c + outDir.dc));
			if (target != null)
			{
				target.inputs.remove(outDir.getOpposite());
				target.updateSignal();
			}
		}

		this.clearConnections();
	}

	public void autoConnectToNeighbors()
	{
		for (HexDirection dir : HexDirection.values())
		{
			BaseHexTile neighbor = screen.tiles.get(new Vector2d(r + dir.dr, c + dir.dc));
			if (neighbor == null || neighbor.getMaxInputs() == 0 && neighbor.getMaxOutputs() == 0)
				continue;

			if (neighbor.canAcceptOutput() && this.canAcceptInput())
			{
				this.addInput(dir);
				neighbor.addOutput(dir.getOpposite());
				neighbor.updateSignal();
			}
			else if (this.canAcceptOutput() && neighbor.canAcceptInput())
			{
				this.addOutput(dir);
				neighbor.addInput(dir.getOpposite());
				neighbor.updateSignal();
			}
		}

		this.updateSignal();
	}

	protected ResourceLocation getTexture()
	{
		return GRID;
	}

	public void updateSignal()
	{
		int maxIncoming = 0;
		for (HexDirection dir : inputs)
		{
			BaseHexTile source = screen.tiles.get(new Vector2d(r + dir.dr, c + dir.dc));
			if (source != null && source.signal > maxIncoming)
				maxIncoming = source.signal;

		}

		this.signal = Math.max(0, maxIncoming - 1);
		for (HexDirection outDir : outputs)
		{
			BaseHexTile target = screen.tiles.get(new Vector2d(r + outDir.dr, c + outDir.dc));
			if (target != null)
				target.updateSignal();
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		if(!active || !visible)
			return false;

		double dx = Math.abs(mouseX-getX());
		double dy = Math.abs(mouseY-getY());

		if(dx > 8 || dy > 8)
			return false;

		return !((dx + 2 * dy) > 16);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int i, int i1, float v)
	{
		graphics.blit(getTexture(), getX()-8, getY()-8, 0, 0,
				16, 16, 16, 16);
		graphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(signal),
				getX(), getY()-4, 0xFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(!isMouseOver(mouseX, mouseY))
			return false;

		if(button == 0 && !(this instanceof StartHexTile) && !(this instanceof EndHexTile))
			screen.placeTile(r,c, new WireHexTile(x, y, r, c, gridRadius, screen));
		if(button == 1)
		{
			if(this instanceof WireHexTile)
				screen.placeTile(r,c, new BaseHexTile(x, y, r, c, gridRadius, screen));
		}
		if(button == 2)
			System.out.println("Position of tile - " + r + " " + c);
		return true;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{

	}
}
