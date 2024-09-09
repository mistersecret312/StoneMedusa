package net.mistersecret312.stonemedusa.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
import net.mistersecret312.stonemedusa.config.RevivalConfig;

@Mod.EventBusSubscriber
public class ConfigInit
{
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;


    static
    {
        COMMON_BUILDER.push("Dr.STONE - Medusa Common Config");

        COMMON_BUILDER.push("Medusa Configuration");
        MedusaConfig.init(COMMON_BUILDER);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Petrification Configuration");
        PetrificationConfig.init(COMMON_BUILDER);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Revival Configuration");
        RevivalConfig.init(COMMON_BUILDER);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
