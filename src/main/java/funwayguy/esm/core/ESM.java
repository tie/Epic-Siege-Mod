package funwayguy.esm.core;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import funwayguy.esm.core.proxies.CommonProxy;
import funwayguy.esm.entities.EntityESMGhast;
import funwayguy.esm.handlers.ESM_EventManager;
import funwayguy.esm.handlers.ESM_UpdateNotification;
import funwayguy.esm.world.dimensions.WorldProviderNewHell;
import funwayguy.esm.world.dimensions.WorldProviderSpace;
import funwayguy.esm.world.gen.WorldGenFortress;

@Mod(modid = ESM_Settings.ID, name = ESM_Settings.Name, version = ESM_Settings.Version, guiFactory = "funwayguy.esm.client.ESMGuiFactory")
public class ESM
{
	@Instance(ESM_Settings.ID)
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
		ESM_EventManager manager = new ESM_EventManager();
		MinecraftForge.EVENT_BUS.register(manager);
		MinecraftForge.TERRAIN_GEN_BUS.register(manager);
		FMLCommonHandler.instance().bus().register(manager);
		FMLCommonHandler.instance().bus().register(new ESM_UpdateNotification());
		
		GameRegistry.registerWorldGenerator(new WorldGenFortress(), 0);
		EntityRegistry.registerModEntity(EntityESMGhast.class, "ESM_Ghast", EntityRegistry.findGlobalUniqueEntityId(), instance, 128, 1, true);
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
}
