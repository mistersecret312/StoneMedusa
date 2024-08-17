package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;

public class BlockInit
{

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StoneMedusa.MOD_ID);

    //public static RegistryObject<Block> BLOCK = BLOCKS.register("block",
    //        () -> new Block(BlockBehaviour.Properties.of().forceSolidOn()));

    public static void register(IEventBus bus)
    {
        BLOCKS.register(bus);
    }
}
