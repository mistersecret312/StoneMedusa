package net.mistersecret312.stonemedusa.medusa.source;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mistersecret312.stonemedusa.init.BeamSourceInit;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaSettings;

public record BlockSource(BlockPos pos) implements MedusaSource
{
    public BlockSource(CompoundTag tag)
    {
        this(BlockPos.of(tag.getLong("pos")));
    }

    @Override
    public MedusaSourceType<?> getType()
    {
        return BeamSourceInit.BLOCK.get();
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.putLong("pos", pos.asLong());
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockSource> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockSource::pos,
            BlockSource::new);

    @Override
    public IMedusa resolve(Level level)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof IMedusa medusa)
            return medusa;
        return null;
    }

    @Override
    public void notifyClient(Level level)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity != null)
            blockEntity.setChanged();
    }

    @Override
    public MedusaSettings.MedusaPosition providePosition(Level level)
    {
        return new MedusaSettings.MedusaPosition(pos.getCenter().toVector3f(), level.dimension());
    }
}