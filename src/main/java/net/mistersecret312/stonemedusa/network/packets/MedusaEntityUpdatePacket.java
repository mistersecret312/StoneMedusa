package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class MedusaEntityUpdatePacket
{
    public float currentRadius;
    public int energy;

    public MedusaEntityUpdatePacket(float currentRadius, int energy) {
        this.currentRadius = currentRadius;
        this.energy = energy;
    }

    public static void write(MedusaEntityUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat(packet.currentRadius);
        buffer.writeInt(packet.energy);
    }

    public static MedusaEntityUpdatePacket read(FriendlyByteBuf buffer) {
        float currentRadius = buffer.readFloat();
        int energy = buffer.readInt();

        return new MedusaEntityUpdatePacket(currentRadius, energy);
    }

    public static void handle(MedusaEntityUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handleMedusa(packet));
        context.get().setPacketHandled(true);
    }
}
