package funwayguy.esm.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.Level;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import funwayguy.esm.core.ESM;
import funwayguy.esm.core.ESM_Settings;

public class ESM_PathCapHandler
{
	public static HashMap<EntityLivingBase, ArrayList<EntityLivingBase>> attackMap = new HashMap<EntityLivingBase, ArrayList<EntityLivingBase>>();
	
	public static void AddNewAttack(EntityLivingBase source, EntityLivingBase target)
	{
		if(source == null || target == null || !source.isEntityAlive() || !target.isEntityAlive())
		{
			return;
		}
		
		if(attackMap.size() >= 128)
		{
			return;
		}
		
		if(attackMap.containsKey(target))
		{
			if(attackMap.get(target).contains(source))
			{
				return;
			} else
			{
				attackMap.get(target).add(source);
				
				UpdateAttackers(target);
			}
		} else
		{
			attackMap.put(target, new ArrayList<EntityLivingBase>());
			attackMap.get(target).add(source);
			
			UpdateAttackers(target);
		}
		
		if(attackMap.size() >= 128)
		{
			ESM.log.log(Level.WARN, "Hard limit of 128 active targets has been reached!");
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void UpdateAttackers(EntityLivingBase target)
	{
		if(attackMap.containsKey(target))
		{
			if(!target.isDead || target.getHealth() <= 0)
			{
				attackMap.remove(target);
				return;
			}
			
			ArrayList<EntityLivingBase> attackers = attackMap.get(target);
			
			// Clear all invalid attackers
			for(int i = attackers.size() - 1; i >= 0; i--)
			{
				EntityLivingBase subject = attackers.get(i);
				
				if(subject == null || subject.isDead || subject.getHealth() <= 0 || subject.dimension != target.dimension || (subject instanceof EntityPlayer && ((EntityPlayer)subject).capabilities.disableDamage))
				{
					attackers.remove(i);
					continue;
				}
				
				if(subject.getAITarget() == target)
				{
					continue;
				} else if(subject instanceof EntityLiving && ((EntityLiving)subject).getAttackTarget() == target)
				{
					continue;
				} else if(subject instanceof EntityCreature && ((EntityCreature)subject).getEntityToAttack() == target)
				{
					continue;
				} else
				{
					attackers.remove(i);
				}
			}
			
			if(attackers.size() > ESM_Settings.TargetCap && ESM_Settings.TargetCap >= 0)
			{
				// There are too many attackers so we will start culling them back
				for(int i = attackers.size() - 1; i >= 0; i--)
				{
					EntityLivingBase entity = attackers.get(i);
					
					boolean flag = false;
					
					Iterator<EntityLivingBase> iterator = attackers.iterator();
					
					while(iterator.hasNext())
					{
						if(iterator.next().getDistanceToEntity(target) > entity.getDistanceToEntity(target))
						{
							flag = true;
							break;
						}
					}
					
					if(!flag)
					{
						attackers.remove(i);
						if(entity instanceof EntityLiving)
						{
							((EntityLiving)entity).setAttackTarget(null);
							
							if(entity instanceof EntityCreature)
							{
								((EntityCreature)entity).setTarget(null);
							}
							((EntityLiving)entity).getNavigator().clearPathEntity();
						}
					}
					
					if(attackers.size() <= ESM_Settings.TargetCap)
					{
						break;
					}
				}
			}

			Collections.sort(attackers, new EntityAINearestAttackableTarget.Sorter(target));
			attackMap.put(target, attackers); // Apply changes after sorting the list
		}
	}
	
	public static void RemoveTarget(EntityLivingBase target)
	{
		attackMap.remove(target);
	}
}
