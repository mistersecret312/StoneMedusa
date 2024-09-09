package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PetrificationConfig
{
    public static ForgeConfigSpec.IntValue player_petrification_time;
    public static ForgeConfigSpec.IntValue entity_petrification_time;


    public static void init(ForgeConfigSpec.Builder builder)
    {
        player_petrification_time = builder
                .comment("For how long does the player get petrified(How long, after getting petrified, does it take for them to break out on their own, in ticks")
                .defineInRange("server.player_petrification_time", 12000, -1, Integer.MAX_VALUE);

        entity_petrification_time = builder
                .comment("Same as above but for entities")
                .defineInRange("server.player_petrification_time", -1, -1, Integer.MAX_VALUE);
    }

}
