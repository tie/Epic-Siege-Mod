package funwayguy.epicsiegemod.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ESM_Settings
{
	//Main
	public static int Awareness;
	public static boolean Xray;
	public static int TargetCap;
	public static boolean VillagerTarget;
	public static boolean Chaos;
	public static boolean AllowSleep;
	public static int ResistanceCoolDown;
	public static int hardDay;
	public static ArrayList<String> AIExempt = new ArrayList<String>();
	
	public static HashMap<Integer, DimSettings> dimSettings = new HashMap<Integer, DimSettings>();
	public static ArrayList<String> diggerList = new ArrayList<String>();
	public static ArrayList<String> demolitionList = new ArrayList<String>();
	public static ArrayList<String> pillarList = new ArrayList<String>();
	
	//Witch
	public static String[] customPotions;
	
	//Creeper
	public static boolean CreeperBreaching;
	public static boolean CreeperNapalm;
	public static boolean CreeperPowered;
	public static int CreeperPoweredRarity;
	public static boolean CreeperChargers;
	public static boolean CenaCreeper = false;
	public static int CenaCreeperRarity = 9;
	
	//Blaze
	public static boolean BlazeSpawn;
	public static int BlazeRarity;
	public static int BlazeFireballs;
	public static ArrayList<Integer> BlazeDimensionBlacklist;
	
	//Ghast
	public static boolean GhastSpawn;
	public static int GhastRarity;
	public static double GhastFireDelay;
	public static boolean GhastBreaching;
	public static double GhastFireDist;
	public static ArrayList<Integer> GhastDimensionBlacklist;
	
	//Skeleton
	public static int SkeletonDistance;
	public static int SkeletonAccuracy;
	
	//Zombie
	public static boolean ZombieInfectious;
	public static boolean ZombieDiggers;
	public static boolean ZombieDiggerTools;
	public static int ZombiePillaring;
	public static ArrayList<String> ZombieGriefBlocks;
	public static ArrayList<String> ZombieDigBlacklist;
	public static boolean ZombieSwapList;
	public static boolean DemolitionZombies;
	
	//Enderman
	public static boolean EndermanPlayerTele;
	
	//Spider
	public static int SpiderWebChance;
	
	//Advanced
	public static ArrayList<String> MobBombs;
	public static int MobBombRarity;
	public static boolean MobBombAll;
	public static boolean CrystalBombs;
	public static boolean WitherSkeletons;
	public static int WitherSkeletonRarity;
	public static int PotionMobs;
	public static int[] PotionMobEffects;
	public static boolean attackEvasion;
	public static float bossModifier;
	public static boolean animalsAttack;
	public static boolean neutralMobs;
	public static boolean mobBoating = true;
	public static boolean attackPets = true;
	public static boolean MobBombInvert = false;
	public static float demolitionChance = 0.1F;
}
