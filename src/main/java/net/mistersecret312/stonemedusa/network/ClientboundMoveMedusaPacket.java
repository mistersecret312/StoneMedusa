package net.mistersecret312.stonemedusa.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundMoveMedusaPacket(UUID uuid, MedusaSettings.MedusaPosition position) implements CustomPacketPayload
{
	public static final Type<ClientboundMoveMedusaPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "s2c_move_medusa"));

	public static final StreamCodec<ByteBuf, ClientboundMoveMedusaPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, ClientboundMoveMedusaPacket::uuid,
			MedusaSettings.MedusaPosition.STREAM_CODEC, ClientboundMoveMedusaPacket::position,
			ClientboundMoveMedusaPacket::new
	);

	@Override
	public Type<ClientboundMoveMedusaPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundMoveMedusaPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.moveMedusa(packet.uuid(), packet.position());
			});
	}
}
