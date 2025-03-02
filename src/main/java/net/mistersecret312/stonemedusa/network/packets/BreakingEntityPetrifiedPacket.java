package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class BreakingEntityPetrifiedPacket
{

    public int entityID;

    public BreakingEntityPetrifiedPacket(int entityID) {
        this.entityID = entityID;
    }

    public static void write(BreakingEntityPetrifiedPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
    }

    public static BreakingEntityPetrifiedPacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();

        return new BreakingEntityPetrifiedPacket(entityID);
    }

    public static void handle(BreakingEntityPetrifiedPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            ServerPlayer player = context.get().getSender();
            if(player != null)
            {
                player.level().getEntity(packet.entityID);
            }
        });
        context.get().setPacketHandled(true);
    }
}
