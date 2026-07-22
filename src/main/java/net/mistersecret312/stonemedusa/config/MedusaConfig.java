package net.mistersecret312.stonemedusa.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MedusaConfig
{
	public static ModConfigSpec.IntValue medusa_max_energy;

	public static void init(ModConfigSpec.Builder server)
	{
		medusa_max_energy = server
										 .comment("The maximum energy that the Medusa can hold")
										 .defineInRange("medusa_max_energy", 5000000, 0, Integer.MAX_VALUE);

	}

	public static ModConfigSpec.BooleanValue beam_dissipate;

	public static void initClient(ModConfigSpec.Builder client)
	{
		beam_dissipate = client
								 .comment("Whether the medusa should shrink or dissipate. Defaults to shrinking")
								 .define("beam_dissipate", false);
	}
}
