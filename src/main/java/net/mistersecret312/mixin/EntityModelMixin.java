package net.mistersecret312.mixin;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AgeableListModel.class)
public class EntityModelMixin<T extends Entity>
{
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch)
    {
        if(pEntity instanceof LivingEntity living)
        {
            living.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
            {
                if (cap.isPetrified())
                    return;
            });
        }
    }

}
