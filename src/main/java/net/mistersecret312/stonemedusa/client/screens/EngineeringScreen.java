package net.mistersecret312.stonemedusa.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec2;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.screens.widgets.HexTileWidget;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngineeringScreen extends AbstractContainerScreen<EngineeringTableMenu>
{
	public Map<Vector2d, HexTileWidget> tiles = new HashMap();
	public boolean solved = false;

	public EngineeringScreen(EngineeringTableMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}

	@Override
	protected void init()
	{
		int x = (width) / 2;
		int y = (height) / 2;
		int radius = 2;
		for (int c = -radius; c <= radius; c++)
		{
			int rStart = Math.max(-radius, -c - radius);
			int rEnd = Math.min(radius, -c + radius);

			for (int r = rStart; r <= rEnd; r++)
			{
				HexTileWidget widget = new HexTileWidget(x, y, r, c, radius, this);
				this.addRenderableWidget(widget);
				tiles.put(new Vector2d(r, c), widget);
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
	{
		int x = (width) / 2;
		int y = (height) / 3;

		if(solved)
			graphics.drawCenteredString(Minecraft.getInstance().font, "Solved!", x, y, 0x00FFF00);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
//		super.renderLabels(guiGraphics, mouseX, mouseY);
	}
}
