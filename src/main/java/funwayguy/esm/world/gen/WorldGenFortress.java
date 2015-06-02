package funwayguy.esm.world.gen;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
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
		
		int chance = Math.max(1, ESM_Settings.fortRarity);
		
		ChunkCoordinates spawnPoint = world.provider.getSpawnPoint();
		int blockX = chunkX * 16 + 8;
		int blockZ = chunkZ * 16 + 8;
		
		if(spawnPoint.getDistanceSquared(blockX, 64, blockZ) < (ESM_Settings.fortDistance * ESM_Settings.fortDistance))
		{
			return;
		}
		
		ArrayList<Type> typeList = new ArrayList<Type>();
		Type[] typeArray = BiomeDictionary.getTypesForBiome(biome);
		for(int i = 0; i < typeArray.length; i++)
		{
			typeList.add(typeArray[i]);
		}
		
		if(random.nextInt(chance) == 0 && ESM_Settings.SpawnForts)
		{
			if(typeList.contains(Type.JUNGLE))
			{
				FortressJungle fortJ = new FortressJungle(world, chunkX, chunkZ);
				if(fortJ.buildStructure())
				{
					ESM.log.log(Level.INFO, "New Jungle Fortress at (" + (chunkX * 16) + "," + (chunkZ * 16) + ")");
				}
			} else if(typeList.contains(Type.END) && world.provider.dimensionId == 1 && ESM_Settings.NewEnd)
			{
				FortressSpace fortS = new FortressSpace(world, chunkX, chunkZ);
				if(fortS.buildStructure())
				{
					ESM.log.log(Level.INFO, "New End Fortress at (" + (chunkX * 16) + "," + (chunkZ * 16) + ")");
				}
			} else if(typeList.contains(Type.NETHER))
			{
				FortressHell fortH = new FortressHell(world, chunkX, chunkZ);
				if(fortH.buildStructure())
				{
					ESM.log.log(Level.INFO, "New Hell Fortress at (" + (chunkX * 16) + "," + (chunkZ * 16) + ")");
				}
			} else if(typeList.contains(Type.SANDY) || typeList.contains(Type.WASTELAND) || typeList.contains(Type.SNOWY) || typeList.contains(Type.JUNGLE) || typeList.contains(Type.SWAMP))
			{
				FortressDesert fortD = new FortressDesert(world, chunkX, chunkZ, biome);
				if(fortD.buildStructure())
				{
					ESM.log.log(Level.INFO, "New Desert Fortress at (" + (chunkX * 16) + "," + (chunkZ * 16) + ")");
				}
			}
		}
	}
}
