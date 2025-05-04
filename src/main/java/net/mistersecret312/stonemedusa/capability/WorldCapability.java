package net.mistersecret312.stonemedusa.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.init.NetworkInit;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.network.packets.MedusaWorldUpdatePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WorldCapability implements INBTSerializable<CompoundTag>
{
    public static final String PETRIFICATION_RAIN_TICKER = "petrification_rain_ticker";

    private int petrificationRainTicker = 0;
    private HashMap<Vec3,MedusaData> medusaData = new HashMap<>();

    public void tick(Level level)
    {
        Random random = new Random();
        if(level.dimensionTypeId().equals(BuiltinDimensionTypes.OVERWORLD) && this.getPetrificationRainTicker() == MedusaConfig.generation_period.get()*20)
        {
            for(Player player : level.players())
            {
                if (random.nextDouble() > 1-MedusaConfig.generation_chance.get())
                {
                    for(int i = 0; i < random.nextInt(MedusaConfig.min_generated_amount.get(), MedusaConfig.max_generated_amount.get()); i++)
                    {
                        ItemStack stack = MedusaItem.getMedusa(ItemInit.MEDUSA.get(), MedusaConfig.max_energy.get(), 5f, 20);
                        MedusaProjectile medusa = new MedusaProjectile(level, MedusaConfig.max_energy.get(), 5f, 20, false, false, "", true);
                        medusa.setItem(stack);
                        medusa.setPos(player.position().x + random.nextFloat(-72, 72), player.position().y + 350 + random.nextFloat(-72, 72), player.position().z + random.nextInt(-72, 72));
                        medusa.setDeltaMovement(new Vec3(random.nextFloat(0, 0.01f), random.nextFloat(-3f, -0.25f), random.nextFloat(0, 0.01f)));
                        level.addFreshEntity(medusa);
                    }

                }
            }
            this.setPetrificationRainTicker(0);
        } else this.setPetrificationRainTicker(this.getPetrificationRainTicker()+1);

        NetworkInit.sendPacketToDimension(level.dimension(), new MedusaWorldUpdatePacket(this.getMedusaData()));
    }

    public int getPetrificationRainTicker()
    {
        return petrificationRainTicker;
    }

    public HashMap<Vec3, MedusaData> getMedusaData()
    {
        return medusaData;
    }

    public void setPetrificationRainTicker(int petrificationRainTicker)
    {
        this.petrificationRainTicker = petrificationRainTicker;
    }

    public void setMedusaData(HashMap<Vec3, MedusaData> medusaData)
    {
        this.medusaData = medusaData;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putInt(PETRIFICATION_RAIN_TICKER, this.getPetrificationRainTicker());
        ListTag medusaData = new ListTag();
        this.medusaData.forEach((vec, data) -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putDouble("x", vec.x);
            entryTag.putDouble("y", vec.y);
            entryTag.putDouble("z", vec.z);
            entryTag.put("data", data.serializeNBT());
            medusaData.add(entryTag);
        });
        tag.put("medusaData", medusaData);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.setPetrificationRainTicker(nbt.getInt(PETRIFICATION_RAIN_TICKER));
        HashMap<Vec3, MedusaData> medusaData = new HashMap<>();
        nbt.getList("medusaData", Tag.TAG_COMPOUND).forEach(tag -> {
            CompoundTag entryTag = ((CompoundTag) tag);
            double x = entryTag.getDouble("x");
            double y = entryTag.getDouble("y");
            double z = entryTag.getDouble("z");
            Vec3 vec = new Vec3(x, y, z);
            MedusaData data = MedusaData.deserializeNBT(entryTag);

            medusaData.put(vec, data);
        });
        this.medusaData = medusaData;
    }

    public static class MedusaData
    {
        public float radius;
        public float transparency;
        public BlockPos position;
        public ResourceKey<EntityType<?>> filter;

        public MedusaData(float radius, float transparency, BlockPos position, ResourceKey<EntityType<?>> filter)
        {
            this.radius = radius;
            this.transparency = transparency;
            this.position = position;
            this.filter = filter;
        }

        public CompoundTag serializeNBT()
        {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("radius", radius);
            tag.putFloat("transparency", transparency);
            NbtUtils.writeBlockPos(position);
            tag.putString("filter", filter.location().toString());

            return tag;
        }

        public static MedusaData deserializeNBT(CompoundTag tag)
        {
            float radius = tag.getFloat("radius");
            float transparency = tag.getFloat("transparency");
            BlockPos position = NbtUtils.readBlockPos(tag);
            String filter = tag.getString("filter");
            ResourceKey<EntityType<?>> filterType = ResourceKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), ResourceLocation.parse(filter));

            return new MedusaData(radius, transparency, position, filterType);
        }

        public float getRadius()
        {
            return radius;
        }

        public float getTransparency()
        {
            return transparency;
        }

        public BlockPos getPosition()
        {
            return position;
        }

        public ResourceKey<EntityType<?>> getFilter()
        {
            return filter;
        }
    }
}
