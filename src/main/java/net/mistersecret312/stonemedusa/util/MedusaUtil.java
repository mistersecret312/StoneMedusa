package net.mistersecret312.stonemedusa.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.source.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class MedusaUtil
{
	public static final ResourceKey<DamageType> PETRIFICATION_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE,
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "petrification"));

	public static DamageSource getPetrificationSource(DamageSources sources)
	{
		Holder<DamageType> type = sources.damageTypes.getHolderOrThrow(PETRIFICATION_DAMAGE_TYPE);
		return new DamageSource(type);
	}

	public static List<MedusaSource> scanForMedusas(ServerLevel level, AABB area)
	{
		List<MedusaSource> foundSources = new ArrayList<>();

		for(Entity entity : level.getEntities(null, area))
		{
			if(entity instanceof ItemEntity itemEntity)
				if(itemEntity.getItem().getItem() instanceof IMedusa medusa)
				{
					foundSources.add(medusa.makeSource(level, new EntitySource(entity.getUUID(), entity.getId())));
					continue;
				}

			if(entity instanceof ItemFrame itemFrame)
				if(itemFrame.getItem().getItem() instanceof IMedusa medusa)
				{
					foundSources.add(medusa.makeSource(level, new EntitySource(entity.getUUID(), entity.getId())));
					continue;
				}

			if(entity instanceof IMedusa medusa)
			{
				foundSources.add(medusa.makeSource(level, new EntitySource(entity.getUUID(), entity.getId())));
				continue;
			}

			IItemHandler itemHandler = entity.getCapability(Capabilities.ItemHandler.ENTITY, null);
			if(itemHandler != null)
			{
				MedusaSource source;
				if(entity instanceof Player player)
					source = new PlayerSource(player.getUUID());
				else source = new EntitySource(entity.getUUID(), entity.getId());
				scanItemHandler(level, itemHandler, source, foundSources);
			}
		}

		BlockPos.betweenClosedStream(area).forEach(pos ->
			{
				BlockEntity be = level.getBlockEntity(pos);
				if(be != null)
				{
					if(be instanceof IMedusa medusa)
					{
						foundSources.add(medusa.makeSource(level, new BlockSource(pos.immutable())));
						return;
					}
					IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
					if(itemHandler != null)
						scanItemHandler(level, itemHandler, new BlockSource(pos.immutable()), foundSources);
				}
			});
		return foundSources;
	}

	private static void scanItemHandler(ServerLevel level, IItemHandler handler, MedusaSource parentContext,
										List<MedusaSource> foundSources)
	{
		for(int slot = 0; slot < handler.getSlots(); slot++)
		{
			ItemStack stack = handler.getStackInSlot(slot);
			if(!stack.isEmpty() && stack.getItem() instanceof IMedusa medusa)
			{
				InventorySource invSource = new InventorySource(parentContext, slot);
				foundSources.add(medusa.makeSource(level, invSource));
			}
		}
	}
}
