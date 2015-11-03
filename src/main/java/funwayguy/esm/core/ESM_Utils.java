package funwayguy.esm.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.EntityRegistry;
import funwayguy.esm.ai.ESMPathNavigator;
import funwayguy.esm.ai.ESM_EntityAIArrowAttack;
import funwayguy.esm.ai.ESM_EntityAIAttackEvasion;
import funwayguy.esm.ai.ESM_EntityAIAttackOnCollide;
import funwayguy.esm.ai.ESM_EntityAIAvoidDetonations;
import funwayguy.esm.ai.ESM_EntityAIBreakDoor_Proxy;
import funwayguy.esm.ai.ESM_EntityAIBuildTrap;
import funwayguy.esm.ai.ESM_EntityAICreeperSwell;
import funwayguy.esm.ai.ESM_EntityAIDigging;
import funwayguy.esm.ai.ESM_EntityAIGrief;
import funwayguy.esm.ai.ESM_EntityAIHurtByTarget;
import funwayguy.esm.ai.ESM_EntityAINearestAttackableTarget;
import funwayguy.esm.ai.ESM_EntityAIPillarUp;
import funwayguy.esm.ai.ESM_EntityAISwimming;
import funwayguy.esm.entities.EntityNeatZombie;
import funwayguy.esm.handlers.ESM_PathCapHandler;

public class ESM_Utils
{
    public static int getSuitableSpawnHeight(World world, int par1, int par2)
    {
        int k;

        for (k = 63; !world.isAirBlock(par1, k, par2) || !world.isAirBlock(par1, k + 1, par2); ++k)
        {
        }
        
        ESM.log.log(Level.INFO, "Suitable spawn at " + par1 + ", " + k + ", " + par2);

        return k;
    }
	
