package funwayguy.esm.core;

import java.util.logging.Logger;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraft.world.gen.structure.StructureScatteredFeatureStart;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import funwayguy.esm.core.proxies.CommonProxy;
import funwayguy.esm.handlers.ESM_EventManager;
import funwayguy.esm.world.dimensions.WorldProviderNewHell;
import funwayguy.esm.world.dimensions.WorldProviderSpace;
import funwayguy.esm.world.gen.WorldGenFortress;

@Mod(modid = ESM_Settings.ID, name = ESM_Settings.Name, version = ESM_Settings.Version)
@NetworkMod(channels = {ESM_Settings.Channel}, clientSideRequired = false, serverSideRequired = false)

public class ESM
{
	@Instance("ESM")
    public static ESM instance;
	
	@SidedProxy(clientSide = ESM_Settings.Proxy + ".ClientProxy", serverSide = ESM_Settings.Proxy + ".CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger log;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		log = event.getModLog();
		ESM_Settings.LoadMainConfig(event.getSuggestedConfigurationFile());
		ESM_Utils.replaceEndPortal();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ESM_EventManager());
		MinecraftForge.TERRAIN_GEN_BUS.register(new ESM_EventManager());
		
		DimensionManager.registerProviderType(ESM_Settings.SpaceDimID, WorldProviderSpace.class, false);
		DimensionManager.registerDimension(ESM_Settings.SpaceDimID, ESM_Settings.SpaceDimID);
		DimensionManager.registerProviderType(ESM_Settings.HellDimID, WorldProviderNewHell.class, false);
		DimensionManager.registerDimension(ESM_Settings.HellDimID,ESM_Settings.HellDimID);
		
		GameRegistry.registerWorldGenerator(new WorldGenFortress());
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
}
