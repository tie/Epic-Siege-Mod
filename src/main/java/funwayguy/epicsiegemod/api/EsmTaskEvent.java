package funwayguy.epicsiegemod.api;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

@HasResult
public class EsmTaskEvent extends Event
{
	public static class Addition extends EsmTaskEvent
	{
		private final ITaskAddition addition;
		
		public Addition(ITaskAddition addition)
		{
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
		
		public Modified(ITaskModifier modifier)
		{
			this.modifier = modifier;
		}
		
		public ITaskModifier getModifier()
		{
			return this.modifier;
		}
	}
}
