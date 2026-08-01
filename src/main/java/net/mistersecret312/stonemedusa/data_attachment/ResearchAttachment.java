package net.mistersecret312.stonemedusa.data_attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class ResearchAttachment implements INBTSerializable<CompoundTag>
{
	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchAttachment> STREAM_CODEC = StreamCodec.of(
			(buf, attach) -> buf.writeNbt(attach.serializeNBT(buf.registryAccess())),
			(buf) -> {
				ResearchAttachment attachment = new ResearchAttachment();
				CompoundTag tag = buf.readNbt();
				if(tag != null)
					attachment.deserializeNBT(buf.registryAccess(), tag);
				return attachment;
			}
	);

	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
	{

	}
}
