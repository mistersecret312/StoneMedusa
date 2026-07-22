package net.mistersecret312.stonemedusa.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundRemoveMedusaPacket(UUID uuid) implements CustomPacketPayload
{
	public static final Type<ClientboundRemoveMedusaPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "s2c_remove_medusa"));

	public static final StreamCodec<ByteBuf, ClientboundRemoveMedusaPacket> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, ClientboundRemoveMedusaPacket::uuid,
			ClientboundRemoveMedusaPacket::new
	);

	@Override
	public Type<ClientboundRemoveMedusaPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundRemoveMedusaPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.removeMedusa(packet.uuid);
			});
	}
}
