package funwayguy.epicsiegemod.ai.hooks;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import funwayguy.epicsiegemod.api.EsmTaskEvent;
import funwayguy.epicsiegemod.api.ITaskModifier;
import funwayguy.epicsiegemod.api.TaskRegistry;

/**
 * Intercepts AI additions to apply modifications or replacements
 */
public class EntityAITasksProxy extends EntityAITasks
{
	EntityLiving host;
	
	public EntityAITasksProxy(EntityLiving host, EntityAITasks original)
	{
		super(host.world == null? null : host.world.profiler);
		this.host = host;
		
		for(EntityAITaskEntry entry : original.taskEntries)
		{
			this.addTask(entry.priority, entry.action);
		}
	}
	
	@Override
	public void addTask(int priority, EntityAIBase task)
	{
		for(ITaskModifier mod : TaskRegistry.INSTANCE.getAllModifiers())
		{
			if(mod.isValid(host, task))
			{
				EntityAIBase ai = mod.getReplacement(host, task);
				
				if(ai != null)
				{
					EsmTaskEvent event = new EsmTaskEvent.Modified(host, mod);
					MinecraftForge.EVENT_BUS.post(event);
					
					if(event.getResult() != Event.Result.DENY)
					{
						super.addTask(priority, ai);
					}
				}
				
				return;
			}
		}
		
		super.addTask(priority, task);
	}
}
