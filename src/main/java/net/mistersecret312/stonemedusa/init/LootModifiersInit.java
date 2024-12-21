package net.mistersecret312.stonemedusa.init;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.lootmodifiers.MedusaLootModifier;

public class LootModifiersInit
{
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, StoneMedusa.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> MEDUSA = MODIFIERS.register("medusa_loot_modifier", MedusaLootModifier.CODEC);

    public static void register(IEventBus bus)
    {
        MODIFIERS.register(bus);
    }
}
