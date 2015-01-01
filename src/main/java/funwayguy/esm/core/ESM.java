package funwayguy.esm.core;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import funwayguy.esm.core.proxies.CommonProxy;
import funwayguy.esm.handlers.ESM_EventManager;
import funwayguy.esm.world.dimensions.WorldProviderNewHell;
import funwayguy.esm.world.dimensions.WorldProviderSpace;
import funwayguy.esm.world.gen.WorldGenFortress;

@Mod(modid = ESM_Settings.ID, name = ESM_Settings.Name, version = ESM_Settings.Version)

public class ESM
{
	@Instance("ESM")
    public static ESM instance;
	
	@SidedProxy(clientSide = ESM_Settings.Proxy + ".ClientProxy", serverSide = ESM_Settings.Proxy + ".CommonProxy")
	public static CommonProxy proxy;
	
	public static org.apache.logging.log4j.Logger log;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		log = event.getModLog();
		ESM_Settings.LoadMainConfig(event.getSuggestedConfigurationFile());
		ESM_Utils.replaceEndPortal();
		
		if(ESM_Settings.NewEnd)
		{
			DimensionManager.unregisterDimension(1);
			DimensionManager.unregisterProviderType(1);
			DimensionManager.registerProviderType(1, WorldProviderSpace.class, false);
			DimensionManager.registerDimension(1, 1);
		}
		
		if(ESM_Settings.NewHell)
		{
			DimensionManager.unregisterDimension(-1);
			DimensionManager.unregisterProviderType(-1);
			DimensionManager.registerProviderType(-1, WorldProviderNewHell.class, false);
			DimensionManager.registerDimension(-1, -1);
		}
		
		//ESM_Utils.replaceEndPortal();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ESM_EventManager());
		MinecraftForge.TERRAIN_GEN_BUS.register(new ESM_EventManager());
		
		GameRegistry.registerWorldGenerator(new WorldGenFortress(), 0);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
}
