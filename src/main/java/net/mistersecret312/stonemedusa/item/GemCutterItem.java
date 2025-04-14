package net.mistersecret312.stonemedusa.item;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GemCutterItem extends Item
{

    public GemCutterItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        ItemStack container = itemStack.copy();
        if(container.hurt(1, RandomSource.create(), null))
            return ItemStack.EMPTY;
        else return itemStack;
    }


}
