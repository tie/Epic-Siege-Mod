package funwayguy.esm.ai;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ESM_EntityAIDigging extends EntityAIBase
{
	EntityLivingBase target;
	int[] markedLoc;
	EntityLiving entityDigger;
	int breakTime = 30;
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
		
		if(target != null && mop != null && mop.typeOfHit == MovingObjectType.BLOCK && entityDigger.getNavigator().noPath() && entityDigger.getNavigator().getPathToEntityLiving(target) == null && !entityDigger.canEntityBeSeen(target))
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
		return target != null && markedLoc != null && entityDigger.getNavigator().noPath() && entityDigger.getNavigator().getPathToEntityLiving(target) == null;
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
		
		float str = this.getBlockStrength(block, entityDigger.worldObj, markedLoc[0], markedLoc[1], markedLoc[2]) * (digTick + 1);
		
		if(str >= 1F)
		{
			digTick = 0;
			
			if(markedLoc != null && markedLoc.length >= 3)
			{
				entityDigger.worldObj.func_147480_a(markedLoc[0], markedLoc[1], markedLoc[2], false);
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
		breakTime = 30;
	}
	
	public float getBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta, int x, int y, int z)
    {
        ItemStack stack = entityDigger.getEquipmentInSlot(0);
        float f = (stack == null ? 1.0F : stack.getItem().getDigSpeed(stack, p_146096_1_, meta));

        if (f > 1.0F)
        {
            int i = EnchantmentHelper.getEfficiencyModifier(entityDigger);
            ItemStack itemstack = entityDigger.getEquipmentInSlot(0);

            if (i > 0 && itemstack != null)
            {
                float f1 = (float)(i * i + 1);

                boolean canHarvest = ForgeHooks.canToolHarvestBlock(p_146096_1_, meta, itemstack);

                if (!canHarvest && f <= 1.0F)
                {
                    f += f1 * 0.08F;
                }
                else
                {
                    f += f1;
                }
            }
        }

        if (entityDigger.isPotionActive(Potion.digSpeed))
        {
            f *= 1.0F + (float)(entityDigger.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (entityDigger.isPotionActive(Potion.digSlowdown))
        {
            f *= 1.0F - (float)(entityDigger.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
        }

        if (entityDigger.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(entityDigger))
        {
            f /= 5.0F;
        }

        if (!entityDigger.onGround)
        {
            f /= 5.0F;
        }
        
        return (f < 0 ? 0 : f);
    }

    public float getBlockStrength(Block block, World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        
        if (hardness < 0.0F)
        {
            return 0.0F;
        }
        
		ItemStack item = entityDigger.getEquipmentInSlot(0);
		boolean canHarvest = !ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(block, item)) || block.getMaterial().isToolNotRequired();

        if (!canHarvest)
        {
            return getBreakSpeed(block, true, metadata, x, y, z) / hardness / 100F;
        }
        else
        {
            return getBreakSpeed(block, false, metadata, x, y, z) / hardness / 30F;
        }
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
                	MovingObjectPosition mop = GetMovingObjectPosition(entityLiving.worldObj, x + entityLiving.posX, y + entityLiving.posY, z + entityLiving.posZ, f2, f1, dist, liquids);
                	
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
    
    public static MovingObjectPosition GetMovingObjectPosition(World world, double x, double y, double z, float yaw, float pitch, double dist, boolean liquids)
    {
        Vec3 vec3 = Vec3.createVectorHelper(x, y, z);
        float f3 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-pitch * 0.017453292F);
        float f6 = MathHelper.sin(-pitch * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = dist; // Ray Distance
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return world.func_147447_a(vec3, vec31, liquids, !liquids, false);
    }
}
