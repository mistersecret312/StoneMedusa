package net.mistersecret312.stonemedusa.medusa.components;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MedusaAttribute implements StringRepresentable
{
	ENERGY_DRAIN("energy_drain"),
	ENERGY_EFFICIENCY("energy_efficiency"),
	MAX_ENERGY_FLUX("max_energy_flux"),
	WEAR_OVER_TIME("wear_over_time");

	public static final EnumCodec<MedusaAttribute> CODEC = StringRepresentable.fromEnum(MedusaAttribute::values);

	private final String name;
	MedusaAttribute(String name)
	{
		this.name = name;
	}

	@Override
	public String getSerializedName()
	{
		return this.name;
	}
}

