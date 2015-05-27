package funwayguy.esm.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import funwayguy.esm.ai.*;
import funwayguy.esm.blocks.ESM_BlockEnderPortal;
import funwayguy.esm.handlers.ESM_PathCapHandler;

public class ESM_Utils
{
    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public static void transferPlayerMPToDimension(EntityPlayerMP player, int par1, boolean forceLoc)
    {
        if (player.dimension == 1 && par1 == 1)
        {
        	player.triggerAchievement(AchievementList.theEnd2);
        	player.worldObj.removeEntity(player);
        	player.playerConqueredTheEnd = true;
        	player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0));
        }
        else
        {
            if(player.dimension == 0 && par1 == 1)
            {
            	player.triggerAchievement(AchievementList.theEnd);
                ChunkCoordinates chunkcoordinates = player.mcServer.worldServerForDimension(par1).getEntrancePortalLocation();

                if (chunkcoordinates != null)
                {
                	player.playerNetServerHandler.setPlayerLocation((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, 0.0F, 0.0F);
                }

                par1 = 1;
            }
            else if(par1 == -1)
            {
            	player.triggerAchievement(AchievementList.portal);
            }

            transferPlayerToDimension(player, par1, forceLoc);
            resetPlayerMPStats(player);
        }
    }
    
	public static void transferDimensions(int par1, Entity par2, boolean forceLoc)
	{
    	if(par2 instanceof EntityPlayerMP)
    	{
    		transferPlayerMPToDimension((EntityPlayerMP)par2, par1, forceLoc);
    		return;
    	}
    	
        if (!par2.worldObj.isRemote && !par2.isDead)
        {
        	par2.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            int j = par2.dimension;
            WorldServer worldserver = minecraftserver.worldServerForDimension(j);
            WorldServer worldserver1 = minecraftserver.worldServerForDimension(par1);
            par2.dimension = par1;

            if(j == 1 && par1 == 1)
            {
                worldserver1 = minecraftserver.worldServerForDimension(0);
                par2.dimension = 0;
            }

            par2.worldObj.removeEntity(par2);
            par2.isDead = false;
            par2.worldObj.theProfiler.startSection("reposition");
            //minecraftserver.getConfigurationManager().transferEntityToWorld(par2, j, worldserver, worldserver1);
            transferEntityToWorld(par2, j, worldserver, worldserver1, forceLoc);
            par2.worldObj.theProfiler.endStartSection("reloading");
            Entity entity = EntityList.createEntityByName(EntityList.getEntityString(par2), worldserver1);

            if (entity != null)
            {
                entity.copyDataFrom(par2, true);

                if(j == 1 && par1 == 1)
                {
                    ChunkCoordinates chunkcoordinates = worldserver1.getSpawnPoint();
                    chunkcoordinates.posY = par2.worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
                    entity.setLocationAndAngles((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, entity.rotationYaw, entity.rotationPitch);
                }

                worldserver1.spawnEntityInWorld(entity);
            }

            par2.isDead = true;
            par2.worldObj.theProfiler.endSection();
            worldserver.resetUpdateEntityTick();
            worldserver1.resetUpdateEntityTick();
            par2.worldObj.theProfiler.endSection();
        }
	}

    /**
     * Transfers an entity from a world to another world.
     */
    private static void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, boolean forceLoc)
    {
        transferEntityToWorld(par1Entity, par2, par3WorldServer, par4WorldServer, par4WorldServer.getDefaultTeleporter(), forceLoc);
    }
	
	private static void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter, boolean forceLoc)
    {
        WorldProvider pOld = par3WorldServer.provider;
        WorldProvider pNew = par4WorldServer.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = par1Entity.posX * moveFactor;
        double d1 = par1Entity.posZ * moveFactor;
        double d3 = par1Entity.posX;
        double d4 = par1Entity.posY;
        double d5 = par1Entity.posZ;
        float f = par1Entity.rotationYaw;
        par3WorldServer.theProfiler.startSection("moving");

        if (par1Entity.dimension == 1 && !ESM_Settings.NewEnd)
        {
            ChunkCoordinates chunkcoordinates;

            if (par2 == 1)
            {
                chunkcoordinates = par4WorldServer.getSpawnPoint();
            }
            else
            {
                chunkcoordinates = par4WorldServer.getEntrancePortalLocation();
            }
            
        	d0 = (double)chunkcoordinates.posX;
        	par1Entity.posY = (double)chunkcoordinates.posY;
            d1 = (double)chunkcoordinates.posZ;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, 90.0F, 0.0F);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        } else if((par1Entity.dimension == 1 || (par1Entity.dimension == 0 && !forceLoc)) && par2 == 1)
        {
            ChunkCoordinates chunkcoordinates = par4WorldServer.getSpawnPoint();
            
        	d0 = (double)chunkcoordinates.posX;
        	par1Entity.posY = getSuitableSpawnHeight(par4WorldServer, chunkcoordinates.posX, chunkcoordinates.posZ);
            d1 = (double)chunkcoordinates.posZ;
            
            par1Entity.setLocationAndAngles(d0 + 0.5D, par1Entity.posY, d1 + 0.5D, 90.0F, 0.0F);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }

        par3WorldServer.theProfiler.endSection();

        if (!(par2 == 1 && par1Entity.dimension == 0 && !ESM_Settings.NewEnd))
        {
            par3WorldServer.theProfiler.startSection("placing");
            d0 = (double)MathHelper.clamp_int((int)d0, -29999872, 29999872);
            d1 = (double)MathHelper.clamp_int((int)d1, -29999872, 29999872);

            if (par1Entity.isEntityAlive())
            {
                par4WorldServer.spawnEntityInWorld(par1Entity);
                par1Entity.setLocationAndAngles(d0, MathHelper.floor_double(par1Entity.posY), d1, par1Entity.rotationYaw, par1Entity.rotationPitch);
                par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
                
                if(!forceLoc)
                {
	                if(par1Entity.dimension == 1)
	                {
	                    int i2 = MathHelper.floor_double(par1Entity.posX);
	                    int j2 = 64;
	                    int k2 = MathHelper.floor_double(par1Entity.posZ);
	                    
	                	for(int i = -2; i <= 2; i++)
	                	{
	                		for(int j = -2; j <= 2; j++)
	                		{
	                			par4WorldServer.setBlock(i2 + i, j2 - 1, k2 + j, Blocks.obsidian);
	                		}
	                	}
	                	
	                	par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0F;
	                	par1Entity.setPosition(i2, j2, k2);
	                } else if(par2 != 1)/* if(par1Entity.dimension == -1 || par1Entity.dimension == ESM_Settings.HellDimID || par1Entity.dimension == 0)*/
	                {
	                	teleporter.placeInPortal(par1Entity, d3, d4, d5, f);
	                }
	                //teleporter.placeInPortal(par1Entity, d3, d4, d5, f);
                }
            }

            par3WorldServer.theProfiler.endSection();
        }

        par1Entity.setWorld(par4WorldServer);
    }

    public static void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, boolean forceLoc)
    {
        transferPlayerToDimension(par1EntityPlayerMP, par2, par1EntityPlayerMP.mcServer.worldServerForDimension(par2).getDefaultTeleporter(), forceLoc);
    }

    public static void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter, boolean forceLoc)
    {
    	ServerConfigurationManager configManager = par1EntityPlayerMP.mcServer.getConfigurationManager();
        int j = par1EntityPlayerMP.dimension;
        WorldServer worldserver = par1EntityPlayerMP.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.dimension = par2;
        WorldServer worldserver1 = par1EntityPlayerMP.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S07PacketRespawn(par1EntityPlayerMP.dimension, par1EntityPlayerMP.worldObj.difficultySetting, par1EntityPlayerMP.worldObj.getWorldInfo().getTerrainType(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        worldserver.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        //configManager.transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter);
        transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter, forceLoc);
        configManager.func_72375_a(par1EntityPlayerMP, worldserver);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(worldserver1);
        configManager.updateTimeAndWeatherForPlayer(par1EntityPlayerMP, worldserver1);
        configManager.syncPlayerInventory(par1EntityPlayerMP);
        Iterator<?> iterator = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(par1EntityPlayerMP.getEntityId(), potioneffect));
        }

        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(par1EntityPlayerMP, j, par2);
    }
    
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
		Field fXP = null;
		Field fHP = null;
		Field fFD = null;
		try
		{
			fXP = EntityPlayerMP.class.getDeclaredField("lastExperience");
			fHP = EntityPlayerMP.class.getDeclaredField("lastHealth");
			fFD = EntityPlayerMP.class.getDeclaredField("lastFoodLevel");
		} catch(Exception e)
		{
			try
			{
				fXP = EntityPlayerMP.class.getDeclaredField("field_71144_ck");
				fHP = EntityPlayerMP.class.getDeclaredField("field_71149_ch");
				fFD = EntityPlayerMP.class.getDeclaredField("field_71146_ci");
			} catch(Exception e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		fXP.setAccessible(true);
		fHP.setAccessible(true);
		fFD.setAccessible(true);
		
		try
		{
			fXP.setInt(player, -1);
			fHP.setFloat(player, -1.0F);
			fFD.setInt(player, -1);
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		return;
	}

	public static int getAIPathCount(World world, EntityLivingBase targetEntity)
	{
		List<EntityLivingBase> attackerList = ESM_PathCapHandler.attackMap.get(targetEntity);
		
		if(attackerList == null)
		{
			return 0;
		} else
		{
			return attackerList.size();
		}
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
	
	@SuppressWarnings("unchecked")
	public static void replaceAI(EntityLiving entityLiving)
	{
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
				
				if(!replaceNAT)
				{
					replaceNAT = true;
					EntityAITaskEntry replacement = entityLiving.targetTasks.new EntityAITaskEntry(task.priority, esmTargetAI);
					entityLiving.targetTasks.taskEntries.set(i, replacement);
					entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(ESM_Settings.Awareness);
				} else
				{
					entityLiving.targetTasks.taskEntries.remove(i); // Remove redundant AI
				}
			}
		}
		
		ESM_EntityAIBreakDoor_Proxy esmBD = null; // Cached for zombies who switch out their AI dynamically
		
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
				esmBD = new ESM_EntityAIBreakDoor_Proxy(entityLiving);
				EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, esmBD);
				entityLiving.tasks.taskEntries.set(i, replacement);
			} else if(task.action.getClass() == EntityAIAttackOnCollide.class && entityLiving instanceof EntityCreature)
			{
				boolean longMemory = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)task.action, "field_75437_f", "longMemory");
				Class<?> targetType = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)task.action, "field_75444_h", "classTarget");
				double speed = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)task.action, "field_75440_e", "speedTowardsTarget");
				
				ESM_EntityAIAttackOnCollide esmAOC = new ESM_EntityAIAttackOnCollide((EntityCreature)entityLiving, targetType, speed, longMemory);
				EntityAITaskEntry replacement = entityLiving.tasks.new EntityAITaskEntry(task.priority, esmAOC);
				entityLiving.tasks.taskEntries.set(i, replacement);
			}
		}
		
		if(entityLiving instanceof EntityCreature)
		{
			if(entityLiving.targetTasks.taskEntries.size() > 0)
			{
				entityLiving.targetTasks.addTask(0, new ESM_EntityAIAvoidDetonations((EntityCreature)entityLiving, 9F, 1.5D, 1.5D));
				if((entityLiving instanceof IMob || ESM_Settings.ambiguous_AI) && !(entityLiving instanceof EntityCreeper))
				{
					entityLiving.targetTasks.addTask(0, new ESM_EntityAIAttackEvasion((EntityCreature)entityLiving, 5F, 1.5D, 1.5D));
				}
			} else
			{
				entityLiving.targetTasks.addTask(0, new ESM_EntityAIAvoidDetonations((EntityCreature)entityLiving, 12F, 1D, 1D));
			}
		}
		
		if(entityLiving instanceof EntityZombie)
		{
			try
			{
				Object tmp = esmBD != null? esmBD : new ESM_EntityAIBreakDoor_Proxy(entityLiving);
				Field aiField = EntityZombie.class.getDeclaredField("field_146075_bs");
				aiField.setAccessible(true);
				aiField.set(entityLiving, tmp);
				
				if(aiField.get(entityLiving) != tmp)
				{
					throw new Exception();
				}
				
				((EntityZombie)entityLiving).func_146070_a(true);
			} catch(Exception e)
			{
				ESM.log.log(Level.ERROR, "Unable to modify Zombie door breaking AI", e);
			}
			
			((EntityZombie)entityLiving).setCanPickUpLoot(true);
			
			if(ESM_Settings.ZombieDiggers)
			{
				entityLiving.tasks.addTask(1, new ESM_EntityAIDigging((EntityZombie)entityLiving));
				entityLiving.tasks.addTask(6, new ESM_EntityAIGrief((EntityZombie)entityLiving));
				
				entityLiving.tasks.addTask(3, new ESM_EntityAIPillarUp(entityLiving));
				entityLiving.tasks.addTask(5, new ESM_EntityAIBuildTrap(entityLiving));
			}
		}
		
		ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, entityLiving, new ESMPathNavigator(entityLiving, entityLiving.worldObj), "field_70699_by", "navigator");
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
	
	// Experience says this is a bad idea... really bad!
	public static void replaceEndPortal()
	{
		Block block = new ESM_BlockEnderPortal(Material.portal).setHardness(-1.0F).setResistance(6000000.0F);
		Field fBlock = null;
		Field modifiers = null;

		try
		{
			fBlock = Blocks.class.getDeclaredField("end_portal");
			modifiers = Field.class.getDeclaredField("modifiers");
		} catch(Exception e)
		{
			try
			{
				fBlock = Blocks.class.getDeclaredField("field_150384_bq");
				modifiers = Field.class.getDeclaredField("modifiers");
			} catch(Exception e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		modifiers.setAccessible(true);
		
		try
		{
			modifiers.setInt(fBlock, fBlock.getModifiers() & ~Modifier.FINAL);
		} catch(Exception e)
		{
			ESM.log.log(Level.ERROR, "Failed to replace End Portal", e);
			return;
		}
		
		fBlock.setAccessible(true);
		
		try
		{
			fBlock.set(null, block);
			
			try
			{
				Method addRawObj = FMLControlledNamespacedRegistry.class.getDeclaredMethod("addObjectRaw", int.class, String.class, Object.class);
				addRawObj.setAccessible(true);
				
				addRawObj.invoke(GameData.getItemRegistry(), 119, "minecraft:end_portal", new ItemBlock(block));
				addRawObj.invoke(GameData.getBlockRegistry(), 119, "minecraft:end_portal", block);
			} catch(Exception e)
			{
				ESM.log.log(Level.ERROR, "Failed to replace End Portal", e);
			}
		} catch(Exception e)
		{
			ESM.log.log(Level.ERROR, "Failed to replace End Portal", e);
			return;
		}
		
		if(Blocks.end_portal instanceof ESM_BlockEnderPortal && Block.blockRegistry.getObject("end_portal") instanceof ESM_BlockEnderPortal)
		{
			ESM.log.log(Level.INFO, "Successfully replaced vanilla End Portal");
		} else
		{
			ESM.log.log(Level.ERROR, "Failed to override vanilla End Portal block");
		}
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
					EntityRegistry.addSpawn(EntityBlaze.class, 100/(ESM_Settings.BlazeRarity <= 0? 1: ESM_Settings.BlazeRarity), 1, 1, EnumCreatureType.monster, biome);
				} else
				{
					EntityRegistry.removeSpawn(EntityBlaze.class, EnumCreatureType.monster, biome);
				}
			}
			
			if(!nativeGhastBiomes.contains(biome))
			{
				if(ESM_Settings.GhastSpawn)
				{
					EntityRegistry.addSpawn(EntityGhast.class, 100/(ESM_Settings.GhastRarity <= 0? 1 : ESM_Settings.GhastRarity), 1, 1, EnumCreatureType.monster, biome);
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
