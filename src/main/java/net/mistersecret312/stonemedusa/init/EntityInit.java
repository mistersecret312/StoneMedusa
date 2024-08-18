package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.entity.ThrownRevivalFluidEntity;

public class EntityInit
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StoneMedusa.MOD_ID);

    public static final RegistryObject<EntityType<MedusaProjectile>> MEDUSA = ENTITIES.register("medusa",
            () -> EntityType.Builder.<MedusaProjectile>of(MedusaProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(64).setCustomClientFactory((spawnEntity, level) -> new MedusaProjectile(level)).build("medusa"));

    public static final RegistryObject<EntityType<ThrownRevivalFluidEntity>> REVIVAL_FLUIID = ENTITIES.register("revival_fluid",
            () -> EntityType.Builder.<ThrownRevivalFluidEntity>of((pEntityType, pLevel) -> new ThrownRevivalFluidEntity(pLevel), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(64).setCustomClientFactory((spawnEntity, level) -> new ThrownRevivalFluidEntity(level)).build("revival_fluid"));


    public static void register(IEventBus bus)
    {
        ENTITIES.register(bus);
    }
}
