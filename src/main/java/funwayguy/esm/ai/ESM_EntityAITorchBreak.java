package funwayguy.esm.ai;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

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
		int i = MathHelper.floor_double(entityLiving.posX);
		int j = MathHelper.floor_double(entityLiving.posY);
		int k = MathHelper.floor_double(entityLiving.posZ);
		
		if(entityLiving.worldObj.getBlockLightValue(i, j, k) <= 0 || entityLiving.getAttackTarget() != null)
		{
			return false;
		}
		
		int[] candidate = null;
		double dist = 99;
		for(int ii = i - 16; ii < i + 16; ii++)
		{
			for(int jj = j - 16; jj < j + 16; jj++)
			{
				for(int kk = k - 16; kk < k + 16; kk++)
				{
					if(entityLiving.worldObj.getBlock(ii, jj, kk).getLightValue() > 0 && entityLiving.getDistance(ii, jj, kk) < dist)
					{
						candidate = new int[]{ii, jj, kk};
						dist = entityLiving.getDistance(ii, jj, kk);
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
		return !ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(block, item)) || block.getMaterial().isToolNotRequired();
	}
	
	@Override
	public void updateTask()
	{
		if(!continueExecuting())
		{
			System.out.println("Can't continue!");
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
				boolean canHarvest = !ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(block, item)) || block.getMaterial().isToolNotRequired();
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
