package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevivalConfig
{

    public static ForgeConfigSpec.IntValue revival_time;

    public static ForgeConfigSpec.IntValue revival_damage;
    public static ForgeConfigSpec.IntValue nitric_damage;

    public static void init(ForgeConfigSpec.Builder builder)
    {
        revival_time = builder
                .comment("How much time should pass before pouring Revival Fluid(or Nitric acid if enabled and conditions are right) before the stone is broken, in ticks")
                .defineInRange("server.revival_time", 100, 0, Integer.MAX_VALUE);

        revival_damage = builder
                .comment("The damage per second you receive while swimming inside Revival Fluid")
                .defineInRange("server.revival_fluid_damage", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

        nitric_damage = builder
                .comment("The damage per second you receive while swimming inside Nitric Acid")
                .defineInRange("server.nitric_acid_damage", 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

}
