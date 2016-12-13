package funwayguy.epicsiegemod.api;

import java.util.ArrayList;

public final class TaskRegistry
{
	public static final TaskRegistry INSTANCE = new TaskRegistry();
	
	private ArrayList<ITaskAddition> additions = new ArrayList<ITaskAddition>();
	private ArrayList<ITaskModifier> modifiers = new ArrayList<ITaskModifier>();
	
	private TaskRegistry()
	{
	}
	
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
	public ArrayList<ITaskModifier> getAllModifiers()
	{
		return (ArrayList<ITaskModifier>)modifiers.clone();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ITaskAddition> getAllAdditions()
	{
		return (ArrayList<ITaskAddition>)additions.clone();
	}
}
