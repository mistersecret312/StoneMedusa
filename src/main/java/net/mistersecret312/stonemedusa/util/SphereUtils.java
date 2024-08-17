package net.mistersecret312.stonemedusa.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class SphereUtils
{
    public static void drawTexturedSphere(PoseStack matrixStack, MultiBufferSource buffer, ResourceLocation texture, float radius, int segments, float x, float z, int packedLight, boolean lightmap2, int overlay, float[] color) {
        Matrix4f positionMatrix = matrixStack.last().pose();
        Matrix3f normalMatrix = matrixStack.last().normal();
        matrixStack.mulPose(new Quaternionf(0.0F, 1.0F, 0.0F, 45.0F));
        VertexConsumer iVertexBuilder = buffer.getBuffer(RenderType.entityTranslucentEmissive(texture));
        addBottomSphere(radius, segments, x, 0.0F, z, iVertexBuilder, positionMatrix, normalMatrix, 0.0625F, 0.0625F, false, packedLight, lightmap2, overlay, color);
        addBottomSphere(radius * 1.01F, segments, x, 0.0F, z, iVertexBuilder, positionMatrix, normalMatrix, 0.0625F, 0.0625F, false, packedLight, lightmap2, overlay, color);
        addBottomSphere(radius, segments, x, 0.0F, z, iVertexBuilder, positionMatrix, normalMatrix, 0.0625F, 0.0625F, true, packedLight, lightmap2, overlay, color);
        addBottomSphere(radius * 1.01F, segments, x, 0.0F, z, iVertexBuilder, positionMatrix, normalMatrix, 0.0625F, 0.0625F, true, packedLight, lightmap2, overlay, color);
        addSideSphere(radius, segments, x, 0.0F, z, iVertexBuilder, positionMatrix, normalMatrix, false, packedLight, lightmap2, overlay, color);
        addSideSphere(radius * 1.01F, segments, x, 0.0F, z, iVertexBuilder, positionMatrix, normalMatrix, true, packedLight, lightmap2, overlay, color);
        matrixStack.mulPose(new Quaternionf(0.0F, 1.0F, 0.0F, -45.0F));
    }

    private static void addBottomSphere(float radius, int segments, float x, float y, float z, VertexConsumer vertexBuilder, Matrix4f positionMatrix, Matrix3f normalMatrix, float u0, float v0, boolean isLower, int packedLight, boolean lightmap2, int overlay, float[] color) {
        float cube_size = 0.0625F;

        for(int j = 0; j < Math.round((float)(segments / 4)); ++j) {
            float theta = (float)(3.141592653589793D * (double)j / (double)(4 * Math.round((float)(segments / 4))));
            float sin_theta = (float)Math.sin((double)theta);
            float cos_theta = (float)Math.cos((double)theta);
            float next_theta = (float)(3.141592653589793D * (double)(j + 1) / (double)(4 * Math.round((float)(segments / 4))));
            float next_sin_theta = (float)Math.sin((double)next_theta);
            float next_cos_theta = (float)Math.cos((double)next_theta);

            for(int i = 0; i < segments; ++i) {
                double angle = -6.283185307179586D * (double)i / (double)segments;
                double next_angle = -6.283185307179586D * (double)(i + 1) / (double)segments;
                if (isLower) {
                    angle = -angle;
                    next_angle = -next_angle;
                }

                float dx = (float)((double)(radius * sin_theta) * Math.cos(angle));
                float dz = (float)((double)(radius * sin_theta) * Math.sin(angle));
                float next_i_dx = (float)((double)(radius * sin_theta) * Math.cos(next_angle));
                float next_i_dz = (float)((double)(radius * sin_theta) * Math.sin(next_angle));
                float next_j_dx = (float)((double)(radius * next_sin_theta) * Math.cos(angle));
                float next_j_dz = (float)((double)(radius * next_sin_theta) * Math.sin(angle));
                float next_ij_dx = (float)((double)(radius * next_sin_theta) * Math.cos(next_angle));
                float next_ij_dz = (float)((double)(radius * next_sin_theta) * Math.sin(next_angle));
                float[] pos_list;
                if (isLower) {
                    pos_list = new float[]{dx, -radius * cos_theta, dz, next_i_dx, -radius * cos_theta, next_i_dz, next_j_dx, -radius * next_cos_theta, next_j_dz, next_ij_dx, -radius * next_cos_theta, next_ij_dz};
                } else {
                    pos_list = new float[]{dx, radius * cos_theta, dz, next_i_dx, radius * cos_theta, next_i_dz, next_j_dx, radius * next_cos_theta, next_j_dz, next_ij_dx, radius * next_cos_theta, next_ij_dz};
                }

                float x1 = (float)Math.cos(angle - 0.7853981633974483D) * sin_theta * (float)Math.sqrt(2.0D);
                float z1 = (float)Math.sin(angle - 0.7853981633974483D) * sin_theta * (float)Math.sqrt(2.0D);
                float next_i_x1 = (float)Math.cos(next_angle - 0.7853981633974483D) * sin_theta * (float)Math.sqrt(2.0D);
                float next_i_z1 = (float)Math.sin(next_angle - 0.7853981633974483D) * sin_theta * (float)Math.sqrt(2.0D);
                float next_j_x1 = (float)Math.cos(angle - 0.7853981633974483D) * next_sin_theta * (float)Math.sqrt(2.0D);
                float next_j_z1 = (float)Math.sin(angle - 0.7853981633974483D) * next_sin_theta * (float)Math.sqrt(2.0D);
                float next_ij_x1 = (float)Math.cos(next_angle - 0.7853981633974483D) * next_sin_theta * (float)Math.sqrt(2.0D);
                float next_ij_z1 = (float)Math.sin(next_angle - 0.7853981633974483D) * next_sin_theta * (float)Math.sqrt(2.0D);
                float square_x = getSquareX(x1, z1, cube_size);
                float square_z = getSquareZ(x1, z1, cube_size);
                float next_i_square_x = getSquareX(next_i_x1, next_i_z1, cube_size);
                float next_i_square_z = getSquareZ(next_i_x1, next_i_z1, cube_size);
                float next_j_square_x = getSquareX(next_j_x1, next_j_z1, cube_size);
                float next_j_square_z = getSquareZ(next_j_x1, next_j_z1, cube_size);
                float next_ij_square_x = getSquareX(next_ij_x1, next_ij_z1, cube_size);
                float next_ij_square_z = getSquareZ(next_ij_x1, next_ij_z1, cube_size);
                float u = u0 + square_x;
                float v = v0 + square_z;
                float next_i_u = u0 + next_i_square_x;
                float next_i_v = v0 + next_i_square_z;
                float next_j_u = u0 + next_j_square_x;
                float next_j_v = v0 + next_j_square_z;
                float next_ij_u = u0 + next_ij_square_x;
                float next_ij_v = v0 + next_ij_square_z;
                float[] uv_list = new float[]{u, v, next_i_u, next_i_v, next_j_u, next_j_v, next_ij_u, next_ij_v};
                addSideSphereQuads(vertexBuilder, positionMatrix, normalMatrix, x, y, z, pos_list, uv_list, packedLight, lightmap2, overlay, color);
            }
        }

    }

    private static void addSideSphere(float radius, int segments, float x, float y, float z, VertexConsumer vertexBuilder, Matrix4f positionMatrix, Matrix3f normalMatrix, boolean isInner, int packedLight, boolean lightmap2, int overlay, float[] color) {
        for(int j = Math.round((float)(segments / 4)); j < Math.round((float)(3 * segments / 4)); ++j) {
            float theta = (float)(3.141592653589793D * (double)j / (double)(4 * Math.round((float)(segments / 4))));
            float sin_theta = (float)Math.sin((double)theta);
            float cos_theta = (float)Math.cos((double)theta);
            float next_theta = (float)(3.141592653589793D * (double)(j + 1) / (double)(4 * Math.round((float)(segments / 4))));
            float next_sin_theta = (float)Math.sin((double)next_theta);
            float next_cos_theta = (float)Math.cos((double)next_theta);

            for(int i = 0; i < segments; ++i) {
                double angle = -1.5707963267948966D - 6.283185307179586D * (double)i / (double)segments;
                double next_angle = -1.5707963267948966D - 6.283185307179586D * (double)(i + 1) / (double)segments;
                float dx = (float)((double)radius * Math.cos(angle) * (double)sin_theta);
                float dz = (float)((double)radius * Math.sin(angle) * (double)sin_theta);
                float next_i_dx = (float)((double)radius * Math.cos(next_angle) * (double)sin_theta);
                float next_i_dz = (float)((double)radius * Math.sin(next_angle) * (double)sin_theta);
                float next_j_dx = (float)((double)radius * Math.cos(angle) * (double)next_sin_theta);
                float next_j_dz = (float)((double)radius * Math.sin(angle) * (double)next_sin_theta);
                float next_ij_dx = (float)((double)radius * Math.cos(next_angle) * (double)next_sin_theta);
                float next_ij_dz = (float)((double)radius * Math.sin(next_angle) * (double)next_sin_theta);
                float[] pos_list = new float[]{dx, radius * cos_theta, dz, next_i_dx, radius * cos_theta, next_i_dz, next_j_dx, radius * next_cos_theta, next_j_dz, next_ij_dx, radius * next_cos_theta, next_ij_dz};
                float u = (float) i / (float)segments;
                float v = 1F + (float) (j - Math.round((float) (segments / 4))) / (float)Math.round((float)(segments / 2));
                float next_i_u = (float) (i + 1) / (float)segments;
                float next_j_u = (float) i / (float)segments;
                float next_j_v = 1F + (float) (j + 1 - Math.round((float) (segments / 4))) / (float)Math.round((float)(segments / 2));
                float next_ij_u = (float) (i + 1) / (float)segments;
                if (isInner) {
                    u += 1.5F;
                    next_i_u += 1.5F;
                    next_j_u += 1.5F;
                    next_ij_u += 1.5F;
                }

                float[] uv_list = new float[]{u, v, next_i_u, v, next_j_u, next_j_v, next_ij_u, next_j_v};
                addSideSphereQuads(vertexBuilder, positionMatrix, normalMatrix, x, y, z, pos_list, uv_list, packedLight, lightmap2, overlay, color);
            }
        }

    }

    private static void addSideSphereQuads(VertexConsumer vertexBuilder, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, float z, float[] pos_list, float[] uv_list, int packedLight, boolean lightmap2, int overlay, float[] color) {
        if (lightmap2) {
            vertexBuilder.vertex(positionMatrix, pos_list[0] + x, pos_list[1] + y, pos_list[2] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[0], uv_list[1]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[0], pos_list[1], pos_list[2]).endVertex();
            vertexBuilder.vertex(positionMatrix, pos_list[3] + x, pos_list[4] + y, pos_list[5] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[2], uv_list[3]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[3], pos_list[4], pos_list[5]).endVertex();
            vertexBuilder.vertex(positionMatrix, pos_list[9] + x, pos_list[10] + y, pos_list[11] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[6], uv_list[7]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[9], pos_list[10], pos_list[11]).endVertex();
            vertexBuilder.vertex(positionMatrix, pos_list[6] + x, pos_list[7] + y, pos_list[8] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[4], uv_list[5]).overlayCoords(overlay).uv2(packedLight).normal(normalMatrix, pos_list[6], pos_list[7], pos_list[8]).endVertex();
        } else {
            vertexBuilder.vertex(positionMatrix, pos_list[0] + x, pos_list[1] + y, pos_list[2] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[0], uv_list[1]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[0], pos_list[1], pos_list[2]).endVertex();
            vertexBuilder.vertex(positionMatrix, pos_list[3] + x, pos_list[4] + y, pos_list[5] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[2], uv_list[3]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[3], pos_list[4], pos_list[5]).endVertex();
            vertexBuilder.vertex(positionMatrix, pos_list[9] + x, pos_list[10] + y, pos_list[11] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[6], uv_list[7]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[9], pos_list[10], pos_list[11]).endVertex();
            vertexBuilder.vertex(positionMatrix, pos_list[6] + x, pos_list[7] + y, pos_list[8] + z).color(color[0], color[1], color[2], color[3]).uv(uv_list[4], uv_list[5]).overlayCoords(overlay).uv2(packedLight).normal(pos_list[6], pos_list[7], pos_list[8]).endVertex();
        }

    }

    private static float getSquareZ(float x1, float z1, float cube_size) {
        return (float)((double)cube_size * (Math.sqrt((double) (2.0F - x1 * x1 + z1 * z1) + 2.0D * Math.sqrt(2.0D) * (double) z1) - Math.sqrt((double) (2.0F - x1 * x1 + z1 * z1) - 2.0D * Math.sqrt(2.0D) * (double) z1)))*4.2f;
    }

    private static float getSquareX(float x1, float z1, float cube_size) {
        return (float)((double)cube_size * (Math.sqrt((double) (2.0F + x1 * x1 - z1 * z1) + 2.0D * Math.sqrt(2.0D) * (double) x1) - Math.sqrt((double) (2.0F + x1 * x1 - z1 * z1) - 2.0D * Math.sqrt(2.0D) * (double) x1)))*4.2f;
    }
}
