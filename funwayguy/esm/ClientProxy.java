package funwayguy.esm;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

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
