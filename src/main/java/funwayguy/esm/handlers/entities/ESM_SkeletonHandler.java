package funwayguy.esm.handlers.entities;

import java.util.List;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ESM_SkeletonHandler
{
	public static void onEntityJoinWorld(EntitySkeleton skeleton)
	{
		if(skeleton.getSkeletonType() == 0)
		{
			if(ESM_Settings.WitherSkeletons && ESM_Settings.WitherSkeletonRarity <= 0)
			{
				skeleton.setDead();
				EntitySkeleton newSkeleton = new EntitySkeleton(skeleton.worldObj);
				newSkeleton.setLocationAndAngles(skeleton.posX, skeleton.posY, skeleton.posZ, 0.0F, 0.0F);
				newSkeleton.setSkeletonType(1);
				newSkeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
				newSkeleton.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0D);
				newSkeleton.setCombatTask();
				newSkeleton.getEntityData().setBoolean("ESM_MODIFIED", true);
				skeleton.worldObj.spawnEntityInWorld(newSkeleton);
			} else if(ESM_Settings.WitherSkeletons && ESM_Settings.WitherSkeletonRarity > 0)
			{
				if(skeleton.getRNG().nextInt(ESM_Settings.WitherSkeletonRarity) == 0)
				{
					skeleton.setDead();
					EntitySkeleton newSkeleton = new EntitySkeleton(skeleton.worldObj);
					newSkeleton.setLocationAndAngles(skeleton.posX, skeleton.posY, skeleton.posZ, 0.0F, 0.0F);
					newSkeleton.setSkeletonType(1);
					newSkeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
					newSkeleton.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0D);
					newSkeleton.setCombatTask();
					newSkeleton.getEntityData().setBoolean("ESM_MODIFIED", true);
					skeleton.worldObj.spawnEntityInWorld(newSkeleton);
				}
			} else
			{
				skeleton.getEntityData().setString("ESM_TASK_ID", skeleton.getUniqueID().toString() + ",NULL");
			}
		}
	}
	
	public static void onLivingUpdate(EntitySkeleton skeleton)
	{
		if(!skeleton.getEntityData().getString("ESM_TASK_ID").equals(skeleton.getUniqueID().toString() + "," + ESM_Settings.SkeletonDistance) && skeleton.getSkeletonType() == 0)
		{
			List<EntityAITaskEntry> taskList = skeleton.tasks.taskEntries;
			
			for(int i = 0; i < taskList.size(); i++)
			{
				EntityAIBase entry = taskList.get(i).action;
				if(entry instanceof EntityAIArrowAttack)
				{
					//taskList.remove(i);
					skeleton.tasks.removeTask(entry);
					skeleton.tasks.addTask(4, new EntityAIArrowAttack(skeleton, 1.0D, 20, 60, (float)ESM_Settings.SkeletonDistance));
					skeleton.getEntityData().setString("ESM_TASK_ID", skeleton.getUniqueID().toString() + "," + ESM_Settings.SkeletonDistance);
					break;
				}
			}
		}
	}
}
