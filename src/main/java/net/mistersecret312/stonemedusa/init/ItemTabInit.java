package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.item.DiamondBatteryItem;
import net.mistersecret312.stonemedusa.item.MedusaItem;

public class ItemTabInit
{
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StoneMedusa.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main_tab",
            () -> CreativeModeTab.builder().icon(() -> MedusaItem.getMedusa(ItemInit.MEDUSA.get(), MedusaConfig.max_energy.get(), 5, 20))
                    .title(Component.translatable("creativetab.main_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(MedusaItem.getMedusa(ItemInit.MEDUSA.get(), MedusaConfig.max_energy.get(), 5, 20));
                        output.accept(DiamondBatteryItem.getBattery(ItemInit.BATTERY.get(), MedusaConfig.max_energy.get()));
                        output.accept(ItemInit.DIAMOND_GEM_CUTTER.get());
                        output.accept(ItemInit.NETHERITE_GEM_CUTTER.get());
                        output.accept(ItemInit.REVIVAL_FLUID.get());
                        output.accept(ItemInit.REVIVAL_FLUID_BUCKET.get());
                        output.accept(ItemInit.NITRIC_ACID_FLASK.get());
                        output.accept(ItemInit.NITRIC_ACID_BUCKET.get());
                    })
                    .build());


    public static void register(IEventBus eventBus)
    {
        TABS.register(eventBus);
    }
}
