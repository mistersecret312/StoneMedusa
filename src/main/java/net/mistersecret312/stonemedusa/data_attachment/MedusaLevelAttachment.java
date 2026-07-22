package net.mistersecret312.stonemedusa.data_attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MedusaLevelAttachment implements INBTSerializable<CompoundTag>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MedusaLevelAttachment> STREAM_CODEC = StreamCodec.of(
            (buf, attach) -> buf.writeNbt(attach.serializeNBT(buf.registryAccess())),
            (buf) -> {
                MedusaLevelAttachment attachment = new MedusaLevelAttachment();
                CompoundTag tag = buf.readNbt();
                if(tag != null)
                    attachment.deserializeNBT(buf.registryAccess(), tag);
                return attachment;
            }
    );

    private final Map<UUID, MedusaBeam> activeBeams = new ConcurrentHashMap<>();

    public void addBeam(Level level, MedusaBeam beam)
    {
        activeBeams.put(beam.getSettings().uuid(), beam);
        if (!level.isClientSide())
            level.syncData(AttachmentTypeInit.MEDUSA);
    }

    public void removeBeam(Level level, UUID beamId)
    {
        activeBeams.remove(beamId);
        if (!level.isClientSide())
            level.syncData(AttachmentTypeInit.MEDUSA);
    }

    public void tickBeams(Level level)
    {
        Iterator<Map.Entry<UUID, MedusaBeam>> iterator = activeBeams.entrySet().iterator();
        while (iterator.hasNext())
        {
            MedusaBeam beam = iterator.next().getValue();
            beam.tick(level);

            if (beam.shouldRemove())
            {
                iterator.remove();
                if (!level.isClientSide())
                    level.syncData(AttachmentTypeInit.MEDUSA);
            }
        }
    }
    
    public Map<UUID, MedusaBeam> getActiveBeams() { return activeBeams; }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();
        
        ListTag beamsList = new ListTag();
        for (MedusaBeam beam : activeBeams.values())
            beamsList.add(beam.serializeNBT());

        tag.put("active_beams", beamsList);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
    {
        activeBeams.clear();
        ListTag beamsList = tag.getList("active_beams", Tag.TAG_COMPOUND);
        for (int i = 0; i < beamsList.size(); i++)
        {
            MedusaBeam beam = MedusaBeam.deserializeNBT(beamsList.getCompound(i));
            activeBeams.put(beam.getSettings().uuid(), beam);
        }
    }

    public MedusaBeam getMedusa(UUID deviceId)
    {
        return activeBeams.get(deviceId);
    }
}