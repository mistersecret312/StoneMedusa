package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;

import java.util.function.Supplier;

public class BreakingEntityPetrifiedPacket
{

    public int entityID;
    public float amount;

    public BreakingEntityPetrifiedPacket(int entityID, float amount) {
        this.entityID = entityID;
        this.amount = amount;
    }

    public static void write(BreakingEntityPetrifiedPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
        buffer.writeFloat(packet.amount);
    }

    public static BreakingEntityPetrifiedPacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();
        float amount = buffer.readFloat();

        return new BreakingEntityPetrifiedPacket(entityID, amount);
    }

    public static void handle(BreakingEntityPetrifiedPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            ServerPlayer player = context.get().getSender();
            if(player.level() != null && packet.amount > 5)
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
