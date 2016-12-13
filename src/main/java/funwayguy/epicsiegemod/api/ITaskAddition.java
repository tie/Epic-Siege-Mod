package funwayguy.epicsiegemod.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public interface ITaskAddition
{
	/**
	 * Returns true if this AI should be added as a target task, otherwise it will be passive
	 */
	boolean isTargetTask();
	
	int getTaskPriority(EntityLiving entityLiving);
	
	/**
	 * Returns true if this entity is valid for the additional AI
	 */
	boolean isValid(EntityLiving entityLiving);
	
	/**
	 * Returns the additional AI to apply to this entity
	 */
	EntityAIBase getAdditionalAI(EntityLiving entityLiving);
}
