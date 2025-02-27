package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.network.ClientPacketHandler;

import java.util.function.Supplier;

public class MedusaTextureUpdatePacket
{
    public int entityID;
    public boolean active;
    public boolean countdown;

    public MedusaTextureUpdatePacket(int entityID, boolean active, boolean countdown) {
        this.entityID = entityID;
        this.active = active;
        this.countdown = countdown;
    }

    public static void write(MedusaTextureUpdatePacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.entityID);
        buffer.writeBoolean(packet.active);
        buffer.writeBoolean(packet.countdown);
    }

    public static MedusaTextureUpdatePacket read(FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();
        boolean active = buffer.readBoolean();
        boolean countdown = buffer.readBoolean();

        return new MedusaTextureUpdatePacket(entityID, active, countdown);
    }

    public static void handle(MedusaTextureUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            Entity entity = ClientPacketHandler.getEntity(packet.entityID);
            if(entity != null && entity instanceof MedusaProjectile medusa)
            {
                ItemStack stack = medusa.getItem();
                MedusaItem item = (MedusaItem) stack.getItem();
                MedusaItem.setActive(stack, packet.active);
                item.setCountdownActive(stack, packet.countdown);
            }
        });
        context.get().setPacketHandled(true);
    }
}
