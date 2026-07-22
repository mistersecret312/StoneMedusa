package net.mistersecret312.stonemedusa.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;

import java.util.function.Function;

public class StoneRenderTypes extends RenderType
{

	public StoneRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize,
							 boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState,
							 Runnable pClearState)
	{
		super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
	}

	public static final RenderType PETRIBEAM = create(
			"petribeam",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
									 .setShaderState(POSITION_COLOR_SHADER)
									 .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
									 .setCullState(NO_CULL)
									 .setLightmapState(NO_LIGHTMAP)
									 .setWriteMaskState(COLOR_WRITE)
									 .createCompositeState(false)
	);

	public static Function<ResourceLocation, RenderType> PETRIFICATION = Util.memoize(
			texture -> create("petrification",
					DefaultVertexFormat.NEW_ENTITY,
					VertexFormat.Mode.QUADS,
					256,
					true,
					false,
					CompositeState.builder()
								  .setShaderState(new ShaderStateShard(() -> StoneMedusa.ClientModEvents.petrificationInstance))
								  .setTextureState(new TextureStateShard(texture, false, false))
								  .setTransparencyState(NO_TRANSPARENCY)
								  .setCullState(NO_CULL)
								  .setLightmapState(LIGHTMAP)
								  .setOverlayState(OVERLAY)
								  .createCompositeState(true)
			));
}
