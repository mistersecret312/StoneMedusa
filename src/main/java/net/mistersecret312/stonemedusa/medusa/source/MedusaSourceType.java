package net.mistersecret312.stonemedusa.medusa.source;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.function.Function;

// This record tells the registry how to read your source from NBT and the Network
public record MedusaSourceType<T extends MedusaSource>(
        Function<CompoundTag, T> deserializer,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
) {}