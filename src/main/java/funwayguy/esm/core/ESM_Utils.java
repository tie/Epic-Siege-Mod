package funwayguy.esm.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import cpw.mods.fml.common.registry.GameRegistry;
import funwayguy.esm.ai.ESM_EntityAICreeperSwell;
import funwayguy.esm.ai.ESM_EntityAINearestAttackableTarget;
import funwayguy.esm.blocks.ESM_BlockEnderPortal;
import funwayguy.esm.handlers.ESM_PathCapHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet9Respawn;
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

public class ESM_Utils
{

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public static void transferPlayerMPToDimension(EntityPlayerMP player, int par1, boolean forceLoc)
    {
        if ((player.dimension == 1 && par1 == 1) || (player.dimension == ESM_Settings.SpaceDimID && par1 == ESM_Settings.SpaceDimID))
        {
        	player.triggerAchievement(AchievementList.theEnd2);
        	player.worldObj.removeEntity(player);
        	player.playerConqueredTheEnd = true;
        	player.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(4, 0));
        }
        else
        {
            if(player.dimension == 0 && (par1 == 1 || par1 == ESM_Settings.SpaceDimID))
            {
            	player.triggerAchievement(AchievementList.theEnd);
                ChunkCoordinates chunkcoordinates = player.mcServer.worldServerForDimension(par1).getEntrancePortalLocation();

                if (chunkcoordinates != null)
                {
                	player.playerNetServerHandler.setPlayerLocation((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, 0.0F, 0.0F);
                }

                if(par1 == ESM_Settings.SpaceDimID)
                {
                	par1 = ESM_Settings.SpaceDimID;
                } else
                {
                	par1 = 1;
                }
            }
            else if(par1 == -1 || par1 == ESM_Settings.HellDimID)
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

            if((j == 1 && par1 == 1) || (j == ESM_Settings.SpaceDimID && par1 == ESM_Settings.SpaceDimID))
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

                if((j == 1 && par1 == 1) || (j == ESM_Settings.SpaceDimID && par1 == ESM_Settings.SpaceDimID))
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

        if (par1Entity.dimension == 1)
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
        } else if((par1Entity.dimension == ESM_Settings.SpaceDimID || (par1Entity.dimension == 0 && !forceLoc)) && par2 == ESM_Settings.SpaceDimID)
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

        if (!(par2 == 1 && par1Entity.dimension == 0))
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
	                if(par1Entity.dimension == ESM_Settings.SpaceDimID)
	                {
	                    int i2 = MathHelper.floor_double(par1Entity.posX);
	                    int j2 = 64;
	                    int k2 = MathHelper.floor_double(par1Entity.posZ);
	                    
	                	for(int i = -2; i <= 2; i++)
	                	{
	                		for(int j = -2; j <= 2; j++)
	                		{
	                			par4WorldServer.setBlock(i2 + i, j2 - 1, k2 + j, Block.obsidian.blockID);
	                		}
	                	}
	                	
	                	par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0F;
	                	par1Entity.setPosition(i2, j2, k2);
	                } else if(par2 != ESM_Settings.SpaceDimID)/* if(par1Entity.dimension == -1 || par1Entity.dimension == ESM_Settings.HellDimID || par1Entity.dimension == 0)*/
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
        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(par1EntityPlayerMP.dimension, (byte)par1EntityPlayerMP.worldObj.difficultySetting, worldserver1.getWorldInfo().getTerrainType(), worldserver1.getHeight(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        worldserver.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        //configManager.transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter);
        transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter, forceLoc);
        configManager.func_72375_a(par1EntityPlayerMP, worldserver);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(worldserver1);
        configManager.updateTimeAndWeatherForPlayer(par1EntityPlayerMP, worldserver1);
        configManager.syncPlayerInventory(par1EntityPlayerMP);
        Iterator iterator = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(par1EntityPlayerMP.entityId, potioneffect));
        }

