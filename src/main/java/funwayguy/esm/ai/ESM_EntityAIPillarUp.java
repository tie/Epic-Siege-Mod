package funwayguy.esm.ai;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class ESM_EntityAIPillarUp extends EntityAIBase
{
	public int placeDelay = 15;
	public int blocks = ESM_Settings.ZombiePillaring;
	public EntityLiving builder;
	public EntityLivingBase target;
	
	public ESM_EntityAIPillarUp(EntityLiving entity)
	{
		this.builder = entity;
	}

	@Override
	public boolean shouldExecute()
	{
		if(blocks <= 0)
		{
			return false;
		}
		
		target = builder.getAttackTarget();
		
		if(target == null || !target.isEntityAlive() || target.posY <= builder.posY)
		{
			return false;
		}
		
		if(builder.getNavigator().noPath() && builder.getDistance(target.posX, builder.posY, target.posZ) < 8D)
		{
			int i = MathHelper.floor_double(builder.posX);
			int j = MathHelper.floor_double(builder.posY);
			int k = MathHelper.floor_double(builder.posZ);

			Block block = builder.worldObj.getBlock(i, j + 2, k);
			
			if(!builder.worldObj.getBlock(i, j - 1, k).isNormalCube() || !block.getMaterial().isReplaceable())
			{
				return false;
			}
			
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
		if(placeDelay > 0)
		{
			placeDelay--;
			return;
		} else
		{
			placeDelay = 15;
			
			int i = MathHelper.floor_double(builder.posX);
			int j = MathHelper.floor_double(builder.posY);
			int k = MathHelper.floor_double(builder.posZ);

			builder.setPositionAndUpdate(builder.posX, builder.posY + 1, builder.posZ);
			
			if(builder.worldObj.getBlock(i, j, k).getMaterial().isReplaceable())
			{
				builder.worldObj.setBlock(i, j, k, Blocks.cobblestone);
			}
			blocks--;
		}
	}

    @Override
    public boolean isInterruptible()
    {
        return false;
    }
}
