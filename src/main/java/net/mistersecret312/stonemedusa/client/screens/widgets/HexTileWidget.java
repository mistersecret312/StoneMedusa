package net.mistersecret312.stonemedusa.client.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.EngineeringScreen;
import org.joml.Vector2d;

public class HexTileWidget extends AbstractWidget implements Renderable
{
	public static final ResourceLocation GRID =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_1.png");
	public static final ResourceLocation GRID_START =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_start.png");
	public static final ResourceLocation GRID_END =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_end.png");
	public static final ResourceLocation GRID_WIRE =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_wire.png");
	public static final ResourceLocation GRID_BOOSTER =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "textures/item/grid_booster.png");

	public final EngineeringScreen screen;

	int x,y;
	int r,c;

	int signal;
	boolean start;
	boolean end;
	boolean booster;

	int requiredSignal;
	boolean hasWire;
	int gridRadius;

	HexDirection in = null;
	HexDirection out = null;

	public HexTileWidget(int x, int y, int row, int column, int radius, EngineeringScreen screen)
	{
		super(x, y, 16, 16, Component.empty());
		this.x = x;
		this.y = y;
		this.r = row;
		this.c = column;
		this.gridRadius = radius;
		this.screen = screen;

		start = c == -radius && r == 0;
		end = c == radius && r == 0;
		hasWire = start || end;

		signal = start ? 15 : 0;
		requiredSignal = end ? 15 : 0;
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

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		ResourceLocation texture = GRID;
		if(hasWire)
			texture = GRID_WIRE;
		if(booster)
			texture = GRID_BOOSTER;
		if(start)
			texture = GRID_START;
		if(end)
			texture = GRID_END;

		graphics.blit(texture, getX()-8, getY()-8, 0, 0, 16, 16, 16, 16);
		graphics.drawCenteredString(Minecraft.getInstance().font, String.valueOf(signal), getX(), getY()-4,
				booster ? 0x200589 : 0xFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(!isMouseOver(mouseX, mouseY))
			return false;

		if(button == 0)
			System.out.println("Hex Tile Clicked - " + " Row : " + r + " Column : " + c + " Button : " + button);
		if(button == 1 && !start && !end)
		{
			hasWire = !hasWire;
			System.out.println("Changed Wire presence to - " + hasWire);
			updateConnectivity();
		}
		if(button == 2 && !start && !end)
		{
			booster = !booster;
			updateSignal();
		}

		return true;
	}

	public void updateConnectivity()
	{
		if(!hasWire)
		{
			disconnect();
			return;
		}
		for(HexDirection direction : HexDirection.values())
		{
			HexTileWidget tile = screen.tiles.get(new Vector2d(r + direction.dr, c + direction.dc));
			if(tile == null || !tile.hasWire)
				continue;

			if(this.in == null && tile.out == null && !tile.end)
			{
				this.in = direction;
				tile.out = direction.getOpposite();

				tile.updateSignal();
			}
			else if(this.out == null && tile.in == null && !tile.start)
			{
				this.out = direction;
				tile.in = direction.getOpposite();

				tile.updateSignal();
			}
		}
		updateSignal();
	}

	public void updateSignal()
	{
		if (this.in != null)
		{
			HexTileWidget sourceTile = screen.tiles.get(new Vector2d(r + this.in.dr, c + this.in.dc));
			if (sourceTile != null || sourceTile.signal == 0)
			{
				this.signal = sourceTile.signal - 1;
				if(this.booster)
					this.signal += 5;
				if(this.end)
					screen.solved = this.signal == this.requiredSignal;

				if (this.out != null)
				{
					HexTileWidget nextTile = screen.tiles.get(new Vector2d(r + this.out.dr, c + this.out.dc));
					if (nextTile != null)
						nextTile.updateSignal();
				}
			}
		}
	}

	public void disconnect()
	{
		if (this.in != null)
		{
			HexTileWidget tile = screen.tiles.get(new Vector2d(r + in.dr, c + in.dc));
			if (tile != null) tile.out = null;
		}
		if (this.out != null)
		{
			HexTileWidget tile = screen.tiles.get(new Vector2d(r + out.dr, c + out.dc));
			if (tile != null)
			{
				tile.in = null;
				tile.signal = 0;
				tile.updateSignal();
			}
		}

		this.signal = 0;
		this.in = null;
		this.out = null;
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
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{

	}
}
