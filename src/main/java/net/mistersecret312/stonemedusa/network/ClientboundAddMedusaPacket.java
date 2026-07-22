package net.mistersecret312.stonemedusa.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.init.BeamTypeInit;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaBeamType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundAddMedusaPacket(MedusaBeam beam) implements CustomPacketPayload
{
	public static final Type<ClientboundAddMedusaPacket> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "s2c_add_medusa"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MedusaBeam> DISPATCH_CODEC =
			ByteBufCodecs.registry(BeamTypeInit.REGISTRY_KEY)
						 .dispatch(MedusaBeam::getType, MedusaBeamType::getStreamCodec);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddMedusaPacket> STREAM_CODEC = StreamCodec.composite(
			DISPATCH_CODEC, ClientboundAddMedusaPacket::beam,
			ClientboundAddMedusaPacket::new
	);

	@Override
	public Type<ClientboundAddMedusaPacket> type()
	{
		return TYPE;
	}

	public static void handle(ClientboundAddMedusaPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
			{
				ClientPacketHandler.addMedusa(packet.beam);
			});
	}
}
