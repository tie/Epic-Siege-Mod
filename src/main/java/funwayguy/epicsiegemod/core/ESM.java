package funwayguy.epicsiegemod.core;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;
import funwayguy.epicsiegemod.core.proxies.CommonProxy;
import funwayguy.epicsiegemod.handlers.ConfigHandler;

@Mod(modid = ESM.MODID, name = ESM.NAME, version = ESM.VERSION, guiFactory = "funwayguy.epicsiegemod.client.ESMGuiFactory")
public class ESM
{
	public static final String MODID = "epicsiegemod";
	public static final String VERSION = "FWG_ESM_VER";
	public static final String NAME = "Epic Siege Mod";
	public static final String PROXY = "funwayguy.epicsiegemod.core.proxies";
	public static final String CHANNEL = "ESM_CH";
	
	@Instance(ESM.MODID)
    public static ESM instance;
	
	@SidedProxy(clientSide = ESM.PROXY + ".ClientProxy", serverSide = ESM.PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public SimpleNetworkWrapper network;
	public static Logger logger;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
		
		ConfigHandler.config = new Configuration(event.getSuggestedConfigurationFile());
		ConfigHandler.initConfigs();
		
		proxy.registerHandlers();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}
}
