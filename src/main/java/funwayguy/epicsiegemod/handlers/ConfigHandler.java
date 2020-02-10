package funwayguy.epicsiegemod.handlers;

import funwayguy.epicsiegemod.ai.ESM_EntityAIPillarUp;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigHandler
{
	public static Configuration config;
	
	private static final String CAT_MAIN = "General";
	private static final String CAT_CREEPER = "Creepers";
	private static final String CAT_SKELETON = "Skeletons";
	private static final String CAT_ADVANCED = "Other";
	
	public static void initConfigs()
	{
		if(config == null)
		{
			ESM.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
			return;
		}
		
		config.load();
		
		// === MAIN ===
		ESM_Settings.hideUpdates =			config.getBoolean("Hide Updates", CAT_MAIN, false, "Hides update notifications");
		ESM_Settings.hardDay =				config.getInt("Hardcore Day Cycle", CAT_MAIN, 8, 0, Integer.MAX_VALUE, "The interval in which 'hard' days will occur where mob spawning is increased and lighting is ignored (0 = off, default = 8/full moon)");
		ESM_Settings.Awareness =			config.getInt("Awareness Radius", CAT_MAIN, 64, 0, Integer.MAX_VALUE, "How far mobs can see potential targets");
		ESM_Settings.Xray =					config.getInt("Xray Mobs", CAT_MAIN, 64, 0, Integer.MAX_VALUE, "Distance mobs can sense targets through walls");
		ESM_Settings.TargetCap =			config.getInt("Pathing Cap", CAT_MAIN, 16, 0, 128, "Maximum number of attackers per target");
		ESM_Settings.VillagerTarget =		config.getBoolean("Villager Targeting", CAT_MAIN, true, "Allows mobs to attack villagers as they would players");
		ESM_Settings.Chaos =				config.getBoolean("Chaos Mode", CAT_MAIN, false, "Everyone one and everything is a target");
		ESM_Settings.AllowSleep =			config.getBoolean("Allow Sleep", CAT_MAIN, false, "Prevents players skipping the night through sleep");
		ESM_Settings.ResistanceCoolDown =	config.getInt("Resistance Cooldown", CAT_MAIN, 200, 0, Integer.MAX_VALUE, "Temporary invulnerability in ticks when respawning and teleporting");
		ESM_Settings.attackPets =			config.getBoolean("Attack Pets", CAT_MAIN, true, "Mobs will attack any player owned pets they find");
		
		ESM_Settings.AIExempt.clear();
		for(String s : config.getStringList("AI Blacklist", CAT_MAIN, new String[]{"minecraft:villager_golem"}, "Mobs that are exempt from AI modifications"))
		{
			ESM_Settings.AIExempt.add(new ResourceLocation(s));
		}
		
		// === CREEPER ===
		ESM_Settings.CreeperBreaching =		config.getBoolean("Breaching", CAT_CREEPER, true, "Creepers will attempt to blast through walls");
		ESM_Settings.CreeperNapalm =		config.getBoolean("Napalm", CAT_CREEPER, true, "Creeper detonations leave behind flaming craters");
		ESM_Settings.CreeperPoweredRarity =	config.getInt("Powered Rarity", CAT_CREEPER, 10, 0, 100, "The chance a Creeper will spawn pre-powered");
		ESM_Settings.CreeperChargers =		config.getBoolean("Walking Fuse", CAT_CREEPER, true, "Creepers will continue approaching their target while arming");
		ESM_Settings.CenaCreeperRarity =	config.getInt("Creeper", CAT_CREEPER, 1, 0, 100, "AND HIS NAME IS...");
		ESM_Settings.MobBombs.clear();
		ESM_Settings.MobBombs.addAll(Arrays.asList(config.getStringList("Creeper Jockey Mobs", CAT_CREEPER, new String[]{}, "Sets which mobs can spawn with Creepers riding them")));
		ESM_Settings.MobBombRarity =		config.getInt("Creeper Jockey Chance", CAT_CREEPER, 10, 0, 100, "The chance a Creeper will spawn riding another mob");
		ESM_Settings.MobBombAll =			config.getBoolean("All Creeper Jockeys", CAT_CREEPER, true, "Ignores the listing and allows any mob to have a Creeper rider");
		
		// === SKELETON ===
		ESM_Settings.SkeletonAccuracy =		config.getFloat(Arrow Error", CAT_SKELETON, 0F, 0F, Float.MAX_VALUE, "How likely Skeletons are to miss their target");
		ESM_Settings.SkeletonDistance =		config.getInt("Fire Distance", CAT_SKELETON, 64, 1, Integer.MAX_VALUE, "How far away can Skeletons shoot from");
		ESM_Settings.WitherSkeletonRarity = config.getInt("Wither Skeleton Chance", CAT_SKELETON, 10, 0, 100, "The chance a skeleton will spawn as Wither in other dimensions");
		
		// === OTHER ===
		ESM_Settings.attackEvasion =		config.getBoolean("Evasive AI", CAT_ADVANCED, true, "Mobs will strafe more than normal and avoid imminent explosions");
		ESM_Settings.bossModifier =			config.getFloat("Boss Kill Modifier", CAT_ADVANCED, 0.1F, 0F, Float.MAX_VALUE, "The factor by which mob health and damage multipliers will be increased when bosses are killed");
		ESM_Settings.animalsAttack =		config.getBoolean("Animals Retaliate", CAT_ADVANCED, true, "Animals will fight back if provoked");
		ESM_Settings.neutralMobs =			config.getBoolean("Neutral Mobs", CAT_ADVANCED, false, "Mobs are passive until provoked");
		//ESM_Settings.mobBoating  =		config.getBoolean("Mob Boating", CAT_ADVANCED, true, "Zombies and Skeletons will use boats in water to catch up to you!");
		
		ESM_Settings.diggerList.clear();
		for(String s : config.getStringList("Digging Mobs", CAT_ADVANCED, new String[]{"minecraft:zombie"}, "List of mobs that can dig through blocks"))
		{
			ESM_Settings.diggerList.add(new ResourceLocation(s));
		}
		
		String pbTemp = config.getString("Pillaring Block", CAT_ADVANCED, "minecraft:cobblestone:0", "The block zombies use to pillar up with");
		String[] pillarBlock = pbTemp.split(":");
		
		if(pillarBlock.length == 2 || pillarBlock.length == 3)
		{
			ESM_EntityAIPillarUp.blockName = new ResourceLocation(pillarBlock[0], pillarBlock[1]);
			
			if(pillarBlock.length == 3)
			{
				try
				{
					ESM_EntityAIPillarUp.blockMeta = Integer.parseInt(pillarBlock[2]);
				} catch(Exception e)
				{
					ESM.logger.error("Unable to parse pillar block metadata from: " + pbTemp, e);
					ESM_EntityAIPillarUp.blockMeta = -1;
				}
			} else
			{
				ESM_EntityAIPillarUp.blockMeta = -1;
			}
		} else
		{
			ESM.logger.error("Incorrectly formatted pillar block config: " + pbTemp);
			ESM_EntityAIPillarUp.blockName = new ResourceLocation("minecraft:cobblestone");
			ESM_EntityAIPillarUp.blockMeta = -1;
		}
		
		ESM_EntityAIPillarUp.updateBlock = true;
		
		ESM_Settings.ZombieDiggerTools =	config.getBoolean("Digging Tools Only", CAT_ADVANCED, true, "Digging mobs require the proper tools to dig");
		ESM_Settings.ZombieSwapList =		config.getBoolean("Invert Digging Blacklist", CAT_ADVANCED, false, "Use the digging blacklist as a whitelist instead");
		ESM_Settings.ZombieDigBlacklist.clear();
		ESM_Settings.ZombieDigBlacklist.addAll(Arrays.asList(config.getStringList("Digging Blacklist", CAT_ADVANCED, new String[]{}, "Blocks blacklisted from digging mobs (Format: 'minecraft:wool:1')")));
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
		ESM_Settings.ZombieGriefBlocks = new ArrayList<>(Arrays.asList(config.getStringList("General Griefable Blocks", CAT_ADVANCED, defGrief, "What blocks will be targeted for destruction when idle (Light sources included by default. Format: 'minecraft:wool:1')")));
		
		ESM_Settings.demolitionList.clear();
		for(String s : config.getStringList("Demolition Mobs", CAT_ADVANCED, new String[]{"minecraft:zombie"}, "List of mobs that can drop live TNT"))
		{
			ESM_Settings.demolitionList.add(new ResourceLocation(s));
		}
		
		ESM_Settings.demolitionChance =		config.getInt("Demolition Chance", CAT_ADVANCED, 10, 0, 100, "How common demolition variants are");
		
		ESM_Settings.pillarList.clear();
		for(String s : config.getStringList("Building Mobs", CAT_ADVANCED, new String[]{"minecraft:zombie"}, "List of mobs that can pillar up and build stairs"))
		{
			ESM_Settings.pillarList.add(new ResourceLocation(s));
		}
		
		ESM_Settings.EndermanPlayerTele =	config.getBoolean("Player Teleport", CAT_ADVANCED, true, "Allows Enderman to teleport the player instead of themelves");
		ESM_Settings.SpiderWebChance =		config.getInt("Webbing Chance", CAT_ADVANCED, 25, 0, 100, "The chance a Spider will web its target to the ground");
		ESM_Settings.ZombieInfectious =		config.getBoolean("Infectious Zombies", CAT_ADVANCED, true, "Dying to zombies will turn your corpse into one of them");
		String[] defPot = new String[]
		{
			PotionTypes.HARMING.getRegistryName() + ":1:0",
			PotionTypes.SLOWNESS.getRegistryName() + ":300:0",
			MobEffects.BLINDNESS.getRegistryName() + ":300:0",
			"minecraft:poison:300:0",
			"minecraft:weakness:300:1",
			"minecraft:mining_fatigue:300:2"
		};
		ESM_Settings.customPotions =		config.getStringList("Witch Potions", CAT_ADVANCED, defPot, "List of custom potion types witches can throw (\"id:duration:lvl\")");
		
		config.save();
	}
}
