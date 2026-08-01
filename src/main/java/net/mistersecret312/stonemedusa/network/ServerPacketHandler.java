package net.mistersecret312.stonemedusa.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import net.neoforged.neoforge.items.IItemHandler;

public class ServerPacketHandler
{
	public static void consumeItem(Player player, int slot, int amount)
	{
		if(player.containerMenu instanceof EngineeringTableMenu menu)
		{
			IItemHandler handler = menu.blockEntity.getItemHandler();
			handler.extractItem(slot, amount, false);
			menu.blockEntity.setChanged();
		}
	}

	public static void saveResearch(Player player, ResourceLocation key, CompoundTag tag)
	{
		player.getData(AttachmentTypeInit.RESEARCH).addResearch(player, key, tag);
	}
}
