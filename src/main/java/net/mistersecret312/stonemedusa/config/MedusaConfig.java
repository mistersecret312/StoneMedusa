package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.mistersecret312.stonemedusa.item.MedusaItem;

public class MedusaConfig
{
    public static ForgeConfigSpec.IntValue max_energy;
    public static ForgeConfigSpec.DoubleValue max_radius;
    public static ForgeConfigSpec.DoubleValue base_speed;
    public static ForgeConfigSpec.BooleanValue can_set_target;

    public static ForgeConfigSpec.IntValue generation_period;
    public static ForgeConfigSpec.DoubleValue generation_chance;
    public static ForgeConfigSpec.IntValue min_generated_amount;
    public static ForgeConfigSpec.IntValue max_generated_amount;

    public static void init(ForgeConfigSpec.Builder builder)
    {
        max_energy = builder
                .comment("The maximum energy that the Medusa can hold")
                .defineInRange("server.medusa_max_energy", 1000000, 1000, Integer.MAX_VALUE);

        max_radius = builder
                .comment("The maximum radius that the Medusa can reach")
                .defineInRange("server.medusa_max_radius", 200f, 2.5f, Float.MAX_VALUE);

        base_speed = builder
                .comment("The base speed of expansion of the Petrification Beam, in ticks per meter")
                .defineInRange("server.medusa_base_speed", 5, 0.01, Double.MAX_VALUE);

        can_set_target = builder
                .comment("Should it be possible to target specific species with the Medusa or not")
                .define("server.medusa_can_set_target", true);

        builder.push("Medusa Generation");

        generation_period = builder
                .comment("The amount of time that should pass to try generate falling Medusa's once more, in seconds")
                .defineInRange("server.medusa_generation_period", 3000, 0, Integer.MAX_VALUE);

        generation_chance = builder
                .comment("The chance for it to actually generate Medusa's once the generation period passes")
                .defineInRange("server.medusa_generation_chance", 0.05f, 0f, 1f);

        min_generated_amount = builder
                .comment("The minimal amount of Medusa's that can generate at once")
                .defineInRange("server.medusa_generation_min", 2, 0, Integer.MAX_VALUE);

        max_generated_amount = builder
                .comment("The maximum amount of Medusa's that can generate at once, has to be above minimum")
                .defineInRange("server.medusa_generation_max", 5, 0, Integer.MAX_VALUE);
    }

}