	public static void resetPlayerMPStats(EntityPlayerMP player)
	{
		ObfuscationReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, -1, "field_71144_ck", "lastExperience");
		ObfuscationReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, -1.0F, "field_71149_ch", "lastHealth");
		ObfuscationReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, -1, "field_71146_ci", "lastFoodLevel");
	}

	public static int getAIPathCount(World world, EntityLivingBase targetEntity)
	{
		List<EntityLivingBase> attackerList = ESM_PathCapHandler.attackMap.get(targetEntity);
		
		return attackerList == null? 0 :  attackerList.size();
	}
	
	public static boolean isCloserThanOtherAttackers(World world, EntityLivingBase attacker, EntityLivingBase target)
	{
		List<EntityLivingBase> attackerList = ESM_PathCapHandler.attackMap.get(target);
		
		if(attackerList == null || attackerList.size() <= 0)
		{
			return true;
		}
		
		float distance = attacker.getDistanceToEntity(target);
		
		EntityLivingBase subject = attackerList.get(attackerList.size() - 1); // We only need to check the farthest, the rest are unnecessary to check

		if(subject != null && subject.getDistanceToEntity(target) > distance && subject != attacker)
		{
			return true;
		} else if(subject == null) // This is bad and needs to be resolved immediately
		{
			ESM_PathCapHandler.UpdateAttackers(target);
		}
		
		return false;
	}
	
	public static void replaceAI(EntityLiving entityLiving)
	{
		replaceAI(entityLiving, false);
	}
	
	@SuppressWarnings("unchecked")
	public static void replaceAI(EntityLiving entityLiving, boolean firstPass)
	{
		PathNavigate oldNav = entityLiving.getNavigator();
		ESMPathNavigator newNav = new ESMPathNavigator(entityLiving, entityLiving.worldObj);
		ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, entityLiving, newNav, "field_70699_by", "navigator");
		newNav.inherit(oldNav);
		
		if(entityLiving instanceof EntityNeatZombie)
		{
			return;
		}
		
		boolean replaceNAT = false;
		boolean replaceCS = false;
		boolean replaceAE = false;
		
		ESM_EntityAINearestAttackableTarget esmTargetAI = null;
		
		for(int i = entityLiving.targetTasks.taskEntries.size() - 1; i >= 0 ; i--)
		{
			EntityAITaskEntry task = (EntityAITaskEntry)entityLiving.targetTasks.taskEntries.get(i);
			
			if(task == null || task.action == null)
			{
				continue;
			}
			
			if(task.action.getClass() == EntityAINearestAttackableTarget.class && entityLiving instanceof EntityCreature) // Changed to allow other targeting based AI
			{
				if(esmTargetAI == null)
				{
					esmTargetAI = new ESM_EntityAINearestAttackableTarget((EntityCreature)entityLiving, new ArrayList<Class<? extends EntityLivingBase>>(), 0, true);
					
					if(ESM_Settings.Chaos)
					{
						esmTargetAI.targetClass.add(EntityLivingBase.class);
					} else
					{
						if(ESM_Settings.ambiguous_AI)
						{
							esmTargetAI.targetClass.add(EntityPlayer.class); // Attacking players is a must in ESM
						}
						
						if(ESM_Settings.VillagerTarget)
						{
							esmTargetAI.targetClass.add(EntityVillager.class);
						}
					}
				}
				
				Class<?> targetType = ObfuscationReflectionHelper.getPrivateValue(EntityAINearestAttackableTarget.class, (EntityAINearestAttackableTarget)task.action, "field_75307_b", "targetClass");
				
				if(targetType != null && !esmTargetAI.targetClass.contains(targetType))
				{
					esmTargetAI.targetClass.add((Class<? extends EntityLivingBase>)targetType); // Accounts for custom targets. Adds to batched AI
				}
				
				if(!replaceNAT && !ESM_Settings.neutralMobs)
				{
					replaceNAT = true;
					EntityAITaskEntry replacement = entityLiving.targetTasks.new EntityAITaskEntry(task.priority, esmTargetAI);
					entityLiving.targetTasks.taskEntries.set(i, replacement);
					entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(ESM_Settings.Awareness);
				} else
				{
					entityLiving.targetTasks.taskEntries.remove(i); // Remove redundant AI
				}
			} else if(task.action.getClass() == EntityAIHurtByTarget.class && entityLiving instanceof EntityCreature)
			{
				EntityAITaskEntry replacement = entityLiving.targetTasks.new EntityAITaskEntry(task.priority, new ESM_EntityAIHurtByTarget((EntityCreature)entityLiving, true));
				entityLiving.targetTasks.taskEntries.set(i, replacement);
			}
		}
		
		// Cached for Zombies who switch out their AI dynamically
		ESM_EntityAIBreakDoor_Proxy cachedBD = null;
		EntityAIBreakDoor oldBD = entityLiving instanceof EntityZombie? (EntityAIBreakDoor)ObfuscationReflectionHelper.getPrivateValue(EntityZombie.class, (EntityZombie)entityLiving, "field_146075_bs") : null;
		
		// Cached for Skeletons who switched out their AI dynamically
		ESM_EntityAIAttackOnCollide cachedAOC = null;
		EntityAIAttackOnCollide oldAOC = entityLiving instanceof EntitySkeleton? (EntityAIAttackOnCollide)ObfuscationReflectionHelper.getPrivateValue(EntitySkeleton.class, (EntitySkeleton)entityLiving, "field_85038_e", "aiAttackOnCollide") : null;
		EntityAIArrowAttack cachedAA = null;
		EntityAIArrowAttack oldAA = entityLiving instanceof EntitySkeleton? (EntityAIArrowAttack)ObfuscationReflectionHelper.getPrivateValue(EntitySkeleton.class, (EntitySkeleton)entityLiving, "field_85037_d", "aiArrowAttack") : null;
		
		for(int i = entityLiving.tasks.taskEntries.size() - 1; i >= 0; i--)
		{
			EntityAITaskEntry task = (EntityAITaskEntry)entityLiving.tasks.taskEntries.get(i);
			
			if(task == null || task.action == null)
			{
				continue;
			}
			
			if(task.action instanceof EntityAIFollowOwner)
			{
				ObfuscationReflectionHelper.setPrivateValue(EntityAIFollowOwner.class, (EntityAIFollowOwner)task.action, entityLiving.getNavigator(), "field_75337_g", "petPathfinder"); // Minor fix to make sure the navigation is using the new one
			} else if(task.action.getClass() == EntityAICreeperSwell.class && entityLiving instanceof EntityCreeper)
			{
				if(!replaceCS)
				{
					replaceCS = true;
					EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, new ESM_EntityAICreeperSwell((EntityCreeper)entityLiving));
					entityLiving.tasks.taskEntries.set(i, replacement);
				} else
				{
					entityLiving.tasks.taskEntries.remove(i);
				}
			} else if(task.action.getClass() == EntityAIAvoidEntity.class && entityLiving instanceof EntityVillager)
			{
				if(!replaceAE)
				{
					replaceAE = true;
					EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, new EntityAIAvoidEntity((EntityVillager)entityLiving, IMob.class, 12.0F, 0.6D, 0.6D));
					entityLiving.tasks.taskEntries.set(i, replacement);
				} else
				{
					entityLiving.tasks.taskEntries.remove(i);
				}
			} else if(task.action.getClass() == EntityAISwimming.class)
			{
				EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, new ESM_EntityAISwimming(entityLiving));
				entityLiving.tasks.taskEntries.set(i, replacement);
			} else if(task.action.getClass() == EntityAIBreakDoor.class)
			{
				ESM_EntityAIBreakDoor_Proxy tmp = new ESM_EntityAIBreakDoor_Proxy(entityLiving);
				if(task.action == oldBD)
				{
					cachedBD = tmp;
				}
				EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, tmp);
				entityLiving.tasks.taskEntries.set(i, replacement);
			} else if(task.action.getClass() == EntityAIAttackOnCollide.class && entityLiving instanceof EntityCreature)
			{
				boolean longMemory = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)task.action, "field_75437_f", "longMemory");
				Class<?> targetType = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)task.action, "field_75444_h", "classTarget");
				double speed = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)task.action, "field_75440_e", "speedTowardsTarget");
				
				ESM_EntityAIAttackOnCollide esmAOC = new ESM_EntityAIAttackOnCollide((EntityCreature)entityLiving, targetType, speed, longMemory);
				if(task.action == oldAOC)
				{
					cachedAOC = esmAOC;
				}
				EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, esmAOC);
				entityLiving.tasks.taskEntries.set(i, replacement);
			} else if(task.action.getClass() == EntityAIArrowAttack.class && entityLiving instanceof IRangedAttackMob)
			{
				ESM_EntityAIArrowAttack tmp = new ESM_EntityAIArrowAttack((IRangedAttackMob)entityLiving, 1.0D, 20, 60, (float)ESM_Settings.SkeletonDistance);
				if(task.action == oldAA)
				{
					cachedAA = tmp;
				}
				EntityAITaskEntry replace = entityLiving.tasks.new EntityAITaskEntry(task.priority, tmp);
				entityLiving.tasks.taskEntries.set(i, replace);
			} else if(ESM_Settings.animalsAttack && task.action.getClass() == EntityAIPanic.class && entityLiving instanceof IAnimals && entityLiving instanceof EntityCreature)
			{
				entityLiving.tasks.taskEntries.remove(i);
			}
		}
		
		if(!firstPass)
		{
			if(entityLiving instanceof EntityCreature)
			{
				entityLiving.targetTasks.addTask(0, new ESM_EntityAIAvoidDetonations((EntityCreature)entityLiving, 9F, 1.5D, 1.25D));
				if(entityLiving instanceof IMob && !(entityLiving instanceof EntityCreeper))
				{
					entityLiving.targetTasks.addTask(0, new ESM_EntityAIAttackEvasion((EntityCreature)entityLiving, 5F, 1.5D, 1.25D));
				}
				
				if(entityLiving instanceof IAnimals && ESM_Settings.animalsAttack)
				{
					entityLiving.tasks.addTask(4, new ESM_EntityAIAttackOnCollide((EntityCreature)entityLiving, 1.25D, true));
					entityLiving.targetTasks.addTask(3, new ESM_EntityAIHurtByTarget((EntityCreature)entityLiving, true));
				}
			}
			
			if(entityLiving instanceof EntitySkeleton)
			{
				ESM_EntityAIAttackOnCollide tmpAOC = cachedAOC != null? cachedAOC : new ESM_EntityAIAttackOnCollide((EntitySkeleton)entityLiving, EntityPlayer.class, 1.2D, false);
				ObfuscationReflectionHelper.setPrivateValue(EntitySkeleton.class, (EntitySkeleton)entityLiving, tmpAOC, "field_85038_e", "aiAttackOnCollide");
				EntityAIArrowAttack tmpAA = cachedAA != null? cachedAA : new ESM_EntityAIArrowAttack((EntitySkeleton)entityLiving, 1.0D, 20, 60, ESM_Settings.SkeletonDistance);
				ObfuscationReflectionHelper.setPrivateValue(EntitySkeleton.class, (EntitySkeleton)entityLiving, tmpAA, "field_85037_d", "aiArrowAttack");
			}
			
			if(entityLiving instanceof EntityZombie)
			{
				ESM_EntityAIBreakDoor_Proxy tmp = cachedBD != null? cachedBD : new ESM_EntityAIBreakDoor_Proxy(entityLiving);
				ObfuscationReflectionHelper.setPrivateValue(EntityZombie.class, (EntityZombie)entityLiving, tmp, "field_146075_bs");
				
				((EntityZombie)entityLiving).setCanPickUpLoot(true);
				
				if(ESM_Settings.ZombieDiggers)
				{
					entityLiving.tasks.addTask(1, new ESM_EntityAIDigging((EntityZombie)entityLiving));
					entityLiving.tasks.addTask(6, new ESM_EntityAIGrief((EntityZombie)entityLiving));
					
					entityLiving.tasks.addTask(3, new ESM_EntityAIPillarUp(entityLiving));
					entityLiving.tasks.addTask(5, new ESM_EntityAIBuildTrap(entityLiving));
				}
			}
		}
	}

	public static EntityLivingBase GetNearestValidTarget(EntityLiving entityLiving)
	{
		return entityLiving.worldObj.getClosestVulnerablePlayerToEntity(entityLiving, ESM_Settings.Awareness);
	}
	
	public static boolean isFortAt(World world, int x, int z, int size)
	{
		int dimension = world.provider.dimensionId;
		
		if(ESM_Settings.fortDistance - x%Math.abs(ESM_Settings.fortDistance) < size || ESM_Settings.fortDistance - z%Math.abs(ESM_Settings.fortDistance) < size)
		{
			return true;
		}
		
		int fGridX = x - (x%Math.abs(ESM_Settings.fortDistance));
		int fGridZ = z - (z%Math.abs(ESM_Settings.fortDistance));
		
		if(ESM_Settings.fortDB.contains((new StringBuilder()).append(dimension).append(",").append(fGridX).append(",").append(fGridZ).toString()))
		{
			return true;
		}
		return false;
	}
	
	public static void addFortToDB(World world, int x, int z)
	{
		int dimension = world.provider.dimensionId;
		
		int fGridX = x - (x%Math.abs(ESM_Settings.fortDistance));
		int fGridZ = z - (z%Math.abs(ESM_Settings.fortDistance));
		
		ESM_Settings.fortDB.add((new StringBuilder()).append(dimension).append(",").append(fGridX).append(",").append(fGridZ).toString());
		ESM_Settings.saveFortDB();
	}
	
	public static void UpdateBiomeSpawns()
	{
		if(nativeBlazeBiomes == null || nativeGhastBiomes == null)
		{
			SetBiomeSpawnDefaults();
		}
		
		BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();
		
		for(BiomeGenBase biome : biomeList)
		{
			if(biome == null)
			{
				continue;
			}
			
			if(!nativeBlazeBiomes.contains(biome))
			{
				if(ESM_Settings.BlazeSpawn)
				{
					EntityRegistry.addSpawn(EntityBlaze.class, MathHelper.ceiling_float_int(100F/(float)(ESM_Settings.BlazeRarity <= 0? 1: ESM_Settings.BlazeRarity)), 1, 1, EnumCreatureType.monster, biome);
				} else
				{
					EntityRegistry.removeSpawn(EntityBlaze.class, EnumCreatureType.monster, biome);
				}
			}
			
			if(!nativeGhastBiomes.contains(biome))
			{
				if(ESM_Settings.GhastSpawn)
				{
					EntityRegistry.addSpawn(EntityGhast.class, MathHelper.ceiling_float_int(100F/(float)(ESM_Settings.GhastRarity <= 0? 1 : ESM_Settings.GhastRarity)), 1, 1, EnumCreatureType.monster, biome);
				} else
				{
					EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.monster, biome);
				}
			}
		}
	}
	
	public static ArrayList<BiomeGenBase> nativeBlazeBiomes;
	public static ArrayList<BiomeGenBase> nativeGhastBiomes;
	
	public static void SetBiomeSpawnDefaults()
	{
		nativeBlazeBiomes = new ArrayList<BiomeGenBase>();
		nativeGhastBiomes = new ArrayList<BiomeGenBase>();
		
		BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();
		
		for(BiomeGenBase biome : biomeList)
		{
			if(biome == null)
			{
				continue;
			}
			@SuppressWarnings("unchecked")
			List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.monster);
			
			for(SpawnListEntry spawn : spawnList)
			{
				if(spawn.entityClass == EntityBlaze.class)
				{
					nativeBlazeBiomes.add(biome);
				}
				
				if(spawn.entityClass == EntityGhast.class)
				{
					nativeGhastBiomes.add(biome);
				}
			}
		}
	}
}
