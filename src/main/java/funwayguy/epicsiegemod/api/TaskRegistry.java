package funwayguy.epicsiegemod.api;

import java.util.ArrayList;
import java.util.List;

public final class TaskRegistry
{
	public static final TaskRegistry INSTANCE = new TaskRegistry();
	
	private List<ITaskAddition> additions = new ArrayList<>();
	private List<ITaskModifier> modifiers = new ArrayList<>();
	
	public void registerTaskModifier(ITaskModifier mod)
	{
		if(mod != null && !modifiers.contains(mod))
		{
			modifiers.add(mod);
		}
	}
	
	public void registerTaskAddition(ITaskAddition add)
	{
		if(add != null && !additions.contains(add))
		{
			additions.add(add);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ITaskModifier> getAllModifiers()
	{
		return modifiers;
	}
	
	@SuppressWarnings("unchecked")
	public List<ITaskAddition> getAllAdditions()
	{
		return additions;
	}
}
