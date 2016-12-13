package funwayguy.epicsiegemod.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ConfigHandler
{
	public static Configuration config;
	
	public static void initConfigs()
	{
		if(config == null)
		{
			ESM.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
			return;
		}
		
		config.load();
		
		//Main
		ESM_Settings.hardDay = config.getInt("Hardcore Day Cycle", "Main", 8, 0, Integer.MAX_VALUE, "The interval in which 'hard' days will occur where mob spawning is increased and lighting is ignored (0 = off, default = 8/full moon)");
		ESM_Settings.Awareness = config.get("Main", "Awareness Radius", 64).getInt(64);
		ESM_Settings.Xray = config.get("Main", "Xray Mobs", true).getBoolean(true);
		ESM_Settings.TargetCap = config.get("Main", "Pathing Cap", 16).getInt(16);
		ESM_Settings.VillagerTarget = config.get("Main", "Villager Targeting", true).getBoolean(true);
		ESM_Settings.Chaos = config.get("Main", "Chaos Mode", false).getBoolean(false);
		ESM_Settings.AllowSleep = config.get("Main", "Allow Sleep", false).getBoolean(false);
		ESM_Settings.ResistanceCoolDown = config.get("Main", "Resistance Cooldown", 200, "The amount of ticks of resistance given to the player after changing dimensions").getInt(200);
		ESM_Settings.attackPets  = config.getBoolean("Attack Pets", "Main", true, "Mobs will attack player owned pets");
		ESM_Settings.attackPets  = config.getBoolean("Attack Pets", "Main", true, "Mobs will attack player owned pets");
		
		ESM_Settings.diggerList.clear();
		ESM_Settings.diggerList.addAll(Arrays.asList(config.getStringList("Digger Entities", Configuration.CATEGORY_GENERAL, new String[]{"Zombie"}, "Entity IDs of mobs that can dig")));
		
		ESM_Settings.demolitionList.clear();
		ESM_Settings.demolitionList.addAll(Arrays.asList(config.getStringList("Demolition Entities", Configuration.CATEGORY_GENERAL, new String[]{"Zombie"}, "Entity IDs of mobs that can drop TNT")));
		ESM_Settings.demolitionChance = config.getFloat("Demolition Chance", Configuration.CATEGORY_GENERAL, 0.1F, 0F, 1F, "Value between 0 - 1 representing how common demolition variants are");
		
		ESM_Settings.pillarList.clear();
		ESM_Settings.pillarList.addAll(Arrays.asList(config.getStringList("Pllaring Entities", Configuration.CATEGORY_GENERAL, new String[]{"Zombie"}, "Entity IDs of mobs that can pillar up")));
		
		String[] tmpAIE = config.get("Main", "AI Exempt Mob IDs", new String[]{"VillagerGolem"}).getStringList();
		ESM_Settings.AIExempt = new ArrayList<String>();
		ESM_Settings.AIExempt.addAll(Arrays.asList(tmpAIE));
		
		//Witch
		String[] defPot = new String[]
		{
			PotionTypes.HARMING.getRegistryName() + ":1:0",
			PotionTypes.SLOWNESS.getRegistryName() + ":300:0",
			MobEffects.BLINDNESS.getRegistryName() + ":300:0",
			"minecraft:poison:300:0",
			"minecraft:weakness:300:1",
			"minecraft:mining_fatigue:300:2"
		};
		ESM_Settings.customPotions = config.getStringList("Custom Potions", "Witch", defPot, "List of potion types witches can throw (\"id:duration:lvl\")");
		
		//Creeper
		ESM_Settings.CreeperBreaching = config.get("Creeper", "Breaching", true).getBoolean(true);
		ESM_Settings.CreeperNapalm = config.get("Creeper", "Napalm", true).getBoolean(true);
		ESM_Settings.CreeperPowered = config.get("Creeper", "Powered", true).getBoolean(true);
		ESM_Settings.CreeperPoweredRarity = config.get("Creeper", "Powered Rarity", 9).getInt(9);
		ESM_Settings.CreeperChargers = config.getBoolean("Chargering", "Creeper", true, "Creepers will run at you at speed before detonating");
		ESM_Settings.CenaCreeper = config.getBoolean("Cena Creeper", "Creeper", false, "AND HIS NAME IS...");
		ESM_Settings.CenaCreeperRarity = config.getInt("Cena Creeper Rarity", "Creeper", 9, 0, Integer.MAX_VALUE, "How rare are they");
		
		//Skeletons
		ESM_Settings.SkeletonAccuracy = config.get("Skeleton", "Arrow Error", 0).getInt(0);
		ESM_Settings.SkeletonDistance = config.get("Skeleton", "Fire Distance", 64).getInt(64);
		
		//Zombies
		ESM_Settings.ZombieInfectious = config.get("Zombie", "Infectious", true).getBoolean(true);
		ESM_Settings.ZombieDiggers = config.get("Zombie", "Diggers", true).getBoolean(true);
		ESM_Settings.ZombieDiggerTools = config.get("Zombie", "Need Required Tools", true).getBoolean(true);
		ESM_Settings.ZombiePillaring = config.get("Zombie", "Pillaring Blocks", 64, "How many blocks to give zombies to pillar up with").getInt(64);
		String[] defGrief = new String[]
		{
				"minecraft:chest", // Might not be a good idea for loot reasons
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
				"minecraft:cake", "minecraft:ladder",
				"minecraft:wooden_door",
				"minecraft:farmland",
				"minecraft:bookshelf",
				"minecraft:sapling",
				"minecraft:bed",
				"minecraft:fence",
				"minecraft:planks"
		};
		ESM_Settings.ZombieGriefBlocks = new ArrayList<String>(Arrays.asList(config.get("Zombie", "General Griefable Blocks", defGrief, "What blocks will be targeted for destruction when not attacking players (Does not affect general digging, light sources are included by default, add ':#' for metadata e.g. 'minecraft:wool:1')").getStringList()));
		ESM_Settings.ZombieDigBlacklist = new ArrayList<String>(Arrays.asList(config.get("Zombie", "Digging Blacklist", new String[]{}, "Blacklisted blocks for digging (Add ':#' for metadata e.g. 'minecraft:wool:1')").getStringList()));
		ESM_Settings.ZombieSwapList = config.get("Zombie", "Blacklist to Whitelist", false, "Use the digging blacklist as a whitelist instead").getBoolean(false);
		ESM_Settings.DemolitionZombies = config.get("Zombie", "Demolition Zombies", true, "Zombies can placed armed TNT").getBoolean(true);
		
		//Blazes
		ESM_Settings.BlazeSpawn = config.get("Blaze", "Spawn", true).getBoolean(true);
		ESM_Settings.BlazeRarity = config.get("Blaze", "Rarity", 9).getInt(9);
		ESM_Settings.BlazeFireballs = config.get("Blaze", "Fireballs", 9).getInt(9);
		int[] tmpBDB = config.get("Blaze", "Dimension Blacklist", new int[]{}).getIntList();
		ESM_Settings.BlazeDimensionBlacklist = new ArrayList<Integer>();
		for(int i : tmpBDB)
		{
			ESM_Settings.BlazeDimensionBlacklist.add(i);
		}
		
		//Ghasts
		ESM_Settings.GhastSpawn = config.get("Ghast", "Spawn", false).getBoolean(false);
		ESM_Settings.GhastRarity = config.get("Ghast", "Rarity", 9).getInt(9);
		ESM_Settings.GhastFireDelay = config.get("Ghast", "Fire Delay", 1.0D).getDouble(1.0D);
		ESM_Settings.GhastBreaching = config.get("Ghast", "Breaching", true).getBoolean(true);
		ESM_Settings.GhastFireDist = config.get("Ghast", "Fire Distance", 64.0D).getDouble(64.0D);
		int[] tmpGDB = config.get("Ghast", "Dimension Blacklist", new int[]{}).getIntList();
		ESM_Settings.GhastDimensionBlacklist = new ArrayList<Integer>();
		for(int i : tmpGDB)
		{
			ESM_Settings.GhastDimensionBlacklist.add(i);
		}
		
		//Endermen
		ESM_Settings.EndermanPlayerTele = config.get("Enderman", "Player Teleport", true).getBoolean(true);
		
		//Spider
		ESM_Settings.SpiderWebChance = MathHelper.clamp_int(config.get("Spider", "Webbing Chance (0-100)", 25).getInt(25), 0, 100);
		
		//Advanced
		String[] tmp = config.get("Advanced Mobs", "Mob Bombs", new String[]{}).getStringList();
		ESM_Settings.MobBombs = new ArrayList<String>();
		ESM_Settings.MobBombs.addAll(Arrays.asList(tmp));
		ESM_Settings.MobBombInvert = config.getBoolean("Mob Bomb Invert", "Advanced Mobs", false, "Inverts the mob bomb listing to be act as a blacklist");
		ESM_Settings.MobBombRarity = config.get("Advanced Mobs", "Mob Bomb Rarity", 9).getInt(9);
		ESM_Settings.MobBombAll = config.get("Advanced Mobs", "Mob Bomb All", true, "Skip the Mob Bomb list and allow everything!").getBoolean(true);
		ESM_Settings.CrystalBombs = config.get("Advanced Mobs", "Crystal Bombs", false, "Mob Bombs are now Crystals instead of Creepers").getBoolean(false);
		ESM_Settings.WitherSkeletons = config.get("Advanced Mobs", "Wither Skeletons", true).getBoolean(true);
		ESM_Settings.WitherSkeletonRarity = config.get("Advanced Mobs", "Wither Skeleton Rarity", 9).getInt(9);
		ESM_Settings.PotionMobs = config.get("Advanced Mobs", "Potion Buff Chance (0-100)", 1).getInt(1);
		ESM_Settings.PotionMobEffects = config.get("Advanced Mobs", "Potion Buff List", new int[]{14, 12, 5, 1}, "List of all the valid potion IDs a mob can spawn with. Amplifier is always x1").getIntList();
		ESM_Settings.attackEvasion = config.get("Advanced Mobs", "Attack Evasion", true).getBoolean(true);
		ESM_Settings.bossModifier = config.getFloat("Boss Kill Modifier", "Advanced Mobs", 0.1F, 0F, Float.MAX_VALUE, "Every time a boss is killed all mob heal and damage multipliers will be increased by this");
		ESM_Settings.animalsAttack = config.getBoolean("Animals Retaliate", "Advanced Mobs", true, "Animals will fight back if provoked");
		ESM_Settings.neutralMobs = config.getBoolean("Neutral Mobs", "Advanced Mobs", false, "Mobs are passive until provoked");
		ESM_Settings.mobBoating  = config.getBoolean("Mob Boating", "Advanced Mobs", true, "Zombies and Skeletons will use boats in water to catch up to you!");
		
		config.save();
	}
}
