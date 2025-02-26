package net.mistersecret312.stonemedusa.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;

public class DiamondBatteryItem extends Item
{
    public static final String ENERGY = "energy";

    public DiamondBatteryItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        NumberFormat percentage = NumberFormat.getPercentInstance();
        percentage.setMaximumFractionDigits(1);
        percentage.setMinimumFractionDigits(0);
        pTooltipComponents.add(Component.translatable("medusa.charge").withStyle(ChatFormatting.GREEN).append(percentage.format((double)this.getEnergy(pStack) / (double)MedusaConfig.max_energy.get())));
    }


    public static ItemStack getBattery(DiamondBatteryItem item, int energy)
    {
        ItemStack stack = new ItemStack(item);
        item.setEnergy(stack, energy);
        return stack;
    }

    public static int getEnergy(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(ENERGY))
            return stack.getTag().getInt(ENERGY);
        else return 0;
    }

    public static void setEnergy(ItemStack stack, int energy)
    {
        stack.getOrCreateTag().putInt(ENERGY, Math.min(energy, MedusaConfig.max_energy.get()));
    }
}
