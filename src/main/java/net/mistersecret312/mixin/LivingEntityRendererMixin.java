package net.mistersecret312.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

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
        {
            original.call(instance, entity, limbSwing, limbSwingAmount, capability.age, headYaw, headPitch);
        }
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
    }
}
