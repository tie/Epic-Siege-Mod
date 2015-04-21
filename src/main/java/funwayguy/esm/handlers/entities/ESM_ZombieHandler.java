package funwayguy.esm.handlers.entities;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import funwayguy.esm.ai.AIUtils;
import funwayguy.esm.core.ESM_Settings;

public class ESM_ZombieHandler
{
	public static void onLivingUpdate(EntityZombie zombie)
	{
		if (!zombie.worldObj.isRemote && zombie.canPickUpLoot() && !zombie.isDead && zombie.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && ESM_Settings.ZombieDiggers)
        {
            @SuppressWarnings("unchecked")
			List<EntityItem> list = zombie.worldObj.getEntitiesWithinAABB(EntityItem.class, zombie.boundingBox.expand(1.0D, 0.0D, 1.0D));
            Iterator<EntityItem> iterator = list.iterator();
            ItemStack itemstack1 = zombie.getEquipmentInSlot(0);
            
            if(itemstack1 != null && itemstack1.getItem() != null && itemstack1.getItem() instanceof ItemSword)
            {
            	return;
            }

            while (iterator.hasNext())
            {
                EntityItem entityitem = iterator.next();

                if (!entityitem.isDead && entityitem.getEntityItem() != null)
                {
                    ItemStack itemstack = entityitem.getEntityItem();
                    int i = EntityZombie.getArmorPosition(itemstack);

                    if (i == 0 && itemstack != null && itemstack.getItem() != null && itemstack.getItem().canHarvestBlock(Blocks.stone, itemstack))
                    {
                        boolean flag = true;
                        
                        if(itemstack1 != null && itemstack1.getItem() != null)
                        {
	                        float curDigSpeed = AIUtils.getBreakSpeed(zombie, itemstack1, Blocks.stone, 0);
	                        float newDigSpeed = AIUtils.getBreakSpeed(zombie, itemstack, Blocks.stone, 0);
	                        
	                    	flag = newDigSpeed > curDigSpeed;
                        }

                        if (flag)
                        {

                            zombie.entityDropItem(itemstack1, 0.0F);

                            zombie.setCurrentItemOrArmor(i, itemstack);
                            zombie.setEquipmentDropChance(i, 2.0F);
                            zombie.func_110163_bv();
                            zombie.onItemPickup(entityitem, 1);
                            entityitem.setDead();
                        }
                    }
                }
            }
        }
	}
}
