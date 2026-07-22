package net.mistersecret312.stonemedusa.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundClearMedusaPacket() implements CustomPacketPayload
{
	public static final Type<ClientboundClearMedusaPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "s2c_clear_medusa"));

	public static final StreamCodec<ByteBuf, ClientboundClearMedusaPacket> STREAM_CODEC = new StreamCodec<>()
	{
		@Override
		public ClientboundClearMedusaPacket decode(ByteBuf byteBuf)
		{
			return new ClientboundClearMedusaPacket();
		}

		@Override
		public void encode(ByteBuf o, ClientboundClearMedusaPacket clientboundClearMedusaPacket)
		{

		}
	};

	@Override
	public Type<ClientboundClearMedusaPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundClearMedusaPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.clearMedusa();
			});
	}
}
