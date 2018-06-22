package funwayguy.epicsiegemod.core.proxies;

import funwayguy.epicsiegemod.client.ESMSounds;

public class ClientProxy extends CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public void registerHandlers()
	{
		super.registerHandlers();
		
		ESMSounds.registerSounds();
	}
}
