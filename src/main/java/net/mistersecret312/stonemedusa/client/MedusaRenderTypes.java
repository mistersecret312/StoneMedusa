package net.mistersecret312.stonemedusa.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class MedusaRenderTypes extends RenderType
{

    public MedusaRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize,
                             boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState,
                             Runnable pClearState)
    {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType halo(ResourceLocation location)
    {
        return create("halo", DefaultVertexFormat.POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS, 256, true, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .createCompositeState(true)
        );
    }
}
