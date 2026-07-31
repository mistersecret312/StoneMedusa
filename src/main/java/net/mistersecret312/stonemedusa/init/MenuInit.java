package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.menus.EngineeringStorageMenu;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuInit
{
	public static final DeferredRegister<MenuType<?>> MENUS =
			DeferredRegister.create(Registries.MENU, StoneMedusa.MODID);

	public static final DeferredHolder<MenuType<?>, MenuType<EngineeringTableMenu>> ENGINEERING_RESEARCH =
			registerMenuType("engineering_research", EngineeringTableMenu::new);
	public static final DeferredHolder<MenuType<?>, MenuType<EngineeringStorageMenu>> ENGINEERING_STORAGE =
			registerMenuType("engineering_storage", EngineeringStorageMenu::new);

	private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
																											   IContainerFactory<T> factory) {
		return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
	}

	public static void register(IEventBus bus)
	{
		MENUS.register(bus);
	}
}
