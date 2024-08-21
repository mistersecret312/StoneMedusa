package net.mistersecret312.stonemedusa.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Entity;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.network.packets.PetrifiedEntityUpdatePacket;

public class ClientPacketHandler
{
    public static void handlePetrified(PetrifiedEntityUpdatePacket packet)
    {
        if(getEntity(packet.entityId) != null)
            getEntity(packet.entityId).getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
            {
                cap.setPetrified(packet.petrified);
            });
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> T getEntity(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return null;
        Entity entity = level.getEntity(entityId);
        return (T) entity;
    }
}
