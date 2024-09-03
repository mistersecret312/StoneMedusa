package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class PetrifiedEntityUpdatePacket
{
    public boolean petrified;
    public float age;
    public int breakStage;
    public int entityId;

    public PetrifiedEntityUpdatePacket(boolean petrified, int breakStage, float age, int entityId) {
        this.petrified = petrified;
        this.breakStage = breakStage;
        this.age = age;
        this.entityId = entityId;
    }

    public static void write(PetrifiedEntityUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.petrified);
        buffer.writeInt(packet.breakStage);
        buffer.writeFloat(packet.age);
        buffer.writeInt(packet.entityId);
    }

    public static PetrifiedEntityUpdatePacket read(FriendlyByteBuf buffer) {
        boolean petrified = buffer.readBoolean();
        int breakStage = buffer.readInt();
        float age = buffer.readFloat();
        int entityId = buffer.readInt();

        return new PetrifiedEntityUpdatePacket(petrified, breakStage, age, entityId);
    }

    public static void handle(PetrifiedEntityUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handlePetrified(packet));
        context.get().setPacketHandled(true);
    }
}
