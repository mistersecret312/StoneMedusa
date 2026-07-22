package net.mistersecret312.stonemedusa.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.mistersecret312.stonemedusa.util.MedusaUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemKilledByPlayerCondition.class)
public class LootItemKilledByPlayerConditionMixin
{
	@Inject(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At("HEAD"), cancellable = true)
	public void test(LootContext pContext, CallbackInfoReturnable<Boolean> cir)
	{
		if(pContext != null && pContext.hasParam(LootContextParams.DAMAGE_SOURCE))
		{
			DamageSource source = pContext.getParam(LootContextParams.DAMAGE_SOURCE);
			if(source.is(MedusaUtil.PETRIFICATION_DAMAGE_TYPE))
				cir.setReturnValue(true);
		}
	}
}
