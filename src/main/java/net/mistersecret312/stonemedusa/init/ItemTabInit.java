package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.items.DiamondBatteryItem;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemTabInit
{
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
			StoneMedusa.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MEDUSA = TABS.register("medusa",
			() -> CreativeModeTab.builder()
								 .icon(() -> new ItemStack(ItemInit.MEDUSA.get()))
								 .title(Component.translatable("tabs.medusa"))
								 .displayItems((parameters, output) -> {
									 output.accept(MedusaItem.getMedusa(ItemInit.MEDUSA.get(),
											 ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "whyman"),
											 MedusaConfig.medusa_max_energy.get()));
									 output.accept(DiamondBatteryItem.getBattery(ItemInit.BATTERY.get(), MedusaConfig.medusa_max_energy.get()));
									 output.accept(ItemInit.REVIVAL_FLUID_FLASK);

									 output.accept(ItemInit.MOBIUS_PLATING);
									 output.accept(ItemInit.WAVEGUIDE_FILAMENT);
									 output.accept(ItemInit.CRYSTAL_RECEPTACLE);
									 output.accept(ItemInit.SPATIAL_MANIFOLD);

									 output.accept(BlockInit.ENGINEERING_TABLE.asItem());
									 output.accept(BlockInit.RUSTY_MEDUSA_BLOCK.asItem());
									 output.accept(BlockInit.RUSTY_MEDUSA.asItem());
								 }).build());

	public static void register(IEventBus bus)
	{
		TABS.register(bus);
	}

}
