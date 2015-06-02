package funwayguy.esm.handlers.entities;

import net.minecraft.entity.monster.EntityBlaze;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import funwayguy.esm.core.ESM_Settings;

public class ESM_BlazeHandler
{
	public static void onEntityJoinWorld(EntityBlaze blaze)
	{
		blaze.getEntityData().setInteger("ESM_FIREBALLS", 0);
	}
	
	public static void onLivingUpdate(EntityBlaze blaze)
	{
		if(blaze.attackTime == 6 && blaze.getEntityToAttack() != null)
		{
			int fireballs = ObfuscationReflectionHelper.getPrivateValue(EntityBlaze.class, blaze, "field_70846_g");;
			
			if(fireballs > 1 && fireballs < 5 && blaze.getEntityData().getInteger("ESM_FIREBALLS") < ESM_Settings.BlazeFireballs)
			{
				ObfuscationReflectionHelper.setPrivateValue(EntityBlaze.class, blaze, 2, "field_70846_g");
			} else if(fireballs > 1)
			{
				ObfuscationReflectionHelper.setPrivateValue(EntityBlaze.class, blaze, 5, "field_70846_g");
				blaze.getEntityData().setInteger("ESM_FIREBALLS", 0);
			}
		}
	}
}
