package funwayguy.esm.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.apache.logging.log4j.Level;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

public class ESM_Settings
{
	//Mod Data
	public static final String Version = "FWG_ESM_VER";
	public static final String ID = "ESM";
	public static final String Channel = "ESM";
	public static final String Name = "Epic Siege Mod";
	public static final String Proxy = "funwayguy.esm.core.proxies";
	
	//Main
	public static int Awareness; //NEEDS FIXING: Non-pathing entities don't work
	public static boolean Xray; //DONE
	public static int TargetCap; //DONE
	public static boolean VillagerTarget; //DONE
	public static boolean Apocalypse; //DONE
	public static boolean Chaos; //DONE
	public static boolean AllowSleep; //DONE
	
	//Creeper
	public static boolean CreeperBreaching; //DONE
	public static boolean CreeperNapalm; //DONE
	public static boolean CreeperPowered; //DONE
	public static int CreeperPoweredRarity; //DONE
	
	//Blaze
	public static boolean BlazeSpawn; //DONE
	public static int BlazeRarity; //DONE
	public static int BlazeFireballs; //DONE
	
	//Ghast
	public static boolean GhastSpawn; //DONE
	public static int GhastRarity; //DONE
	
	//Skeleton
	public static int SkeletonDistance; //DONE
	public static int SkeletonAccuracy; //DONE
	
	//Zombie
	public static boolean ZombieInfectious; //DONE
	
	//Enderman
	public static String EndermanMode; //DONE
	public static boolean EndermanPlayerTele; //DONE
	
	//Advanced
	public static boolean SpiderBombs; //DONE
	public static int SpiderBombRarity; //DONE
	public static boolean WitherSkeletons; //DONE
	public static int WitherSkeletonRarity; //DONE
	
	//Generation
    public static boolean NewEnd; //DONE
    public static int NewEndID = 2;
    public static boolean NewHell; //DONE
    public static int NewHellID = -2;
    public static boolean SpawnForts;
    public static int fortRarity = 2;
    public static int fortDistance = 16;
    
    //Non-configurables
    public static ArrayList<String> fortDB = new ArrayList<String>();
    public static WorldServer[] currentWorlds = null;
    public static File currentWorldConfig = null;

	public static void LoadMainConfig(File file)
	{
		Configuration config = new Configuration(file);
		ESM.log.log(Level.INFO, "Loading ESM Global Config");
		
        config.load();
        
        config.setCategoryComment("World", "For the main list of options please refer to the ESM_Options.cfg file in your world directory.");
        
        NewEnd = config.get("World", "Use New End", false).getBoolean(false);
        NewHell = config.get("World", "Use New Nether", false).getBoolean(false);
        NewEndID = config.get("World", "New End ID", 2).getInt(2);
        NewHellID = config.get("World", "New Hell ID", -2).getInt(-2);

        config.save();
	}
	
