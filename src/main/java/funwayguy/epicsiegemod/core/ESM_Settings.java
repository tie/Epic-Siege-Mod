package funwayguy.epicsiegemod.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ESM_Settings
{
	//Main
	public static boolean hideUpdates;
	public static int Awareness;
	public static boolean Xray;
	public static int TargetCap;
	public static boolean VillagerTarget;
	public static boolean Chaos;
	public static boolean AllowSleep;
	public static int ResistanceCoolDown;
	public static int hardDay = 8;
	public static ArrayList<String> AIExempt = new ArrayList<String>();
	
	public static HashMap<Integer, DimSettings> dimSettings = new HashMap<Integer, DimSettings>();
	public static ArrayList<String> diggerList = new ArrayList<String>();
	public static ArrayList<String> demolitionList = new ArrayList<String>();
	public static ArrayList<String> pillarList = new ArrayList<String>();
	
	//Witch
	public static String[] customPotions = new String[0];
	
	//Creeper
	public static boolean CreeperBreaching;
	public static boolean CreeperNapalm;
	public static int CreeperPoweredRarity;
	public static boolean CreeperChargers;
	public static int CenaCreeperRarity = 1;
	
	//Skeleton
	public static int SkeletonDistance;
	public static int SkeletonAccuracy;
	
	//Zombie
	public static boolean ZombieInfectious;
	public static boolean ZombieDiggerTools;
	public static ArrayList<String> ZombieGriefBlocks = new ArrayList<String>();
	public static ArrayList<String> ZombieDigBlacklist = new ArrayList<String>();
	public static boolean ZombieSwapList;
	
	//Enderman
	public static boolean EndermanPlayerTele;
	
	//Spider
	public static int SpiderWebChance;
	
	//Advanced
	public static ArrayList<String> MobBombs = new ArrayList<String>();
	public static int MobBombRarity;
	public static boolean MobBombAll;
	public static boolean WitherSkeletons;
	public static int WitherSkeletonRarity;
	public static boolean attackEvasion;
	public static float bossModifier;
	public static boolean animalsAttack;
	public static boolean neutralMobs;
	public static boolean mobBoating = true;
	public static boolean attackPets = true;
	public static int demolitionChance = 10;
}
