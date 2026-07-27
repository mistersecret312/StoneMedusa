package net.mistersecret312.stonemedusa.medusa.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

public record MedusaModifier(MedusaAttribute attribute, double amount, AttributeOperation operation,
							MedusaComponentType integrityComponent, double minIntegrity, double maxIntegrity)
{
	public static final Codec<MedusaModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MedusaAttribute.CODEC.fieldOf("attribute").forGetter(MedusaModifier::attribute),
			Codec.DOUBLE.fieldOf("amount").forGetter(MedusaModifier::amount),
			AttributeOperation.CODEC.fieldOf("operation").forGetter(MedusaModifier::operation),
			MedusaComponentType.CODEC.fieldOf("integrity_component").forGetter(MedusaModifier::integrityComponent),
			Codec.DOUBLE.optionalFieldOf("min_integrity", 0.0d).forGetter(MedusaModifier::minIntegrity),
			Codec.DOUBLE.optionalFieldOf("max_integrity", 1.0d).forGetter(MedusaModifier::maxIntegrity)
	).apply(instance, MedusaModifier::new));

	public boolean isActive(double integrity)
	{
		return integrity >= minIntegrity && integrity <= maxIntegrity;
	}

	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		tag.putString("attribute", attribute().getSerializedName());
		tag.putDouble("amount", amount());
		tag.putString("operation", operation().getSerializedName());
		tag.putString("integrity_component", integrityComponent().getSerializedName());
		tag.putDouble("min_integrity", minIntegrity());
		tag.putDouble("max_integrity", maxIntegrity());
		return tag;
	}

	public static MedusaModifier deserializeNBT(CompoundTag tag)
	{
		MedusaAttribute attribute = MedusaAttribute.CODEC.byName(tag.getString("attribute"));
		double amount = tag.getDouble("amount");
		AttributeOperation operation = AttributeOperation.CODEC.byName(tag.getString("operation"));
		MedusaComponentType integrityComponent = MedusaComponentType.CODEC.byName(tag.getString("integrity_component"));
		double min = tag.getDouble("min_integrity");
		double max = tag.getDouble("max_integrity");

		return new MedusaModifier(attribute, amount, operation, integrityComponent, min, max);
	}
}
