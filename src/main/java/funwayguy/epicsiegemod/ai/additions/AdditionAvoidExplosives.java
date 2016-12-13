package funwayguy.epicsiegemod.ai.additions;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import funwayguy.epicsiegemod.ai.ESM_EntityAIAvoidExplosion;
import funwayguy.epicsiegemod.api.ITaskAddition;

public class AdditionAvoidExplosives implements ITaskAddition
{
	@Override
	public boolean isTargetTask()
	{
		return false;
	}
	
	@Override
	public int getTaskPriority(EntityLiving entityLiving)
	{
		return 1;
	}
	
	@Override
	public boolean isValid(EntityLiving entityLiving)
	{
		return entityLiving instanceof EntityCreature;
	}
	
	@Override
	public EntityAIBase getAdditionalAI(EntityLiving entityLiving)
	{
		return new ESM_EntityAIAvoidExplosion((EntityCreature)entityLiving, 12F, 1.25D, 1.25D);
	}
	
}
