package net.mistersecret312.stonemedusa;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.mistersecret312.stonemedusa.client.MedusaBeamRenderers;
import net.mistersecret312.stonemedusa.client.beams.DefaultBeamRenderer;
import net.mistersecret312.stonemedusa.client.tooltip.DiamondBatteryTooltipRenderer;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.entity.ThrownMedusaEntity;
import net.mistersecret312.stonemedusa.entity.ThrownRevivalFluidEntity;
import net.mistersecret312.stonemedusa.init.*;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import net.mistersecret312.stonemedusa.items.properties.DiamondBatteryItemProperty;
import net.mistersecret312.stonemedusa.items.properties.MedusaActivatedProperty;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.source.EntitySource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.io.IOException;

import static net.neoforged.fml.loading.FMLEnvironment.dist;

@Mod(StoneMedusa.MODID)
public class StoneMedusa
{
	public static final String MODID = "stonemedusa";
	private static final Logger LOGGER = LogUtils.getLogger();

	public StoneMedusa(IEventBus modEventBus, ModContainer modContainer)
	{
		ItemInit.register(modEventBus);
		ItemTabInit.register(modEventBus);
		EntityInit.register(modEventBus);
		AttachmentTypeInit.register(modEventBus);
		DataComponentInit.register(modEventBus);
		BeamTypeInit.register(modEventBus);
		BeamSourceInit.register(modEventBus);

		modEventBus.addListener(NetworkInit::registerPackets);
		modEventBus.addListener(this::registerRegistries);
		modEventBus.addListener(this::commonSetup);

		modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG, "stonemedusa-client.toml");
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG, "stonemedusa-common.toml");
		if(dist.isClient())
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	public void registerRegistries(NewRegistryEvent event)
	{
		event.register(BeamTypeInit.REGISTRY);
		event.register(BeamSourceInit.REGISTRY);
	}

	public void commonSetup(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() -> {
			DispenserBlock.registerBehavior(ItemInit.MEDUSA, (blockSource, itemStack) ->
				{
					Level level = blockSource.level();
					Position pos = DispenserBlock.getDispensePosition(blockSource);
					Direction dir = blockSource.state().getValue(DispenserBlock.FACING);
					ProjectileItem.DispenseConfig config = ProjectileItem.DispenseConfig.DEFAULT;

					ThrownMedusaEntity medusa = new ThrownMedusaEntity(level, pos.x(), pos.y(), pos.z());
					medusa.setItem(itemStack);

					MedusaLevelAttachment medusaAttachment = level.getData(AttachmentTypeInit.MEDUSA);
					MedusaBeam beam = medusaAttachment.getMedusa(MedusaItem.getDeviceId(itemStack));
					if(beam != null)
					{
						EntitySource source = new EntitySource(medusa.getUUID(), medusa.getId());
						beam.setSource(source);
						level.syncData(AttachmentTypeInit.MEDUSA);
					}

					medusa.shoot(dir.getStepX(), (float)dir.getStepY() + 0.1F, dir.getStepZ(), config.power(), config.uncertainty());
					level.addFreshEntity(medusa);

					itemStack.shrink(1);
					return itemStack;
				});

			DispenserBlock.registerBehavior(ItemInit.REVIVAL_FLUID_FLASK, (blockSource, itemStack) ->
				{
					Level level = blockSource.level();
					Position pos = DispenserBlock.getDispensePosition(blockSource);
					Direction dir = blockSource.state().getValue(DispenserBlock.FACING);
					ProjectileItem.DispenseConfig config = ProjectileItem.DispenseConfig.DEFAULT;

					ThrownRevivalFluidEntity flask = new ThrownRevivalFluidEntity(level, pos.x(), pos.y(), pos.z());
					flask.shoot(dir.getStepX(), (float)dir.getStepY() + 0.1F, dir.getStepZ(), config.power(), config.uncertainty());
					level.addFreshEntity(flask);

					itemStack.shrink(1);
					return itemStack;
				});
		});
	}

	@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents
	{
		public static ShaderInstance petrificationInstance;

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event)
		{
			event.enqueueWork(() ->
				{
					MedusaBeamRenderers.register(BeamTypeInit.DEFAULT.get(), new DefaultBeamRenderer());
					ItemProperties.register(ItemInit.MEDUSA.get(),
							ResourceLocation.fromNamespaceAndPath(MODID, "is_active"),
							new MedusaActivatedProperty());

					ItemProperties.register(ItemInit.BATTERY.get(),
							ResourceLocation.fromNamespaceAndPath(MODID, "battery"),
							new DiamondBatteryItemProperty());

				});
		}

		@SubscribeEvent
		public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
		{
			event.registerEntityRenderer(EntityInit.THROWN_REVIVAL_FLUID.get(), ThrownItemRenderer::new);
			event.registerEntityRenderer(EntityInit.MEDUSA.get(), ThrownItemRenderer::new);
		}

		@SubscribeEvent
		public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event)
		{
			event.register(MedusaItem.DiamondBatteryTooltip.class, DiamondBatteryTooltipRenderer::new);
		}

		@SubscribeEvent
		public static void registerShaders(RegisterShadersEvent event) throws IOException
		{
			event.registerShader(new ShaderInstance(event.getResourceProvider(),
							ResourceLocation.fromNamespaceAndPath(MODID, "rendertype_petrification"),
							DefaultVertexFormat.NEW_ENTITY),
					shaderInstance -> petrificationInstance = shaderInstance);
		}
	}
}
