package net.mistersecret312.stonemedusa.medusa.source;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.init.BeamSourceInit;
import net.mistersecret312.stonemedusa.init.DataComponentInit;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;
import net.mistersecret312.stonemedusa.medusa.design.MedusaDesign;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.UUID;

public record InventorySource(MedusaSource parent, int slot) implements MedusaSource {

    public InventorySource(CompoundTag tag)
    {
        this(MedusaSource.load(tag.getCompound("Parent")), tag.getInt("Slot"));
    }

    @Override
    public MedusaSourceType<?> getType()
    {
        return BeamSourceInit.INVENTORY.get();
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.put("Parent", parent.save());
        tag.putInt("Slot", slot);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, InventorySource> STREAM_CODEC = StreamCodec.composite(
            MedusaSource.STREAM_CODEC, InventorySource::parent,
            ByteBufCodecs.INT, InventorySource::slot,
            InventorySource::new
    );

    @Override
    public @Nullable IMedusa resolve(Level level)
    {
		IItemHandler handler = null;
        if (parent instanceof BlockSource(BlockPos pos))
            handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        else if (parent instanceof EntitySource(UUID entityId, int clientId))
        {
            Entity entity;
            if(level.isClientSide())
                entity = level.getEntity(clientId);
            else entity = ((ServerLevel) level).getEntity(entityId);
            if(entity == null)
                return null;
            handler = entity.getCapability(Capabilities.ItemHandler.ENTITY, null);
        }
        else if(parent instanceof PlayerSource(UUID playerId))
        {
            Player player = level.getPlayerByUUID(playerId);
            if(player == null)
                return null;
            handler = player.getCapability(Capabilities.ItemHandler.ENTITY, null);
            if(slot == -1 && player.containerMenu.getCarried().getItem() instanceof IMedusa medusa)
                return medusa;
        }

        if (handler != null && slot < handler.getSlots())
        {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.getItem() instanceof IMedusa medusaItem)
                return medusaItem;
        }
        return null;
    }

    public ItemStack getMedusaItem(Level level)
    {
        IItemHandler handler = null;
        if (parent instanceof BlockSource(BlockPos pos))
            handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        else if (parent instanceof EntitySource(UUID entityId, int clientId))
        {
            Entity entity;
            if(level.isClientSide())
                entity = level.getEntity(clientId);
            else entity = ((ServerLevel) level).getEntity(entityId);
            if(entity == null)
                return null;
            handler = entity.getCapability(Capabilities.ItemHandler.ENTITY, null);
        }
        else if(parent instanceof PlayerSource(UUID playerId))
        {
            Player player = level.getPlayerByUUID(playerId);
            if(player == null)
                return null;
            handler = player.getCapability(Capabilities.ItemHandler.ENTITY, null);
            if(slot == -1 && player.containerMenu.getCarried().getItem() instanceof IMedusa)
                return player.containerMenu.getCarried();
        }

        if (handler != null && slot < handler.getSlots())
        {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.getItem() instanceof IMedusa)
                return stack;
        }
        return null;
    }

    @Override
    public void toggleMedusaItem(Level level)
    {
        IItemHandler handler = null;
        if (parent instanceof BlockSource(BlockPos pos))
            handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        else if (parent instanceof EntitySource(UUID entityId, int clientId))
        {
            Entity entity;
            if(level.isClientSide())
                entity = level.getEntity(clientId);
            else entity = ((ServerLevel) level).getEntity(entityId);
            if(entity == null)
                return;
            handler = entity.getCapability(Capabilities.ItemHandler.ENTITY, null);
        }
        else if(parent instanceof PlayerSource(UUID playerId))
        {
            Player player = level.getPlayerByUUID(playerId);
            if(player == null) return;
            handler = player.getCapability(Capabilities.ItemHandler.ENTITY, null);
            if(slot == -1 && player.containerMenu.getCarried().getItem() instanceof IMedusa)
            {
                ItemStack stack = player.containerMenu.getCarried();
                stack.set(DataComponentInit.IS_ACTIVE, !stack.getOrDefault(DataComponentInit.IS_ACTIVE, false));
            }
        }

        if (handler != null && slot < handler.getSlots())
        {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.getItem() instanceof IMedusa)
                stack.set(DataComponentInit.IS_ACTIVE, !stack.getOrDefault(DataComponentInit.IS_ACTIVE, false));
            parent.notifyClient(level);
        }
    }

    @Override
    public @Nullable  MedusaSettings.MedusaPosition providePosition(
            Level level)
    {
        Vector3f pos = null;
        if (parent instanceof BlockSource(BlockPos blockPos))
            pos = blockPos.getCenter().toVector3f();
        else if (parent instanceof EntitySource(UUID entityId, int clientId))
        {
            Entity entity;
            if(level.isClientSide())
                entity = level.getEntity(clientId);
            else entity = ((ServerLevel) level).getEntity(entityId);
            if(entity == null)
                return null;
            pos = entity.getBoundingBox().getCenter().toVector3f();
        }
        else if(parent instanceof PlayerSource(UUID playerId))
        {
            Player player = level.getPlayerByUUID(playerId);
            if(player == null)
                return null;
            pos = player.getBoundingBox().getCenter().toVector3f();
        }
        if(pos != null)
            return new MedusaSettings.MedusaPosition(pos, level.dimension());
        return null;
    }
}