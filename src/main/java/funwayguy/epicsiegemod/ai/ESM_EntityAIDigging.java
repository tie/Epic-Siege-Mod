package funwayguy.epicsiegemod.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import funwayguy.epicsiegemod.ai.utils.AiUtils;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ESM_EntityAIDigging extends EntityAIBase
{
	EntityLivingBase target;
	EntityLiving digger;
	BlockPos curBlock;
	int scanTick = 0;
	int digTick = 0;
	
	public ESM_EntityAIDigging(EntityLiving digger)
	{
		this.digger = digger;
		this.setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute()
	{
		target = digger.getAttackTarget();
		
		if(target == null || !target.isEntityAlive() || !digger.getNavigator().noPath() || digger.getDistanceToEntity(target) < 1D)
		{
			return false;
		}
		
        //digger.getLookHelper().setLookPosition(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ, (float)digger.getHorizontalFaceSpeed(), (float)digger.getVerticalFaceSpeed());
		curBlock = (curBlock != null && digger.getDistanceSq(curBlock) <= (4D * 4D) && canHarvest(digger, curBlock))? curBlock : getNextBlock(digger, target, 2D);
		
		return curBlock != null;
	}
	
	@Override
	public void startExecuting()
	{
		super.startExecuting();
		digger.getNavigator().clearPathEntity();
	}
	
	@Override
	public void resetTask()
	{
		curBlock = null;
		digTick = 0;
	}
	
	@Override
	public boolean continueExecuting()
	{
		return curBlock != null && digger.getDistanceSq(curBlock) <= (4D * 4D) && canHarvest(digger, curBlock);
	}
	
	@Override
	public void updateTask()
	{
		if(!this.continueExecuting())
		{
			return;
		}
		
		digger.getLookHelper().setLookPosition(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ, (float)digger.getHorizontalFaceSpeed(), (float)digger.getVerticalFaceSpeed());
		digger.getNavigator().clearPathEntity();
		
		digTick++;
		float str = AiUtils.getBlockStrength(digger, digger.world, curBlock) * (digTick + 1F);
		ItemStack heldItem = digger.getHeldItem(EnumHand.MAIN_HAND);
		IBlockState state = digger.world.getBlockState(curBlock);
		
		if(digger.world.isAirBlock(curBlock))
		{
			this.resetTask();
		} else if(str >= 1F)
		{
			boolean canHarvest = state.getMaterial().isToolNotRequired() || (heldItem != null && heldItem.canHarvestBlock(state));
			digger.world.destroyBlock(curBlock, canHarvest);
			digger.getNavigator().setPath(digger.getNavigator().getPathToEntityLiving(target), digger.getMoveHelper().getSpeed());
			this.resetTask();
		} else if(digTick%5 == 0)
		{
			digger.world.playSound(null, curBlock, state.getBlock().getSoundType(state, digger.world, curBlock, digger).getHitSound(), SoundCategory.BLOCKS, 1F, 1F);
			digger.swingArm(EnumHand.MAIN_HAND);
			digger.world.sendBlockBreakProgress(digger.getEntityId(), curBlock, (int)(str * 10F));
		}
	}
	
	public BlockPos getNextBlock(EntityLiving entityLiving, EntityLivingBase target, double dist)
	{
        int digWidth = MathHelper.ceil(entityLiving.width);
        int digHeight = MathHelper.ceil(entityLiving.height);
        
        int passMax = digWidth * digWidth * digHeight;

        int y = scanTick%digHeight;
        int x = (scanTick%(digWidth * digHeight))/digHeight;
        int z = scanTick/(digWidth * digHeight);
        
		double rayX = x + entityLiving.posX - (digWidth/2);
		double rayY = y + entityLiving.posY + 0.5D;
		double rayZ = z + entityLiving.posZ - (digWidth/2);
		Vec3d rayOrigin = new Vec3d(rayX, rayY, rayZ);
		Vec3d rayOffset = target.getPositionVector();
		rayOffset = rayOrigin.add(rayOffset.subtract(rayOrigin).normalize().scale(dist));
		
		BlockPos p1 = entityLiving.getPosition();
		BlockPos p2 = target.getPosition();
		
		if(p1.getDistance(p2.getX(), p1.getY(), p2.getZ()) < 4)
		{
			if(p2.getY() - p1.getY() > 2D)
			{
				rayOffset = rayOrigin.addVector(0D, dist, 0D);
			} else if(p2.getY() - p1.getY() < 2D)
			{
				rayOffset = rayOrigin.addVector(0D, -dist, 0D);
			} else
			{
				//rayOffset = rayOrigin.add(digger.getLook(1F).scale(dist));
			}
		} else
		{
			//rayOffset = rayOrigin.add(digger.getLook(1F).scale(dist));
		}
		
		RayTraceResult ray = entityLiving.world.rayTraceBlocks(rayOrigin, rayOffset, false, true, false);
		scanTick = (scanTick + 1)%passMax;
		
		if(ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = ray.getBlockPos();
			IBlockState state = entityLiving.world.getBlockState(pos);
			
			if(canHarvest(entityLiving, pos) && ESM_Settings.ZombieDigBlacklist.contains(state.getBlock().getRegistryName().toString()) == ESM_Settings.ZombieSwapList)
			{
				return pos;
			}
		}
		
		return null;
	}
	
	public boolean canHarvest(EntityLiving entity, BlockPos pos)
	{
		IBlockState state = entity.world.getBlockState(pos);
		
		if(!state.getMaterial().isSolid() || state.getBlockHardness(entity.world, pos) < 0F)
		{
			return false;
		} else if(state.getMaterial().isToolNotRequired() || !ESM_Settings.ZombieDiggerTools)
		{
			return true;
		}
		
		ItemStack held = entity.getHeldItem(EnumHand.MAIN_HAND);
		return held != null && held.getItem().canHarvestBlock(state, held);
	}
}
