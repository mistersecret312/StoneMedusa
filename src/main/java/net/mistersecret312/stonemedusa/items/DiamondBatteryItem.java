package net.mistersecret312.stonemedusa.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.init.DataComponentInit;

import java.text.NumberFormat;
import java.util.List;

public class DiamondBatteryItem extends Item implements IBorderCustom
{
    public DiamondBatteryItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        int energy = getEnergy(stack);
        if(energy > 0 && false)
        {
            setEnergy(stack, energy);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        NumberFormat percentage = NumberFormat.getPercentInstance();
        percentage.setMaximumFractionDigits(1);
        percentage.setMinimumFractionDigits(0);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        MutableComponent name = Component.translatable(this.getDescriptionId(stack));
        int color = getNameColor(stack);

        Style medusaStyle = Style.EMPTY.withColor(color);
        return name.withStyle(medusaStyle);
    }

    public static ItemStack getBattery(DiamondBatteryItem item, int energy)
    {
        ItemStack stack = new ItemStack(item);
        setEnergy(stack, energy);
        return stack;
    }

    public static int getEnergy(ItemStack stack)
    {
        return stack.getOrDefault(DataComponentInit.ENERGY, 0);
    }

    public static void setEnergy(ItemStack stack, int energy)
    {
        stack.set(DataComponentInit.ENERGY, energy);
    }

    @Override
    public int getNameColor(ItemStack stack)
    {
        float percentage = 1 - getEnergy(stack) / MedusaConfig.medusa_max_energy.get().floatValue();
		return FastColor.ARGB32.lerp(percentage, 0xff00aeff, 0xff8c8c8c);
    }

    @Override
    public Pair<Integer, Integer> getBorderColors(ItemStack stack)
    {
        float percentage = 1 - DiamondBatteryItem.getEnergy(stack) / MedusaConfig.medusa_max_energy.get().floatValue();
        int colorStart = FastColor.ARGB32.lerp(percentage, 0xff00aeff, 0xff8c8c8c);
        int colorEnd = FastColor.ARGB32.lerp(percentage, 0xff00628c, 0xff595959);

        return new Pair<>(colorStart, colorEnd);
    }
}