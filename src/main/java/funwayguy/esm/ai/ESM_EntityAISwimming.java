package funwayguy.esm.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;

public class ESM_EntityAISwimming extends EntityAIBase
{
    private EntityLiving theEntity;

    public ESM_EntityAISwimming(EntityLiving p_i1624_1_)
    {
        this.theEntity = p_i1624_1_;
        this.setMutexBits(4);
        p_i1624_1_.getNavigator().setCanSwim(true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	int x = MathHelper.floor_double(this.theEntity.posX);
    	int y = MathHelper.floor_double(this.theEntity.posY);
    	int z = MathHelper.floor_double(this.theEntity.posZ);
    	
    	if(!this.theEntity.worldObj.getBlock(x, y, z).getMaterial().isLiquid())
    	{
    		return false;
    	}
    	
    	PathEntity path = this.theEntity.getNavigator().getPath();
		
		if(path != null && path.getFinalPathPoint() != null && path.getFinalPathPoint().yCoord > this.theEntity.posY) // If our navigation says to go up then we do it
		{
	        return true;
		} else if(this.theEntity.getAttackTarget() != null && this.theEntity.getAir() >= 150 && this.theEntity.getAttackTarget().posY < this.theEntity.posY) // Our target is under water, swim down
		{
			return false;
		}
    	
    	return true;
    }
    
    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.theEntity.getJumpHelper().setJumping();
    }
}