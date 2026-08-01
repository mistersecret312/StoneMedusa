package net.mistersecret312.stonemedusa.data_attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;

public class ResearchAttachment implements INBTSerializable<CompoundTag>
{
	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchAttachment> STREAM_CODEC = StreamCodec.of(
			(buf, attach) -> buf.writeNbt(attach.serializeNBT(buf.registryAccess())),
			(buf) -> {
				ResearchAttachment attachment = new ResearchAttachment();
				CompoundTag tag = buf.readNbt();
				if(tag != null)
					attachment.deserializeNBT(buf.registryAccess(), tag);
				return attachment;
			}
	);

	public HashMap<ResourceLocation, CompoundTag> researches = new HashMap<>();
	public ResourceLocation latestResearch;

	public void addResearch(Player player, ResourceLocation key, CompoundTag tag)
	{
		this.researches.put(key, tag);
		this.latestResearch = key;
		player.syncData(AttachmentTypeInit.RESEARCH);
	}

	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		CompoundTag tag = new CompoundTag();
		if(latestResearch != null)
			tag.putString("latest_research", latestResearch.toString());
		researches.forEach((rl, rTag) -> tag.put(rl.toString(), rTag));
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
	{
		if(tag.contains("latest_research"))
			this.latestResearch = ResourceLocation.parse(tag.getString("latest_research"));
		for(String key : tag.getAllKeys())
		{
			CompoundTag research = tag.getCompound(key);
			this.researches.put(ResourceLocation.parse(key), research);
		}
	}
}
