package net.mistersecret312.stonemedusa.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;

public class NitricAcidBottleItem extends Item
{
    public NitricAcidBottleItem(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity)
    {
        Player player = pLivingEntity instanceof Player ? (Player)pLivingEntity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!pLevel.isClientSide) {
            DamageSource source = new DamageSource(Holder.direct(pLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(StoneMedusa.MOD_ID, "nitric_acid_inner")))),
                    null, null, null);
            pLivingEntity.hurt(source, 4f);
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (pStack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        pLivingEntity.gameEvent(GameEvent.DRINK);
        return pStack;
    }

    @Override
    public int getUseDuration(ItemStack pStack)
    {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }
}
