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
import java.util.logging.Level;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.Configuration;

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
    public static boolean NewEnd; //50% (Requires alternative access methods)
    public static int SpaceDimID = 2; //DONE
    public static boolean NewHell; //DONE
    public static int HellDimID = -2; //DONE
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
        
        SpaceDimID = config.get("World", "Space ID", 2).getInt(2);
        HellDimID = config.get("World", "New Hell ID", -2).getInt(-2);

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
        Awareness = config.get("Main", "Awareness Radius", 16).getInt(16);
        Xray = config.get("Main", "Xray Mobs", false).getBoolean(false);
        TargetCap = config.get("Main", "Pathing Cap", -1).getInt(-1);
        VillagerTarget = config.get("Main", "Villager Targeting", false).getBoolean(false);
        Apocalypse = config.get("Main", "Apocalypse Mode", false).getBoolean(false);
        Chaos = config.get("Main", "Chaos Mode", false).getBoolean(false);
        AllowSleep = config.get("Main", "Allow Sleep", true).getBoolean(true);
        
        //Creeper
        CreeperBreaching = config.get("Creeper", "Breaching", false).getBoolean(false);
        CreeperNapalm = config.get("Creeper", "Napalm", false).getBoolean(false);
        CreeperPowered = config.get("Creeper", "Powered", false).getBoolean(false);
        CreeperPoweredRarity = config.get("Creeper", "Powered Rarity", 9).getInt(9);
        
        //Skeletons
        SkeletonAccuracy = config.get("Skeleton", "Arrow Error", 12).getInt(12);
        SkeletonDistance = config.get("Skeleton", "Fire Distance", 16).getInt(16);
        
        //Zombies
        ZombieInfectious = config.get("Zombie", "Infectious", false).getBoolean(false);
        
        //Blazes
        BlazeSpawn = config.get("Blaze", "Spawn", false).getBoolean(false);
        BlazeRarity = config.get("Blaze", "Rarity", 9).getInt(9);
        BlazeFireballs = config.get("Blaze", "Fireballs", 9).getInt(9);
        
        //Ghasts
        GhastSpawn = config.get("Ghast", "Spawn", false).getBoolean(false);
        GhastRarity = config.get("Ghast", "Rarity", 9).getInt(9);
        
        //Endermen
        EndermanMode = config.get("Enderman", "Mode", "Default").getString();
    	EndermanPlayerTele = config.get("Enderman", "Player Teleport", false).getBoolean(false);
        
        
        //Advanced
        SpiderBombs = config.get("Advanced Mobs", "Spider Bombs", false).getBoolean(false);
        SpiderBombRarity = config.get("Advanced Mobs", "Spider Bomb Rarity", 9).getInt(9);
        WitherSkeletons = config.get("Advanced Mobs", "Wither Skeletons", false).getBoolean(false);
        WitherSkeletonRarity = config.get("Advanced Mobs", "Wither Skeleton Rarity", 9).getInt(9);
        
        //World
        NewEnd = config.get("World", "Use New End", false).getBoolean(false);
        //SpaceDimID = config.get("World", "Space ID", 2).getInt(2);
        NewHell = config.get("World", "Use New Nether", false).getBoolean(false);
        //HellDimID = config.get("World", "New Hell ID", -2).getInt(-2);
        SpawnForts = config.get("World", "Spawn Forts", false).getBoolean(false);
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
