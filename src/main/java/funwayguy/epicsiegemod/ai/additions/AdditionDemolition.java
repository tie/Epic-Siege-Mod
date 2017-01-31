package funwayguy.epicsiegemod.ai.additions;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import funwayguy.epicsiegemod.ai.ESM_EntityAIDemolition;
import funwayguy.epicsiegemod.api.ITaskAddition;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class AdditionDemolition implements ITaskAddition
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
		return ESM_Settings.demolitionList.contains(EntityList.getEntityString(entityLiving));
	}
	
	@Override
	public EntityAIBase getAdditionalAI(EntityLiving entityLiving)
	{
		if(!entityLiving.world.isRemote && (entityLiving.getRNG().nextInt(100) < ESM_Settings.demolitionChance || (entityLiving.getRNG().nextInt(3) == 0 && entityLiving.getCustomNameTag().equalsIgnoreCase("Funwayguy"))))
		{
			entityLiving.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Blocks.TNT));
		}
		
		return new ESM_EntityAIDemolition(entityLiving);
	}
}
