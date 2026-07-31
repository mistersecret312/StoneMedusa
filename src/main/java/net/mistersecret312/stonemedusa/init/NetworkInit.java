package net.mistersecret312.stonemedusa.init;

import net.mistersecret312.stonemedusa.network.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkInit
{
	@SubscribeEvent
	public static void registerPackets(final RegisterPayloadHandlersEvent event)
	{
		final PayloadRegistrar registrar = event.registrar("1");

		//Server
		registrar.playToServer(ServerboundConsumeItemPacket.TYPE, ServerboundConsumeItemPacket.STREAM_CODEC,
				ServerboundConsumeItemPacket::handle);

		//Client

		registrar.playToClient(ClientboundAddMedusaPacket.TYPE, ClientboundAddMedusaPacket.STREAM_CODEC,
				ClientboundAddMedusaPacket::handle);
		registrar.playToClient(ClientboundSyncMedusaPacket.TYPE, ClientboundSyncMedusaPacket.STREAM_CODEC,
				ClientboundSyncMedusaPacket::handle);
		registrar.playToClient(ClientboundRemoveMedusaPacket.TYPE, ClientboundRemoveMedusaPacket.STREAM_CODEC,
				ClientboundRemoveMedusaPacket::handle);
		registrar.playToClient(ClientboundClearMedusaPacket.TYPE, ClientboundClearMedusaPacket.STREAM_CODEC,
				ClientboundClearMedusaPacket::handle);
		registrar.playToClient(ClientboundMoveMedusaPacket.TYPE, ClientboundMoveMedusaPacket.STREAM_CODEC,
				ClientboundMoveMedusaPacket::handle);
	}
}
