package net.mistersecret312.stonemedusa.client;

import net.mistersecret312.stonemedusa.client.beams.MedusaBeamRenderer;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaBeamType;

import java.util.HashMap;
import java.util.Map;

public class MedusaBeamRenderers
{
	private static final Map<MedusaBeamType<?>, MedusaBeamRenderer<?>> RENDERERS = new HashMap<>();

	public static <T extends MedusaBeam> void register(MedusaBeamType<T> type, MedusaBeamRenderer<T> renderer)
	{
		RENDERERS.put(type, renderer);
	}

	@SuppressWarnings("unchecked")
	public static <T extends MedusaBeam> MedusaBeamRenderer<T> getRenderer(T beam)
	{
		return (MedusaBeamRenderer<T>) RENDERERS.get(beam.getType());
	}
}
