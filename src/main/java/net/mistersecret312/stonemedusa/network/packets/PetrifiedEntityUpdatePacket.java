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
    public boolean broken;
    public float limbSwing;
    public float limbSwingAmount;
    public float headYaw;
    public float headPitch;
    public int entityId;

    public PetrifiedEntityUpdatePacket(boolean petrified, int breakStage, float age, boolean broken, float limbSwing,
                                       float limbSwingAmount, float headYaw, float headPitch, int entityId) {
        this.petrified = petrified;
        this.breakStage = breakStage;
        this.age = age;
        this.broken = broken;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.entityId = entityId;
    }

    public static void write(PetrifiedEntityUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.petrified);
        buffer.writeInt(packet.breakStage);
        buffer.writeFloat(packet.age);
        buffer.writeBoolean(packet.broken);
        buffer.writeFloat(packet.limbSwing);
        buffer.writeFloat(packet.limbSwingAmount);
        buffer.writeFloat(packet.headYaw);
        buffer.writeFloat(packet.headPitch);
        buffer.writeInt(packet.entityId);
    }

    public static PetrifiedEntityUpdatePacket read(FriendlyByteBuf buffer) {
        boolean petrified = buffer.readBoolean();
        int breakStage = buffer.readInt();
        float age = buffer.readFloat();
        boolean broken = buffer.readBoolean();
        float limbSwing = buffer.readFloat();
        float limbSwingAmount = buffer.readFloat();
        float headYaw = buffer.readFloat();
        float headPitch = buffer.readFloat();
        int entityId = buffer.readInt();

        return new PetrifiedEntityUpdatePacket(petrified, breakStage, age, broken, limbSwing, limbSwingAmount, headYaw, headPitch, entityId);
    }

    public static void handle(PetrifiedEntityUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientPacketHandler.handlePetrified(packet));
        context.get().setPacketHandled(true);
    }
}
