package funwayguy.epicsiegemod.api;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

@HasResult
public abstract class EsmTaskEvent extends Event
{
	private final EntityLiving entity;
	
	public EsmTaskEvent(EntityLiving entity)
	{
		this.entity = entity;
	}
	
	public EntityLiving getEntity()
	{
		return entity;
	}
	
	public static class Addition extends EsmTaskEvent
	{
		private final ITaskAddition addition;
		
		public Addition(EntityLiving entity, ITaskAddition addition)
		{
			super(entity);
			this.addition = addition;
		}
		
		public ITaskAddition getAddition()
		{
			return this.addition;
		}
	}
	
	public static class Modified extends EsmTaskEvent
	{
		private final ITaskModifier modifier;
		
		public Modified(EntityLiving entity, ITaskModifier modifier)
		{
			super(entity);
			this.modifier = modifier;
		}
		
		public ITaskModifier getModifier()
		{
			return this.modifier;
		}
	}
}
