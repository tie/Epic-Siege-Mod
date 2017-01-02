package funwayguy.epicsiegemod.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.ai.hooks.EntityAITasksProxy;
import funwayguy.epicsiegemod.ai.hooks.EntitySensesProxy;
import funwayguy.epicsiegemod.api.EsmTaskEvent;
import funwayguy.epicsiegemod.api.ITaskAddition;
import funwayguy.epicsiegemod.api.TaskRegistry;
import funwayguy.epicsiegemod.capabilities.combat.CapabilityAttackerHandler;
import funwayguy.epicsiegemod.capabilities.combat.IAttackerHandler;
import funwayguy.epicsiegemod.capabilities.combat.ProviderAttackerHandler;
import funwayguy.epicsiegemod.capabilities.modified.CapabilityModifiedHandler;
import funwayguy.epicsiegemod.capabilities.modified.ProviderModifiedHandler;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class MainHandler
{
	private static boolean hooksReady = false;
	public static Field f_modifiers;
	private static Field f_tasks;
	private static Field f_targetTasks;
	private static Field f_senses;
	private static Field f_navigator;
	
	@SubscribeEvent
	public void onEntityConstruct(EntityJoinWorldEvent event)
	{
		if(!hooksReady)
		{
			return;
		}
		
		if(event.getEntity() instanceof EntityLiving)
		{
			EntityLiving entityLiving = (EntityLiving)event.getEntity();
			
			if(!ESM_Settings.AIExempt.contains(EntityList.getEntityString(entityLiving)))
			{
				try
				{
					f_tasks.set(entityLiving, new EntityAITasksProxy(entityLiving, entityLiving.tasks));
					f_targetTasks.set(entityLiving, new EntityAITasksProxy(entityLiving, entityLiving.targetTasks));
					f_senses.set(entityLiving, new EntitySensesProxy(entityLiving));
					
					if(entityLiving.getNavigator().getClass() == PathNavigateGround.class)
					{
						//f_navigator.set(entityLiving, new ESMPathNavigateGround(entityLiving, event.getWorld()));
					}
				} catch(Exception e)
				{
					ESM.logger.log(Level.ERROR, "Unable to set AI hooks in " + entityLiving.getName(), e);
				}
			}
			
			for(ITaskAddition add : TaskRegistry.INSTANCE.getAllAdditions())
			{
				if(!add.isValid(entityLiving))
				{
					continue;
				}
				
				EntityAIBase entry = add.getAdditionalAI(entityLiving);
				
				if(entry != null)
				{
					EsmTaskEvent taskEvent = new EsmTaskEvent.Addition(add);
					MinecraftForge.EVENT_BUS.post(taskEvent);
					
					if(taskEvent.getResult() != Event.Result.DENY)
					{
						if(add.isTargetTask())
						{
							entityLiving.targetTasks.addTask(add.getTaskPriority(entityLiving), entry);
						} else
						{
							entityLiving.tasks.addTask(add.getTaskPriority(entityLiving), entry);
						}
					}
				}
			}
			
			IAttributeInstance att = entityLiving.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE);
			if(att != null && att.getBaseValue() < ESM_Settings.Awareness)
			{
				att.setBaseValue(ESM_Settings.Awareness);
			}
		}
	}
	
	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof EntityLiving)
		{
			event.addCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_ID, new ProviderAttackerHandler());
			event.addCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_ID, new ProviderModifiedHandler());
		} else if(event.getObject().getClass() == EntityTippedArrow.class || event.getObject().getClass() == EntityPotion.class)
		{
			event.addCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_ID, new ProviderModifiedHandler());
		}
	}
	
	@SubscribeEvent
	public void onTargetSet(LivingSetAttackTargetEvent event)
	{
		if(event.getTarget() != null && event.getEntityLiving() instanceof EntityLiving && event.getTarget().hasCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null))
		{
			IAttackerHandler handler = event.getTarget().getCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null);
			handler.addAttacker(event.getTarget(), (EntityLiving)event.getEntityLiving());
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.getEntityLiving().worldObj.isRemote)
		{
			return;
		}
		
		if(event.getEntityLiving().ticksExisted%20 == 0 && event.getEntityLiving().hasCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null))
		{
			// Culls the attacker count once every second (relative to the lifetime of the target)
			IAttackerHandler handler = event.getEntityLiving().getCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null);
			handler.updateAttackers(event.getEntityLiving());
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event)
	{
		if(event.getModID().equals(ESM.MODID))
		{
			ConfigHandler.config.save();
			ConfigHandler.initConfigs();
		}
	}
	
	static
	{
		// AI hook preparation
		try
		{
			f_modifiers = Field.class.getDeclaredField("modifiers");
			f_modifiers.setAccessible(true);
		} catch(Exception e)
		{
			ESM.logger.log(Level.ERROR, "Unable to enable write access to variable modifiers", e);
		}
		
		try
		{
			f_tasks = EntityLiving.class.getDeclaredField("field_70714_bg");
			f_targetTasks = EntityLiving.class.getDeclaredField("field_70715_bh");
			f_senses = EntityLiving.class.getDeclaredField("field_70723_bA");
			f_navigator = EntityLiving.class.getDeclaredField("field_70699_by");
			f_modifiers.set(f_tasks, f_tasks.getModifiers() & ~Modifier.FINAL);
			f_modifiers.set(f_targetTasks, f_targetTasks.getModifiers() & ~Modifier.FINAL);
			f_tasks.setAccessible(true);
			f_targetTasks.setAccessible(true);
			f_senses.setAccessible(true);
			f_navigator.setAccessible(true);
			hooksReady = true;
		} catch(Exception e1)
		{
			try
			{
				f_tasks = EntityLiving.class.getDeclaredField("tasks");
				f_targetTasks = EntityLiving.class.getDeclaredField("targetTasks");
				f_senses = EntityLiving.class.getDeclaredField("senses");
				f_navigator = EntityLiving.class.getDeclaredField("navigator");
				f_modifiers.set(f_tasks, f_tasks.getModifiers() & ~Modifier.FINAL);
				f_modifiers.set(f_targetTasks, f_targetTasks.getModifiers() & ~Modifier.FINAL);
				f_tasks.setAccessible(true);
				f_targetTasks.setAccessible(true);
				f_senses.setAccessible(true);
				f_navigator.setAccessible(true);
				hooksReady = true;
			} catch(Exception e2)
			{
				ESM.logger.log(Level.ERROR, "Unable to enable write access to AI. Hooks disabled!", e2);
			}
		}
	}
}
