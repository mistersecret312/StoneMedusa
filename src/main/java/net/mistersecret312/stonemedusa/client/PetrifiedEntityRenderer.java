package net.mistersecret312.stonemedusa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.data_attachment.PetrificationAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = StoneMedusa.MODID, value = Dist.CLIENT)
public class PetrifiedEntityRenderer
{
    private static final Map<UUID, LivingEntity> DUMMY_CACHE = new HashMap<>();
    
    private static boolean isRenderingDummy = false;

    public static boolean isRenderingDummy() {
        return isRenderingDummy;
    }

    public static void renderFrozenDummy(LivingEntity realEntity, RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity dummy = getOrCreateDummy(realEntity);
        if (dummy == null) return;

        dummy.setPos(realEntity.getX(), realEntity.getY(), realEntity.getZ());
        dummy.xo = realEntity.xo;
        dummy.yo = realEntity.yo;
        dummy.zo = realEntity.zo;
        realEntity.tickCount = dummy.tickCount;

        PetrificationAttachment dummyCap = dummy.getData(AttachmentTypeInit.PETRIFICATION);
        PetrificationAttachment realCap = realEntity.getData(AttachmentTypeInit.PETRIFICATION);
        dummyCap.setDepetrificationProgress(realCap.getDepetrificationProgress());

        isRenderingDummy = true;
        ((LivingEntityRenderer) event.getRenderer()).render(
                dummy,
                realEntity.getYRot(), // Entity Yaw
                0,
                event.getPoseStack(),
                event.getMultiBufferSource(),
                event.getPackedLight()
        );
        isRenderingDummy = false;

        if(realCap.getDepetrificationProgress() == 100)
            DUMMY_CACHE.remove(dummy.getUUID());
    }

    private static LivingEntity getOrCreateDummy(LivingEntity realEntity)
    {
        UUID id = realEntity.getUUID();
        if (!DUMMY_CACHE.containsKey(id))
        {
            LivingEntity dummy = (LivingEntity) realEntity.getType().create(realEntity.level());

            if (dummy != null)
            {
                CompoundTag tag = new CompoundTag();
                realEntity.saveWithoutId(tag);
                dummy.load(tag);

                dummy.tickCount = realEntity.tickCount;

                dummy.yHeadRot = realEntity.yHeadRot;
                dummy.yHeadRotO = realEntity.yHeadRot;
                dummy.yBodyRot = realEntity.yBodyRot;
                dummy.yBodyRotO = realEntity.yBodyRot;
                dummy.setXRot(realEntity.getXRot());
                dummy.setYRot(realEntity.getYRot());
                dummy.xRotO = realEntity.getXRot();
                dummy.yRotO = realEntity.getYRot();

                dummy.setYHeadRot(realEntity.getYHeadRot());
                dummy.setYBodyRot(realEntity.yBodyRot);

                dummy.walkAnimation.setSpeed(realEntity.walkAnimation.speed());

                PetrificationAttachment dummyCap = dummy.getData(AttachmentTypeInit.PETRIFICATION);
                dummyCap.setPetrificationProgress(100);

                DUMMY_CACHE.put(id, dummy);
            }
        }
        else
        {
            LivingEntity living = DUMMY_CACHE.get(id);
            living.load(realEntity.saveWithoutId(new CompoundTag()));
        }
        return DUMMY_CACHE.get(id);
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event)
    {
        if (event.getEntity() instanceof LivingEntity)
            DUMMY_CACHE.remove(event.getEntity().getUUID());
    }

    public static void removeDummy(LivingEntity entity)
    {
        DUMMY_CACHE.remove(entity.getUUID());
    }
}