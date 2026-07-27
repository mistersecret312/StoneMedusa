package net.mistersecret312.stonemedusa.util;

import java.util.Random;

public class StructureUtil
{
	public static int getChunkX(long levelSeed, int salt,
						 int chunkOffset, int chunkBoundsMax, int chunkBoundsMin)
	{
		if(chunkBoundsMax == 0)
			return chunkOffset;

		Random random = new Random(levelSeed + 2 + salt);
		int xBound = random.nextBoolean() ? random.nextInt(-chunkBoundsMax, -chunkBoundsMin + 1) : random.nextInt(
				chunkBoundsMin,
				chunkBoundsMax + 1);

		return chunkBoundsMax <= 0 ? chunkOffset : chunkOffset + xBound;
	}


	public static int getChunkZ(long levelSeed, int salt,
						 int chunkOffset, int chunkBoundsMax, int chunkBoundsMin)
	{
		if(chunkBoundsMax == 0)
			return chunkOffset;

		Random random = new Random(levelSeed + 3 + salt);
		int zBound = random.nextBoolean() ? random.nextInt(-chunkBoundsMax, -chunkBoundsMin + 1) : random.nextInt(
				chunkBoundsMin,
				chunkBoundsMax + 1);

		return chunkBoundsMax <= 0 ? chunkOffset : chunkOffset + zBound;
	}
}
