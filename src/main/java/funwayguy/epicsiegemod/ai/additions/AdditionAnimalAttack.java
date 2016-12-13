package funwayguy.epicsiegemod.ai.additions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityAnimal;
import funwayguy.epicsiegemod.ai.ESM_EntityAIAttackMelee;
import funwayguy.epicsiegemod.api.ITaskAddition;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class AdditionAnimalAttack implements ITaskAddition
{
	@Override
	public boolean isTargetTask()
	{
		return false;
	}
	
	@Override
	public int getTaskPriority(EntityLiving entityLiving)
	{
		return 3;
	}
	
	@Override
	public boolean isValid(EntityLiving entityLiving)
	{
		if(!ESM_Settings.animalsAttack || !(entityLiving instanceof EntityAnimal))
		{
			return false;
		}
		
		for(EntityAITaskEntry entry : entityLiving.tasks.taskEntries)
		{
			if(entry.action.getClass() == EntityAIAttackMelee.class)
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public EntityAIBase getAdditionalAI(EntityLiving entityLiving)
	{
		return new ESM_EntityAIAttackMelee(entityLiving, 1.5D, true);
	}
}
