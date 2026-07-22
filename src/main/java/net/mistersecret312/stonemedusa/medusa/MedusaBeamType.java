package net.mistersecret312.stonemedusa.medusa;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class MedusaBeamType<T extends MedusaBeam>
{
	private final Function<CompoundTag, T> deserializeFunc;
	private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

	public MedusaBeamType(Function<CompoundTag, T> deserializeFunc, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
	{
		this.deserializeFunc = deserializeFunc;
		this.streamCodec = streamCodec;
	}

	public T deserializeNBT(CompoundTag tag)
	{
		return deserializeFunc.apply(tag);
	}

	public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec()
	{
		return streamCodec;
	}
}
