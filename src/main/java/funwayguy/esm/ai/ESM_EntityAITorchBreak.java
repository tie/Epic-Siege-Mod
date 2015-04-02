package funwayguy.esm.ai;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import funwayguy.esm.core.ESM_Settings;

public class ESM_EntityAITorchBreak extends EntityAIBase
{
	EntityLiving entityLiving;
	int[] markedLoc;
	int digTick = 0;
	
	public ESM_EntityAITorchBreak(EntityLiving entity)
	{
		this.entityLiving = entity;
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(this.entityLiving.getRNG().nextInt(10) != 0) // Severely nerfs how many time the next part of the script can run
		{
			return false;
		}
		
    	// Returns true if something like Iguana Tweaks is nerfing the vanilla picks. This will then cause zombies to ignore the harvestability of blocks when holding picks
    	boolean nerfedPick = !Items.iron_pickaxe.canHarvestBlock(Blocks.stone, new ItemStack(Items.iron_pickaxe));
		int i = MathHelper.floor_double(entityLiving.posX);
		int j = MathHelper.floor_double(entityLiving.posY);
		int k = MathHelper.floor_double(entityLiving.posZ);
		
		if(entityLiving.worldObj.getBlockLightValue(i, j, k) <= 0 || entityLiving.getAttackTarget() != null || (entityLiving instanceof EntityCreature && ((EntityCreature)entityLiving).getEntityToAttack() != null))
		{
			return false;
		}
		
		int[] candidate = null;
		double dist = 99;
		ItemStack item = entityLiving.getEquipmentInSlot(0);
		
		for(int ii = i - 16; ii < i + 16; ii++)
		{
			for(int jj = j - 16; jj < j + 16; jj++)
			{
				for(int kk = k - 16; kk < k + 16; kk++)
				{
					Block block = entityLiving.worldObj.getBlock(ii, jj, kk);
					int meta = entityLiving.worldObj.getBlockMetadata(ii, jj, kk);
					
					if(ESM_Settings.ZombieDigBlacklist.contains(Block.blockRegistry.getNameForObject(block)) || ESM_Settings.ZombieDigBlacklist.contains(Block.blockRegistry.getNameForObject(block) + ":" + meta))
            		{
            			continue;
            		}
					
					if(block.getLightValue() > 0 && entityLiving.getDistance(ii, jj, kk) < dist && block.getBlockHardness(entityLiving.worldObj, ii, jj, kk) >= 0 && !block.getMaterial().isLiquid())
					{
						PathEntity path = entityLiving.getNavigator() == null? null : entityLiving.getNavigator().getPathToXYZ(ii, jj, kk);
						
						if(path == null)
						{
							continue;
						}
						
						PathPoint pp = path.getFinalPathPoint();
						MovingObjectPosition mop = AIUtils.RayCastBlocks(entityLiving.worldObj, Vec3.createVectorHelper(pp.xCoord + entityLiving.height, pp.yCoord + 0.5D, pp.zCoord + 0.5D), Vec3.createVectorHelper(ii + 0.5D, jj + 0.5D, kk + 0.5D), true);
						if((pp.xCoord == ii && pp.yCoord == jj && pp.zCoord == kk) || (mop != null && mop.typeOfHit == MovingObjectType.BLOCK && mop.blockX == ii && mop.blockY == jj && mop.blockZ == kk)) // Check if path reaches a point at which the light source is accessible
						{
						} else
						{
							continue; // We cannot easily reach this object so we'll ignore it for now
						}
						
						if(!ESM_Settings.ZombieDiggerTools || (item != null && (item.getItem().canHarvestBlock(block, item) || (item.getItem() instanceof ItemPickaxe && nerfedPick && block.getMaterial() == Material.rock))) || block.getMaterial().isToolNotRequired())
						{
							candidate = new int[]{ii, jj, kk};
							dist = entityLiving.getDistance(ii, jj, kk);
						}
					}
				}
			}
		}
		
		if(candidate == null)
		{
			return false;
		} else
		{
			markedLoc = candidate;
			entityLiving.getNavigator().tryMoveToXYZ(markedLoc[0], markedLoc[1], markedLoc[2], 1D);
			digTick = 0;
			return true;
		}
	}
	
	@Override
	public boolean continueExecuting()
	{
    	// Returns true if something like Iguana Tweaks is nerfing the vanilla picks. This will then cause zombies to ignore the harvestability of blocks when holding picks
    	boolean nerfedPick = !Items.iron_pickaxe.canHarvestBlock(Blocks.stone, new ItemStack(Items.iron_pickaxe));
    	
		if(markedLoc == null || !entityLiving.isEntityAlive() || entityLiving.getAttackTarget() != null)
		{
			return false;
		}
		
		Block block = entityLiving.worldObj.getBlock(markedLoc[0], markedLoc[1], markedLoc[2]);
		
		if(block == null || block == Blocks.air || entityLiving.worldObj.getBlockLightValue(markedLoc[0], markedLoc[1], markedLoc[2]) <= 0)
		{
			return false;
		}
		
		ItemStack item = entityLiving.getEquipmentInSlot(0);
		return !ESM_Settings.ZombieDiggerTools || (item != null && (item.getItem().canHarvestBlock(block, item) || (item.getItem() instanceof ItemPickaxe && nerfedPick && block.getMaterial() == Material.rock))) || block.getMaterial().isToolNotRequired();
	}
	
	@Override
	public void updateTask()
	{
    	// Returns true if something like Iguana Tweaks is nerfing the vanilla picks. This will then cause zombies to ignore the harvestability of blocks when holding picks
    	boolean nerfedPick = !Items.iron_pickaxe.canHarvestBlock(Blocks.stone, new ItemStack(Items.iron_pickaxe));
    	
		if(!continueExecuting())
		{
			digTick = 0;
			return;
		}
		
		if(entityLiving.getDistance(markedLoc[0], markedLoc[1], markedLoc[2]) >= 3)
		{
			entityLiving.getNavigator().tryMoveToXYZ(markedLoc[0], markedLoc[1], markedLoc[2], 1D);
			digTick = 0;
			return;
		}
		
		Block block = entityLiving.worldObj.getBlock(markedLoc[0], markedLoc[1], markedLoc[2]);
		digTick++;
		
		float str = AIUtils.getBlockStrength(entityLiving, block, entityLiving.worldObj, markedLoc[0], markedLoc[1], markedLoc[2], !ESM_Settings.ZombieDiggerTools) * (digTick + 1);
		
		if(str >= 1F)
		{
			digTick = 0;
			
			if(markedLoc != null && markedLoc.length >= 3)
			{
				ItemStack item = entityLiving.getEquipmentInSlot(0);
				boolean canHarvest = !ESM_Settings.ZombieDiggerTools || (item != null && (item.getItem().canHarvestBlock(block, item) || (item.getItem() instanceof ItemPickaxe && nerfedPick && block.getMaterial() == Material.rock))) || block.getMaterial().isToolNotRequired();
				entityLiving.worldObj.func_147480_a(markedLoc[0], markedLoc[1], markedLoc[2], canHarvest);
				markedLoc = null;
			} else
			{
				markedLoc = null;
			}
		} else
		{
			if(digTick%5 == 0)
			{
				entityLiving.worldObj.playSoundAtEntity(entityLiving, block.stepSound.getStepResourcePath(), block.stepSound.getVolume() + 1F, block.stepSound.getPitch());
				entityLiving.swingItem();
			}
		}
	}
}
