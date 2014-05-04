package funwayguy.esm.handlers.entities;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityXPOrb;

public class ESM_DragonHandler
{
	public static void onLivingUpdate(EntityDragon dragon)
	{
		/*if(dragon.deathTicks >= 200 && dragon.worldObj.provider.dimensionId == ESM_Settings.SpaceDimID && !dragon.worldObj.isRemote)
		{
            int i = 2000;

            while (i > 0)
            {
                int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                dragon.worldObj.spawnEntityInWorld(new EntityXPOrb(dragon.worldObj, dragon.posX, dragon.posY, dragon.posZ, j));
            }
            
            dragon.setDead();
		}*/
	}
}
