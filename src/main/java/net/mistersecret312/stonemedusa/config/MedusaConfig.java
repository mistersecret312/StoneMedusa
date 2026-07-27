package net.mistersecret312.stonemedusa.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MedusaConfig
{
	public static ModConfigSpec.IntValue medusa_max_energy;
	public static ModConfigSpec.IntValue medusa_idle_time;

	public static ModConfigSpec.IntValue pyramid_generation_x_chunk_offset;
	public static ModConfigSpec.IntValue pyramid_generation_z_chunk_offset;
	public static ModConfigSpec.IntValue pyramid_generation_x_chunk_bounds;
	public static ModConfigSpec.IntValue pyramid_generation_z_chunk_bounds;

	public static void init(ModConfigSpec.Builder server)
	{
		medusa_max_energy = server
									.comment("The maximum energy that the Medusa can hold")
									.defineInRange("server.medusa_max_energy", 5000000, 0, Integer.MAX_VALUE);
		medusa_idle_time = server
								   .comment("The amount of ticks the Medusa idles after fully expanding")
								   .defineInRange("server.medusa_idle_time", 40, 0, Integer.MAX_VALUE);

		pyramid_generation_x_chunk_offset = server
													.comment("X chunk center offset of structures that contain a Medusa Pyramid")
													.defineInRange("server.stargate_generation_center_x_chunk_offset", 0, -512, 512);

		pyramid_generation_z_chunk_offset = server
													.comment("Z chunk center offset of structures that contain a Medusa Pyramid")
													.defineInRange("server.stargate_generation_center_z_chunk_offset", 0, -512, 512);

		pyramid_generation_x_chunk_bounds = server
											  .comment("X chunk bounds within which a Medusa Pyramid may generate")
											  .defineInRange("server.stargate_generation_x_bound", 64, 0, 64);

		pyramid_generation_z_chunk_bounds = server
											  .comment("Z chunk bounds within which a Medusa Pyramid may generate")
											  .defineInRange("server.stargate_generation_z_bound", 64, 0, 64);
	}

	public static ModConfigSpec.BooleanValue beam_dissipate;

	public static void initClient(ModConfigSpec.Builder client)
	{
		beam_dissipate = client
								 .comment("Whether the medusa should shrink or dissipate. Defaults to shrinking")
								 .define("client.beam_dissipate", true);
	}
}
