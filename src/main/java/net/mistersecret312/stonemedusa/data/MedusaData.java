package net.mistersecret312.stonemedusa.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.init.BeamTypeInit;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaBeamType;
import net.mistersecret312.stonemedusa.network.ClientboundRemoveMedusaPacket;
import net.mistersecret312.stonemedusa.network.ClientboundAddMedusaPacket;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class MedusaData extends SavedData
{
	private static final String FILE_NAME = StoneMedusa.MODID + "-medusa";

	private static final String MEDUSA = "medusa";

	public HashMap<UUID, MedusaBeam> beams = new HashMap<>();

	private MinecraftServer server;

	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================

	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();

		tag.put(MEDUSA, serializeMedusaData());

		return tag;
	}

	private CompoundTag serializeMedusaData()
	{
		CompoundTag objectsTag = new CompoundTag();

		this.beams.forEach((uuid, beam) ->
			{
				objectsTag.put(uuid.toString(), beam.serializeNBT());
			});

		return objectsTag;
	}

	private void deserialize(CompoundTag tag)
	{
		deserializeMedusaData(tag.getCompound(MEDUSA));
	}

	private void deserializeMedusaData(CompoundTag tag)
	{
		for(String key : tag.getAllKeys())
		{
			CompoundTag beamTag = tag.getCompound(key);
			ResourceLocation type = ResourceLocation.parse(beamTag.getString("type"));
			MedusaBeamType<?> beamType = BeamTypeInit.REGISTRY.get(type);
			if(beamType != null)
				this.beams.put(UUID.fromString(key), beamType.deserializeNBT(tag.getCompound(key)));
		}
	}

	public void addMedusa(MedusaBeam beam)
	{
		this.beams.put(beam.getSettings().uuid(), beam);
		PacketDistributor.sendToAllPlayers(new ClientboundAddMedusaPacket(beam));
		this.setDirty();
	}

	public void removeMedusa(UUID uuid)
	{
		this.beams.remove(uuid);
		PacketDistributor.sendToAllPlayers(new ClientboundRemoveMedusaPacket(uuid));
		this.setDirty();
	}

	public MedusaBeam getMedusa(UUID uuid)
	{
		return this.beams.get(uuid);
	}

	@Override
	public void setDirty()
	{
		super.setDirty();
	}

	public MedusaData(MinecraftServer server)
	{
		this.server = server;
	}

	public static MedusaData create(MinecraftServer server)
	{
		return new MedusaData(server);
	}

	public static MedusaData load(MinecraftServer server, CompoundTag tag)
	{
		MedusaData data = create(server);

		data.server = server;
		data.deserialize(tag);

		return data;
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
	{
		tag = serialize();

		return tag;
	}

	@Nonnull
	public static MedusaData get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");

		return MedusaData.get(level.getServer());
	}

	public static Factory<MedusaData> dataFactory(MinecraftServer server)
	{
		return new Factory<>(() -> create(server), (tag, provider) -> load(server, tag));
	}

	@Nonnull
	public static MedusaData get(MinecraftServer server)
	{
		DimensionDataStorage storage = server.overworld().getDataStorage();

		return storage.computeIfAbsent(dataFactory(server), FILE_NAME);
	}
}
