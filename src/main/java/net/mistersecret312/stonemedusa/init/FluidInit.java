package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;

public class FluidInit
{
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, StoneMedusa.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_REVIVAL_FLUID = FLUIDS.register("revival_fluid",
            () -> new ForgeFlowingFluid.Source(FluidInit.REVIVAL_FLUID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_REVIVAL_FLUID = FLUIDS.register("flowing_revival_fluid",
            () -> new ForgeFlowingFluid.Flowing(FluidInit.REVIVAL_FLUID_PROPERTIES));

    public static final RegistryObject<FlowingFluid> SOURCE_NITRIC_ACID = FLUIDS.register("nitric_acid",
            () -> new ForgeFlowingFluid.Source(FluidInit.NITRIC_ACID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_NITRIC_ACID = FLUIDS.register("flowing_nitric_acid",
            () -> new ForgeFlowingFluid.Flowing(FluidInit.NITRIC_ACID_PROPERTIES));


    public static final ForgeFlowingFluid.Properties REVIVAL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            FluidTypeInit.REVIVAL_FLUID_TYPE, SOURCE_REVIVAL_FLUID, FLOWING_REVIVAL_FLUID)
            .slopeFindDistance(2).levelDecreasePerBlock(1).block(BlockInit.REVIVAL_FLUID)
            .bucket(ItemInit.REVIVAL_FLUID_BUCKET);

    public static final ForgeFlowingFluid.Properties NITRIC_ACID_PROPERTIES = new ForgeFlowingFluid.Properties(
            FluidTypeInit.NITRIC_ACID_TYPE, SOURCE_NITRIC_ACID, FLOWING_NITRIC_ACID)
            .slopeFindDistance(2).levelDecreasePerBlock(1).block(BlockInit.NITRIC_ACID)
            .bucket(ItemInit.NITRIC_ACID_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
