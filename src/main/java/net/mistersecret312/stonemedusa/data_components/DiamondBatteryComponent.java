package net.mistersecret312.stonemedusa.data_components;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record DiamondBatteryComponent(ItemStack batteryStack)
{
    public static final Codec<DiamondBatteryComponent> CODEC =
            ItemStack.CODEC.xmap(DiamondBatteryComponent::new, DiamondBatteryComponent::batteryStack);

    public static final StreamCodec<RegistryFriendlyByteBuf, DiamondBatteryComponent> STREAM_CODEC =
            ItemStack.STREAM_CODEC.map(DiamondBatteryComponent::new, DiamondBatteryComponent::batteryStack);

    public DiamondBatteryComponent(ItemStack batteryStack)
    {
        this.batteryStack = batteryStack.copy();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof DiamondBatteryComponent(ItemStack stack))
            return ItemStack.matches(this.batteryStack, stack);

        return false;
    }

    @Override
    public int hashCode()
    {
        return ItemStack.hashItemAndComponents(this.batteryStack) * 31 + this.batteryStack.getCount();
    }
}