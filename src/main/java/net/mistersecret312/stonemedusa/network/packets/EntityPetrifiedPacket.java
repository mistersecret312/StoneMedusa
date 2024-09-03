package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class EntityPetrifiedPacket
{

    public int entityID;

    public EntityPetrifiedPacket(int entityID) {
        this.entityID = entityID;
    }

    public static void write(EntityPetrifiedPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
    }

    public static EntityPetrifiedPacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();

        return new EntityPetrifiedPacket(entityID);
    }

    public static void handle(EntityPetrifiedPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handlePetrifcationSound(packet));
        context.get().setPacketHandled(true);
    }
}
