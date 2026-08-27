package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundInit
{
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, StoneMedusa.MODID);

	public static DeferredHolder<SoundEvent, SoundEvent> ONE_SMALL_STEP_TO_HERO = registerSoundEvent("one_small_step_to_hero");
	public static final ResourceKey<JukeboxSong> ONE_SMALL_STEP_TO_HERO_KEY = createSong("one_small_step_to_hero");

	private static ResourceKey<JukeboxSong> createSong(String name)
	{
		return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, name));
	}

	private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String sound)
	{
		return SOUNDS.register(sound, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID, sound)));
	}

	public static void register(IEventBus bus)
	{
		SOUNDS.register(bus);
	}
}
