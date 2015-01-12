package funwayguy.esm.world.gen;

import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import funwayguy.esm.core.ESM;
import funwayguy.esm.core.ESM_Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class FortressDesert extends FortressBase
{
	Block castleMaterial = Blocks.stonebrick;
	Block moatMaterial = Blocks.lava;
	//static final Block moatBaseMaterial = Blocks.sandstone;
	Block bridgeMaterial = Blocks.sandstone;
	Block grateMaterial = Blocks.iron_bars;
	
    public FortressDesert(World par1World, int chunkX, int chunkZ, BiomeGenBase biome)
    {
    	super(par1World, chunkX, chunkZ);
    	
    	ArrayList<Type> typeList = new ArrayList<Type>();
		Type[] typeArray = BiomeDictionary.getTypesForBiome(biome);
		for(int i = 0; i < typeArray.length; i++)
		{
			typeList.add(typeArray[i]);
		}
		
    	if(typeList.contains(Type.SANDY) || typeList.contains(Type.WASTELAND))
    	{
    		castleMaterial = Blocks.sandstone;
    	} else if(typeList.contains(Type.SNOWY))
    	{
    		castleMaterial = Blocks.packed_ice;
    		moatMaterial = Blocks.water;
    		grateMaterial = Blocks.glass_pane;
    	} else if(typeList.contains(Type.NETHER))
    	{
    		castleMaterial = Blocks.nether_brick;
    		grateMaterial = Blocks.nether_brick_fence;
    	} else if(typeList.contains(Type.SWAMP) || typeList.contains(Type.JUNGLE))
    	{
    		castleMaterial = Blocks.mossy_cobblestone;
    	}
    }
    
    public boolean buildStructure()
    {
    	if(ESM_Utils.isFortAt(worldObj, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), 48))
		{
    		return false;
		} else
		{
    		ESM_Utils.addFortToDB(worldObj, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0));
		}
		
		customFillWithBlocks(worldObj, 0, -3, 0, 24, 20, 32, Blocks.air, Blocks.air, false);
		
		// Moat
		customFillWithBlocks(worldObj, 0, -3, 0, 24, -1, 32, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 3, -1, 1, 21, -1, 31, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 2, -1, 2, 22, -1, 30, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 1, -1, 3, 23, -1, 29, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj, 3, -2, 1, 21, -2, 31, moatMaterial, moatMaterial, false);
		customFillWithBlocks(worldObj, 2, -2, 2, 22, -2, 30, moatMaterial, moatMaterial, false);
		customFillWithBlocks(worldObj, 1, -2, 3, 23, -2, 29, moatMaterial, moatMaterial, false);
		
		// Castle Foundation
		customFillWithBlocks(worldObj, 6, -2, 6, 18, 14, 26, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj,  4, -2,  6, 20, 14,  7, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj,  6, -2,  4,  7, 14, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 17, -2,  4, 18, 14, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj,  4, -2, 25, 20, 14, 26, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 5,  -2,  5,  8, 14,  8, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 16, -2,  5, 19, 14,  8, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 5,  -2, 24,  8, 14, 27, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 16, -2, 24, 19, 14, 27, castleMaterial, castleMaterial, false);
		
		// Hollow Out 1st Floor
		customFillWithBlocks(worldObj, 7, 0, 7, 17, 3, 25, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj,  5, 0,  6,  8, 3,  7, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 16, 0,  6, 19, 3,  7, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj,  5, 0, 25,  8, 3, 26, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 16, 0, 25, 19, 3, 26, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj,  6, 0,  5,  7, 3,  8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj,  6, 0, 24,  7, 3, 27, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 17, 0,  5, 18, 3,  8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 17, 0, 24, 18, 3, 27, Blocks.air, Blocks.air, false);
		
		// Hollow Out 2nd Floor
		customFillWithBlocks(worldObj, 7, 5, 7, 17, 8, 25, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj,  5, 5,  6,  8, 8,  7, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 16, 5,  6, 19, 8,  7, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj,  5, 5, 25,  8, 8, 26, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 16, 5, 25, 19, 8, 26, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj,  6, 5,  5,  7, 8,  8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj,  6, 5, 24,  7, 8, 27, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 17, 5,  5, 18, 8,  8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 17, 5, 24, 18, 8, 27, Blocks.air, Blocks.air, false);
		
		// Hollow Out 3rd Floor
		customFillWithBlocks(worldObj, 7, 10, 7, 17, 13, 25, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj,  5, 10,  6,  8, 13,  7, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 16, 10,  6, 19, 13,  7, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj,  5, 10, 25,  8, 13, 26, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 16, 10, 25, 19, 13, 26, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj,  6, 10,  5,  7, 13,  8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj,  6, 10, 24,  7, 13, 27, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 17, 10,  5, 18, 13,  8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 17, 10, 24, 18, 13, 27, Blocks.air, Blocks.air, false);
		
		// Entrance
		customFillWithBlocks(worldObj, 18, 0, 14, 18, 1, 18, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 18, 2, 15, 18, 2, 17, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj, 19, -1, 14, 23, -1, 18, bridgeMaterial, bridgeMaterial, false);
		
		// Wall Spikes
		customFillWithBlocks(worldObj, 5, 14, 10, 5, 15, 22, castleMaterial, castleMaterial, false);
		for(int i = 10; i <= 22; i++)
		{
			if(i % 2 == 0)
			{
				customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 5, 16, i);
				if(i == 10 || i == 22)
				{
					customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 5, 17, i);
				}
			}
		}
		
		customFillWithBlocks(worldObj, 19, 14, 10, 19, 15, 22, castleMaterial, castleMaterial, false);
		for(int i = 10; i <= 22; i++)
		{
			if(i % 2 == 0)
			{
				customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 19, 16, i);
				if(i == 10 || i == 22)
				{
					customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 19, 17, i);
				}
			}
		}
		
		customFillWithBlocks(worldObj, 10, 14, 5, 14, 15, 5, castleMaterial, castleMaterial, false);
		for(int i = 10; i <= 14; i++)
		{
			if(i % 2 == 0)
			{
				customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, i, 16, 5);
				if(i == 10 || i == 14)
				{
					customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, i, 17, 5);
				}
			}
		}
		
		customFillWithBlocks(worldObj, 10, 14, 27, 14, 15, 27, castleMaterial, castleMaterial, false);
		for(int i = 10; i <= 14; i++)
		{
			if(i % 2 == 0)
			{
				customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, i, 16, 27);
				if(i == 10 || i == 14)
				{
    				customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, i, 17, 27);
				}
			}
		}
		
		//Corner Turret 1
		customFillWithBlocks(worldObj, 3, 15, 5, 3, 16, 8, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 3, 14, 6, 3, 14, 7, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 3, 17, 5);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 3, 17, 8);
		
		customFillWithBlocks(worldObj, 5, 15, 3, 8, 16, 3, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 6, 14, 3, 7, 14, 3, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 5, 17, 3);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 8, 17, 3);

		customFillWithBlocks(worldObj, 5, 14, 4, 8, 14, 9, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 4, 14, 5, 9, 14, 8, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 4, 15, 4, 4, 16, 4, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 4, 15, 9, 4, 16, 9, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 9, 15, 4, 9, 16, 4, castleMaterial, castleMaterial, false);
		
		//Corner Turret 2
		customFillWithBlocks(worldObj, 3, 15, 24, 3, 16, 27, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 3, 14, 25, 3, 14, 26, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 3, 17, 24);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 3, 17, 27);
		
		customFillWithBlocks(worldObj, 5, 15, 29, 8, 16, 29, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 6, 14, 29, 7, 14, 29, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 5, 17, 29);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 8, 17, 29);

		customFillWithBlocks(worldObj, 5, 14, 23, 8, 14, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 4, 14, 24, 9, 14, 27, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 4, 15, 23, 4, 16, 23, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 4, 15, 28, 4, 16, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 9, 15, 28, 9, 16, 28, castleMaterial, castleMaterial, false);
		
		//Corner Turret 3
		customFillWithBlocks(worldObj, 16, 15, 29, 19, 16, 29, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 17, 14, 29, 18, 14, 29, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 16, 17, 29);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 19, 17, 29);
		
		customFillWithBlocks(worldObj, 21, 15, 24, 21, 16, 27, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 21, 14, 25, 21, 14, 26, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 21, 17, 24);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 21, 17, 27);

		customFillWithBlocks(worldObj, 16, 14, 23, 19, 14, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 15, 14, 24, 20, 14, 27, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 15, 15, 28, 15, 16, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 20, 15, 28, 20, 16, 28, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 20, 15, 23, 20, 16, 23, castleMaterial, castleMaterial, false);
		
		//Corner Turret 4
		customFillWithBlocks(worldObj, 16, 15, 3, 19, 16, 3, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 17, 14, 3, 18, 14, 3, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 16, 17, 3);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 19, 17, 3);
		
		customFillWithBlocks(worldObj, 21, 15, 5, 21, 16, 8, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 21, 14, 6, 21, 14, 7, castleMaterial, castleMaterial, false);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 21, 17, 5);
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 21, 17, 8);

		customFillWithBlocks(worldObj, 16, 14, 4, 19, 14, 9, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 15, 14, 5, 20, 14, 8, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 15, 15, 4, 15, 16, 4, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 20, 15, 4, 20, 16, 4, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 20, 15, 9, 20, 16, 9, castleMaterial, castleMaterial, false);
		
		// 1st floor stairs
		for(int i = 0; i <= 3; i ++)
		{
			customFillWithBlocks(worldObj, 7, 0, 11 + i, 8, i, 11 + i, castleMaterial, castleMaterial, false);
		}
		
		for(int i = 0; i <= 3; i ++)
		{
			customFillWithBlocks(worldObj, 7, 0, 18 + i, 8, 3 - i, 18 + i, castleMaterial, castleMaterial, false);
		}
		
		customFillWithBlocks(worldObj, 7, 0, 15, 7, 3, 17, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 7, 3, 15, 8, 3, 17, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 7, 4, 10, 8, 4, 14, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 7, 4, 18, 8, 4, 22, Blocks.air, Blocks.air, false);
		
		// 2nd floor divider
		customFillWithBlocks(worldObj, 9, 5, 10, 9, 8, 22, castleMaterial, castleMaterial, false);
		
		customFillWithBlocks(worldObj, 9, 6, 10, 9, 7, 14, grateMaterial, grateMaterial, false);
		customFillWithBlocks(worldObj, 9, 6, 18, 9, 7, 22, grateMaterial, grateMaterial, false);
		
		customFillWithBlocks(worldObj, 9, 5, 16, 9, 6, 16, Blocks.air, Blocks.air, false);
		customPlaceIronDoorAtCurrentPosition(worldObj, 9, 5, 16, 2);
		
		// 2nd floor sniper nests
		customFillWithBlocks(worldObj, 7, 5, 7, 9, 8, 9, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 7, 5, 7, 8, 8, 8, Blocks.air, Blocks.air, false);

		customFillWithBlocks(worldObj, 7, 5, 23, 9, 8, 25, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 7, 5, 24, 8, 8, 25, Blocks.air, Blocks.air, false);

		customFillWithBlocks(worldObj, 15, 5, 7, 17, 8, 9, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 16, 5, 7, 17, 8, 8, Blocks.air, Blocks.air, false);

		customFillWithBlocks(worldObj, 15, 5, 23, 17, 8, 25, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 16, 5, 24, 17, 8, 25, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj, 9, 5, 8, 9, 6, 8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 9, 5, 24, 9, 6, 24, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 15, 5, 8, 15, 6, 8, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 15, 5, 24, 15, 6, 24, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(worldObj, 5, 5, 5, 5, 6, 27, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 5, 5, 5, 19, 6, 5, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 5, 5, 27, 19, 6, 27, Blocks.air, Blocks.air, false);
		customFillWithBlocks(worldObj, 19, 5, 5, 19, 6, 27, Blocks.air, Blocks.air, false);
		
		// 2nd floor ladder
		
		customFillWithBlocks(worldObj, 17, 5, 15, 17, 11, 17, castleMaterial, castleMaterial, false);
		
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 17, 12, 16);
		
		for(int i = 5; i <= 11; i++)
		{
			int ladderMeta = 4;
			customPlaceBlockAtCurrentPosition(worldObj, Blocks.ladder, ladderMeta, 17, i, 16);
		}
		
		// 3rd floor ladder
		
		customFillWithBlocks(worldObj, 7, 10, 15, 7, 16, 17, castleMaterial, castleMaterial, false);
		customFillWithBlocks(worldObj, 6, 15, 16, 6, 16, 16, castleMaterial, castleMaterial, false);
		
		customPlaceBlockAtCurrentPosition(worldObj, castleMaterial, 0, 7, 17, 16);
		
		for(int i = 10; i <= 16; i++)
		{
			int ladderMeta = 5;
			customPlaceBlockAtCurrentPosition(worldObj, Blocks.ladder, ladderMeta, 7, i, 16);
		}
		
		// Spawners
		customGenerateSpawner(worldObj, 7, 1, 15, "Zombie");
		customGenerateSpawner(worldObj, 7, 1, 16, "Creeper");
		customGenerateSpawner(worldObj, 7, 1, 17, "Zombie");
		
		for(int i = 0; i <= 1; i++)
		{
			for(int j = 0; j <= 1; j ++)
			{
				customGenerateSpawner(worldObj, 7 + (i * 10), 5, 7 + (j * 18), "Skeleton");
			}
		}
		customGenerateSpawner(worldObj, 17, 5, 13, "Zombie");
		customGenerateSpawner(worldObj, 17, 5, 19, "Zombie");
		
		for(int i = 0; i <= 1; i++)
		{
			for(int j = 0; j <= 1; j ++)
			{
				customGenerateSpawner(worldObj, 7 + (i * 10), 10, 13 + (j * 6), "Zombie");
			}
		}
		customGenerateSpawner(worldObj, 12, 10, 8, "CaveSpider");
		customGenerateSpawner(worldObj, 12, 10, 24, "CaveSpider");
		
		for(int i = 0; i <= 1; i++)
		{
			for(int j = 0; j <= 1; j ++)
			{
				customGenerateSpawner(worldObj, 7 + (i * 10), 15, 7 + (j * 18), "Skeleton");
			}
		}
		customGenerateSpawner(worldObj, 12, 15, 14, "Zombie");
		customGenerateSpawner(worldObj, 12, 15, 18, "Zombie");
		
		// Loot
		customFillWithBlocks(worldObj, 11, 13, 15, 13, 13, 17, Blocks.iron_block, Blocks.iron_block, false);
		
		customPlaceBlockAtCurrentPosition(worldObj, Blocks.diamond_block, 0, 11, 13, 16);
		customPlaceBlockAtCurrentPosition(worldObj, Blocks.diamond_block, 0, 12, 13, 15);
		customPlaceBlockAtCurrentPosition(worldObj, Blocks.diamond_block, 0, 12, 13, 17);
		customPlaceBlockAtCurrentPosition(worldObj, Blocks.diamond_block, 0, 13, 13, 16);
		
		customPlaceBlockAtCurrentPosition(worldObj, Blocks.beacon, 0, 12, 14, 16);
		
		if(customGetBlockAtCurrentPosition(worldObj, 12, 15, 16) != Blocks.chest)
		{
			customGenerateStructureChestContents(worldObj, 12, 15, 16);
		}
		
		ESM.log.log(Level.INFO, "Desert Fort generated at (" + this.getXWithOffset(0, 0) + "," + this.getZWithOffset(0, 0) + ")");

        return true;
    }
}