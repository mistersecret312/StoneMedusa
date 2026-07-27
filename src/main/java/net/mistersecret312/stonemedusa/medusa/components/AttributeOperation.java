package net.mistersecret312.stonemedusa.medusa.components;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum AttributeOperation implements StringRepresentable
{
	ADD("add"),
	MULTIPLY_BASE("multiply_base"),
	MULTIPLY_TOTAL("multiply_total");

	public static final EnumCodec<AttributeOperation> CODEC = StringRepresentable.fromEnum(AttributeOperation::values);

	private final String name;
	AttributeOperation(String name)
	{
		this.name = name;
	}

	@Override
	public String getSerializedName()
	{
		return name;
	}
}
