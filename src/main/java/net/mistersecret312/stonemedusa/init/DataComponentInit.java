package net.mistersecret312.stonemedusa.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.data_components.DiamondBatteryComponent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class DataComponentInit
{
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents(
			Registries.DATA_COMPONENT_TYPE, StoneMedusa.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
			register("energy", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_ENERGY =
			register("max_energy", builder -> builder.persistent(Codec.INT));


	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_ACTIVE =
			register("is_active", builder -> builder.persistent(Codec.BOOL));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> START_DELAY =
			register("start_delay", builder -> builder.persistent(Codec.INT));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TICK_DELAY =
			register("tick_delay", builder -> builder.persistent(Codec.INT));


	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> MEDUSA_DESIGN =
			register("medusa_design", builder -> builder.persistent(ResourceLocation.CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> DEVICE_ID =
			register("device_id", builder -> builder.persistent(UUIDUtil.CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<DiamondBatteryComponent>> BATTERY =
			register("battery", builder -> builder.persistent(DiamondBatteryComponent.CODEC));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FRAGMENT_TYPE =
			register("fragment_type", builder -> builder.persistent(Codec.INT));

	private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
	{
		return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
	}

	public static void register(IEventBus bus)
	{
		DATA_COMPONENTS.register(bus);
	}
}