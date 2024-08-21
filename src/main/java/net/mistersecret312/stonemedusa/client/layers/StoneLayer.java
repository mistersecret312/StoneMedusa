package net.mistersecret312.stonemedusa.client.layers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.client.Layers;

@Mod.EventBusSubscriber(value =  Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StoneLayer extends HumanoidModel<LivingEntity>
{
    public static StoneLayer INSTANCE;

    public StoneLayer(ModelPart pRoot)
    {
        super(pRoot);
    }



    public static LayerDefinition createBodyLayer()
    {
        

        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(1f), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.getChild("head");
        PartDefinition hat = partdefinition.getChild("hat");
        PartDefinition body = partdefinition.getChild("body");
        PartDefinition right_arm = partdefinition.getChild("right_arm");
        PartDefinition left_arm = partdefinition.getChild("left_arm");
        PartDefinition right_leg = partdefinition.getChild("right_leg");
        PartDefinition left_leg = partdefinition.getChild("left_leg");


        head.addOrReplaceChild("stone_head", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                        new CubeDeformation(2f)),
                PartPose.ZERO);
        hat.addOrReplaceChild("stone_hat", CubeListBuilder.create()
                .texOffs(32, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                        new CubeDeformation(2f)),
                PartPose.ZERO);
        body.addOrReplaceChild("stone_body", CubeListBuilder.create()
                .texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F,
                        new CubeDeformation(2f)),
                PartPose.ZERO);
        right_arm.addOrReplaceChild("stone_right_arm", CubeListBuilder.create()
                .texOffs(40, 16)
                .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(2f)), PartPose.ZERO);
        left_arm.addOrReplaceChild("stone_left_arm", CubeListBuilder.create()
                .texOffs(40, 16)
                .mirror()
                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(2f)),
                PartPose.ZERO);
        right_leg.addOrReplaceChild("stone_right_leg", CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(2f)), PartPose.ZERO);
        left_leg.addOrReplaceChild("stone_left_leg", CubeListBuilder.create()
                .texOffs(0, 16)
                .mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(2f)),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    protected Iterable<ModelPart> bodyParts()
    {
        return ImmutableList.of();
    }

    @SubscribeEvent
    public static void bakeModelLayers(EntityRenderersEvent.AddLayers event)
    {
        EntityModelSet entityModelSet = event.getEntityModels();
        INSTANCE = new StoneLayer(entityModelSet.bakeLayer(Layers.STONE));
    }
}
