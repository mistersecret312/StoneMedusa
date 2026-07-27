package net.mistersecret312.stonemedusa.medusa;

import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.data_attachment.MedusaLevelAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;

import java.util.UUID;

public interface IMedusa
{
	default MedusaSource makeSource(Level level, MedusaSource context) {return context;}
	default void emitBeam(Level level){}
	default void emitBeam(Level level, MedusaBeam beam)
	{
		MedusaLevelAttachment levelAttachment = level.getData(AttachmentTypeInit.MEDUSA);
		levelAttachment.addBeam(level, beam);
	}
	void consumeActivationEnergy(MedusaBeam beam, Level level, int energy);
	int getAvailableEnergy(MedusaBeam beam, Level level);
	default void beamTick(MedusaBeam beam, Level level){}
	default void beamStart(MedusaBeam beam, Level level){}
	default void beamEnd(MedusaBeam beam, Level level){}

	default UUID getDeviceID(Level level) { return null; }

	void consumeEnergy(Level level, MedusaSource source, int energy);
	int getMaximumEnergy(MedusaSource source, Level level);
}
