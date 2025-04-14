package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MedusaConfig
{
    public static ForgeConfigSpec.IntValue max_energy;
    public static ForgeConfigSpec.IntValue flat_activation_cast;
    public static ForgeConfigSpec.IntValue cost_per_meter;
    public static ForgeConfigSpec.DoubleValue max_radius;
    public static ForgeConfigSpec.DoubleValue base_speed;
    public static ForgeConfigSpec.BooleanValue can_set_target;
    public static ForgeConfigSpec.BooleanValue dispenser_activate;

    public static ForgeConfigSpec.IntValue generation_period;
    public static ForgeConfigSpec.DoubleValue generation_chance;
    public static ForgeConfigSpec.IntValue min_generated_amount;
    public static ForgeConfigSpec.IntValue max_generated_amount;
    public static ForgeConfigSpec.DoubleValue break_chance;
    public static ForgeConfigSpec.DoubleValue min_generated_energy;
    public static ForgeConfigSpec.DoubleValue max_generated_energy;
    public static ForgeConfigSpec.DoubleValue player_target_chance;
    public static ForgeConfigSpec.DoubleValue min_generated_radius;
    public static ForgeConfigSpec.DoubleValue max_generated_radius;

    public static void init(ForgeConfigSpec.Builder builder)
    {
        max_energy = builder
                .comment("The maximum energy that the Medusa can hold")
                .defineInRange("server.medusa_max_energy", 5000000, 1000, Integer.MAX_VALUE);

        flat_activation_cast = builder
                .comment("The amount of energy that the Medusa will consume on activation, no matter how large the target radius")
                .defineInRange("server.medusa_flat_activation_cost", 5000, 0, Integer.MAX_VALUE);

        cost_per_meter = builder
                .comment("The amount of energy that the Medusa will consume on activation for each meter of the target radius")
                .defineInRange("server.medusa_cost_per_meter", 500, 0, Integer.MAX_VALUE);

        max_radius = builder
                .comment("The maximum radius that the Medusa can reach")
                .defineInRange("server.medusa_max_radius", 8192, 2.5d, Float.MAX_VALUE);

        base_speed = builder
                .comment("The base speed of expansion of the Petrification Beam, in ticks per meter")
                .defineInRange("server.medusa_base_speed", 5, 0.01, Double.MAX_VALUE);

        can_set_target = builder
                .comment("Should it be possible to target specific species with the Medusa or not")
                .define("server.medusa_can_set_target", true);

        dispenser_activate = builder
                .comment("Should dispensing the Medusa with a Dispenser activate it to it's latest settings or not")
                .define("server.medusa_dispenser_activate", true);

        builder.push("Medusa Generation");

        generation_period = builder
                .comment("The amount of time that should pass to try generate falling Medusa's once more, in seconds")
                .defineInRange("server.medusa_generation_period", 3600, 0, Integer.MAX_VALUE);

        generation_chance = builder
                .comment("The chance for it to actually generate Medusa's once the generation period passes")
                .defineInRange("server.medusa_generation_chance", 0.05d, 0d, 1d);

        min_generated_amount = builder
                .comment("The minimal amount of Medusa's that can generate at once")
                .defineInRange("server.medusa_generation_min", 5, 0, Integer.MAX_VALUE);

        max_generated_amount = builder
                .comment("The maximum amount of Medusa's that can generate at once, has to be above minimum")
                .defineInRange("server.medusa_generation_max", 14, 0, Integer.MAX_VALUE);

        break_chance = builder
                .comment("The chance of a generated Medusa to break on impact with the ground")
                .defineInRange("server.medusa_break_chance", 0.2d, 0f, 1d);

        min_generated_energy = builder
                .comment("The minimal energy percentage stored in the Medusa when it's generated")
                .defineInRange("server.medusa_min_generated_energy", 0.5d, 0f, 1d);

        max_generated_energy = builder
                .comment("The maximum energy percentage stored in the Medusa when it's generated")
                .defineInRange("server.medusa_max_generated_energy", 1.0d, 0f, 1d);

        player_target_chance = builder
                .comment("The chance of a generated Medusa to be targetted onto Players")
                .defineInRange("server.medusa_player_target_chance", 0.99d, 0f, 1f);

        min_generated_radius = builder
                .comment("The minimal target radius of a generated Medusa")
                .defineInRange("server.medusa_min_generated_radius", 3d, 0d, Integer.MAX_VALUE);

        max_generated_radius = builder
                .comment("The maximum target radius of a generated Medusa")
                .defineInRange("server.medusa_max_generated_radius", 8f, 0f, Integer.MAX_VALUE);
    }

}
