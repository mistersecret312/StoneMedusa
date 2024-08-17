package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.item.MedusaItem;

public class ItemInit
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StoneMedusa.MOD_ID);

    public static final RegistryObject<MedusaItem> MEDUSA = ITEMS.register("medusa",
            () -> new MedusaItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

}

