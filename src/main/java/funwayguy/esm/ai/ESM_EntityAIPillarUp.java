package funwayguy.esm.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import funwayguy.esm.core.ESM_Settings;

public class ESM_EntityAIPillarUp extends EntityAIBase
{
	public int placeDelay = 15;
	public int blocks = ESM_Settings.ZombiePillaring;
	public EntityLiving builder;
	public EntityLivingBase target;
	
	int blockX = 0;
	int blockY = 0;
	int blockZ = 0;
	
	public ESM_EntityAIPillarUp(EntityLiving entity)
	{
		this.builder = entity;
	}

	@Override
	public boolean shouldExecute()
	{
		target = builder.getAttackTarget();
		
		if(target == null || !target.isEntityAlive() || builder.posY + 1D >= target.posY)
		{
			return false;
		}
		
		if(builder.getNavigator().noPath() && builder.getDistance(target.posX, builder.posY, target.posZ) < 8D)
		{
			int i = MathHelper.floor_double(builder.posX);
			int j = MathHelper.floor_double(builder.posY);
			int k = MathHelper.floor_double(builder.posZ);
			
			int origI = i;
			int origJ = j;
			int origK = k;
			
			int xOff = (int)Math.signum(MathHelper.floor_double(target.posX) - origI);
			int zOff = (int)Math.signum(MathHelper.floor_double(target.posZ) - origK);
			
			if(target.posY - builder.posY < 16 && builder.worldObj.getBlock(i, j - 2, k).isNormalCube())
			{
				if(builder.worldObj.getBlock(i + xOff, j - 1, k).getMaterial().isReplaceable())
				{
					i += xOff;
					j -= 1;
				} else if(builder.worldObj.getBlock(i, j - 1, k + zOff).getMaterial().isReplaceable())
				{
					k += zOff;
					j -= 1;
				} else if(target.posY <= builder.posY)
				{
					return false;
				}
			} else if(target.posY <= builder.posY)
			{
				return false;
			}
			
			if(!builder.worldObj.getBlock(origI, origJ - 1, origK).isNormalCube() || builder.worldObj.getBlock(origI, origJ + 2, origK).getMaterial().blocksMovement() || builder.worldObj.getBlock(i, j + 2, k).getMaterial().blocksMovement())
			{
				return false;
			}
			
			blockX = i;
			blockY = j;
			blockZ = k;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void startExecuting()
	{
		placeDelay = 15;
	}
	
	@Override
	public boolean continueExecuting()
	{
		return shouldExecute();
	}
	
	@Override
	public void updateTask()
	{
		if(placeDelay > 0 || target == null)
		{
			placeDelay--;
			return;
		} else
		{
			placeDelay = 15;

			builder.setPositionAndUpdate(blockX + 0.5D, blockY + 1D, blockZ + 0.5D);
			
			if(builder.worldObj.getBlock(blockX, blockY, blockZ).getMaterial().isReplaceable())
			{
				builder.worldObj.setBlock(blockX, blockY, blockZ, Blocks.cobblestone);
			}
			
			builder.getNavigator().setPath(builder.getNavigator().getPathToEntityLiving(target), 1D);
		}
	}

    @Override
    public boolean isInterruptible()
    {
        return false;
    }
}
