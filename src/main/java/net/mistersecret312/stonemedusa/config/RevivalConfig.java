package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RevivalConfig
{

    public static ForgeConfigSpec.IntValue revival_time;

    public static ForgeConfigSpec.IntValue revival_damage;
    public static ForgeConfigSpec.IntValue nitric_damage;

    public static ForgeConfigSpec.IntValue revival_fluid_burntime;
    public static ForgeConfigSpec.IntValue revival_fluid_bucket_burntime;

    public static ForgeConfigSpec.BooleanValue nitric_revival;
    public static ForgeConfigSpec.BooleanValue nitric_revival_early;
    public static ForgeConfigSpec.IntValue nitric_revival_early_time;
    public static ForgeConfigSpec.BooleanValue nitric_revival_late;
    public static ForgeConfigSpec.IntValue nitric_revival_late_time;


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

        revival_fluid_burntime = builder
                .comment("The time it takes for a vile of revival fluid to burn out in the Furnace")
                .defineInRange("server.revival_fluid_burntime", 2400, 1, Integer.MAX_VALUE);

        revival_fluid_bucket_burntime = builder
                .comment("The time it takes for a bucket of revival fluid to burn out in the Furnace")
                .defineInRange("server.revival_fluid_bucket_burntime", 9600, 1, Integer.MAX_VALUE);

        nitric_revival = builder
                .comment("Should Nitric Acid be capable of reviving entities under certain conditions")
                .define("server.nitric_revival", true);

        builder.push("Nitric Acid Revival");

        nitric_revival_early = builder
                .comment("Can Nitric Acid be used right after petrification to remove it or not")
                .define("server.nitric_revival_early", true);

        nitric_revival_early_time = builder
                .comment("How much time can pass until Nitric Acid can no longer be used to remove petrification, in seconds")
                .defineInRange("server.nitric_revival_early_time", 60, 0, Integer.MAX_VALUE);

        nitric_revival_late = builder
                .comment("Can Nitric Acid be used after some period of time to remove petrification or not")
                .define("server.nitric_revival_late", true);

        nitric_revival_late_time = builder
                .comment("How much time needs to pass until Nitric Acid can be used to remove petrification, in seconds")
                .defineInRange("server.nitric_revival_late_time", 500, 0, Integer.MAX_VALUE);
    }

}
