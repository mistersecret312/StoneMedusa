package net.mistersecret312.stonemedusa.worldgen.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.mistersecret312.stonemedusa.init.StructureTypeInit;

import java.util.Optional;

public class MedusaPyramid extends Structure
{
	protected final Holder<StructureTemplatePool> startPool;
	protected final Optional<ResourceLocation> startJigsawName;
	protected final int size;
	protected final HeightProvider startHeight;
	protected final Optional<Heightmap.Types> projectStartToHeightmap;
	protected final int maxDistanceFromCenter;

	public static final MapCodec<MedusaPyramid> CODEC = RecordCodecBuilder.<MedusaPyramid>mapCodec(instance ->
            instance.group(MedusaPyramid.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, MedusaPyramid::new));

    public MedusaPyramid(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
						 int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter)
    {
		super(config);
		this.startPool = startPool;
		this.startJigsawName = startJigsawName;
		this.size = size;
		this.startHeight = startHeight;
		this.projectStartToHeightmap = projectStartToHeightmap;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

	protected boolean extraSpawningChecks(GenerationContext context)
	{
		return true;
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context)
	{
		if(!extraSpawningChecks(context))
			return Optional.empty();

		int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

		ChunkPos chunkPos = context.chunkPos();
		BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

		Optional<GenerationStub> structurePiecesGenerator =
				JigsawPlacement.addPieces(
						context,
						this.startPool,
						this.startJigsawName,
						this.size,
						blockPos,
						false,
						this.projectStartToHeightmap,
						this.maxDistanceFromCenter,
						PoolAliasLookup.EMPTY,
						JigsawStructure.DEFAULT_DIMENSION_PADDING,
						JigsawStructure.DEFAULT_LIQUID_SETTINGS);

		return structurePiecesGenerator;
	}

	@Override
    public StructureType<?> type()
    {
        return StructureTypeInit.MEDUSA_PYRAMID.get();
    }
}
