package net.mistersecret312.stonemedusa.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.data_components.DiamondBatteryComponent;
import net.mistersecret312.stonemedusa.entity.ThrownMedusaEntity;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.init.DataComponentInit;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.medusa.IMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.source.EntitySource;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class MedusaItem extends Item implements IMedusa,IBorderCustom
{
	public MedusaItem(Properties properties)
	{
		super(properties);
	}

	public static ItemStack getMedusa(Item item, int charge)
	{
		ItemStack stack = new ItemStack(item);
		ItemStack battery = DiamondBatteryItem.getBattery(ItemInit.BATTERY.get(), charge);
		stack.set(DataComponentInit.BATTERY, new DiamondBatteryComponent(battery));

		return stack;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
	{
		if(!stack.has(DataComponentInit.DEVICE_ID))
			stack.set(DataComponentInit.DEVICE_ID, UUID.randomUUID());
		if(!level.isClientSide())
		{
			MedusaLevelAttachment data = level.getData(AttachmentTypeInit.MEDUSA);
			MedusaBeam beam = data.getMedusa(getDeviceId(stack));
			if(beam == null)
			{
				setActive(stack, false);
				stack.remove(DataComponentInit.TICK_DELAY);
				stack.remove(DataComponentInit.START_DELAY);
			}
		}
	}

	@Override
	public Component getName(ItemStack stack)
	{
		MutableComponent name = Component.translatable(this.getDescriptionId(stack));
		int color = getNameColor(stack);

		Style medusaStyle = Style.EMPTY.withColor(color);
		return name.withStyle(medusaStyle);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
//		if(stack.has(DataComponentInit.BATTERY))
//			return Optional.of(new DiamondBatteryTooltip(stack.get(DataComponentInit.BATTERY)));
		return Optional.empty();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
	{
		ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
		if(pPlayer.isShiftKeyDown())
		{
			InteractionHand otherHand = pUsedHand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
			if(itemstack.has(DataComponentInit.BATTERY))
			{
				DiamondBatteryComponent battery = itemstack.get(DataComponentInit.BATTERY);
				if(battery != null)
				{
					itemstack.remove(DataComponentInit.BATTERY);
					if(!pPlayer.addItem(battery.batteryStack()))
						pPlayer.drop(battery.batteryStack(), true, false);
				}
			}
			else if(pPlayer.getItemInHand(otherHand).getItem() instanceof DiamondBatteryItem
							&& !itemstack.has(DataComponentInit.BATTERY))
			{
				ItemStack battery = pPlayer.getItemInHand(otherHand);
				itemstack.set(DataComponentInit.BATTERY, new DiamondBatteryComponent(battery));
				pPlayer.setItemInHand(otherHand, ItemStack.EMPTY);
			}
			return InteractionResultHolder.consume(itemstack);
		}

		pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
				SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!pLevel.isClientSide)
		{
			ThrownMedusaEntity medusa = new ThrownMedusaEntity(pLevel, pPlayer);
			medusa.setItem(itemstack);
			medusa.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1F, 0F);

			UUID deviceId = MedusaItem.getDeviceId(itemstack);
			MedusaBeam beam = pLevel.getData(AttachmentTypeInit.MEDUSA).getMedusa(deviceId);
			if(beam != null)
			{
				beam.setSource(new EntitySource(medusa.getUUID(), medusa.getId()));
				pLevel.syncData(AttachmentTypeInit.MEDUSA);
			}
			pLevel.addFreshEntity(medusa);
		}

		pPlayer.awardStat(Stats.ITEM_USED.get(this));
		if (!pPlayer.getAbilities().instabuild)
			itemstack.shrink(1);
		else itemstack.set(DataComponentInit.DEVICE_ID, UUID.randomUUID());

		return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
	}

	@Override
	public void beamStart(MedusaBeam beam, Level level)
	{
		MedusaSource source = beam.getSettings().source();
		source.toggleMedusaItem(level);
	}

	@Override
	public void beamTick(MedusaBeam beam, Level level)
	{
		MedusaSource source = beam.getSettings().source();
		ItemStack stack = source.getMedusaItem(level);
		if(stack == null)
			return;

		stack.set(DataComponentInit.TICK_DELAY, beam.getDelayTicker());
		stack.set(DataComponentInit.START_DELAY, beam.getMaxDelay());
	}

	@Override
	public void beamEnd(MedusaBeam beam, Level level)
	{
		MedusaSource source = beam.getSettings().source();
		source.toggleMedusaItem(level);
	}

	@Override
	public void consumeActivationEnergy(MedusaBeam beam, Level level, int energy)
	{
		MedusaSource source = beam.getSettings().source();
		ItemStack stack = source.getMedusaItem(level);
		if(stack != null && stack.has(DataComponentInit.BATTERY))
		{
			DiamondBatteryComponent batteryComponent = stack.get(DataComponentInit.BATTERY);
			if(batteryComponent == null)
				return;
			ItemStack battery = batteryComponent.batteryStack();
			int newEnergy = DiamondBatteryItem.getEnergy(battery)-energy;
			if(newEnergy < 0)
			{
				newEnergy = 0;
				beam.end(level);
			}
			DiamondBatteryItem.setEnergy(battery, newEnergy);
			stack.set(DataComponentInit.BATTERY, new DiamondBatteryComponent(battery));
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged;
	}

	@Override
	public int getAvailableEnergy(MedusaBeam beam, Level level)
	{
		ItemStack stack = beam.getSettings().source().getMedusaItem(level);
		if(stack != null && stack.has(DataComponentInit.BATTERY))
		{
			DiamondBatteryComponent component = stack.get(DataComponentInit.BATTERY);
			if(component != null)
				return DiamondBatteryItem.getEnergy(component.batteryStack());
		}
		return 0;
	}

	public static boolean isActive(@NotNull ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.IS_ACTIVE, false);
	}

	public static UUID getDeviceId(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.DEVICE_ID, UUID.randomUUID());
	}

	public void setActive(ItemStack stack, boolean active)
	{
		stack.set(DataComponentInit.IS_ACTIVE, active);
	}

	@Override
	public int getNameColor(ItemStack stack)
	{
		int color = 0xff8c8c8c;

		ItemStack diamondBattery = stack.getOrDefault(DataComponentInit.BATTERY, new DiamondBatteryComponent(ItemStack.EMPTY)).batteryStack();
		if(diamondBattery.getItem() instanceof IBorderCustom custom)
			color = custom.getNameColor(diamondBattery);

		float maxDelay = stack.getOrDefault(DataComponentInit.START_DELAY, -1);
		float ticker = stack.getOrDefault(DataComponentInit.TICK_DELAY, -1);
		boolean isActive = stack.getOrDefault(DataComponentInit.IS_ACTIVE, false);
		if(maxDelay != -1 && ticker != -1)
		{
			if(ticker != maxDelay)
			{
				float percentage = 1 - ticker / maxDelay;
				color = FastColor.ARGB32.lerp(percentage, color, 0xff4dff3d);
			}
		}
		else if(isActive)
			color = 0xff4dff3d;

		return color;
	}

	@Override
	public Pair<Integer, Integer> getBorderColors(ItemStack stack)
	{
		ItemStack diamondBattery = stack.getOrDefault(DataComponentInit.BATTERY, new DiamondBatteryComponent(ItemStack.EMPTY)).batteryStack();
		int startChargeColor = 0xff8c8c8c;
		int endChargeColor = 0xff595959;
		if(diamondBattery.getItem() instanceof IBorderCustom custom)
		{
			startChargeColor = custom.getBorderColors(diamondBattery).getFirst();
			endChargeColor = custom.getBorderColors(diamondBattery).getSecond();
		}

		float maxDelay = stack.getOrDefault(DataComponentInit.START_DELAY, -1);
		float ticker = stack.getOrDefault(DataComponentInit.TICK_DELAY, -1);
		boolean isActive = stack.getOrDefault(DataComponentInit.IS_ACTIVE, false);
		Pair<Integer, Integer> startEndPair = new Pair<>(startChargeColor, endChargeColor);
		if(maxDelay != -1 && ticker != -1)
		{
			if(ticker != maxDelay)
			{
				float percentage = 1 - ticker / maxDelay;
				int colorStart = FastColor.ARGB32.lerp(percentage, startChargeColor, 0xff4dff3d);
				int colorEnd = FastColor.ARGB32.lerp(percentage, endChargeColor, 0xff208c20);

				startEndPair = new Pair<>(colorStart, colorEnd);
			}
		}
		else if(isActive)
			startEndPair = new Pair<>(0xff4dff3d, 0xff208c20);

		return startEndPair;
	}

	public record DiamondBatteryTooltip(DiamondBatteryComponent stack) implements TooltipComponent {}
}