	public static void LoadWorldConfig()
	{
		if(currentWorldConfig == null)
		{
			return;
		}
		Configuration config = new Configuration(currentWorldConfig);
		ESM.log.log(Level.INFO, "Loaded ESM Config: " + currentWorldConfig.getAbsolutePath());
		
        config.load();
        
        //Main
        Awareness = config.get("Main", "Awareness Radius", 64).getInt(64);
        Xray = config.get("Main", "Xray Mobs", true).getBoolean(true);
        TargetCap = config.get("Main", "Pathing Cap", 16).getInt(16);
        VillagerTarget = config.get("Main", "Villager Targeting", true).getBoolean(true);
        Apocalypse = config.get("Main", "Apocalypse Mode", false).getBoolean(false);
        Chaos = config.get("Main", "Chaos Mode", false).getBoolean(false);
        AllowSleep = config.get("Main", "Allow Sleep", false).getBoolean(false);
        
        //Creeper
        CreeperBreaching = config.get("Creeper", "Breaching", true).getBoolean(true);
        CreeperNapalm = config.get("Creeper", "Napalm", true).getBoolean(true);
        CreeperPowered = config.get("Creeper", "Powered", true).getBoolean(true);
        CreeperPoweredRarity = config.get("Creeper", "Powered Rarity", 9).getInt(9);
        
        //Skeletons
        SkeletonAccuracy = config.get("Skeleton", "Arrow Error", 0).getInt(0);
        SkeletonDistance = config.get("Skeleton", "Fire Distance", 64).getInt(64);
        
        //Zombies
        ZombieInfectious = config.get("Zombie", "Infectious", true).getBoolean(true);
        
        //Blazes
        BlazeSpawn = config.get("Blaze", "Spawn", true).getBoolean(true);
        BlazeRarity = config.get("Blaze", "Rarity", 9).getInt(9);
        BlazeFireballs = config.get("Blaze", "Fireballs", 9).getInt(9);
        
        //Ghasts
        GhastSpawn = config.get("Ghast", "Spawn", false).getBoolean(false);
        GhastRarity = config.get("Ghast", "Rarity", 9).getInt(9);
        
        //Endermen
        EndermanMode = config.get("Enderman", "Mode", "Slender", "Valid Endermen Modes (Slender, Normal)").getString();
    	EndermanPlayerTele = config.get("Enderman", "Player Teleport", true).getBoolean(true);
        
        
        //Advanced
        SpiderBombs = config.get("Advanced Mobs", "Spider Bombs", true).getBoolean(true);
        SpiderBombRarity = config.get("Advanced Mobs", "Spider Bomb Rarity", 9).getInt(9);
        WitherSkeletons = config.get("Advanced Mobs", "Wither Skeletons", true).getBoolean(true);
        WitherSkeletonRarity = config.get("Advanced Mobs", "Wither Skeleton Rarity", 9).getInt(9);
        
        //World
        SpawnForts = config.get("World", "Spawn Forts", true).getBoolean(true);
        fortRarity = config.get("World", "Fort Rarity", 100).getInt(100);
        fortDistance = config.get("World", "Fort Distance", 1024).getInt(1024);
        
        config.save();
        
        fortDB = loadFortDB();
	}
	
	public static ArrayList<String> loadFortDB()
	{
		File fileFortDB = new File(currentWorldConfig.getAbsolutePath().replaceAll(currentWorldConfig.getName(), "ESM_fortDB"));
		ESM.log.log(Level.INFO, "Loading fortDB from " + fileFortDB.getAbsolutePath());
		
		if(!fileFortDB.exists())
		{
			return new ArrayList<String>();
		} else
		{
			try
			{
				FileInputStream fileIn = new FileInputStream(fileFortDB);
				BufferedInputStream buffer = new BufferedInputStream(fileIn);
				ObjectInputStream objIn = new ObjectInputStream(buffer);
				
				ArrayList<String> savedDB = (ArrayList<String>)objIn.readObject();
				
				objIn.close();
				buffer.close();
				fileIn.close();
				
				return savedDB;
			} catch(IOException e)
			{
				return new ArrayList<String>();
			} catch(ClassNotFoundException e)
			{
				return new ArrayList<String>();
			} catch(ClassCastException e)
			{
				return new ArrayList<String>();
			}
		}
	}
	
	public static void saveFortDB()
	{
		if(fortDB == null || fortDB.size() <= 0)
		{
			return;
		}
		
		File fileFortDB = new File(currentWorldConfig.getAbsolutePath().replaceAll(currentWorldConfig.getName(), "ESM_fortDB"));
		
		ESM.log.log(Level.INFO, "Saving fortDB to " + fileFortDB.getAbsolutePath());
		
		try
		{
			FileOutputStream fileOut = new FileOutputStream(fileFortDB);
			BufferedOutputStream buffer = new BufferedOutputStream(fileOut);
			ObjectOutputStream objOut = new ObjectOutputStream(buffer);
			
			objOut.writeObject(fortDB);
			
			objOut.close();
			buffer.close();
			fileOut.close();
		} catch(IOException e)
		{
			return;
		}
	}
}
