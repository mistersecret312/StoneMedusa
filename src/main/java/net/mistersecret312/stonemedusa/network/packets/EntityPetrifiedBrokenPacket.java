package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class EntityPetrifiedBrokenPacket
{

    public int entityID;

    public EntityPetrifiedBrokenPacket(int entityID) {
        this.entityID = entityID;
    }

    public static void write(EntityPetrifiedBrokenPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
    }

    public static EntityPetrifiedBrokenPacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();

        return new EntityPetrifiedBrokenPacket(entityID);
    }

    public static void handle(EntityPetrifiedBrokenPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handlePetrificationBreak(packet));
        context.get().setPacketHandled(true);
    }
}
