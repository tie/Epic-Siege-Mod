package funwayguy.esm.ai;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class GenericEntitySelector implements IEntitySelector
{
	EntityLivingBase host;
	
	public GenericEntitySelector(EntityLivingBase host)
	{
		this.host = host;
	}
	
	@Override
	public boolean isEntityApplicable(Entity subject)
	{
		if(!(subject instanceof EntityLivingBase))
		{
			return false;
		}
		
		if(subject == null || subject.isDead || ((EntityLivingBase)subject).getHealth() <= 0F)
		{
			return false;
		} else if(!ESM_Settings.friendlyFire && host instanceof IMob && (ESM_Settings.Chaos? host.getClass() == subject.getClass() : subject instanceof IMob))
		{
			return false;
		}
		
		if(subject instanceof EntityPlayer)
		{
			EntityPlayer tmpPlayer = (EntityPlayer)subject;
			
			if(tmpPlayer.capabilities.disableDamage)
			{
				return false;
			}
		} else if(subject instanceof EntityVillager)
		{
			if(!ESM_Settings.VillagerTarget)
			{
				return false;
			}
		} else if(!ESM_Settings.Chaos)
		{
			return false;
		}
		
		return (ESM_Settings.Xray || host instanceof EntitySpider || host.canEntityBeSeen(subject));
	}
	
}
