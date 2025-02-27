package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
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
            if(player.level() != null)
            {
                LivingEntity entity = (LivingEntity) player.level().getEntity(packet.entityID);
                entity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap -> {
                    cap.setBreakStage(cap.getBreakStage()+1);
                    if(cap.getBreakStage() >= 9 && PetrificationConfig.petrified_entity_destroy.get())
                    {
                        entity.kill();
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