        GameRegistry.onPlayerChangedDimension(par1EntityPlayerMP);
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
		} catch(NoSuchFieldException e)
		{
			try
			{
				fXP = EntityPlayerMP.class.getDeclaredField("field_71144_ck");
				fHP = EntityPlayerMP.class.getDeclaredField("field_71149_ch");
				fFD = EntityPlayerMP.class.getDeclaredField("field_71146_ci");
			} catch(NoSuchFieldException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			} catch(SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		} catch(SecurityException e)
		{
			try
			{
				fXP = EntityPlayerMP.class.getDeclaredField("field_71144_ck");
				fHP = EntityPlayerMP.class.getDeclaredField("field_71149_ch");
				fFD = EntityPlayerMP.class.getDeclaredField("field_71146_ci");
			} catch(NoSuchFieldException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			} catch(SecurityException e1)
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
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			return;
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return;
		}
		
		return;
	}

	public static int getAIPathCount(World world, EntityLivingBase targetEntity)
	{
		ESM_PathCapHandler.UpdateAttackers(targetEntity);
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
		ESM_PathCapHandler.UpdateAttackers(target);
		List<EntityLivingBase> attackerList = ESM_PathCapHandler.attackMap.get(target);
		
		if(attackerList == null || attackerList.size() <= 0)
		{
			return true;
		}
		
		EntityLivingBase furthest = attacker;
		
		for (int iteration = 0; iteration < attackerList.size(); ++iteration)
		{
			EntityLivingBase subject = attackerList.get(iteration);

			if(target.getDistanceToEntity(subject) > target.getDistanceSqToEntity(furthest))
			{
				furthest = (EntityLiving)subject;
			}
		}
		
		if(furthest != attacker)
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public static void replaceAI(EntityLiving entityLiving)
	{
		boolean replaceNAT = false;
		boolean replaceCS = false;
		
		if(entityLiving.targetTasks.taskEntries.size() >= 1)
		{
			List<EntityAITaskEntry> taskList = entityLiving.targetTasks.taskEntries;
			
			for(int i = taskList.size()-1; i >= 0; i--)
			{
				if(taskList.get(i).action instanceof EntityAINearestAttackableTarget && entityLiving instanceof EntityCreature)
				{
					entityLiving.targetTasks.removeTask(taskList.get(i).action);
					replaceNAT = true;
				}
			}
		}
		
		if(entityLiving.tasks.taskEntries.size() >= 1)
		{
			List<EntityAITaskEntry> taskList = entityLiving.tasks.taskEntries;
			
			for(int i = taskList.size()-1; i >= 0; i--)
			{
				if(taskList.get(i).action instanceof EntityAICreeperSwell && entityLiving instanceof EntityCreeper)
				{
					entityLiving.tasks.removeTask(taskList.get(i).action);
					replaceCS = true;
				}
			}
		}
		
		if(replaceNAT)
		{
			entityLiving.targetTasks.addTask(2, new ESM_EntityAINearestAttackableTarget((EntityCreature)entityLiving, EntityPlayer.class, 0, true));
			if(entityLiving instanceof EntityZombie)
			{
				entityLiving.targetTasks.addTask(2, new ESM_EntityAINearestAttackableTarget((EntityCreature)entityLiving, EntityVillager.class, 0, false));
			} else
			{
				entityLiving.targetTasks.addTask(2, new ESM_EntityAINearestAttackableTarget((EntityCreature)entityLiving, EntityVillager.class, 0, true));
			}
			entityLiving.targetTasks.addTask(2, new ESM_EntityAINearestAttackableTarget((EntityCreature)entityLiving, EntityCreature.class, 0, true));
		}
		
		if(replaceCS)
		{
			entityLiving.tasks.addTask(2, new ESM_EntityAICreeperSwell((EntityCreeper)entityLiving));
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
	
	public static void replaceEndPortal()
	{
		Block.blocksList[Block.endPortal.blockID] = null;
		
		Field field = null;
		Field modifiers = null;

		try
		{
			field = Block.class.getDeclaredField("endPortal");
			modifiers = Field.class.getDeclaredField("modifiers");
		} catch(NoSuchFieldException e)
		{
			try
			{
				field = Block.class.getDeclaredField("field_72102_bH");
				modifiers = Field.class.getDeclaredField("modifiers");
			} catch(NoSuchFieldException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			} catch(SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		} catch(SecurityException e)
		{
			try
			{
				field = Block.class.getDeclaredField("field_72102_bH");
				modifiers = Field.class.getDeclaredField("modifiers");
			} catch(NoSuchFieldException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			} catch(SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		modifiers.setAccessible(true);
		
		try
		{
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		} catch(IllegalArgumentException | IllegalAccessException e1)
		{
			e1.printStackTrace();
			return;
		}
		
		field.setAccessible(true);
		try
		{
			field.set(null, (new ESM_BlockEnderPortal(119, Material.portal)).setHardness(-1.0F).setResistance(6000000.0F));
		} catch(IllegalArgumentException e2)
		{
			e2.printStackTrace();
			return;
		} catch(IllegalAccessException e2)
		{
			e2.printStackTrace();
			return;
		}
		
		ESM.log.log(Level.INFO, "Successfully replaced BlockEndPortal");
	}
}
