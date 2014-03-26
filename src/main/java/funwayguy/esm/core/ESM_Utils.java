package funwayguy.esm.core;

import java.lang.reflect.Field;
import java.util.Iterator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityBlaze;
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
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

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
        	player.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(4, 0));
        }
        else
        {
            if (player.dimension == 0 && par1 == 1)
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

            if (j == 1 && par1 == 1)
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

                if (j == 1 && par1 == 1)
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
            
            if(!forceLoc)
            {
            	d0 = (double)chunkcoordinates.posX;
            	par1Entity.posY = (double)chunkcoordinates.posY;
	            d1 = (double)chunkcoordinates.posZ;
	            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, 90.0F, 0.0F);
            } else
            {
            	d0 = (double)chunkcoordinates.posX;
	            d1 = (double)chunkcoordinates.posZ;
            }

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }

        par3WorldServer.theProfiler.endSection();

        if (par2 != 1)
        {
            par3WorldServer.theProfiler.startSection("placing");
            d0 = (double)MathHelper.clamp_int((int)d0, -29999872, 29999872);
            d1 = (double)MathHelper.clamp_int((int)d1, -29999872, 29999872);

            if (par1Entity.isEntityAlive())
            {
                par4WorldServer.spawnEntityInWorld(par1Entity);
                par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);
                par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
                
                if(par2 == ESM_Settings.SpaceDimID && par1Entity.worldObj.isAirBlock(MathHelper.floor_double(d3), MathHelper.floor_double(d4), MathHelper.floor_double(d5)))
                {
                	par1Entity.worldObj.setBlock(MathHelper.floor_double(d3), MathHelper.floor_double(d4), MathHelper.floor_double(d5), Block.obsidian.blockID);
                }
                //teleporter.placeInPortal(par1Entity, d3, d4, d5, f);

                par1Entity.setLocationAndAngles(d3, d4, d5, par1Entity.rotationYaw, 0.0F);
                par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
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
			e.printStackTrace();
			return;
		} catch(SecurityException e)
		{
			e.printStackTrace();
			return;
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
}
