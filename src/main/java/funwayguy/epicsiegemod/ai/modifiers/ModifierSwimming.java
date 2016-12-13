package funwayguy.epicsiegemod.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import funwayguy.epicsiegemod.ai.ESM_EntityAISwimming;
import funwayguy.epicsiegemod.api.ITaskModifier;

public class ModifierSwimming implements ITaskModifier
{
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		return task.getClass() == EntityAISwimming.class;
	}
	
	@Override
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
	{
		return new ESM_EntityAISwimming(host);
	}
}
