package funwayguy.epicsiegemod.capabilities.modified;

public class ModifiedHandler implements IModifiedHandler
{
	private boolean modified = false;
	
	@Override
	public boolean isModified()
	{
		return modified;
	}

	@Override
	public void setModified(boolean state)
	{
		this.modified = state;
	}
}
