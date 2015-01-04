package funwayguy.esm.world.gen;

import java.util.Random;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.IWorldGenerator;
import funwayguy.esm.core.ESM;
import funwayguy.esm.core.ESM_Settings;

public class WorldGenFortress implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
		
		int chance = 1;
		
		if(ESM_Settings.fortRarity > 1)
		{
			chance = ESM_Settings.fortRarity;
		}
		
		ChunkCoordinates spawnPoint = world.provider.getSpawnPoint();
		int blockX = chunkX * 16 + 8;
		int blockZ = chunkZ * 16 + 8;
		
		if(spawnPoint.getDistanceSquared(blockX, 64, blockZ) < (ESM_Settings.fortDistance * ESM_Settings.fortDistance))
		{
			return;
		}
		
		if(random.nextInt(chance) == 0 && ESM_Settings.SpawnForts)
		{
			if(biome.biomeID == BiomeGenBase.desert.biomeID)
			{
				FortressDesert fortD = new FortressDesert(world, chunkX, chunkZ);
				if(fortD.buildStructure())
				{
					ESM.log.log(Level.INFO, "New Desert Fort at (" + (chunkX * 16) + "," + (chunkZ * 16) + ")");
				}
			} else if(biome == BiomeGenBase.sky && world.provider.dimensionId == 1 && ESM_Settings.NewEnd)
			{
				FortressSpace fortS = new FortressSpace(world, chunkX, chunkZ);
				fortS.buildStructure();
			} else if(biome == BiomeGenBase.hell && world.provider.dimensionId == -1 && ESM_Settings.NewHell)
			{
				FortressHell fortH = new FortressHell(world, chunkX, chunkZ);
				fortH.buildStructure();
			}
		}
	}
}
