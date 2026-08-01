package net.mistersecret312.stonemedusa.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundResearchProgressPacket(ResourceLocation key, CompoundTag tag) implements CustomPacketPayload
{
	public static final Type<ServerboundResearchProgressPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "c2s_research_progress"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundResearchProgressPacket> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ServerboundResearchProgressPacket::key,
			ByteBufCodecs.COMPOUND_TAG, ServerboundResearchProgressPacket::tag,
			ServerboundResearchProgressPacket::new
	);

	@Override
	public Type<ServerboundResearchProgressPacket> type()
	{
		return TYPE;
	}

	public static void handle(ServerboundResearchProgressPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ServerPacketHandler.saveResearch(ctx.player(), packet.key, packet.tag);
			});
	}
}
