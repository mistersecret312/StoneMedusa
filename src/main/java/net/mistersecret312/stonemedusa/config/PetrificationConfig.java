package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PetrificationConfig
{
    public static ForgeConfigSpec.IntValue player_petrification_time;
    public static ForgeConfigSpec.IntValue entity_petrification_time;

    public static ForgeConfigSpec.BooleanValue petrified_entity_destroy;
    public static ForgeConfigSpec.BooleanValue petrified_entity_damage;


    public static void init(ForgeConfigSpec.Builder builder)
    {
        player_petrification_time = builder
                .comment("For how long does the player get petrified(How long, after getting petrified, does it take for them to break out on their own, in ticks")
                .defineInRange("server.player_petrification_time", 12000, -1, Integer.MAX_VALUE);

        entity_petrification_time = builder
                .comment("Same as above but for entities")
                .defineInRange("server.entity_petrification_time", -1, -1, Integer.MAX_VALUE);

        petrified_entity_destroy = builder
                .comment("Whether petrified entities should be able to be broken by damaging them")
                .define("server.petrified_entity_destroy", true);

        petrified_entity_damage = builder
                .comment("Whether petrified entites should be able to get cracked from taking damage")
                .define("server.petrified_entity_damage", true);
    }

}
