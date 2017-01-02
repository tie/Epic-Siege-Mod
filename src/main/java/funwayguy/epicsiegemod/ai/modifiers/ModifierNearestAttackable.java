package funwayguy.epicsiegemod.ai.modifiers;

import java.lang.reflect.Field;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.Level;
import com.google.common.base.Predicate;
import funwayguy.epicsiegemod.ai.ESM_EntityAINearestAttackableTarget;
import funwayguy.epicsiegemod.ai.ESM_EntityAISpiderTarget;
import funwayguy.epicsiegemod.api.ITaskModifier;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ModifierNearestAttackable implements ITaskModifier
{
	private static Field f_targetClass;
	private static Field f_targetChance;
	private static Field f_targetSelector;
	
	private static Field f_shouldCheckSight;
	private static Field f_nearbyOnly;
	
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		if(entityLiving instanceof EntitySpider && task instanceof EntityAINearestAttackableTarget)
		{
			return true; // This will need a custom replacement
		}
		
		return task != null && task.getClass() == EntityAINearestAttackableTarget.class;
	}
	
	@Override
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase task)
	{
		if(ESM_Settings.neutralMobs)
		{
			return null;
		}
		
		boolean hasExisting = false;
		ESM_EntityAINearestAttackableTarget ai = null;
		
		for(EntityAITaskEntry t : host.targetTasks.taskEntries)
		{
			if(t.action instanceof ESM_EntityAINearestAttackableTarget)
			{
				ai = (ESM_EntityAINearestAttackableTarget)t.action;
				hasExisting = true;
				break;
			}
		}
		
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends EntityLivingBase> tarClass = (Class<? extends EntityLivingBase>)f_targetClass.get(task);
			int tarChance = f_targetChance.getInt(task);
			Predicate<? super EntityLivingBase> selector = null;//(Predicate<? super EntityLivingBase>)f_targetSelector.get(task);
			
			boolean sight = f_shouldCheckSight.getBoolean(task);
			boolean nearby = f_nearbyOnly.getBoolean(task);
			
			if(ai == null)
			{
				if(host instanceof EntitySpider)
				{
					ai = new ESM_EntityAISpiderTarget((EntitySpider)host);
				} else
				{
					ai = new ESM_EntityAINearestAttackableTarget(host, tarChance, sight, nearby, selector);
				}
				
				if(ESM_Settings.Chaos)
				{
					ai.addTarget(EntityLivingBase.class);
				}
			}
			
			if(!ESM_Settings.Chaos)
			{
				ai.addTarget(tarClass);
				
				if(ESM_Settings.VillagerTarget && EntityPlayer.class.isAssignableFrom(tarClass))
				{
					ai.addTarget(EntityVillager.class);
				}
			}
		} catch(Exception e)
		{
			ESM.logger.log(Level.ERROR, "Hook failed", e);
		}
		
		return hasExisting? null : ai;
	}
	
	static
	{
		try
		{
			f_targetClass = EntityAINearestAttackableTarget.class.getDeclaredField("field_75307_b");
			f_targetChance = EntityAINearestAttackableTarget.class.getDeclaredField("field_75308_c");
			f_targetSelector = EntityAINearestAttackableTarget.class.getDeclaredField("field_82643_g");
			f_targetClass.setAccessible(true);
			f_targetChance.setAccessible(true);
			f_targetSelector.setAccessible(true);
			
			f_shouldCheckSight = EntityAITarget.class.getDeclaredField("field_75297_f");
			f_nearbyOnly = EntityAITarget.class.getDeclaredField("field_75303_a");
			f_shouldCheckSight.setAccessible(true);
			f_nearbyOnly.setAccessible(true);
		} catch(Exception e1)
		{
			try
			{
				f_targetClass = EntityAINearestAttackableTarget.class.getDeclaredField("targetClass");
				f_targetChance = EntityAINearestAttackableTarget.class.getDeclaredField("targetChance");
				f_targetSelector = EntityAINearestAttackableTarget.class.getDeclaredField("targetEntitySelector");
				f_targetClass.setAccessible(true);
				f_targetChance.setAccessible(true);
				f_targetSelector.setAccessible(true);
				
				f_shouldCheckSight = EntityAITarget.class.getDeclaredField("shouldCheckSight");
				f_nearbyOnly = EntityAITarget.class.getDeclaredField("nearbyOnly");
				f_shouldCheckSight.setAccessible(true);
				f_nearbyOnly.setAccessible(true);
			} catch(Exception e2)
			{
				ESM.logger.log(Level.ERROR, "Unable to enable access to AI targetting variables", e2);
			}
		}
	}
}
