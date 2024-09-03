package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class MedusaActivatedPacket
{

    public int entityID;

    public MedusaActivatedPacket(int entityID) {
        this.entityID = entityID;
    }

    public static void write(MedusaActivatedPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
    }

    public static MedusaActivatedPacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();

        return new MedusaActivatedPacket(entityID);
    }

    public static void handle(MedusaActivatedPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handleMedusaActivationSound(packet));
        context.get().setPacketHandled(true);
    }
}
