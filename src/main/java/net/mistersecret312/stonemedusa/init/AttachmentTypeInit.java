package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.data_attachment.PetrificationAttachment;
import net.mistersecret312.stonemedusa.data_attachment.ResearchAttachment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class AttachmentTypeInit
{
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
			NeoForgeRegistries.Keys.ATTACHMENT_TYPES, StoneMedusa.MODID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PetrificationAttachment>> PETRIFICATION =
			ATTACHMENT_TYPES.register("petrification",
					() -> AttachmentType.builder(PetrificationAttachment::new)
										.serialize(new IAttachmentSerializer<CompoundTag, PetrificationAttachment>() {
											@Override
											public PetrificationAttachment read(IAttachmentHolder holder, CompoundTag tag,
																		   HolderLookup.Provider provider)
											{
												PetrificationAttachment capability = new PetrificationAttachment();
												capability.deserializeNBT(provider, tag);
												return capability;
											}

											@Override
											public @Nullable CompoundTag write(PetrificationAttachment attachment,
																			   HolderLookup.Provider provider)
											{
												return attachment.serializeNBT(provider);
											}
										})
										.sync(PetrificationAttachment.STREAM_CODEC)
										.build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ResearchAttachment>> RESEARCH =
			ATTACHMENT_TYPES.register("research",
					() -> AttachmentType.builder(ResearchAttachment::new)
										.serialize(new IAttachmentSerializer<CompoundTag, ResearchAttachment>() {
											@Override
											public ResearchAttachment read(IAttachmentHolder holder, CompoundTag tag,
																				HolderLookup.Provider provider)
											{
												ResearchAttachment capability = new ResearchAttachment();
												capability.deserializeNBT(provider, tag);
												return capability;
											}

											@Override
											public @Nullable CompoundTag write(ResearchAttachment attachment,
																			   HolderLookup.Provider provider)
											{
												return attachment.serializeNBT(provider);
											}
										})
										.sync(ResearchAttachment.STREAM_CODEC)
										.build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<MedusaLevelAttachment>> MEDUSA =
			ATTACHMENT_TYPES.register("medusas",
					() -> AttachmentType.builder(MedusaLevelAttachment::new)
										.serialize(new IAttachmentSerializer<CompoundTag, MedusaLevelAttachment>() {
											@Override
											public MedusaLevelAttachment read(IAttachmentHolder holder, CompoundTag tag,
																				HolderLookup.Provider provider)
											{
												MedusaLevelAttachment capability = new MedusaLevelAttachment();
												capability.deserializeNBT(provider, tag);
												return capability;
											}

											@Override
											public @Nullable CompoundTag write(MedusaLevelAttachment attachment,
																			   HolderLookup.Provider provider)
											{
												return attachment.serializeNBT(provider);
											}
										})
										.sync(MedusaLevelAttachment.STREAM_CODEC)
										.build());

	public static void register(IEventBus eventBus)
	{
		ATTACHMENT_TYPES.register(eventBus);
	}
}
