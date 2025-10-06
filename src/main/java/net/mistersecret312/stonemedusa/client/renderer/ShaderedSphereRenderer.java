package net.mistersecret312.stonemedusa.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import org.joml.Matrix4f;

public class ShaderedSphereRenderer
{
    private static VertexBuffer sphereBuffer;
    private static ShaderInstance sphereShader;

    public static void init() {
        buildSphere(3f, 32, 32);
        try {
            sphereShader = new ShaderInstance(
                    Minecraft.getInstance().getResourceManager(),
                    ResourceLocation.fromNamespaceAndPath(StoneMedusa.MOD_ID, "petrification_ray"),
                    DefaultVertexFormat.POSITION_COLOR
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sphere shader", e);
        }
    }

    private static void buildSphere(float radius, int segments, int rings) {
        BufferBuilder builder = new BufferBuilder(32768);
        builder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= rings; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i - 1) / rings);
            double z0 = Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double) i / rings);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            for (int j = 0; j <= segments; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / segments;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                builder.vertex((float) (x * zr0 * radius), (float) (y * zr0 * radius), (float) (z0 * radius))
                       .color(255, 0, 0, 255); // semi-transparent
                builder.vertex((float) (x * zr1 * radius), (float) (y * zr1 * radius), (float) (z1 * radius))
                       .color(255, 0, 0, 0);
            }
        }

        sphereBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        sphereBuffer.bind();
        sphereBuffer.upload(builder.end());
        VertexBuffer.unbind();
    }

    public static void render(Matrix4f pose) {
        if (sphereShader == null || sphereBuffer == null)
            return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // src alpha, 1 - src alpha
        RenderSystem.depthMask(false);

        RenderSystem.setShader(StoneMedusa.ClientModEvents::petrificationRay);

        sphereBuffer.bind();
        sphereBuffer.drawWithShader(pose, RenderSystem.getProjectionMatrix(), StoneMedusa.ClientModEvents.petrificationRay());
        VertexBuffer.unbind();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}
