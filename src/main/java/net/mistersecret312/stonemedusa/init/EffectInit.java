package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.effects.PetrificationEffect;

public class EffectInit
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, StoneMedusa.MOD_ID);

    public static final RegistryObject<MobEffect> PETRIFICATION = EFFECTS.register("petrification",
            () -> new PetrificationEffect(MobEffectCategory.NEUTRAL, 0x5b5b5b)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890",
                            -1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.FLYING_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160891",
                            -1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "7107DE5E-7CE8-4030-940E-514C1F160892",
                            -1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "7107DE5E-7CE8-4030-940E-514C1F160893",
                            -1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.FOLLOW_RANGE, "7107DE5E-7CE8-4030-940E-514C1F160894",
                            -1f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "7107DE5E-7CE8-4030-940E-514C1F160895",
                            10f, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static void register(IEventBus bus)
    {
        EFFECTS.register(bus);
    }
}
