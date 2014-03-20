package funwayguy.esm.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.Configuration;

public class ESM_Settings
{
	//Mod Data
	public static final String Version = "9.0";
	public static final String ID = "ESM";
	public static final String Channel = "ESM";
	public static final String Name = "Epic Siege Mod";
	public static final String Proxy = "funwayguy.esm.core.proxies";
	
	//Main
	public static int Awareness; //50%
	public static boolean Xray;
	public static int TargetCap;
	public static boolean VillagerTarget;
	public static boolean Apocalypse; //NEEDS FIXING
	public static boolean Chaos;
	public static boolean AllowSleep; //DONE
	
	//Creeper
	public static boolean CreeperBreaching; //DONE
	public static boolean CreeperNapalm; //50%
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
	public static String EndermanMode;
	public static boolean EndermanPlayerTele;
	
	//Advanced
	public static boolean SpiderBombs; //DONE
	public static int SpiderBombRarity; //DONE
	public static boolean WitherSkeletons; //DONE
	public static int WitherSkeletonRarity; //DONE
	
	//Generation
    public static int EndType;
    public static int NetherType;
    public static boolean SpawnForts;
    
    //Non-configurables
    public static ArrayList FortDB;
    public static WorldServer[] currentWorlds = null;
    public static File currentWorldConfig = null;
	
	public static void LoadConfig()
	{
		if(currentWorldConfig == null)
		{
			return;
		}
		Configuration config = new Configuration(currentWorldConfig);
		System.out.println("Loaded ESM Config: " + currentWorldConfig.getAbsolutePath());
		
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
        EndType = config.get("World", "End Type", 0).getInt(0);
        NetherType = config.get("World", "Nether Type", 0).getInt(0);
        SpawnForts = config.get("World", "Spawn Forts", false).getBoolean(false);
        
        config.save();
        
        System.out.println("Successfully loaded ESM configs");
	}

	public static EntityLivingBase GetNearestValidTarget(EntityLiving entityLiving)
	{
		return entityLiving.worldObj.getClosestVulnerablePlayerToEntity(entityLiving, ESM_Settings.Awareness);
	}
}
