package funwayguy.esm.handlers.entities;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.core.ESM_Utils;

public class ESM_SkeletonHandler
{
	public static void onEntityJoinWorld(EntitySkeleton skeleton)
	{
		skeleton.getEntityData().setBoolean("ESM_SKELETON_SETUP", skeleton.getEntityData().getBoolean("ESM_MODIFIED"));
		skeleton.getEntityData().setBoolean("ESM_MODIFIED", true);
	}
	
	public static void onLivingUpdate(EntitySkeleton skeleton)
	{
		if(skeleton.ticksExisted == 1)
		{
			if(!skeleton.getEntityData().getBoolean("ESM_SKELETON_SETUP"))
			{
				skeleton.getEntityData().setBoolean("ESM_SKELETON_SETUP", true);
				
				if(ESM_Settings.WitherSkeletons && (ESM_Settings.WitherSkeletonRarity <= 0 || skeleton.getRNG().nextInt(ESM_Settings.WitherSkeletonRarity) == 0))
				{
					skeleton.setDead();
					EntitySkeleton newSkeleton = new EntitySkeleton(skeleton.worldObj);
					newSkeleton.setLocationAndAngles(skeleton.posX, skeleton.posY, skeleton.posZ, 0.0F, 0.0F);
					newSkeleton.setSkeletonType(1);
					newSkeleton.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
					newSkeleton.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
					newSkeleton.setCombatTask();
					newSkeleton.getEntityData().setBoolean("ESM_MODIFIED", true);
					skeleton.worldObj.spawnEntityInWorld(newSkeleton);
					ESM_Utils.replaceAI(newSkeleton, true);
					return;
				}
			}
		}
	}
}
