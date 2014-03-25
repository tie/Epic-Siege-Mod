package funwayguy.esm.world.structures;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class StructureFortress implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
		
		if(biome.biomeName.equals(BiomeGenBase.sky.biomeName))
		{
			StructureFortressEnd.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		} else if(biome.biomeName.equals(BiomeGenBase.desert.biomeName))
		{
			StructureFortressDesert.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}
}