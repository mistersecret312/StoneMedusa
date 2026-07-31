package net.mistersecret312.stonemedusa.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.menus.EngineeringStorageMenu;

public class EngineeringStorageScreen extends AbstractContainerScreen<EngineeringStorageMenu>
{
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID,
			"textures/gui/engineering_storage.png");

	public EngineeringStorageScreen(EngineeringStorageMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float v, int i, int i1)
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;

		graphics.blit(TEXTURE, x, y, 0, 0, 256, 256);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.renderBackground(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
		super.renderTooltip(graphics, mouseX, mouseY);
	}
}
