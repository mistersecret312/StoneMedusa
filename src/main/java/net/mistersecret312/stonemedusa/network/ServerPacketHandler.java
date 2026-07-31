package net.mistersecret312.stonemedusa.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
}
