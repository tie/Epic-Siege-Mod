package funwayguy.esm.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.EnumSet;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import funwayguy.esm.core.ESM;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.handlers.entities.ESM_CreeperHandler;

public class ESM_ServerScheduledTickHandler implements ITickHandler
{
	static List<int[]> breachSchedule = new ArrayList<int[]>();
	
	@Override public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if(ESM_Settings.currentWorlds != null)
		{
			if(!ESM_Settings.currentWorlds[0].isRemote)
			{
				updateBreachSchedule();
			}
		}
	}
	
	@Override public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}
	
	@Override public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}
	
	@Override public String getLabel()
	{
		return null;
	}
	
	public static void registerNewBreach(EntityCreeper creeper)
	{
		if(creeper.worldObj.isRemote || ESM_Settings.currentWorlds == null)
		{
			return;
		}
		
		if(!breachSchedule.isEmpty())
		{
			Iterator<int[]> iterator = breachSchedule.iterator();
			while(iterator.hasNext())
			{
				int[] details = iterator.next();
				if(details.length == 7)
				{
					int id = details[4];
					if(id == creeper.entityId)
					{
						return;
					}
				} else
				{
					ESM.log.log(Level.WARNING, "Scheduled breach contained illegal details! Removing...");
					iterator.remove();
				}
			}
		}
		
		int[] entry = new int[7];
		entry[0] = 30;
		entry[1] = (int)creeper.posX;
		entry[2] = (int)creeper.posY;
		entry[3] = (int)creeper.posZ;
		entry[4] = creeper.entityId;
		entry[5] = ESM_CreeperHandler.getCreeperRadius(creeper);
		entry[6] = creeper.worldObj.provider.dimensionId;
		breachSchedule.add(entry);
		creeper.setDead();
		
		if(ESM_Settings.currentWorlds[entry[6]] != creeper.worldObj)
		{
			ESM.log.log(Level.WARNING, "Scheduled breach is in a different world to source entity!");
		}
	}
	
	public static void updateBreachSchedule()
	{
		List<int[]> updatedList = new ArrayList<int[]>();
		
		if(!breachSchedule.isEmpty())
		{
			Iterator<int[]> iterator = breachSchedule.iterator();
			while(iterator.hasNext())
			{
				int[] details = iterator.next();
				if(details.length == 7)
				{
					int timeRemaining = details[0];
					if(timeRemaining <= 0)
					{
						ESM_Settings.currentWorlds[details[6]].newExplosion((Entity)null, details[1], details[2], details[3], details[5], ESM_Settings.CreeperNapalm, ESM_Settings.currentWorlds[details[6]].getGameRules().getGameRuleBooleanValue("mobGriefing"));
						iterator.remove();
					} else
					{
						if(details[0] == 30)
						{
							ESM_Settings.currentWorlds[details[6]].playSoundEffect(details[1], details[2], details[3], "random.fuse", 1.0F, 0.5F);
						}
						
						iterator.remove();
						details[0] -= 1;
						updatedList.add(details);
					}
				} else
				{
					ESM.log.log(Level.WARNING, "Scheduled breach contained illegal details! Removing...");
					iterator.remove();
				}
			}
		}
		
		
		if(!updatedList.isEmpty())
		{
			Iterator<int[]> updateIterator = updatedList.iterator();
			
			while(updateIterator.hasNext())
			{
				breachSchedule.add(updateIterator.next());
			}
			
			updatedList.clear();
		}
	}
}
