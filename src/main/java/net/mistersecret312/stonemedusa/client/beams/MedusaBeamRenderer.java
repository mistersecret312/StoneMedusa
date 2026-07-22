package net.mistersecret312.stonemedusa.client.beams;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;

public interface MedusaBeamRenderer<T extends MedusaBeam>
{
    void render(T beam, PoseStack poseStack, MultiBufferSource.BufferSource buffer, Camera camera, float partialTick);
}