package net.mistersecret312.stonemedusa.client.beams;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.mistersecret312.stonemedusa.client.PetriBeamRenderer;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class DefaultBeamRenderer implements MedusaBeamRenderer<MedusaBeam>
{
	@Override
	public void render(MedusaBeam beam, PoseStack poseStack, MultiBufferSource.BufferSource buffer, Camera camera, float partialTick)
	{
		double x = Mth.clampedLerp(beam.getPreviousPosition().x, beam.getSettings().position().x, partialTick);
		double y = Mth.clampedLerp(beam.getPreviousPosition().y, beam.getSettings().position().y, partialTick);
		double z = Mth.clampedLerp(beam.getPreviousPosition().z, beam.getSettings().position().z, partialTick);

		Vector3d pos = new Vector3d(x, y, z);
		double radius = Mth.clampedLerp(beam.getPreviousRadius(), beam.getCurrentRadius(), partialTick);

		float shrinkTick = beam.getShrinkingTick();
		float expandTick = beam.getExpansionTick()-40;

		float progress = 1-shrinkTick/expandTick;
		if(progress < 0)
			progress = 0;
		if(!MedusaConfig.beam_dissipate.get() || !beam.hasReachedMaxRadius())
			progress = 0;
		if(MedusaConfig.beam_dissipate.get() && beam.hasReachedMaxRadius())
			radius = beam.getSettings().radius();

		int mainColor = FastColor.ARGB32.lerp(progress,0x7861F278, 0x0061F278);
		int secColor = FastColor.ARGB32.lerp(progress,0x784C9954, 0x004C9954);
		int triColor = FastColor.ARGB32.lerp(progress,0x78245A2E, 0x00245A2E);

		poseStack.pushPose();
		poseStack.mulPose(camera.rotation().conjugate());
		poseStack.translate(pos.x-camera.getPosition().x,
				pos.y-camera.getPosition().y, pos.z-camera.getPosition().z);
		PetriBeamRenderer.renderBeam(poseStack, buffer, (float) radius, mainColor);
		PetriBeamRenderer.renderBeam(poseStack, buffer, (float) radius/1.5f, secColor);
		PetriBeamRenderer.renderBeam(poseStack, buffer, (float) radius/4f, triColor);
		poseStack.popPose();
	}
}
