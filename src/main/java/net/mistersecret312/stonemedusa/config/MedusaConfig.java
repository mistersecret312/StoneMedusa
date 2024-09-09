package net.mistersecret312.stonemedusa.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.mistersecret312.stonemedusa.item.MedusaItem;

public class MedusaConfig
{

    public static ForgeConfigSpec.IntValue max_energy;

    public static void init(ForgeConfigSpec.Builder builder)
    {
        max_energy = builder
                .comment("The maximum energy that the Medusa can hold")
                .defineInRange("server.medusa_max_energy", MedusaItem.maxEnergy, 1000, Integer.MIN_VALUE);
    }

}
