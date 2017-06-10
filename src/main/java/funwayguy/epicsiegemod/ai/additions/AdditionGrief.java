package funwayguy.epicsiegemod.ai.additions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import funwayguy.epicsiegemod.ai.ESM_EntityAIGrief;
import funwayguy.epicsiegemod.api.ITaskAddition;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class AdditionGrief implements ITaskAddition
{
	@Override
	public boolean isTargetTask()
	{
		return false;
	}
	
	@Override
	public int getTaskPriority(EntityLiving entityLiving)
	{
		return 4;
	}
	
	@Override
	public boolean isValid(EntityLiving entityLiving)
	{
		EntityEntry ee = EntityRegistry.getEntry(entityLiving.getClass());
		return ee != null && ESM_Settings.diggerList.contains(ee.getRegistryName());
	}
	
	@Override
	public EntityAIBase getAdditionalAI(EntityLiving entityLiving)
	{
		return new ESM_EntityAIGrief(entityLiving);
	}
}
