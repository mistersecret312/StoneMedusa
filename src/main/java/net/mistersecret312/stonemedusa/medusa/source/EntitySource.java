package net.mistersecret312.stonemedusa.medusa.source;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.init.BeamSourceInit;
import net.mistersecret312.stonemedusa.init.DataComponentInit;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

public record EntitySource(UUID entityId, int clientId) implements MedusaSource
{
    public EntitySource(CompoundTag tag)
    {
        this(tag.getUUID("uuid"), tag.getInt("client_id"));
    }

    @Override
    public MedusaSourceType<?> getType()
    {
        return BeamSourceInit.ENTITY.get();
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.putUUID("uuid", entityId);
        tag.putInt("client_id", clientId);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySource> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, EntitySource::entityId,
            ByteBufCodecs.INT, EntitySource::clientId,
            EntitySource::new);

    @Override
    public IMedusa resolve(Level level)
    {
        Entity entity;
        if(level.isClientSide())
            entity = level.getEntity(clientId);
        else entity = ((ServerLevel) level).getEntity(entityId);

        if(entity instanceof IMedusa medusa)
            return medusa;
        if(entity instanceof ItemEntity itemEntity)
            if(itemEntity.getItem().getItem() instanceof IMedusa medusa)
                return medusa;
        if(entity instanceof ItemFrame frame)
            if(frame.getItem().getItem() instanceof IMedusa medusa)
                return medusa;
        return null;
    }

    @Override
    public @Nullable ItemStack getMedusaItem(Level level)
    {
        Entity entity;
        if(level.isClientSide())
            entity = level.getEntity(clientId);
        else entity = ((ServerLevel) level).getEntity(entityId);

        if(entity instanceof ItemEntity itemEntity)
            if(itemEntity.getItem().getItem() instanceof IMedusa)
                return itemEntity.getItem();
        if(entity instanceof ItemFrame frame)
            if(frame.getItem().getItem() instanceof IMedusa)
                return frame.getItem();

        return null;
    }

    @Override
    public void toggleMedusaItem(Level level)
    {
        ItemStack stack = getMedusaItem(level);
        if(stack == null)
            return;
        stack.set(DataComponentInit.IS_ACTIVE, !stack.getOrDefault(DataComponentInit.IS_ACTIVE, false));

        Entity entity;
        if(level.isClientSide())
            entity = level.getEntity(clientId);
        else entity = ((ServerLevel) level).getEntity(entityId);

        if(entity instanceof ItemEntity itemEntity)
            if(itemEntity.getItem().getItem() instanceof IMedusa)
                itemEntity.setItem(stack.copy());

        if(entity instanceof ItemFrame frame)
            if(frame.getItem().getItem() instanceof IMedusa)
                frame.setItem(stack);
    }

    @Override
    public MedusaSettings.MedusaPosition providePosition(Level level)
    {
        Vector3f pos = null;

        Entity entity;
        if(level.isClientSide())
            entity = level.getEntity(clientId);
        else entity = ((ServerLevel) level).getEntity(entityId);

        if(entity instanceof IMedusa)
            pos = entity.getBoundingBox().getCenter().toVector3f();
        if(entity instanceof ItemEntity itemEntity)
            if(itemEntity.getItem().getItem() instanceof IMedusa)
                pos = entity.getBoundingBox().getCenter().toVector3f();
        if(entity instanceof ItemFrame frame)
            if(frame.getItem().getItem() instanceof IMedusa)
                pos = frame.getBoundingBox().getCenter().toVector3f();

        if(pos != null)
            return new MedusaSettings.MedusaPosition(pos, level.dimension());
        return null;
    }
}