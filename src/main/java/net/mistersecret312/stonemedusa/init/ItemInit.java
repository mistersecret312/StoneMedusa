package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.item.*;

public class ItemInit
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StoneMedusa.MOD_ID);

    public static final RegistryObject<MedusaItem> MEDUSA = ITEMS.register("medusa",
            () -> new MedusaItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public static final RegistryObject<DiamondBatteryItem> BATTERY = ITEMS.register("diamond_battery",
            () -> new DiamondBatteryItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<GemCutterItem> DIAMOND_GEM_CUTTER = ITEMS.register("diamond_gem_cutter",
            () -> new GemCutterItem(new Item.Properties().durability(4).rarity(Rarity.COMMON)));
    public static final RegistryObject<GemCutterItem> NETHERITE_GEM_CUTTER = ITEMS.register("netherite_gem_cutter",
            () -> new GemCutterItem(new Item.Properties().durability(10).fireResistant().rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> NITRIC_ACID_FLASK = ITEMS.register("nitric_acid_jar",
            () -> new NitricAcidBottleItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<RevivalFluidItem> REVIVAL_FLUID = ITEMS.register("revival_fluid_flask",
            () -> new RevivalFluidItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> REVIVAL_FLUID_BUCKET = ITEMS.register("revival_fluid_bucket",
            () -> new RevivalFluidBucketItem(FluidInit.SOURCE_REVIVAL_FLUID,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> NITRIC_ACID_BUCKET = ITEMS.register("nitric_acid_bucket",
            () -> new BucketItem(FluidInit.SOURCE_NITRIC_ACID,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).craftRemainder(Items.BUCKET)));


    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
    }

}

