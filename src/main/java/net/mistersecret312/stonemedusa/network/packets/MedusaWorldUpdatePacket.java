package net.mistersecret312.stonemedusa.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.capability.WorldCapability;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Supplier;

public class MedusaWorldUpdatePacket
{
    public HashMap<Vec3, WorldCapability.MedusaData> medusaData;

    public MedusaWorldUpdatePacket(HashMap<Vec3, WorldCapability.MedusaData> medusaData)
    {
        this.medusaData = medusaData;
    }

    public static void write(MedusaWorldUpdatePacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeCollection(packet.medusaData.entrySet(), (writer, value) -> {
            WorldCapability.MedusaData data = value.getValue();
            writer.writeVector3f(new Vector3f(value.getKey().toVector3f()));
            writer.writeFloat(data.getRadius());
            writer.writeFloat(data.getTransparency());
            writer.writeBlockPos(value.getValue().getPosition());
            writer.writeResourceKey(value.getValue().getFilter());
        });
    }

    public static MedusaWorldUpdatePacket read(FriendlyByteBuf buffer)
    {
        List<Map.Entry<Vec3, WorldCapability.MedusaData>> medusaData = buffer.readCollection(i -> new ArrayList<>(), reader -> {
            Vec3 vec = new Vec3(buffer.readVector3f());

            float radius = reader.readFloat();
            float transparency = reader.readFloat();
            BlockPos position = reader.readBlockPos();
            ResourceKey<EntityType<?>> filter = reader.readResourceKey(ForgeRegistries.ENTITY_TYPES.getRegistryKey());
            WorldCapability.MedusaData data = new WorldCapability.MedusaData(radius, transparency, position, filter);

            return Map.entry(vec, data);
        });
        HashMap<Vec3, WorldCapability.MedusaData> data = new HashMap<>();
        medusaData.forEach(entry -> data.put(entry.getKey(), entry.getValue()));

        return new MedusaWorldUpdatePacket(data);
    }

    public static void handle(MedusaWorldUpdatePacket packet, Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level != null)
            {
                level.getCapability(CapabilitiesInit.WORLD).ifPresent(cap -> {
                    cap.setMedusaData(packet.medusaData);
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
