package net.mistersecret312.stonemedusa;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.mistersecret312.stonemedusa.client.Layers;
import net.mistersecret312.stonemedusa.client.layers.StoneRenderLayer;
import net.mistersecret312.stonemedusa.client.renderer.ShaderedSphereRenderer;
import net.mistersecret312.stonemedusa.init.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mistersecret312.stonemedusa.item.ActiveMedusaItemProperty;
import net.mistersecret312.stonemedusa.item.DiamondBatteryItemProperty;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.item.RevivalFluidItem;
import org.slf4j.Logger;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StoneMedusa.MOD_ID)
public class StoneMedusa
{
    public static final String MOD_ID = "stonemedusa";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final TagKey<EntityType<?>> PETRIFICATION_IMMUNE = new TagKey<>(Registries.ENTITY_TYPE, new ResourceLocation(StoneMedusa.MOD_ID, "petrification_immune"));

    public StoneMedusa() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.register(modEventBus);
        ItemTabInit.register(modEventBus);
        BlockInit.register(modEventBus);
        EntityInit.register(modEventBus);
        EffectInit.register(modEventBus);

        FluidInit.register(modEventBus);
        FluidTypeInit.register(modEventBus);

        LootModifiersInit.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Layers::registerLayers);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigInit.COMMON_CONFIG, "stonemedusa-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(
        () -> {
            NetworkInit.registerPackets();
            DispenserBlock.registerBehavior(ItemInit.MEDUSA.get(), MedusaItem.getBehaviour());
            DispenserBlock.registerBehavior(ItemInit.REVIVAL_FLUID.get(), RevivalFluidItem.getBehaviour());

            BrewingRecipeRegistry.addRecipe(Ingredient.of(ItemInit.NITRIC_ACID_FLASK.get()), Ingredient.of(Items.SUGAR), new ItemStack(ItemInit.REVIVAL_FLUID.get()));
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        private static ShaderInstance petrificationRay;

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                EntityRenderers.register(EntityInit.MEDUSA.get(), ThrownItemRenderer::new);
                EntityRenderers.register(EntityInit.REVIVAL_FLUIID.get(), ThrownItemRenderer::new);

                ItemBlockRenderTypes.setRenderLayer(FluidInit.SOURCE_REVIVAL_FLUID.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(FluidInit.FLOWING_REVIVAL_FLUID.get(), RenderType.translucent());

                ItemProperties.register(ItemInit.MEDUSA.get(), new ResourceLocation(MOD_ID, "is_active"), new ActiveMedusaItemProperty());
                ItemProperties.register(ItemInit.BATTERY.get(), new ResourceLocation(MOD_ID, "battery"), new DiamondBatteryItemProperty());
                ShaderedSphereRenderer.init();
            });
        }

        @SubscribeEvent
        public static void onShaderRegister(RegisterShadersEvent event) throws IOException
        {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(),
                                       ResourceLocation.fromNamespaceAndPath(MOD_ID, "petrification_ray"),
                                       DefaultVertexFormat.NEW_ENTITY),
                    (shaderInstance -> petrificationRay = shaderInstance));
        }

        @SubscribeEvent
        @SuppressWarnings({"unchecked", "rawtypes"})
        public static void addLayers(EntityRenderersEvent.AddLayers event)
        {
            addPlayerLayer(event, "default");
            addPlayerLayer(event, "slim");

            event.getContext().getEntityRenderDispatcher().renderers.forEach((type, renderer) ->
            {
                if(renderer instanceof LivingEntityRenderer livingEntityRenderer)
                    livingEntityRenderer.addLayer(new StoneRenderLayer(livingEntityRenderer));
            });
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, String skin)
        {
            EntityRenderer<? extends Player> renderer = event.getSkin(skin);

            if (renderer instanceof LivingEntityRenderer livingRenderer)
            {
                livingRenderer.addLayer(new StoneRenderLayer(livingRenderer));
            }
        }

        public static ShaderInstance petrificationRay()
        {
            return petrificationRay;
        }
    }
}
