package net.mistersecret312.stonemedusa.data_attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.PetrifiedArea;
import net.mistersecret312.stonemedusa.medusa.MedusaHandler;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MedusaLevelAttachment
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
    private final Map<UUID, MedusaHandler> medusaHandlers = new ConcurrentHashMap<>();
    private final List<PetrifiedArea> petrifiedAreas = new ArrayList<>();

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

    public void addHandler(MedusaHandler handler)
    {
        medusaHandlers.put(handler.medusaID, handler);
    }

    public void removeHandler(UUID uuid)
    {
        medusaHandlers.remove(uuid);
    }

    public void tickHandlers(Level level)
    {
        Iterator<Map.Entry<UUID, MedusaHandler>> iterator = medusaHandlers.entrySet().iterator();
        while (iterator.hasNext())
        {
            MedusaHandler handler = iterator.next().getValue();
            handler.tick(level);

            if (handler.shouldRemove())
                iterator.remove();
        }
    }

    public Map<UUID, MedusaHandler> getMedusaHandlers()
    {
        return medusaHandlers;
    }

    public void addPetrifiedArea(Level level, PetrifiedArea area)
    {
        if(level.isClientSide())
            return;
        if(area.removalTimeStamp() == -1 || area.removalTimeStamp() > level.getGameTime())
            this.petrifiedAreas.add(area);
    }

    public void tickAreas(Level level)
    {
        if(level.isClientSide())
            return;
        petrifiedAreas.removeIf(area -> level.getGameTime() >= area.removalTimeStamp() && area.removalTimeStamp() != -1);
    }

    public List<PetrifiedArea> getPetrifiedAreas()
    {
        return petrifiedAreas;
    }

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();
        
        ListTag beamsList = new ListTag();
        for (MedusaBeam beam : activeBeams.values())
            beamsList.add(beam.serializeNBT());
        tag.put("active_beams", beamsList);

        ListTag areasList = new ListTag();
        for (PetrifiedArea area : petrifiedAreas)
            areasList.add(area.serializeNBT());
        tag.put("petrified_areas", areasList);

        ListTag handlersList = new ListTag();
        for(MedusaHandler handler : medusaHandlers.values())
            handlersList.add(handler.serializeNBT(provider));
        tag.put("handlers", handlersList);

        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
    {
        activeBeams.clear();
        petrifiedAreas.clear();
        medusaHandlers.clear();

        ListTag beamsList = tag.getList("active_beams", Tag.TAG_COMPOUND);
        for (int i = 0; i < beamsList.size(); i++)
        {
            MedusaBeam beam = MedusaBeam.deserializeNBT(beamsList.getCompound(i));
            activeBeams.put(beam.getSettings().uuid(), beam);
        }

        ListTag areasList = tag.getList("petrified_areas", Tag.TAG_COMPOUND);
        for (int i = 0; i < areasList.size(); i++)
        {
            PetrifiedArea area = PetrifiedArea.deserializeNBT(areasList.getCompound(i));
            petrifiedAreas.add(area);
        }

        ListTag handlersList = tag.getList("handlers", Tag.TAG_COMPOUND);
        for (int i = 0; i < handlersList.size(); i++)
        {
            MedusaHandler handler = MedusaHandler.deserializeNBT(handlersList.getCompound(i), provider);
            medusaHandlers.put(handler.medusaID, handler);
        }
    }

    public MedusaBeam getMedusa(UUID deviceId)
    {
        return activeBeams.get(deviceId);
    }
}