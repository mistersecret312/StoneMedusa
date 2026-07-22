package net.mistersecret312.stonemedusa.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundSyncMedusaPacket(UUID uuid, MedusaBeam beam) implements CustomPacketPayload
{
	public static final Type<ClientboundSyncMedusaPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "s2c_sync_medusa"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncMedusaPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, ClientboundSyncMedusaPacket::uuid,
			MedusaBeam.STREAM_CODEC, ClientboundSyncMedusaPacket::beam,
			ClientboundSyncMedusaPacket::new
	);

	@Override
	public Type<ClientboundSyncMedusaPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundSyncMedusaPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.syncMedusa(packet.uuid(), packet.beam());
			});
	}
}
