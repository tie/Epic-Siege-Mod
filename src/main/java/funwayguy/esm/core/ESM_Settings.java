package funwayguy.esm.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.util.MathHelper;
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
	public static int ResistanceCoolDown; //DONE
	public static boolean keepLoaded; // DONE
	public static boolean moreSpawning; // DONE
	public static boolean forcePath; // DONE
	
	//Creeper
	public static boolean CreeperBreaching; //DONE
	public static boolean CreeperNapalm; //DONE
	public static boolean CreeperPowered; //DONE
	public static int CreeperPoweredRarity; //DONE
	
	//Blaze
	public static boolean BlazeSpawn; //DONE
	public static int BlazeRarity; //DONE
	public static int BlazeFireballs; //DONE
	public static ArrayList<Integer> BlazeDimensionBlacklist; // DONE
	
	//Ghast
	public static boolean GhastSpawn; //DONE
	public static int GhastRarity; //DONE
	public static double GhastFireDelay; //DONE
	public static boolean GhastBreaching; //DONE
	public static double GhastFireDist; //DONE
	public static ArrayList<Integer> GhastDimensionBlacklist; // DONE
	
	//Skeleton
	public static int SkeletonDistance; //DONE
	public static int SkeletonAccuracy; //DONE
	
	//Zombie
	public static boolean ZombieInfectious; //DONE
	public static boolean ZombieDiggers; //DONE
	public static boolean ZombieDiggerTools; //DONE
	public static boolean ZombieTraps; //DONE
	public static int ZombiePillaring; //DONE
	public static ArrayList<String> ZombieGriefBlocks; //DONE
	public static ArrayList<String> ZombieDigBlacklist; // DONE
	
	//Enderman
	public static String EndermanMode; //DONE
	public static boolean EndermanPlayerTele; //DONE
	
	//Spider
	public static int SpiderWebChance; //DONE
	
	//Advanced
	public static ArrayList<Integer> MobBombs; //DONE
	public static int MobBombRarity; //DONE
	public static boolean MobBombAll; // DONE
	public static boolean WitherSkeletons; //DONE
	public static int WitherSkeletonRarity; //DONE
	public static int PotionMobs;
	public static int[] PotionMobEffects;
	public static boolean attackEvasion;
	
	//Generation
    public static boolean NewEnd; //DONE
    public static boolean NewHell; //DONE
    public static boolean SpawnForts;
    public static int fortRarity = 2;
    public static int fortDistance = 16;
    public static ArrayList<Integer> fortDimensions = new ArrayList<Integer>();
    public static boolean fallFromEnd = true;
    public static ArrayList<String> fortSpawners = new ArrayList<String>();
    
    //Non-configurables
    public static ArrayList<String> fortDB = new ArrayList<String>();
    public static WorldServer[] currentWorlds = null;
    public static File worldDir = null;
	public static boolean ambiguous_AI = true;
	public static Configuration defConfig;
	public static ArrayList<Integer> AIExempt = new ArrayList<Integer>();

	public static void LoadMainConfig(File file)
	{
		Configuration config = new Configuration(file);
		ESM.log.log(Level.INFO, "Loading ESM Global Config");
        
        defConfig = config;
		
        config.load();
        
        config.setCategoryComment("World", "For the main list of options please refer to the ESM_Options.cfg file in your world directory.");
        
        NewEnd = config.get("World", "Use New End", false).getBoolean(false);
        NewHell = config.get("World", "Use New Nether", false).getBoolean(false);
        
        config.save();
        
        ResetToDefault();
	}
	
	public static void ResetToDefault()
	{
		if(defConfig == null)
		{
			ESM.log.log(Level.ERROR, "Failed to reset options to default! Global default is null");
			return;
		}
		
		defConfig.load();
		
        //Main
        Awareness = defConfig.get("Main", "Awareness Radius", 64).getInt(64);
        Xray = defConfig.get("Main", "Xray Mobs", true).getBoolean(true);
        TargetCap = defConfig.get("Main", "Pathing Cap", 16).getInt(16);
        VillagerTarget = defConfig.get("Main", "Villager Targeting", true).getBoolean(true);
        Apocalypse = defConfig.get("Main", "Apocalypse Mode", false).getBoolean(false);
        Chaos = defConfig.get("Main", "Chaos Mode", false).getBoolean(false);
        AllowSleep = defConfig.get("Main", "Allow Sleep", false).getBoolean(false);
        ambiguous_AI = defConfig.get("Main", "Ambiguous AI", true, "If set to true, ESM will not check whether the entity is a mob or not when setting up new AI").getBoolean(true);
        QuickPathing = defConfig.get("Main", "Quick Pathing", false, "If set to fales, mobs can use much longer routes to get to their target").getBoolean(false);
        ResistanceCoolDown = defConfig.get("Main", "Resistance Cooldown", 200, "The amount of ticks of resistance given to the player after changing dimensions").getInt(200);
        keepLoaded = defConfig.get("Main", "Keep Loaded", false, "Keeps mobs with an active target from despawning. Can causes issues with chunk loading/unloading").getBoolean(false);
        moreSpawning = defConfig.get("Main", "More Spawning", true, "Reduces spawning safe zone from 24 blocks to 8 and makes mobs require only basic conditions to spawn").getBoolean(true);
        forcePath = defConfig.get("Main", "Force Non-AI Pathing", false, "Forces non pathing mobs to attack from further away. Can cause additional lag").getBoolean(false);
        
        int[] tmpAIE = defConfig.get("Main", "AI Exempt Mob IDs", new int[]{}).getIntList();
        AIExempt = new ArrayList<Integer>();
        for(int i : tmpAIE)
        {
        	AIExempt.add(i);
        }
        
        //Creeper
        CreeperBreaching = defConfig.get("Creeper", "Breaching", true).getBoolean(true);
        CreeperNapalm = defConfig.get("Creeper", "Napalm", true).getBoolean(true);
        CreeperPowered = defConfig.get("Creeper", "Powered", true).getBoolean(true);
        CreeperPoweredRarity = defConfig.get("Creeper", "Powered Rarity", 9).getInt(9);
        
        //Skeletons
        SkeletonAccuracy = defConfig.get("Skeleton", "Arrow Error", 0).getInt(0);
        SkeletonDistance = defConfig.get("Skeleton", "Fire Distance", 64).getInt(64);
        
        //Zombies
        ZombieInfectious = defConfig.get("Zombie", "Infectious", true).getBoolean(true);
        ZombieDiggers = defConfig.get("Zombie", "Diggers", true).getBoolean(true);
        ZombieDiggerTools = defConfig.get("Zombie", "Need Required Tools", true).getBoolean(true);
        ZombieTraps = defConfig.get("Zombie", "Zombies Build Traps", true).getBoolean(true);
        ZombiePillaring = defConfig.get("Zombie", "Pillaring Blocks", 64, "How many blocks to give zombies to pillar up with").getInt(64);
        String[] defGrief = new String[]{
        	//"minecraft:chest", // Might not be a good idea for loot reasons
        	"minecraft:furnace",
        	"minecraft:crafting_table",
        	"minecraft:melon_stem",
        	"minecraft:pumpkin_stem",
        	"minecraft:fence_gate",
        	"minecraft:melon_block",
        	"minecraft:pumpkin",
        	"minecraft:glass",
        	"minecraft:glass_pane",
        	"minecraft:stained_glass",
        	"minecraft:stained_glass_pane",
        	"minecraft:carrots",
        	"minecraft:potatoes",
        	"minecraft:brewing_stand",
        	"minecraft:enchanting_table",
        	"minecraft:cake",
        	"minecraft:ladder",
        	"minecraft:wooden_door",
        	"minecraft:farmland",
        	"minecraft:bookshelf",
        	"minecraft:sapling",
        	"minecraft:bed",
        	"minecraft:fence"
        };
        ZombieGriefBlocks = new ArrayList<String>(Arrays.asList(defConfig.get("Zombie", "General Griefable Blocks", defGrief, "What blocks will be targeted for destruction when not attacking players (Does not affect general digging, light sources are included by default, add ':#' for metadata e.g. 'minecraft:wool:1')").getStringList()));
        ZombieDigBlacklist = new ArrayList<String>(Arrays.asList(defConfig.get("Zombie", "General Griefable Blocks", new String[]{}, "Blacklisted blocks for digging (Add ':#' for metadata e.g. 'minecraft:wool:1')").getStringList()));
        
        //Blazes
        BlazeSpawn = defConfig.get("Blaze", "Spawn", true).getBoolean(true);
        BlazeRarity = defConfig.get("Blaze", "Rarity", 9).getInt(9);
        BlazeFireballs = defConfig.get("Blaze", "Fireballs", 9).getInt(9);
        int[] tmpBDB = defConfig.get("Blaze", "Dimension Blacklist", new int[]{}).getIntList();
        BlazeDimensionBlacklist = new ArrayList<Integer>();
        for(int i : tmpBDB)
        {
        	BlazeDimensionBlacklist.add(i);
        }
        
        //Ghasts
        GhastSpawn = defConfig.get("Ghast", "Spawn", false).getBoolean(false);
        GhastRarity = defConfig.get("Ghast", "Rarity", 9).getInt(9);
        GhastFireDelay = defConfig.get("Ghast", "Fire Delay", 1.0D).getDouble(1.0D);
        GhastBreaching = defConfig.get("Ghast", "Breaching", true).getBoolean(true);
        GhastFireDist = defConfig.get("Ghast", "Fire Distance", 64.0D).getDouble(64.0D);
        int[] tmpGDB = defConfig.get("Ghast", "Dimension Blacklist", new int[]{}).getIntList();
        GhastDimensionBlacklist = new ArrayList<Integer>();
        for(int i : tmpGDB)
        {
        	GhastDimensionBlacklist.add(i);
        }
        
        //Endermen
        EndermanMode = defConfig.get("Enderman", "Mode", "Slender", "Valid Endermen Modes (Slender, Normal)").getString();
    	EndermanPlayerTele = defConfig.get("Enderman", "Player Teleport", true).getBoolean(true);
        
    	//Spider
        SpiderWebChance = MathHelper.clamp_int(defConfig.get("Spider", "Webbing Chance (0-100)", 25).getInt(25), 0, 100);
        
        //Advanced
        int[] tmp = defConfig.get("Advanced Mobs", "Mob Bombs", new int[]{52}).getIntList();
        MobBombs = new ArrayList<Integer>();
        for(int id : tmp)
        {
        	MobBombs.add(id);
        }
        MobBombRarity = defConfig.get("Advanced Mobs", "Mob Bomb Rarity", 9).getInt(9);
        MobBombAll = defConfig.get("Advanced Mobs", "Mob Bomb All", true, "Skip the Mob Bomb list and allow everything!").getBoolean(true);
        WitherSkeletons = defConfig.get("Advanced Mobs", "Wither Skeletons", true).getBoolean(true);
        WitherSkeletonRarity = defConfig.get("Advanced Mobs", "Wither Skeleton Rarity", 9).getInt(9);
        PotionMobs = defConfig.get("Advanced Mobs", "Potion Buff Chance (0-100)", 1).getInt(1);
        PotionMobEffects = defConfig.get("Advanced Mobs", "Potion Buff List", new int[]{14, 12, 5, 1}, "List of all the valid potion IDs a mob can spawn with. Amplifier is always x1").getIntList();
        attackEvasion = defConfig.get("Advanced Mobs", "Attack Evasion", true).getBoolean(true);
        
        //World
        SpawnForts = defConfig.get("World", "Spawn Forts", true).getBoolean(true);
        fortRarity = defConfig.get("World", "Fort Rarity", 100).getInt(100);
        fortDistance = defConfig.get("World", "Fort Distance", 1024).getInt(1024);
        String[] defSpawn = new String[]
        {
        	"Zombie",
        	"Creeer",
        	"Skeleton",
        	"CaveSpider",
        	"Silverfish",
        	"Spider",
        	"Slime",
        	"Witch"
        };
        fortSpawners = new ArrayList<String>(Arrays.asList(defConfig.get("World", "Fort Spawner Types", defSpawn).getStringList()));
        fallFromEnd = defConfig.get("World", "Fall From End", true, "Whether the player should fall into the overworld from the new End").getBoolean(true);
        int[] tmpFD = defConfig.get("World", "Fort Dimensions", new int[]{0}).getIntList();
        
        for(int dimID : tmpFD)
        {
        	fortDimensions.add(dimID);
        }
        
        defConfig.save();
	}
	
	public static void LoadWorldConfig()
	{
		if(worldDir == null)
		{
			ESM.log.log(Level.ERROR, "Failed to load world configs! Directory is null");
			return;
		}
		
		ResetToDefault();
		
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
        int[] tmpAIE = new int[AIExempt.size()];
        for(int i = 0; i < AIExempt.size(); i++)
        {
        	tmpAIE[i] = AIExempt.get(i);
        }
        tmpAIE = config.get("Main", "AI Exempt Mob IDs", tmpAIE).getIntList();
        AIExempt = new ArrayList<Integer>();
        for(int i : tmpAIE)
        {
        	AIExempt.add(i);
        }
        Awareness = config.get("Main", "Awareness Radius", Awareness).getInt(Awareness);
        Xray = config.get("Main", "Xray Mobs", Xray).getBoolean(Xray);
        TargetCap = config.get("Main", "Pathing Cap", TargetCap).getInt(TargetCap);
        VillagerTarget = config.get("Main", "Villager Targeting", VillagerTarget).getBoolean(VillagerTarget);
        Apocalypse = config.get("Main", "Apocalypse Mode", Apocalypse).getBoolean(Apocalypse);
        Chaos = config.get("Main", "Chaos Mode", Chaos).getBoolean(Chaos);
        AllowSleep = config.get("Main", "Allow Sleep", AllowSleep).getBoolean(AllowSleep);
        ambiguous_AI = config.get("Main", "Ambiguous AI", ambiguous_AI, "If set to true, ESM will not check whether the entity is a mob or not when setting up new AI").getBoolean(ambiguous_AI);
        QuickPathing = config.get("Main", "Quick Pathing", QuickPathing, "If set to fales, mobs can use much longer routes to get to their target").getBoolean(QuickPathing);
        ResistanceCoolDown = config.get("Main", "Resistance Cooldown", ResistanceCoolDown, "The amount of ticks of resistance given to the player after changing dimensions").getInt(ResistanceCoolDown);
        keepLoaded = config.get("Main", "Keep Loaded", false, "Keeps mobs with an active target from despawning. Can causes issues with chunk loading/unloading").getBoolean(false);
        moreSpawning = config.get("Main", "More Spawning", true, "Reduces spawning safe zone from 24 blocks to 8 and makes mobs require only basic conditions to spawn").getBoolean(true);
        forcePath = config.get("Main", "Force Non-AI Pathing", false, "Forces non pathing mobs to attack from further away. Can cause additional lag").getBoolean(false);
        
        //Creeper
        CreeperBreaching = config.get("Creeper", "Breaching", CreeperBreaching).getBoolean(CreeperBreaching);
        CreeperNapalm = config.get("Creeper", "Napalm", CreeperNapalm).getBoolean(CreeperNapalm);
        CreeperPowered = config.get("Creeper", "Powered", CreeperPowered).getBoolean(CreeperPowered);
        CreeperPoweredRarity = config.get("Creeper", "Powered Rarity", CreeperPoweredRarity).getInt(CreeperPoweredRarity);
        
        //Skeletons
        SkeletonAccuracy = config.get("Skeleton", "Arrow Error", SkeletonAccuracy).getInt(SkeletonAccuracy);
        SkeletonDistance = config.get("Skeleton", "Fire Distance", SkeletonDistance).getInt(SkeletonDistance);
        
        //Zombies
        ZombieInfectious = config.get("Zombie", "Infectious", ZombieInfectious).getBoolean(ZombieInfectious);
        ZombieDiggers = config.get("Zombie", "Diggers", ZombieDiggers).getBoolean(ZombieDiggers);
        ZombieDiggerTools = config.get("Zombie", "Need Required Tools", ZombieDiggerTools).getBoolean(ZombieDiggerTools);
        ZombieTraps = config.get("Zombie", "Zombies Build Traps", ZombieTraps).getBoolean(ZombieTraps);
        ZombiePillaring = config.get("Zombie", "Pillaring Blocks", ZombiePillaring, "How many blocks to give zombies to pillar up with").getInt(ZombiePillaring);
        ZombieGriefBlocks = new ArrayList<String>(Arrays.asList(config.get("Zombie", "General Griefable Blocks", ZombieGriefBlocks.toArray(new String[]{}), "What blocks will be targeted for destruction when not attacking players (Does not affect general digging, light sources are included by default, add ':#' for metadata e.g. 'minecraft:wool:1')").getStringList()));
        ZombieDigBlacklist = new ArrayList<String>(Arrays.asList(config.get("Zombie", "General Griefable Blocks", ZombieDigBlacklist.toArray(new String[]{}), "Blacklisted blocks for digging (Add ':#' for metadata e.g. 'minecraft:wool:1')").getStringList()));
        
        //Blazes
        BlazeSpawn = config.get("Blaze", "Spawn", BlazeSpawn).getBoolean(BlazeSpawn);
        BlazeRarity = config.get("Blaze", "Rarity", BlazeRarity).getInt(BlazeRarity);
        BlazeFireballs = config.get("Blaze", "Fireballs", BlazeFireballs).getInt(BlazeFireballs);
        int[] tmpBDB = new int[BlazeDimensionBlacklist.size()];
        for(int i = 0; i < BlazeDimensionBlacklist.size(); i++)
        {
        	tmpBDB[i] = BlazeDimensionBlacklist.get(i);
        }
        tmpBDB = config.get("Blaze", "Dimension Blacklist", tmpBDB).getIntList();
        BlazeDimensionBlacklist = new ArrayList<Integer>();
        for(int i : tmpBDB)
        {
        	BlazeDimensionBlacklist.add(i);
        }
        
        //Ghasts
        GhastSpawn = config.get("Ghast", "Spawn", GhastSpawn).getBoolean(GhastSpawn);
        GhastRarity = config.get("Ghast", "Rarity", GhastRarity).getInt(GhastRarity);
        GhastFireDelay = config.get("Ghast", "Fire Delay", GhastFireDelay).getDouble(GhastFireDelay);
        GhastBreaching = config.get("Ghast", "Breaching", GhastBreaching).getBoolean(GhastBreaching);
        GhastFireDist = config.get("Ghast", "Fire Distance", GhastFireDist).getDouble(GhastFireDist);
        int[] tmpGDB = new int[GhastDimensionBlacklist.size()];
        for(int i = 0; i < GhastDimensionBlacklist.size(); i++)
        {
        	tmpGDB[i] = GhastDimensionBlacklist.get(i);
        }
        tmpGDB = config.get("Ghast", "Dimension Blacklist", tmpGDB).getIntList();
        GhastDimensionBlacklist = new ArrayList<Integer>();
        for(int i : tmpGDB)
        {
        	GhastDimensionBlacklist.add(i);
        }
        
        //Endermen
        EndermanMode = config.get("Enderman", "Mode", EndermanMode, "Valid Endermen Modes (Slender, Normal)").getString();
    	EndermanPlayerTele = config.get("Enderman", "Player Teleport", EndermanPlayerTele).getBoolean(EndermanPlayerTele);
        
    	//Spider
        SpiderWebChance = MathHelper.clamp_int(config.get("Spider", "Webbing Chance (0-100)", SpiderWebChance).getInt(SpiderWebChance), 0, 100);
        
        //Advanced
    	int[] tmpDef = new int[MobBombs.size()];
    	
    	for(int i = 0; i < MobBombs.size(); i++)
    	{
    		tmpDef[i] = MobBombs.get(i);
    	}
        int[] tmp = config.get("Advanced Mobs", "Mob Bombs", tmpDef).getIntList();
        MobBombs = new ArrayList<Integer>();
        for(int id : tmp)
        {
        	MobBombs.add(id);
        }
        MobBombRarity = config.get("Advanced Mobs", "Mob Bomb Rarity", MobBombRarity).getInt(MobBombRarity);
        MobBombAll = config.get("Advanced Mobs", "Mob Bomb All", MobBombAll, "Skip the Mob Bomb list and allow everything!").getBoolean(MobBombAll);
        WitherSkeletons = config.get("Advanced Mobs", "Wither Skeletons", WitherSkeletons).getBoolean(WitherSkeletons);
        WitherSkeletonRarity = config.get("Advanced Mobs", "Wither Skeleton Rarity", WitherSkeletonRarity).getInt(WitherSkeletonRarity);
        PotionMobs = config.get("Advanced Mobs", "Potion Buff Chance (0-100)", PotionMobs).getInt(PotionMobs);
        PotionMobEffects = config.get("Advanced Mobs", "Potion Buff List", PotionMobEffects, "List of all the valid potion IDs a mob can spawn with. Amplifier is always x1").getIntList();
        attackEvasion = config.get("Advanced Mobs", "Attack Evasion", attackEvasion).getBoolean(attackEvasion);
        
        //World
        SpawnForts = config.get("World", "Spawn Forts", SpawnForts).getBoolean(SpawnForts);
        fortRarity = config.get("World", "Fort Rarity", fortRarity).getInt(fortRarity);
        fortDistance = config.get("World", "Fort Distance", fortDistance).getInt(fortDistance);
        fallFromEnd = config.get("World", "Fall From End", fallFromEnd, "Whether the player should fall into the overworld from the new End").getBoolean(fallFromEnd);
    	int[] tmpFDDef = new int[fortDimensions.size()];
    	for(int i = 0; i < fortDimensions.size(); i++)
    	{
    		tmpFDDef[i] = fortDimensions.get(i);
    	}
        int[] tmpFD = config.get("World", "Fort Dimensions", tmpFDDef).getIntList();
        fortDimensions = new ArrayList<Integer>();
        for(int dimID : tmpFD)
        {
        	fortDimensions.add(dimID);
        }
        
        config.save();
        
        ESM_Utils.UpdateBiomeSpawns();
        
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
