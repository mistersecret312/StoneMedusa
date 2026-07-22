package net.mistersecret312.stonemedusa.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RevivalConfig
{
	public static ModConfigSpec.IntValue revival_fluid_burntime;

	public static void init(ModConfigSpec.Builder server)
	{
		revival_fluid_burntime = server
									.comment("The time it takes for a vile of revival fluid to burn out in the Furnace")
									.defineInRange("revival_fluid_burntime", 2400, 0, Integer.MAX_VALUE);

	}
}
