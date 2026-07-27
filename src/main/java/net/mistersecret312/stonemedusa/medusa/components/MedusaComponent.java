package net.mistersecret312.stonemedusa.medusa.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.mistersecret312.stonemedusa.medusa.MedusaHandler;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

public class MedusaComponent
{
	private MedusaHandler handler;

	private final String componentID;
	private final MedusaComponentType type;
	private double maxIntegrity;
	private double integrity;
	private final List<MedusaModifier> modifiers;

	public MedusaComponent(String componentID, MedusaComponentType type, double maxIntegrity,
						   List<MedusaModifier> modifiers, MedusaHandler handler)
	{
		this.componentID = componentID;
		this.type = type;
		this.maxIntegrity = maxIntegrity;
		this.integrity = maxIntegrity;
		this.modifiers = modifiers;
		this.handler = handler;
	}

	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		tag.putString("id", componentID);
		tag.putString("type", type.getSerializedName());
		tag.putDouble("max_integrity", maxIntegrity);
		tag.putDouble("integrity", integrity);

		ListTag modifiersTag = new ListTag();
		for(MedusaModifier modifier : modifiers)
			modifiersTag.add(modifier.serializeNBT());
		tag.put("modifiers", modifiersTag);

		return tag;
	}

	public static MedusaComponent deserializeNBT(CompoundTag tag, MedusaHandler handler)
	{
		String componentID = tag.getString("id");
		MedusaComponentType type = MedusaComponentType.CODEC.byName(tag.getString("type"));
		double max = tag.getDouble("max_integrity");
		double integrity = tag.getDouble("integrity");

		List<MedusaModifier> modifiers = new ArrayList<>();
		ListTag modifiersTag = tag.getList("modifiers", ListTag.TAG_COMPOUND);
		for(Tag listTag : modifiersTag)
			modifiers.add(MedusaModifier.deserializeNBT(((CompoundTag) listTag)));

		MedusaComponent component = new MedusaComponent(componentID, type, max, modifiers, handler);
		component.integrity = integrity;
		return component;
	}

	public String getComponentID()
	{
		return componentID;
	}

	public MedusaComponentType getType()
	{
		return type;
	}

	public double getMaxIntegrity()
	{
		return maxIntegrity;
	}

	public double getIntegrity()
	{
		return integrity;
	}

	public double getIntegrityPercentage()
	{
		return integrity/maxIntegrity;
	}

	public void setIntegrity(double integrity)
	{
		this.integrity = integrity;
	}

	public void setMaxIntegrity(double maxIntegrity)
	{
		this.maxIntegrity = maxIntegrity;
	}

	public List<MedusaModifier> getModifiers()
	{
		return modifiers;
	}

	public List<MedusaModifier> getActiveModifiers()
	{
		return modifiers.stream().filter(mod ->
			{
				double integrityPercentage = handler.components.get(mod.integrityComponent()).getIntegrityPercentage();
				return mod.isActive(integrityPercentage);
			}).toList();
	}

	public void damage(double damage)
	{
		this.integrity -= damage;
		if(integrity < 0)
			integrity = 0;
		if(integrity > maxIntegrity)
			integrity = maxIntegrity;
	}
}
