package net.mistersecret312.stonemedusa;

import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.config.RevivalConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
	private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec COMMON_CONFIG;

	private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec CLIENT_CONFIG;

	static
	{
		COMMON_BUILDER.push("medusa");
		MedusaConfig.init(COMMON_BUILDER);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.push("revival");
		RevivalConfig.init(COMMON_BUILDER);
		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();

		CLIENT_BUILDER.push("medusa_client");
		MedusaConfig.initClient(CLIENT_BUILDER);
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}
}
