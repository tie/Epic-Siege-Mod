package funwayguy.esm.core.proxies;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import funwayguy.esm.handlers.ESM_ServerScheduledTickHandler;

public class CommonProxy
{
	public void registerTickHandlers()
	{
        TickRegistry.registerTickHandler(new ESM_ServerScheduledTickHandler(), Side.SERVER);
	}
	
	public boolean isClient()
	{
		return false;
	}
}
