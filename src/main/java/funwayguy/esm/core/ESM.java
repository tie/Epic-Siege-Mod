package funwayguy.esm.core;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import funwayguy.esm.core.proxies.CommonProxy;
import funwayguy.esm.handlers.ESM_EventManager;

@Mod(modid = ESM_Settings.ID, name = ESM_Settings.Name, version = ESM_Settings.Version)
@NetworkMod(channels = {ESM_Settings.Channel}, clientSideRequired = false, serverSideRequired = false)

public class ESM
{
	@Instance("ESM")
    public static ESM instance;
	
	@SidedProxy(clientSide = ESM_Settings.Proxy + ".ClientProxy", serverSide = ESM_Settings.Proxy + ".CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		proxy.registerTickHandlers();
		RegisterESMHandlers();
		System.out.print("Registering TickHandlers for ESM");
	}

	private static void RegisterESMHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new ESM_EventManager());
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
}
