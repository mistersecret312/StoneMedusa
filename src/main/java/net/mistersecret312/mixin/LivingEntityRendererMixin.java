package net.mistersecret312.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends Entity>
{
    @SuppressWarnings({"MixinExtrasOperationParameters"})
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    public void setupAnim(EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, Operation<Void> original)
    {
        LazyOptional<PetrifiedCapability> capabilityLazyOptional = entity.getCapability(CapabilitiesInit.PETRIFIED);
        Optional<PetrifiedCapability> capabilityOptional = capabilityLazyOptional.resolve();
        PetrifiedCapability capability = capabilityOptional.orElse(null);
        if(capability == null || !capability.isPetrified())
            original.call(instance, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);

        if(capability != null && capability.isPetrified())
            original.call(instance, entity, capability.limbSwing, capability.limbSwingAmount, capability.getAge(), capability.headYaw, capability.headPitch);
    }

    @SuppressWarnings({"MixinExtrasOperationParameters"})
    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V"))
    public void prepModel(EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float partialTick,
                          Operation<Void> original)
    {
        LazyOptional<PetrifiedCapability> capabilityLazyOptional = entity.getCapability(CapabilitiesInit.PETRIFIED);
        Optional<PetrifiedCapability> capabilityOptional = capabilityLazyOptional.resolve();
        PetrifiedCapability capability = capabilityOptional.orElse(null);
        if(capability == null || !capability.isPetrified())
            original.call(instance, entity, limbSwing, limbSwingAmount, partialTick);

        if(capability != null && capability.isPetrified())
        {
            Entity renderEntity = entity.getType().create(entity.level());
            if(entity instanceof LivingEntity && renderEntity instanceof LivingEntity living)
            {
                LivingEntity oldEntity = (LivingEntity) entity;
                living.walkAnimation.setSpeed(capability.limbSwingAmount);
                living.tickCount = (int) capability.age;

                living.yHeadRot = oldEntity.yHeadRot;
                living.yHeadRotO = oldEntity.yHeadRotO;
                living.yBodyRot = oldEntity.yBodyRot;
                living.yBodyRotO = oldEntity.yBodyRotO;
            }
            original.call(instance, renderEntity, capability.limbSwing, capability.limbSwingAmount, partialTick);
        }
    }
}
