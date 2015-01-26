package funwayguy.esm.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import funwayguy.esm.core.ESM_Settings;

public class ESM_EntityAIDigging extends EntityAIBase
{
	EntityLivingBase target;
	int[] markedLoc;
	EntityLiving entityDigger;
	int digTick = 0;
	
	public ESM_EntityAIDigging(EntityLiving entity)
	{
		this.entityDigger = entity;
	}
	
	@Override
	public boolean shouldExecute()
	{
		MovingObjectPosition mop = GetNextObstical(entityDigger, 3D, false);
		target = entityDigger.getAttackTarget();
		
		if(target != null && mop != null && mop.typeOfHit == MovingObjectType.BLOCK && entityDigger.getNavigator().noPath() && entityDigger.getNavigator().getPathToEntityLiving(target) == null)// && !entityDigger.canEntityBeSeen(target))
		{
			ItemStack item = entityDigger.getEquipmentInSlot(0);
			Block block = entityDigger.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
			
			if(!ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(block, item)) || block.getMaterial().isToolNotRequired())
			{
				markedLoc = new int[]{mop.blockX, mop.blockY, mop.blockZ};
				
				return true;
			} else
			{
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean continueExecuting()
	{
		return target != null && entityDigger != null && target.isEntityAlive() && entityDigger.isEntityAlive() && markedLoc != null && entityDigger.getNavigator().noPath() && entityDigger.getNavigator().getPathToEntityLiving(target) == null;
	}
	
	@Override
	public void updateTask()
	{
		MovingObjectPosition mop = GetNextObstical(entityDigger, 3D, false);
		
		if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
		{
			markedLoc = new int[]{mop.blockX, mop.blockY, mop.blockZ};
		}
		
		if(markedLoc == null || entityDigger.worldObj.getBlock(markedLoc[0], markedLoc[1], markedLoc[2]) == Blocks.air)
		{
			digTick = 0;
			return;
		}
		
		Block block = entityDigger.worldObj.getBlock(markedLoc[0], markedLoc[1], markedLoc[2]);
		digTick++;
		
		float str = AIUtils.getBlockStrength(this.entityDigger, block, entityDigger.worldObj, markedLoc[0], markedLoc[1], markedLoc[2], !ESM_Settings.ZombieDiggerTools) * (digTick + 1);
		
		if(str >= 1F)
		{
			digTick = 0;
			
			if(markedLoc != null && markedLoc.length >= 3)
			{
				ItemStack item = entityDigger.getEquipmentInSlot(0);
				boolean canHarvest = !ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(block, item)) || block.getMaterial().isToolNotRequired();
				entityDigger.worldObj.func_147480_a(markedLoc[0], markedLoc[1], markedLoc[2], canHarvest);
				markedLoc = null;
			} else
			{
				markedLoc = null;
			}
		} else
		{
			if(digTick%5 == 0)
			{
				entityDigger.worldObj.playSoundAtEntity(entityDigger, block.stepSound.getStepResourcePath(), block.stepSound.getVolume() + 1F, block.stepSound.getPitch());
				entityDigger.swingItem();
			}
		}
	}
	
	@Override
	public void resetTask()
	{
		markedLoc = null;
		digTick = 0;
	}
	
	/**
	 * Rolls through all the points in the bounding box of the entity and raycasts them toward it's current heading to return any blocks that may be obstructing it's path.
	 * The bigger the entity the longer this calculation will take due to the increased number of points (Generic bipeds should only need 2)
	 */
    public static MovingObjectPosition GetNextObstical(EntityLivingBase entityLiving, double dist, boolean liquids)
    {
        float f = 1.0F;
        float f1 = entityLiving.prevRotationPitch + (entityLiving.rotationPitch - entityLiving.prevRotationPitch) * f;
        float f2 = entityLiving.prevRotationYaw + (entityLiving.rotationYaw - entityLiving.prevRotationYaw) * f;
        
        double pointsW = MathHelper.ceiling_double_int(entityLiving.width);
        double pointsH = MathHelper.ceiling_double_int(entityLiving.height);
        
        for(double x = 0D; x <= pointsW; x += 0.5D)
        {
        	for(double y = 0D; y <= pointsH; y += 0.5D)
            {
        		for(double z = 0D; z <= pointsW; z += 0.5D)
                {
                	MovingObjectPosition mop = AIUtils.RayCastBlocks(entityLiving.worldObj, x + entityLiving.posX, y + entityLiving.posY, z + entityLiving.posZ, f2, f1, dist, liquids);
                	
                	if(mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
                	{
                		Block block = entityLiving.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
                		ItemStack item = entityLiving.getEquipmentInSlot(0);
                		if(!ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(block, item)) || block.getMaterial().isToolNotRequired())
                		{
                			return mop;
                		} else
                		{
                			continue;
                		}
                	} else
                	{
                		continue;
                	}
                }
            }
        }
        return null;
    }
}
