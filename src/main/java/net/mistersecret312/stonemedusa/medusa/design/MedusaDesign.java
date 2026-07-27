package net.mistersecret312.stonemedusa.medusa.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;

public record MedusaDesign(String name, DesignComponent hull, DesignComponent wiring,
						   DesignComponent batterySlot, DesignComponent focalPoint)
{
	public static final ResourceLocation DESIGN_LOCATION =
			ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, "medusa_design");
	public static final ResourceKey<Registry<MedusaDesign>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(DESIGN_LOCATION);

	public static final Codec<MedusaDesign> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(MedusaDesign::name),
			DesignComponent.CODEC.fieldOf("hull").forGetter(MedusaDesign::hull),
			DesignComponent.CODEC.fieldOf("wiring").forGetter(MedusaDesign::wiring),
			DesignComponent.CODEC.fieldOf("battery_slot").forGetter(MedusaDesign::batterySlot),
			DesignComponent.CODEC.fieldOf("focal_point").forGetter(MedusaDesign::focalPoint)
	).apply(instance, MedusaDesign::new));
}
