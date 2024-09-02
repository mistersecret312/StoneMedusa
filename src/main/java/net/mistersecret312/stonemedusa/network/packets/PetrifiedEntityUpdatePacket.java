package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class PetrifiedEntityUpdatePacket
{
    public boolean petrified;
    public float age;
    public int entityId;

    public PetrifiedEntityUpdatePacket(boolean petrified, float age, int entityId) {
        this.petrified = petrified;
        this.age = age;
        this.entityId = entityId;
    }

    public static void write(PetrifiedEntityUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.petrified);
        buffer.writeFloat(packet.age);
        buffer.writeInt(packet.entityId);
    }

    public static PetrifiedEntityUpdatePacket read(FriendlyByteBuf buffer) {
        boolean petrified = buffer.readBoolean();
        float age = buffer.readFloat();
        int entityId = buffer.readInt();

        return new PetrifiedEntityUpdatePacket(petrified, age, entityId);
    }

    public static void handle(PetrifiedEntityUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handlePetrified(packet));
        context.get().setPacketHandled(true);
    }
}
