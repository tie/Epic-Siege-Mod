package funwayguy.esm.world.gen;

import funwayguy.esm.core.ESM_Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class FortressSpace extends FortressBase
{
	static final Block baseBlock = Blocks.obsidian;
	static final Block detailBlock = Blocks.end_stone;
	
	public FortressSpace(World par1World, int chunkX, int chunkZ)
	{
		super(par1World, chunkX, chunkZ);
		originY = 64;
		orientation = 0;
	}
	
	@Override
	public boolean buildStructure()
	{
    	if(ESM_Utils.isFortAt(worldObj, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), 32))
		{
    		return false;
		} else
		{
    		ESM_Utils.addFortToDB(worldObj, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0));
		}
    	
		this.buildTower(worldObj, -12, 0, -12, 2);
		this.buildTower(worldObj, 12, 0, 12, 2);
		this.buildTower(worldObj, 12, 0, -12, 2);
		this.buildTower(worldObj, -12, 0, 12, 2);
		
		this.buildTower(worldObj, 0, 0, 0, 4);
		
		this.buildWall(worldObj, -7, 0, -12, 1, 15, 1);
		this.buildWall(worldObj, -12, 0, -7, 0, 15, 1);
		
		this.buildWall(worldObj, 7, 0, 12, 3, 15, 1);
		this.buildWall(worldObj, 12, 0, 7, 2, 15, 1);
		
		int dragonX = this.getXWithOffset(0, 0);
		int dragonY = 96;
		int dragonZ = this.getZWithOffset(0, 0);
		
		EntityDragon var4 = new EntityDragon(worldObj);
		var4.setLocationAndAngles(dragonX, dragonY, dragonZ, worldObj.rand.nextFloat() * 360.0F, 0.0F);
		worldObj.spawnEntityInWorld(var4);
		
		System.out.println("Fort Generated Successfully!");
		
		return true;
	}
	
	protected void buildTower(World world, int x, int y, int z, int floors)
	{
		if(floors < 1)
		{
			return;
		}
		
		//Entrance Floor - Base
		
		customFillWithBlocks(world, x - 4, y, z - 2, x + 4, y, z + 2, baseBlock, baseBlock, false);
		customFillWithBlocks(world, x - 3, y, z - 3, x + 3, y, z + 3, baseBlock, baseBlock, false);
		customFillWithBlocks(world, x - 2, y, z - 4, x + 2, y, z + 4, baseBlock, baseBlock, false);
		
		customFillWithBlocks(world, x - 4, y, z, x + 4, y, z, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x, y, z - 4, x, y, z + 4, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x - 1, y, z - 1, x + 1, y, z + 1, detailBlock, detailBlock, false);
		
		//Entrance Floor - Corners
		
		for(int i = -4; i <= 4; i += 8)
		{
			for(int k = -2; k <= 2; k += 4)
			{
				customFillWithBlocks(world, x + i, y, z + k, x + i, y + 4, z + k, baseBlock, baseBlock, false);
			}
		}
		
		for(int i = -3; i <= 3; i += 6)
		{
			for(int k = -3; k <= 3; k += 6)
			{
				customFillWithBlocks(world, x + i, y, z + k, x + i, y + 4, z + k, baseBlock, baseBlock, false);
			}
		}
		
		for(int i = -2; i <= 2; i += 4)
		{
			for(int k = -4; k <= 4; k += 8)
			{
				customFillWithBlocks(world, x + i, y, z + k, x + i, y + 4, z + k, baseBlock, baseBlock, false);
			}
		}
		
		for(int i = -2; i <= 2; i += 4)
		{
			for(int k = -4; k <= 4; k += 8)
			{
				customFillWithBlocks(world, x + i, y, z + k, x + i, y + 4, z + k, baseBlock, baseBlock, false);
			}
		}
		
		//Entrance Floor - Doorways
		
		for(int i = -1; i <= 1; i += 2)
		{
			for(int k = -4; k <= 4; k += 8)
			{
				customFillWithBlocks(world, x + i, y, z + k, x + i, y + 4, z + k, baseBlock, baseBlock, false);
			}
		}
		for(int i = -4; i <= 4; i += 8)
		{
			for(int k = -1; k <= 1; k += 2)
			{
				customFillWithBlocks(world, x + i, y, z + k, x + i, y + 4, z + k, baseBlock, baseBlock, false);
			}
		}
		
		for(int i = -1; i <= 1; i += 2)
		{
			for(int k = -4; k <= 4; k += 8)
			{
				customFillWithBlocks(world, x + i, y + 1, z + k, x + i, y + 3, z + k, detailBlock, detailBlock, false);
			}
		}
		for(int i = -4; i <= 4; i += 8)
		{
			for(int k = -1; k <= 1; k += 2)
			{
				customFillWithBlocks(world, x + i, y + 1, z + k, x + i, y + 3, z + k, detailBlock, detailBlock, false);
			}
		}
		
		customFillWithBlocks(world, x - 4, y, z, x - 4, y + 4, z, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x + 4, y, z, x + 4, y + 4, z, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x, y, z - 4, x, y + 4, z - 4, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x, y, z + 4, x, y + 4, z + 4, detailBlock, detailBlock, false);
		
		customFillWithBlocks(world, x - 4, y + 1, z, x - 4, y + 3, z, Blocks.air, Blocks.air, false);
		customFillWithBlocks(world, x + 4, y + 1, z, x + 4, y + 3, z, Blocks.air, Blocks.air, false);
		customFillWithBlocks(world, x, y + 1, z - 4, x, y + 3, z - 4, Blocks.air, Blocks.air, false);
		customFillWithBlocks(world, x, y + 1, z + 4, x, y + 3, z + 4, Blocks.air, Blocks.air, false);
		
		//Middle Floors
		for(int currentFloor = 1; currentFloor < floors; currentFloor++)
		{
			//Middle Floor - Base
			
			customFillWithBlocks(world, x - 4, y + (currentFloor * 5), z - 2, x + 4, y + (currentFloor * 5), z + 2, baseBlock, baseBlock, false);
			customFillWithBlocks(world, x - 3, y + (currentFloor * 5), z - 3, x + 3, y + (currentFloor * 5), z + 3, baseBlock, baseBlock, false);
			customFillWithBlocks(world, x - 2, y + (currentFloor * 5), z - 4, x + 2, y + (currentFloor * 5), z + 4, baseBlock, baseBlock, false);
			
			customFillWithBlocks(world, x - 1, y + (currentFloor * 5), z - 1, x + 1, y + (currentFloor * 5), z + 1, Blocks.air, Blocks.air, false);
			
			customFillWithBlocks(world, x - 4, y + (currentFloor * 5), z, x + 4, y + (currentFloor * 5), z, detailBlock, detailBlock, false);
			customFillWithBlocks(world, x, y + (currentFloor * 5), z - 4, x, y + (currentFloor * 5), z + 4, detailBlock, detailBlock, false);
			
			//Middle Floor - Corners
			
			for(int i = -4; i <= 4; i += 8)
			{
				for(int k = -2; k <= 2; k += 4)
				{
					customFillWithBlocks(world, x + i, y + (currentFloor * 5), z + k, x + i, y + 4 + (currentFloor * 5), z + k, baseBlock, baseBlock, false);
				}
			}
			
			for(int i = -3; i <= 3; i += 6)
			{
				for(int k = -3; k <= 3; k += 6)
				{
					customFillWithBlocks(world, x + i, y + (currentFloor * 5), z + k, x + i, y + 4 + (currentFloor * 5), z + k, baseBlock, baseBlock, false);
				}
			}
			
			for(int i = -2; i <= 2; i += 4)
			{
				for(int k = -4; k <= 4; k += 8)
				{
					customFillWithBlocks(world, x + i, y + (currentFloor * 5), z + k, x + i, y + 4 + (currentFloor * 5), z + k, baseBlock, baseBlock, false);
				}
			}
			
			for(int i = -2; i <= 2; i += 4)
			{
				for(int k = -4; k <= 4; k += 8)
				{
					customFillWithBlocks(world, x + i, y + (currentFloor * 5), z + k, x + i, y + 4 + (currentFloor * 5), z + k, baseBlock, baseBlock, false);
				}
			}
			
			//Middle Floor - Windows
			
			for(int i = -1; i <= 1; i += 2)
			{
				for(int k = -4; k <= 4; k += 8)
				{
					customFillWithBlocks(world, x + i, y + (currentFloor * 5), z + k, x + i, y + 4 + (currentFloor * 5), z + k, baseBlock, baseBlock, false);
				}
			}
			for(int i = -4; i <= 4; i += 8)
			{
				for(int k = -1; k <= 1; k += 2)
				{
					customFillWithBlocks(world, x + i, y + (currentFloor * 5), z + k, x + i, y + 4 + (currentFloor * 5), z + k, baseBlock, baseBlock, false);
				}
			}
			
			for(int i = -1; i <= 1; i += 2)
			{
				for(int k = -4; k <= 4; k += 8)
				{
					customFillWithBlocks(world, x + i, y + 2 + (currentFloor * 5), z + k, x + i, y + 3 + (currentFloor * 5), z + k, detailBlock, detailBlock, false);
				}
			}
			for(int i = -4; i <= 4; i += 8)
			{
				for(int k = -1; k <= 1; k += 2)
				{
					customFillWithBlocks(world, x + i, y + 2 + (currentFloor * 5), z + k, x + i, y + 3 + (currentFloor * 5), z + k, detailBlock, detailBlock, false);
				}
			}
			
			customFillWithBlocks(world, x - 4, y + (currentFloor * 5), z, x - 4, y + 4 + (currentFloor * 5), z, detailBlock, detailBlock, false);
			customFillWithBlocks(world, x + 4, y + (currentFloor * 5), z, x + 4, y + 4 + (currentFloor * 5), z, detailBlock, detailBlock, false);
			customFillWithBlocks(world, x, y + (currentFloor * 5), z - 4, x, y + 4 + (currentFloor * 5), z - 4, detailBlock, detailBlock, false);
			customFillWithBlocks(world, x, y + (currentFloor * 5), z + 4, x, y + 4 + (currentFloor * 5), z + 4, detailBlock, detailBlock, false);
			
			customFillWithBlocks(world, x - 4, y + 2 + (currentFloor * 5), z, x - 4, y + 3 + (currentFloor * 5), z, Blocks.air, Blocks.air, false);
			customFillWithBlocks(world, x + 4, y + 2 + (currentFloor * 5), z, x + 4, y + 3 + (currentFloor * 5), z, Blocks.air, Blocks.air, false);
			customFillWithBlocks(world, x, y + 2 + (currentFloor * 5), z - 4, x, y + 3 + (currentFloor * 5), z - 4, Blocks.air, Blocks.air, false);
			customFillWithBlocks(world, x, y + 2 + (currentFloor * 5), z + 4, x, y + 3 + (currentFloor * 5), z + 4, Blocks.air, Blocks.air, false);
		}
		
		//Roof
		
		customFillWithBlocks(world, x - 4, y + (floors * 5), z - 2, x + 4, y + (floors * 5), z + 2, baseBlock, baseBlock, false);
		customFillWithBlocks(world, x - 3, y + (floors * 5), z - 3, x + 3, y + (floors * 5), z + 3, baseBlock, baseBlock, false);
		customFillWithBlocks(world, x - 2, y + (floors * 5), z - 4, x + 2, y + (floors * 5), z + 4, baseBlock, baseBlock, false);
		
		customFillWithBlocks(world, x - 4, y + (floors * 5), z, x + 4, y + (floors * 5), z, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x, y + (floors * 5), z - 4, x, y + (floors * 5), z + 4, detailBlock, detailBlock, false);
		customFillWithBlocks(world, x - 1, y + (floors * 5), z - 1, x + 1, y + (floors * 5), z + 1, Blocks.air, Blocks.air, false);
		
		customFillWithBlocks(world, x - 4, y + (floors * 5) + 1, z - 4, x + 4, y + (floors * 5) + 1, z + 4, baseBlock, baseBlock, false);
		
		customFillWithBlocks(world, x - 3, y + (floors * 5) + 1, z - 3, x + 3, y + (floors * 5) + 1, z + 3, Blocks.air, Blocks.air, false);
		customFillWithBlocks(world, x - 2, y + (floors * 5) + 1, z - 4, x + 2, y + (floors * 5) + 1, z + 4, Blocks.air, Blocks.air, false);
		customFillWithBlocks(world, x - 4, y + (floors * 5) + 1, z - 2, x + 4, y + (floors * 5) + 1, z + 2, Blocks.air, Blocks.air, false);
		
		customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 4, y + (floors * 5) + 2, z - 4);
		customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 4, y + (floors * 5) + 2, z + 4);
		customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 4, y + (floors * 5) + 2, z + 4);
		customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 4, y + (floors * 5) + 2, z - 4);
		
		for(int i = -2; i < 3; i++)
		{
			customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + (floors * 5) + 1, z + 5);
			customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + (floors * 5) + 1, z - 5);
			
			if(i % 2 == 0)
			{
				customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + (floors * 5) + 2, z + 5);
				customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + (floors * 5) + 2, z - 5);
			}
		}
		for(int i = -2; i < 3; i++)
		{
			customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 5, y + (floors * 5) + 1, z + i);
			customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 5, y + (floors * 5) + 1, z + i);
			
			if(i % 2 == 0)
			{
				customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 5, y + (floors * 5) + 2, z + i);
				customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 5, y + (floors * 5) + 2, z + i);
			}
		}
		
		//Spire
		
		customFillWithBlocks(world, x, y, z, x, y + (floors * 5) + 1, z, detailBlock, detailBlock, false);
		
		EntityEnderCrystal crystal = new EntityEnderCrystal(world);
		crystal.setLocationAndAngles(this.getXWithOffset(x, z) + 0.5F, 64 + y + (floors * 5) + 2, this.getZWithOffset(x, z) + 0.5F, 0.0F, 0.0F);
		world.spawnEntityInWorld(crystal);
		world.setBlock(this.getXWithOffset(x, z), 64 + y + (floors * 5) + 2, this.getZWithOffset(x, z), Blocks.bedrock, 0, 2);
	}
	
	protected void buildWall(World world, int x, int y, int z, int direction, int length, int phase)
	{
		switch(direction)
		{
			case 0:
			{
				customFillWithBlocks(world, x - 2, y + 0, z, x + 2, y + 5, z + length - 1, baseBlock, baseBlock, false);
				customFillWithBlocks(world, x - 1, y + 1, z, x + 1, y + 4, z + length - 1, Blocks.air, Blocks.air, false);
				
				customFillWithBlocks(world, x + 2, y + 5, z, x + 2, y + 5, z + length - 1, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x + 0, y + 5, z, x + 0, y + 5, z + length - 1, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x - 2, y + 5, z, x - 2, y + 5, z + length - 1, detailBlock, detailBlock, false);
				
				customFillWithBlocks(world, x + 0, y, z, x + 0, y, z + length - 1, detailBlock, detailBlock, false);
				
				for(int i = 0; i < length; i++)
				{
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 2, y + 6, z + i);
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 2, y + 6, z + i);
					
					if(i % 2 == phase)
					{
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 2, y + 7, z + i);
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 2, y + 7, z + i);
					}
				}
				break;
			}
			case 1:
			{
				customFillWithBlocks(world, x, y + 0, z - 2, x + length - 1, y + 5, z + 2, baseBlock, baseBlock, false);
				customFillWithBlocks(world, x, y + 1, z - 1, x + length - 1, y + 4, z + 1, Blocks.air, Blocks.air, false);
				
				customFillWithBlocks(world, x, y + 5, z - 2, x + length - 1, y + 5, z - 2, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x, y + 5, z + 0, x + length - 1, y + 5, z + 0, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x, y + 5, z + 2, x + length - 1, y + 5, z + 2, detailBlock, detailBlock, false);
				
				customFillWithBlocks(world, x, y, z + 0, x + length - 1, y, z + 0, detailBlock, detailBlock, false);
				
				for(int i = 0; i < length; i++)
				{
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + 6, z - 2);
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + 6, z + 2);
					
					if(i % 2 == phase)
					{
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + 7, z - 2);
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + i, y + 7, z + 2);
					}
				}
				break;
			}
			case 2:
			{
				customFillWithBlocks(world, x - 2, y + 0, z - length + 1, x + 2, y + 5, z, baseBlock, baseBlock, false);
				customFillWithBlocks(world, x - 1, y + 1, z - length + 1, x + 1, y + 4, z, Blocks.air, Blocks.air, false);
				
				customFillWithBlocks(world, x + 2, y + 5, z - length + 1, x + 2, y + 5, z, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x + 0, y + 5, z - length + 1, x + 0, y + 5, z, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x - 2, y + 5, z - length + 1, x - 2, y + 5, z, detailBlock, detailBlock, false);
				
				customFillWithBlocks(world, x + 0, y, z - length, x + 0, y, z, detailBlock, detailBlock, false);
				
				for(int i = 0; i < length; i++)
				{
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 2, y + 6, z - i);
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 2, y + 6, z - i);
					
					if(i % 2 == phase)
					{
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - 2, y + 7, z - i);
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x + 2, y + 7, z - i);
					}
				}
				break;
			}
			case 3:
			{
				customFillWithBlocks(world, x - length + 1, y + 0, z - 2, x, y + 5, z + 2, baseBlock, baseBlock, false);
				customFillWithBlocks(world, x - length + 1, y + 1, z - 1, x, y + 4, z + 1, Blocks.air, Blocks.air, false);
				
				customFillWithBlocks(world, x - length + 1, y + 5, z - 2, x, y + 5, z - 2, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x - length + 1, y + 5, z + 0, x, y + 5, z + 0, detailBlock, detailBlock, false);
				customFillWithBlocks(world, x - length + 1, y + 5, z + 2, x, y + 5, z + 2, detailBlock, detailBlock, false);
				
				customFillWithBlocks(world, x - length + 1, y, z + 0, x, y, z + 0, detailBlock, detailBlock, false);
				
				for(int i = 0; i < length; i++)
				{
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - i, y + 6, z - 2);
					customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - i, y + 6, z + 2);
					
					if(i % 2 == phase)
					{
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - i, y + 7, z - 2);
						customPlaceBlockAtCurrentPosition(world, baseBlock, 0, x - i, y + 7, z + 2);
					}
				}
				break;
			}
		}
	}
}
