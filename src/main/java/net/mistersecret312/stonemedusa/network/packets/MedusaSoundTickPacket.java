package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class MedusaSoundTickPacket
{

    public int entityID;

    public MedusaSoundTickPacket(int entityID) {
        this.entityID = entityID;
    }

    public static void write(MedusaSoundTickPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
    }

    public static MedusaSoundTickPacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();

        return new MedusaSoundTickPacket(entityID);
    }

    public static void handle(MedusaSoundTickPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handleMedusaTickPacket(packet));
        context.get().setPacketHandled(true);
    }
}
