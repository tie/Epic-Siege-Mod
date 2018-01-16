package funwayguy.epicsiegemod.ai;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import funwayguy.epicsiegemod.ai.utils.AiUtils;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ESM_EntityAIGrief extends EntityAIBase
{
	EntityLiving entityLiving;
	BlockPos markedLoc;
	int digTick = 0;
	
	public ESM_EntityAIGrief(EntityLiving entity)
	{
		this.entityLiving = entity;
		this.setMutexBits(5);
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(this.entityLiving.getRNG().nextInt(4) != 0) // Severely nerfs how many time the next part of the script can run
		{
			return false;
		}
		
		BlockPos curPos = entityLiving.getPosition();
		
		BlockPos candidate = null;
		ItemStack item = entityLiving.getHeldItemMainhand();
		
		BlockPos tarPos = curPos.add(entityLiving.getRNG().nextInt(32) - 16, entityLiving.getRNG().nextInt(16) - 8, entityLiving.getRNG().nextInt(32) - 16);
		
		IBlockState state = entityLiving.world.getBlockState(tarPos);
		ResourceLocation regName = Block.REGISTRY.getNameForObject(state.getBlock());
		
		if((ESM_Settings.ZombieGriefBlocks.contains(regName.toString()) || state.getLightValue(entityLiving.world, tarPos) > 0) && state.getBlockHardness(entityLiving.world, tarPos) >= 0 && !state.getMaterial().isLiquid())
		{
			if(!ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(state, item)) || state.getMaterial().isToolNotRequired())
			{
				candidate = tarPos;
			}
		}
		
		if(candidate == null)
		{
			return false;
		} else
		{
			markedLoc = candidate;
			entityLiving.getNavigator().tryMoveToXYZ(markedLoc.getX(), markedLoc.getY(), markedLoc.getZ(), 1D);
			digTick = 0;
			return true;
		}
	}
	
	@Override
	public boolean shouldContinueExecuting()
	{
		if(markedLoc == null || !entityLiving.isEntityAlive())
		{
			markedLoc = null;
			return false;
		}
		
		IBlockState state = entityLiving.world.getBlockState(markedLoc);
		ResourceLocation regName = Block.REGISTRY.getNameForObject(state.getBlock());
		
		if(state.getBlock() == Blocks.AIR || (!ESM_Settings.ZombieGriefBlocks.contains(regName) && !ESM_Settings.ZombieGriefBlocks.contains(regName.toString()) && state.getLightValue(entityLiving.world, markedLoc) <= 0))
		{
			markedLoc = null;
			return false;
		}
		
		ItemStack item = entityLiving.getHeldItemMainhand();
		return !ESM_Settings.ZombieDiggerTools || (item != null && item.getItem().canHarvestBlock(state, item)) || state.getMaterial().isToolNotRequired();
	}
	
	@Override
	public void updateTask()
	{
		if(!shouldContinueExecuting())
		{
			digTick = 0;
			return;
		}
		
		if(entityLiving.getDistance(markedLoc.getX(), markedLoc.getY(), markedLoc.getZ()) >= 3)
		{
			if(entityLiving.getNavigator().noPath())
			{
				entityLiving.getNavigator().tryMoveToXYZ(markedLoc.getX(), markedLoc.getY(), markedLoc.getZ(), 1D);
			}
			digTick = 0;
			return;
		}
		
		IBlockState state = entityLiving.world.getBlockState(markedLoc);
		digTick++;
		
		float str = AiUtils.getBlockStrength(entityLiving, entityLiving.world, markedLoc) * (digTick + 1);
		
		if(str >= 1F)
		{
			digTick = 0;
			
			if(markedLoc != null)
			{
				ItemStack item = entityLiving.getHeldItemMainhand();
				boolean canHarvest = state.getMaterial().isToolNotRequired() || (item != null && item.getItem().canHarvestBlock(state, item));
				entityLiving.world.destroyBlock(markedLoc, canHarvest);
				markedLoc = null;
			} else
			{
				markedLoc = null;
			}
		} else
		{
			if(digTick%5 == 0)
			{
				SoundType sndType = state.getBlock().getSoundType(state, entityLiving.world, markedLoc, entityLiving);
				entityLiving.playSound(sndType.getHitSound(), sndType.volume, sndType.pitch);
				entityLiving.swingArm(EnumHand.MAIN_HAND);
			}
		}
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
