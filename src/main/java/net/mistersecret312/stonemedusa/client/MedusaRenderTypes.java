package net.mistersecret312.stonemedusa.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;

import java.util.function.BiFunction;

public class MedusaRenderTypes extends RenderType
{
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_PETRIFICATION_RAY = new RenderStateShard.ShaderStateShard(
            StoneMedusa.ClientModEvents::petrificationRay);

    public MedusaRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize,
                             boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState,
                             Runnable pClearState)
    {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType petrificationRay(ResourceLocation location)
    {
        return PETRIFICATION_RAY.apply(location, true);
    }

    private static final BiFunction<ResourceLocation, Boolean, RenderType> PETRIFICATION_RAY =
            Util.memoize((texture, outline) -> {
        RenderType.CompositeState rendertype$compositestate =
                RenderType.CompositeState
                        .builder()
                        .setShaderState(RENDERTYPE_PETRIFICATION_RAY)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(outline);
        return create("petrification_ray",
                      DefaultVertexFormat.NEW_ENTITY,
                      VertexFormat.Mode.QUADS,
                      256,
                      true,
                      true,
                      rendertype$compositestate);
    });


}
