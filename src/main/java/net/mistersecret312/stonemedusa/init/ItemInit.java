package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.items.DiamondBatteryItem;
import net.mistersecret312.stonemedusa.items.MedusaFragmentItem;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import net.mistersecret312.stonemedusa.items.RevivalFluidItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemInit
{
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StoneMedusa.MODID);

	public static final DeferredItem<MedusaItem> MEDUSA = ITEMS.register("medusa",
			() -> new MedusaItem(new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.EPIC)));
	public static final DeferredItem<MedusaFragmentItem> MOBIUS_PLATING = ITEMS.register("mobius_plating",
			() -> new MedusaFragmentItem(new Item.Properties().fireResistant().stacksTo(64).rarity(Rarity.EPIC)));
	public static final DeferredItem<MedusaFragmentItem> WAVEGUIDE_FILAMENT = ITEMS.register("waveguide_filament",
			() -> new MedusaFragmentItem(new Item.Properties().fireResistant().stacksTo(64).rarity(Rarity.EPIC)));
	public static final DeferredItem<MedusaFragmentItem> SPATIAL_MANIFOLD = ITEMS.register("spatial_manifold",
			() -> new MedusaFragmentItem(new Item.Properties().fireResistant().stacksTo(64).rarity(Rarity.EPIC)));
	public static final DeferredItem<MedusaFragmentItem> CRYSTAL_RECEPTACLE = ITEMS.register("crystal_receptacle",
			() -> new MedusaFragmentItem(new Item.Properties().fireResistant().stacksTo(64).rarity(Rarity.EPIC)));

	public static final DeferredItem<DiamondBatteryItem> BATTERY = ITEMS.register("diamond_battery",
			() -> new DiamondBatteryItem(new Item.Properties().fireResistant().stacksTo(1).rarity(Rarity.EPIC)));

	public static final DeferredItem<RevivalFluidItem> REVIVAL_FLUID_FLASK = ITEMS.register("revival_fluid_flask",
			() -> new RevivalFluidItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

	public static final DeferredItem<Item> ONE_SMALL_STEP_TO_HERO_DISC = ITEMS.register("one_small_step_to_hero_disc",
			() -> new Item(new Item.Properties().jukeboxPlayable(SoundInit.ONE_SMALL_STEP_TO_HERO_KEY).stacksTo(1).rarity(Rarity.RARE)));

	public static void register(IEventBus bus)
	{
		ITEMS.register(bus);
	}
}
