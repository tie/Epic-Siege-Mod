package funwayguy.esm.core.proxies;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import funwayguy.esm.handlers.ESM_ClientScheduledTickHandler;

public class ClientProxy extends CommonProxy
{
	public void registerTickHandlers()
	{
		super.registerTickHandlers();
		TickRegistry.registerTickHandler(new ESM_ClientScheduledTickHandler(), Side.CLIENT);
    }
	
	@Override
	public boolean isClient()
	{
		return true;
	}
}
