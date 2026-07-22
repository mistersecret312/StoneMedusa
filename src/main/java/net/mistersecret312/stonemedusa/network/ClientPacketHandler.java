package net.mistersecret312.stonemedusa.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;

import java.util.UUID;

public class ClientPacketHandler
{
	public static void removeMedusa(UUID uuid)
	{
		Level level = Minecraft.getInstance().level;
	}

	public static void addMedusa(MedusaBeam beam)
	{
		Level level = Minecraft.getInstance().level;
	}

	public static void syncMedusa(UUID uuid, MedusaBeam beam)
	{
		Level level = Minecraft.getInstance().level;
	}

	public static void clearMedusa()
	{

	}

	public static void moveMedusa(UUID uuid, MedusaSettings.MedusaPosition position)
	{
		Level level = Minecraft.getInstance().level;
		if(level != null)
		{

		}
	}
}
