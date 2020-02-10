package funwayguy.epicsiegemod.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public class ESM_Settings
{
	//Main
	public static boolean hideUpdates;
	public static int Awareness;
	public static int Xray;
	public static int TargetCap;
	public static boolean VillagerTarget;
	public static boolean Chaos;
	public static boolean AllowSleep;
	public static int ResistanceCoolDown;
	public static int hardDay = 8;
	public static List<ResourceLocation> AIExempt = new ArrayList<>();
	
	public static HashMap<Integer, DimSettings> dimSettings = new HashMap<>();
	public static List<ResourceLocation> diggerList = new ArrayList<>();
	public static List<ResourceLocation> demolitionList = new ArrayList<>();
	public static List<ResourceLocation> pillarList = new ArrayList<>();
	
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
	public static float SkeletonAccuracy;
	
	//Zombie
	public static boolean ZombieInfectious;
	public static boolean ZombieDiggerTools;
	public static List<String> ZombieGriefBlocks = new ArrayList<>();
	public static List<String> ZombieDigBlacklist = new ArrayList<>();
	public static boolean ZombieSwapList;
	
	//Enderman
	public static boolean EndermanPlayerTele;
	
	//Spider
	public static int SpiderWebChance;
	
	//Advanced
	public static ArrayList<String> MobBombs = new ArrayList<>();
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
