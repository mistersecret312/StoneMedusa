package net.mistersecret312.stonemedusa.medusa.components;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MedusaComponentType implements StringRepresentable
{
	HULL("hull"),
	WIRING("wiring"),
	BATTERY_SLOT("battery_slot"),
	FOCAL_POINT("focal_point");

	public static final EnumCodec<MedusaComponentType> CODEC = StringRepresentable.fromEnum(MedusaComponentType::values);

	private final String name;
	MedusaComponentType(String name)
	{
		this.name = name;
	}

	@Override
	public String getSerializedName()
	{
		return this.name;
	}
}
