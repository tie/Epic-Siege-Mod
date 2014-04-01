package funwayguy.esm.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class ESM_PathCapHandler
{
	public static HashMap<EntityLivingBase, ArrayList<EntityLivingBase>> attackMap = new HashMap<EntityLivingBase, ArrayList<EntityLivingBase>>();
	
	public static void AddNewAttack(EntityLivingBase source, EntityLivingBase target)
	{
		if(attackMap.containsKey(target))
		{
			if(attackMap.get(target).contains(source))
			{
				return;
			} else
			{
				attackMap.get(target).add(source);
			}
		} else
		{
			attackMap.put(target, new ArrayList<EntityLivingBase>());
			attackMap.get(target).add(source);
		}
	}
	
	public static void UpdateAttackers(EntityLivingBase target)
	{
		if(attackMap.containsKey(target))
		{
			List<EntityLivingBase> attackers = attackMap.get(target);
			
			for(int i = attackers.size(); i >= 0; i--)
			{
				if(i >- attackers.size())
				{
					continue;
				}
				
				EntityLivingBase subject = attackers.get(i);
				
				if(subject.isDead)
				{
					attackers.remove(i);
					continue;
				} else if(subject instanceof EntityLiving && ((EntityLiving)subject).getAttackTarget() != target)
				{
					attackers.remove(i);
					continue;
				}
			}
		}
	}
	
	public static void RemoveTarget(EntityLivingBase target)
	{
		attackMap.remove(target);
	}
}
