package net.mistersecret312.stonemedusa.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.network.packets.EntityPetrifiedBrokenPacket;
import net.mistersecret312.stonemedusa.network.packets.EntityPetrifiedPacket;
import net.mistersecret312.stonemedusa.network.packets.MedusaActivatedPacket;
import net.mistersecret312.stonemedusa.network.packets.PetrifiedEntityUpdatePacket;

public class ClientPacketHandler
{
    public static void handlePetrified(PetrifiedEntityUpdatePacket packet)
    {
        if(getEntity(packet.entityId) != null)
            getEntity(packet.entityId).getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
            {
                cap.setPetrified(packet.petrified);
                cap.setBreakStage(packet.breakStage);
                cap.setAge(packet.age);
                cap.setBroken(packet.broken);
            });
    }

    public static void handleMedusaActivationSound(MedusaActivatedPacket packet)
    {
        Entity entity = getEntity(packet.entityID);
        if(entity != null)
        {
            entity.level().playSound(getPlayer(), entity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1F, 1F);
        }

    }

    public static void handlePetrifcationSound(EntityPetrifiedPacket packet)
    {
        Entity entity = getEntity(packet.entityID);
        if(entity != null)
        {
            entity.level().playSound(getPlayer(), entity.blockPosition(), SoundEvents.DRIPSTONE_BLOCK_PLACE, SoundSource.MASTER, 1F, 1F);
        }
    }

    public static void handlePetrificationBreak(EntityPetrifiedBrokenPacket packet)
    {
        Entity entity = getEntity(packet.entityID);
        if(entity != null)
        {
            Minecraft.getInstance().particleEngine.destroy(entity.blockPosition(), Blocks.STONE.defaultBlockState());
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f, 1f);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> T getEntity(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return null;
        Entity entity = level.getEntity(entityId);
        return (T) entity;
    }

    public static LocalPlayer getPlayer()
    {
        return Minecraft.getInstance().player;
    }
}
