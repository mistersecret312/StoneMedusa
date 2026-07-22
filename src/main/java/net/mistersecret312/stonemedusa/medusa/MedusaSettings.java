package net.mistersecret312.stonemedusa.medusa;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import org.joml.Vector3f;

import java.util.UUID;

public record MedusaSettings(double radius, double speed, int color,
							 MedusaPosition location, UUID uuid,
							 MedusaSource source)
{
	public static final StreamCodec<RegistryFriendlyByteBuf, MedusaSettings> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE, MedusaSettings::radius,
			ByteBufCodecs.DOUBLE, MedusaSettings::speed,
			ByteBufCodecs.INT, MedusaSettings::color,
			MedusaPosition.STREAM_CODEC, MedusaSettings::location,
			UUIDUtil.STREAM_CODEC, MedusaSettings::uuid,
			MedusaSource.STREAM_CODEC, MedusaSettings::source,
			MedusaSettings::new
	);

	public Vector3f position()
	{
		return location().position;
	}

	public ResourceKey<Level> dimension()
	{
		return location().dimension;
	}

	public record MedusaPosition(Vector3f position, ResourceKey<Level> dimension)
	{
		public static final StreamCodec<ByteBuf, MedusaPosition> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VECTOR3F, MedusaPosition::position,
				ResourceKey.streamCodec(Registries.DIMENSION), MedusaPosition::dimension,
				MedusaPosition::new
		);
	}


}
