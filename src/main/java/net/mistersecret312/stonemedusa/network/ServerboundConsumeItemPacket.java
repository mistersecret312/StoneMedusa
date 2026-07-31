package net.mistersecret312.stonemedusa.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.init.BeamTypeInit;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaBeamType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundConsumeItemPacket(int slot, int amount) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundConsumeItemPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "c2s_consume_item"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundConsumeItemPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ServerboundConsumeItemPacket::slot,
			ByteBufCodecs.INT, ServerboundConsumeItemPacket::amount,
			ServerboundConsumeItemPacket::new
	);

	@Override
	public CustomPacketPayload.Type<ServerboundConsumeItemPacket> type()
	{
		return TYPE;
	}

	public static void handle(ServerboundConsumeItemPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ServerPacketHandler.consumeItem(ctx.player(), packet.slot, packet.amount);
			});
	}
}
