package funwayguy.esm;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

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
