package funwayguy.epicsiegemod.ai.additions;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import funwayguy.epicsiegemod.ai.ESM_EntityAIDigging;
import funwayguy.epicsiegemod.api.ITaskAddition;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class AdditionDigger implements ITaskAddition
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
		return ESM_Settings.diggerList.contains(EntityList.getEntityString(entityLiving));
	}
	
	@Override
	public EntityAIBase getAdditionalAI(EntityLiving entityLiving)
	{
		if(!entityLiving.worldObj.isRemote && (entityLiving.getRNG().nextInt(20) == 0 || (entityLiving.getRNG().nextInt(3) == 0 && entityLiving.getCustomNameTag().equalsIgnoreCase("Funwayguy"))))
		{
			entityLiving.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
		}
		
		return new ESM_EntityAIDigging(entityLiving);
	}
}
