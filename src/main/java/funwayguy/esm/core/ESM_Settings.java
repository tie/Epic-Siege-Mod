package funwayguy.esm.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public class ESM_Settings
{
	//Mod Data
	public static final String Version = "FWG_ESM_VER";
	public static final String ID = "ESM";
	public static final String Channel = "ESM";
	public static final String Name = "Epic Siege Mod";
	public static final String Proxy = "funwayguy.esm.core.proxies";
	
	//Main
	public static int Awareness; //DONE
	public static boolean Xray; //DONE
	public static int TargetCap; //DONE
	public static boolean VillagerTarget; //DONE
	public static boolean Apocalypse; //DONE
	public static boolean Chaos; //DONE
	public static boolean AllowSleep; //DONE
	public static boolean QuickPathing; //DONE
	
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
	public static double GhastFireDelay; //DONE
	public static boolean GhastBreaching; //DONE
	public static double GhastFireDist; //DONE
	
	//Skeleton
	public static int SkeletonDistance; //DONE
	public static int SkeletonAccuracy; //DONE
	
	//Zombie
	public static boolean ZombieInfectious; //DONE
	public static boolean ZombieDiggers; //DONE
	public static boolean ZombieDiggerTools; //DONE
	
	//Enderman
	public static String EndermanMode; //DONE
	public static boolean EndermanPlayerTele; //DONE
	
	//Advanced
	public static ArrayList<Integer> MobBombs; //DONE
	public static int MobBombRarity; //DONE
	public static boolean MobBombAll; // DONE
	public static boolean WitherSkeletons; //DONE
	public static int WitherSkeletonRarity; //DONE
	
	//Generation
    public static boolean NewEnd; //DONE
    public static boolean NewHell; //DONE
    public static boolean SpawnForts;
    public static int fortRarity = 2;
    public static int fortDistance = 16;
    public static ArrayList<Integer> fortDimensions = new ArrayList<Integer>();
    public static boolean fallFromEnd = true;
    
    //Non-configurables
    public static ArrayList<String> fortDB = new ArrayList<String>();
    public static WorldServer[] currentWorlds = null;
    public static File worldDir = null;
	public static boolean ambiguous_AI = true;

	public static void LoadMainConfig(File file)
	{
		Configuration config = new Configuration(file);
		ESM.log.log(Level.INFO, "Loading ESM Global Config");
		
        config.load();
        
        config.setCategoryComment("World", "For the main list of options please refer to the ESM_Options.cfg file in your world directory.");
        
        NewEnd = config.get("World", "Use New End", false).getBoolean(false);
        NewHell = config.get("World", "Use New Nether", false).getBoolean(false);

        config.save();
	}
	
	public static void LoadWorldConfig()
	{
		if(worldDir == null)
		{
			ESM.log.log(Level.ERROR, "Failed to load world configs! Directory is null");
			return;
		}
		
		File conFile = new File(worldDir, "ESM_Options.cfg");
		
		if(!conFile.exists())
		{
			try
			{
				conFile.createNewFile();
			} catch(Exception e)
			{
				ESM.log.log(Level.INFO, "Failed to load ESM Config: " + conFile.getPath(), e);
				return;
			}
		}
		
		Configuration config = new Configuration(conFile, true);
		ESM.log.log(Level.INFO, "Loading ESM Config: " + conFile.getPath());
		
        config.load();
        
        //Main
        Awareness = config.get("Main", "Awareness Radius", 64).getInt(64);
        Xray = config.get("Main", "Xray Mobs", true).getBoolean(true);
        TargetCap = config.get("Main", "Pathing Cap", 16).getInt(16);
        VillagerTarget = config.get("Main", "Villager Targeting", true).getBoolean(true);
        Apocalypse = config.get("Main", "Apocalypse Mode", false).getBoolean(false);
        Chaos = config.get("Main", "Chaos Mode", false).getBoolean(false);
        AllowSleep = config.get("Main", "Allow Sleep", false).getBoolean(false);
        ambiguous_AI = config.get("Main", "Ambiguous AI", true, "If set to true, ESM will not check whether the entity is a mob or not when setting up new AI").getBoolean(true);
        QuickPathing = config.get("Main", "Quick Pathing", false, "If set to fales, mobs can use much longer routes to get to their target").getBoolean(false);
        
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
        ZombieDiggers = config.get("Zombie", "Diggers", true).getBoolean(true);
        ZombieDiggerTools = config.get("Zombie", "Need Required Tools", true).getBoolean(true);
        
        //Blazes
        BlazeSpawn = config.get("Blaze", "Spawn", true).getBoolean(true);
        BlazeRarity = config.get("Blaze", "Rarity", 9).getInt(9);
        BlazeFireballs = config.get("Blaze", "Fireballs", 9).getInt(9);
        
        //Ghasts
        GhastSpawn = config.get("Ghast", "Spawn", false).getBoolean(false);
        GhastRarity = config.get("Ghast", "Rarity", 9).getInt(9);
        GhastFireDelay = config.get("Ghast", "Fire Delay", 1.0D).getDouble(1.0D);
        GhastBreaching = config.get("Ghast", "Breaching", true).getBoolean(true);
        GhastFireDist = config.get("Ghast", "Fire Distance", 64.0D).getDouble(64.0D);
        
        //Endermen
        EndermanMode = config.get("Enderman", "Mode", "Slender", "Valid Endermen Modes (Slender, Normal)").getString();
    	EndermanPlayerTele = config.get("Enderman", "Player Teleport", true).getBoolean(true);
        
        
        //Advanced
        int[] tmp = config.get("Advanced Mobs", "Mob Bombs", new int[]{52}).getIntList();
        MobBombs = new ArrayList<Integer>();
        for(int id : tmp)
        {
        	MobBombs.add(id);
        }
        MobBombRarity = config.get("Advanced Mobs", "Mob Bomb Rarity", 9).getInt(9);
        MobBombAll = config.get("Advanced Mobs", "Mob Bomb All", true, "Skip the Mob Bomb list and allow everything!").getBoolean(true);
        WitherSkeletons = config.get("Advanced Mobs", "Wither Skeletons", true).getBoolean(true);
        WitherSkeletonRarity = config.get("Advanced Mobs", "Wither Skeleton Rarity", 9).getInt(9);
        
        //World
        SpawnForts = config.get("World", "Spawn Forts", true).getBoolean(true);
        fortRarity = config.get("World", "Fort Rarity", 100).getInt(100);
        fortDistance = config.get("World", "Fort Distance", 1024).getInt(1024);
        fallFromEnd = config.get("World", "Fall From End", true, "Whether the player should fall into the overworld from the new End").getBoolean(true);
        int[] tmpFD = config.get("World", "Fort Dimensions", new int[]{0}).getIntList();
        
        for(int dimID : tmpFD)
        {
        	fortDimensions.add(dimID);
        }
        
        config.save();
        
        fortDB = loadFortDB();
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> loadFortDB()
	{
		File fileFortDB = new File(worldDir, "ESM_fortDB");
		ESM.log.log(Level.INFO, "Loading fortDB from " + fileFortDB.getPath());
		
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
			} catch(Exception e)
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
		
		File fileFortDB = new File(worldDir, "ESM_fortDB");
		
		ESM.log.log(Level.INFO, "Saving fortDB to " + fileFortDB.getPath());
		
		try
		{
			if(!fileFortDB.exists())
			{
				fileFortDB.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream(fileFortDB);
			BufferedOutputStream buffer = new BufferedOutputStream(fileOut);
			ObjectOutputStream objOut = new ObjectOutputStream(buffer);
			
			objOut.writeObject(fortDB);
			
			objOut.close();
			buffer.close();
			fileOut.close();
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
}
