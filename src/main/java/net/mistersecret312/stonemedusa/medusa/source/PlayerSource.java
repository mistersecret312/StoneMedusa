package net.mistersecret312.stonemedusa.medusa.source;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.init.BeamSourceInit;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

public record PlayerSource(UUID playerId) implements MedusaSource
{
    public PlayerSource(CompoundTag tag)
    {
        this(tag.getUUID("uuid"));
    }

    @Override
    public MedusaSourceType<?> getType()
    {
        return BeamSourceInit.PLAYER.get();
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.putUUID("uuid", playerId);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSource> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PlayerSource::playerId,
            PlayerSource::new);

    @Override
    public IMedusa resolve(Level level)
    {
        if(!(level instanceof ServerLevel serverLevel))
            return null;

        Entity entity = serverLevel.getEntity(playerId);
        if(entity instanceof IMedusa medusa)
            return medusa;
        return null;
    }

    @Override
    public MedusaSettings.MedusaPosition providePosition(Level level)
    {
        if(!(level instanceof ServerLevel serverLevel))
            return null;
        Vector3f pos = null;

        Entity entity = serverLevel.getEntity(playerId);
        if(entity instanceof IMedusa)
            pos = entity.getBoundingBox().getCenter().toVector3f();
        if(entity instanceof ItemEntity itemEntity)
            if(itemEntity.getItem().getItem() instanceof IMedusa)
                pos = entity.getBoundingBox().getCenter().toVector3f();

        if(pos != null)
            return new MedusaSettings.MedusaPosition(pos, level.dimension());
        return null;
    }
}