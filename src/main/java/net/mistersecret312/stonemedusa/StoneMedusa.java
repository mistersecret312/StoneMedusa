package net.mistersecret312.stonemedusa;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.mistersecret312.stonemedusa.client.Layers;
import net.mistersecret312.stonemedusa.client.layers.StoneRenderLayer;
import net.mistersecret312.stonemedusa.client.renderer.MedusaProjectileRenderer;
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
import org.slf4j.Logger;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.isEntityUpsideDown;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StoneMedusa.MOD_ID)
public class StoneMedusa
{
    public static final String MOD_ID = "stonemedusa";
    public static final Logger LOGGER = LogUtils.getLogger();

    public StoneMedusa() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.register(modEventBus);
        ItemTabInit.register(modEventBus);
        BlockInit.register(modEventBus);
        EntityInit.register(modEventBus);
        EffectInit.register(modEventBus);

        FluidInit.register(modEventBus);
        FluidTypeInit.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Layers::registerLayers);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(
        () -> {
            NetworkInit.registerPackets();
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                EntityRenderers.register(EntityInit.MEDUSA.get(), MedusaProjectileRenderer::new);
                EntityRenderers.register(EntityInit.REVIVAL_FLUIID.get(), ThrownItemRenderer::new);

                ItemBlockRenderTypes.setRenderLayer(FluidInit.SOURCE_REVIVAL_FLUID.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(FluidInit.FLOWING_REVIVAL_FLUID.get(), RenderType.translucent());

                ItemProperties.register(ItemInit.MEDUSA.get(), new ResourceLocation(MOD_ID, "is_active"), new ActiveMedusaItemProperty());
            });
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
    }
}
