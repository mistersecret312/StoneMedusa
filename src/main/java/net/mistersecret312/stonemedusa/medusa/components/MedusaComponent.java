package net.mistersecret312.stonemedusa.medusa.components;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.mistersecret312.stonemedusa.StoneMedusa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mojang.text2speech.Narrator.LOGGER;

public class MedusaComponent
{
	private final String componentID;
	private final MedusaComponentType type;
	private double maxIntegrity;
	private double integrity;
	private final List<MedusaModifier> modifiers;

	public MedusaComponent(String componentID, MedusaComponentType type, double maxIntegrity, List<MedusaModifier> modifiers)
	{
		this.componentID = componentID;
		this.type = type;
		this.maxIntegrity = maxIntegrity;
		this.integrity = maxIntegrity;
		this.modifiers = modifiers;
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

	public static MedusaComponent deserializeNBT(CompoundTag tag)
	{
		String componentID = tag.getString("id");
		MedusaComponentType type = MedusaComponentType.CODEC.byName(tag.getString("type"));
		double max = tag.getDouble("max_integrity");
		double integrity = tag.getDouble("integrity");

		List<MedusaModifier> modifiers = new ArrayList<>();
		ListTag modifiersTag = tag.getList("modifiers", ListTag.TAG_COMPOUND);
		for(Tag listTag : modifiersTag)
			modifiers.add(MedusaModifier.deserializeNBT(((CompoundTag) listTag)));

		MedusaComponent component = new MedusaComponent(componentID, type, max, modifiers);
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
		return integrity/maxIntegrity * 100d;
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
		return modifiers.stream().filter(mod -> mod.isActive(getIntegrity())).toList();
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
