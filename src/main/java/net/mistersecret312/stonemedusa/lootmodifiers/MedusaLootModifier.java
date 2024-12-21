package net.mistersecret312.stonemedusa.lootmodifiers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class MedusaLootModifier extends LootModifier
{
    public static final Supplier<Codec<MedusaLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, MedusaLootModifier::new)));

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected MedusaLootModifier(LootItemCondition[] conditionsIn)
    {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        if(context.getQueriedLootTableId().equals(BuiltInLootTables.END_CITY_TREASURE))
            if(context.getRandom().nextDouble() > 1- MedusaConfig.generation_chance.get())
                generatedLoot.add(MedusaItem.getMedusa(ItemInit.MEDUSA.get(), medusaEnergyGeneration(0.70d, 0.90d), 5f, 20));

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.BASTION_TREASURE))
            if(context.getRandom().nextDouble() > 1- MedusaConfig.generation_chance.get())
                generatedLoot.add(MedusaItem.getMedusa(ItemInit.MEDUSA.get(), medusaEnergyGeneration(0.30d, 0.50d), 5f, 20));

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.JUNGLE_TEMPLE) ||
           context.getQueriedLootTableId().equals(BuiltInLootTables.DESERT_PYRAMID))
            if(context.getRandom().nextDouble() > 1- MedusaConfig.generation_chance.get())
                generatedLoot.add(MedusaItem.getMedusa(ItemInit.MEDUSA.get(), medusaEnergyGeneration(0.05d, 0.20d), 5f, 20));

        return generatedLoot;
    }

    public int medusaEnergyGeneration(double min, double max)
    {
        Random random = new Random();
        return (int) (MedusaConfig.max_energy.get() * random.nextDouble(min, max));
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec()
    {
        return CODEC.get();
    }
}
