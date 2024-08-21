package net.mistersecret312.stonemedusa.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.client.layers.StoneLayer;

public class Layers
{
    public static final ModelLayerLocation STONE = new ModelLayerLocation(new ResourceLocation(StoneMedusa.MOD_ID, "stone_medusa"), "main");

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(STONE, StoneLayer::createBodyLayer);
    }
}
