package funwayguy.epicsiegemod.handlers.entities;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import funwayguy.epicsiegemod.capabilities.modified.CapabilityModifiedHandler;
import funwayguy.epicsiegemod.capabilities.modified.IModifiedHandler;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class WitchHandler
{
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		if(event.getEntity().getClass() != EntityPotion.class)
		{
			return;
		}
		
		IModifiedHandler handler;
		
		if(event.getEntity().hasCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null))
		{
			handler = event.getEntity().getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
			
			if(handler.isModified())
			{
				return;
			} else
			{
				handler.setModified(true);
			}
		} else
		{
			return; // This handler needs to be present
		}
		
		EntityPotion potion = (EntityPotion)event.getEntity();
		
		if(!(potion.getThrower() instanceof EntityWitch))
		{
			PotionEffect effect = null;
			
			if(ESM_Settings.customPotions.length > 0)
			{
				String[] type = ESM_Settings.customPotions[event.getWorld().rand.nextInt(ESM_Settings.customPotions.length)].split(":");
				
				if(type.length == 4)
				{
					try
					{
						Potion p = Potion.getPotionFromResourceLocation(type[0] + ":" + type[1]);
						
						if(p != null)
						{
							effect = new PotionEffect(p, Integer.parseInt(type[2], Integer.parseInt(type[3])));
						}
					} catch(Exception e)
					{
						effect = null;
					}
				}
			}
			
			if(effect != null)
			{
				ItemStack itemPotion = new ItemStack(Items.POTIONITEM);
				PotionUtils.addPotionToItemStack(itemPotion, new PotionType(effect));
				potion.setItem(itemPotion);
			}
		}
	}
}
