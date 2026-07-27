package net.mistersecret312.stonemedusa.medusa.source;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.init.BeamSourceInit;
import net.mistersecret312.stonemedusa.init.DataComponentInit;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;
import net.mistersecret312.stonemedusa.medusa.design.MedusaDesign;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface MedusaSource
{
    MedusaSourceType<?> getType();
    @Nullable
    IMedusa resolve(Level level);

    @Nullable
    default MedusaSettings.MedusaPosition providePosition(Level level)
    {
        return null;
    }

    @Nullable
    default ItemStack getMedusaItem(Level level)
    {
        return null;
    }

    default void toggleMedusaItem(Level level){}

    void saveAdditional(CompoundTag tag);

    default void notifyClient(Level level) {}

    default UUID getMedusaUUID(Level level)
    {
        ItemStack stack = getMedusaItem(level);
        if(stack != null)
            return stack.get(DataComponentInit.DEVICE_ID);
        IMedusa medusa = resolve(level);
        if(medusa != null)
            return medusa.getDeviceID(level);
        return null;
    }

    default CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        ResourceLocation id = BeamSourceInit.REGISTRY.getKey(this.getType());
        tag.putString("type", id.toString());
        this.saveAdditional(tag);
        return tag;
    }

    static MedusaSource load(CompoundTag tag)
    {
        ResourceLocation id = ResourceLocation.parse(tag.getString("type"));
        MedusaSourceType<?> type = BeamSourceInit.REGISTRY.get(id);

        if (type != null)
            return type.deserializer().apply(tag);
        return null;
    }

    StreamCodec<RegistryFriendlyByteBuf, MedusaSource> STREAM_CODEC =
            ByteBufCodecs.registry(BeamSourceInit.REGISTRY_KEY)
                         .dispatch(MedusaSource::getType, MedusaSourceType::streamCodec);
}