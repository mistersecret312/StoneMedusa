package net.mistersecret312.stonemedusa.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PetriBeamRenderer
{
    
    private static final List<Vector3f> CACHED_SPHERE = generateSphereQuads(45, 45);

    public static void renderBeam(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                  float currentRadius, int color)
    {
        poseStack.pushPose();
        VertexConsumer consumer = bufferSource.getBuffer(StoneRenderTypes.PETRIBEAM);

        PoseStack.Pose currentPose = poseStack.last();
        Matrix4f matrix = currentPose.pose();
        Color mainColor = new Color(color,true);

        for (Vector3f pos : CACHED_SPHERE)
        {
            float scaledX = pos.x() * currentRadius;
            float scaledY = pos.y() * currentRadius;
            float scaledZ = pos.z() * currentRadius;

            consumer.addVertex(matrix, scaledX, scaledY, scaledZ)
                    .setColor(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), mainColor.getAlpha())
                    .setNormal(pos.x(), pos.y(), pos.z());
        }

        poseStack.popPose();
        bufferSource.endBatch(StoneRenderTypes.PETRIBEAM);
    }

    private static List<Vector3f> generateSphereQuads(int rings, int sectors)
    {
        List<Vector3f> vertices = new ArrayList<>();
        float R = 1.0f / (float) (rings - 1);
        float S = 1.0f / (float) (sectors - 1);

        for(int r = 0; r < rings - 1; r++)
            for(int s = 0; s < sectors - 1; s++)
            {
                vertices.add(getPoint(r, s, R, S));
                vertices.add(getPoint(r + 1, s, R, S));
                vertices.add(getPoint(r + 1, s + 1, R, S));
                vertices.add(getPoint(r, s + 1, R, S));
            }

        return vertices;
    }

    private static Vector3f getPoint(int r, int s, float R, float S)
    {
        float y = (float) Math.sin(-Math.PI / 2 + Math.PI * r * R);
        float x = (float) Math.cos(2 * Math.PI * s * S) * (float) Math.sin(Math.PI * r * R);
        float z = (float) Math.sin(2 * Math.PI * s * S) * (float) Math.sin(Math.PI * r * R);
        return new Vector3f(x, y, z);
    }
}