package funwayguy.esm.handlers;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import funwayguy.esm.core.ESM;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.core.ESM_Utils;
import funwayguy.esm.handlers.entities.ESM_BlazeHandler;
import funwayguy.esm.handlers.entities.ESM_CreeperHandler;
import funwayguy.esm.handlers.entities.ESM_DragonHandler;
import funwayguy.esm.handlers.entities.ESM_EndermanHandler;
import funwayguy.esm.handlers.entities.ESM_SkeletonHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class ESM_EventManager
{	
	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.world.isRemote)
		{
			return;
		}
		
		if(ESM_Settings.Apocalypse && event.entity instanceof EntityLivingBase && !(event.entity instanceof EntityZombie || event.entity instanceof EntityPlayer || (event.entity instanceof EntityEnderman && ESM_Settings.EndermanMode.equalsIgnoreCase("Slender"))))
		{
			event.entity.setDead();
			event.setCanceled(true);
			return;
		}
		
		if(event.entity instanceof EntityLiving)
		{
			updateEntityAwareness((EntityLiving)event.entity);
			ESM_Utils.replaceAI((EntityLiving)event.entity);
		}
		
		if(event.entity.getEntityData().getBoolean("ESM_MODIFIED"))
		{
			return;
		}
		
		if(event.entity instanceof EntityCreeper)
		{
			ESM_CreeperHandler.onEntityJoinWorld((EntityCreeper)event.entity);
		} else if(event.entity instanceof EntitySpider)
		{
			if(event.entity.riddenByEntity == null)
			{
				if(ESM_Settings.SpiderBombs && ESM_Settings.SpiderBombRarity <= 0)
				{
					EntityCreeper passenger = new EntityCreeper(event.entity.worldObj);
					passenger.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
					passenger.onSpawnWithEgg((EntityLivingData)null);
					event.entity.worldObj.spawnEntityInWorld(passenger);
					passenger.mountEntity(event.entity);
				} else if(ESM_Settings.SpiderBombs && ESM_Settings.SpiderBombRarity > 0)
				{
					if(event.world.rand.nextInt(ESM_Settings.SpiderBombRarity) == 0)
					{
						EntityCreeper passenger = new EntityCreeper(event.entity.worldObj);
						passenger.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						passenger.onSpawnWithEgg((EntityLivingData)null);
						event.entity.worldObj.spawnEntityInWorld(passenger);
						passenger.mountEntity(event.entity);
					}
				}
			}
		} else if(event.entity instanceof EntitySkeleton)
		{
			ESM_SkeletonHandler.onEntityJoinWorld((EntitySkeleton)event.entity);
		} else if(event.entity instanceof EntityZombie && !ESM_Settings.Apocalypse)
		{
			switch(event.world.rand.nextInt(3))
			{
				case 0:
				{
					if(ESM_Settings.GhastSpawn && ESM_Settings.GhastRarity <= 0 && event.world.canBlockSeeTheSky((int)event.entity.posX, (int)event.entity.posY, (int)event.entity.posZ) && event.entity.posY >= 64)
					{
						event.setCanceled(true);
						EntityGhast newGhast = new EntityGhast(event.world);
						newGhast.setLocationAndAngles(event.entity.posX, event.entity.posY + 32, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						event.world.spawnEntityInWorld(newGhast);
					} else if(ESM_Settings.GhastSpawn && ESM_Settings.GhastRarity > 0 && event.world.canBlockSeeTheSky((int)event.entity.posX, (int)event.entity.posY, (int)event.entity.posZ) && event.entity.posY >= 64)
					{
						if(event.world.rand.nextInt(ESM_Settings.GhastRarity) == 0)
						{
							event.setCanceled(true);
							EntityGhast newGhast = new EntityGhast(event.world);
							newGhast.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
							event.world.spawnEntityInWorld(newGhast);
						}
					}
					break;
				}
				
				case 1:
				{
					if(ESM_Settings.BlazeSpawn && ESM_Settings.BlazeRarity <= 0)
					{
						event.setCanceled(true);
						EntityBlaze newBlaze = new EntityBlaze(event.world);
						newBlaze.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						newBlaze.getEntityData().setBoolean("ESM_MODIFIED", true);
						event.world.spawnEntityInWorld(newBlaze);
					} else if(ESM_Settings.BlazeSpawn && ESM_Settings.BlazeRarity > 0)
					{
						if(event.world.rand.nextInt(ESM_Settings.BlazeRarity) == 0)
						{
							event.setCanceled(true);
							EntityBlaze newBlaze = new EntityBlaze(event.world);
							newBlaze.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
							newBlaze.getEntityData().setBoolean("ESM_MODIFIED", true);
							event.world.spawnEntityInWorld(newBlaze);
						}
					}
					break;
				}
			}
		} else if(event.entity instanceof EntityArrow)
		{
			EntityArrow arrow = (EntityArrow)event.entity;
			if(arrow.shootingEntity instanceof EntitySkeleton)
			{
				EntitySkeleton shooter = (EntitySkeleton)arrow.shootingEntity;
				EntityLivingBase target = shooter.getAttackTarget();
				
				if(target != null)
				{
					replaceArrowAttack(shooter, target, arrow.getDamage());
					event.setCanceled(true);
				}
			}
		} else if(event.entity instanceof EntityBlaze)
		{
			ESM_BlazeHandler.onEntityJoinWorld((EntityBlaze)event.entity);
		} else if(event.entity instanceof EntitySmallFireball)
		{
			EntitySmallFireball fireball = (EntitySmallFireball)event.entity;
			if(fireball.shootingEntity instanceof EntityBlaze)
			{
				fireball.shootingEntity.getEntityData().setInteger("ESM_FIREBALLS", fireball.shootingEntity.getEntityData().getInteger("ESM_FIREBALLS") + 1);
			}
		} else if(event.entity instanceof EntityEnderman)
		{
			ESM_EndermanHandler.onEntityJoinWorld((EntityEnderman)event.entity);
		}
		
		event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
	}
	
	public static void replaceArrowAttack(EntitySkeleton shooter, EntityLivingBase par1EntityLivingBase, double par2)
	{
    	EntityArrow entityarrow;
        double targetDist = shooter.getDistance(par1EntityLivingBase.posX, par1EntityLivingBase.boundingBox.minY, par1EntityLivingBase.posZ);
    	
    	if(ESM_Settings.SkeletonDistance == 0)
    	{
    		entityarrow = new EntityArrow(shooter.worldObj, shooter, par1EntityLivingBase, 1.6F, ESM_Settings.SkeletonAccuracy);
    	} else
    	{
    		entityarrow = new EntityArrow(shooter.worldObj, shooter, par1EntityLivingBase, (float)((0.00013*(targetDist)*(targetDist)) + (0.02*targetDist) + 1.25), ESM_Settings.SkeletonAccuracy);
    	}
    	
        //EntityArrow entityarrow = new EntityArrow(shooter.worldObj, shooter, par1EntityLivingBase, 1.6F, (float)(14 - shooter.worldObj.difficultySetting * 4));
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, shooter.getHeldItem());
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, shooter.getHeldItem());
        entityarrow.setDamage(par2);

        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, shooter.getHeldItem()) > 0 || shooter.getSkeletonType() == 1)
        {
            entityarrow.setFire(100);
        }

        shooter.playSound("random.bow", 1.0F, 1.0F / (shooter.getRNG().nextFloat() * 0.4F + 0.8F));
        entityarrow.getEntityData().setBoolean("ESM_MODIFIED", true);
        shooter.worldObj.spawnEntityInWorld(entityarrow);
	}
	
	@ForgeSubscribe
	public void onEntityDeath(LivingDeathEvent event)
	{
		if(event.entity.worldObj.isRemote)
		{
			return;
		}
		
		ESM_PathCapHandler.RemoveTarget(event.entityLiving);
		
		if(event.entity instanceof EntityPlayer)
		{
			if(event.source.getSourceOfDamage() instanceof EntityZombie && ESM_Settings.ZombieInfectious)
			{
				EntityZombie zombie = new EntityZombie(event.entity.worldObj);
				zombie.setPosition(event.entity.posX, event.entity.posY, event.entity.posZ);
				zombie.setCanPickUpLoot(true);
				zombie.setCustomNameTag(event.entity.getEntityName());
				zombie.getEntityData().setBoolean("ESM_MODIFIED", true);
				event.entity.worldObj.spawnEntityInWorld(zombie);
			}
		}
	}
	
	public static void updateEntityAwareness(EntityLivingBase entityLivingBase)
	{
		EntityLiving entityLiving;
		
		if(entityLivingBase instanceof EntityLiving)
		{
			entityLiving = (EntityLiving)entityLivingBase;
		} else
		{
			return;
		}
		
		if(entityLiving.getNavigator() != null)
		{
			if(entityLiving instanceof EntityZombie && ESM_Settings.Awareness < 40)
			{
				entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
			} else
			{
				entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(ESM_Settings.Awareness);
			}
		} else
		{
			//entityLiving.setAttackTarget(ESM_Settings.GetNearestValidTarget(entityLiving));
		}
	}
	
	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(getPortalTime(event.entityLiving) >= event.entityLiving.getMaxInPortalTime()-1 && getInPortal(event.entityLiving) && ESM_Settings.NewHell)
		{
			if(event.entityLiving.dimension != ESM_Settings.HellDimID)
			{
				event.entityLiving.timeUntilPortal = event.entityLiving.getPortalCooldown();
				setInPortal(event.entityLiving, false);
				ESM_Utils.transferDimensions(ESM_Settings.HellDimID, event.entityLiving, false);
			} else
			{
				event.entityLiving.timeUntilPortal = event.entityLiving.getPortalCooldown();
				setInPortal(event.entityLiving, false);
				ESM_Utils.transferDimensions(0, event.entityLiving, false);
			}
		}
		
		if(event.entityLiving.posY < 0 && event.entityLiving.dimension == ESM_Settings.SpaceDimID && event.entityLiving instanceof EntityPlayer)
		{
			event.entityLiving.setPosition(event.entityLiving.posX, 255D, event.entityLiving.posZ);
			ESM_Utils.transferDimensions(0, event.entityLiving, true);
		}
		
		if(event.entity.worldObj.isRemote)
		{
			return;
		}
		
		if(ESM_Settings.Apocalypse && !(event.entityLiving instanceof EntityPlayer || event.entityLiving instanceof EntityZombie || (event.entityLiving instanceof EntityEnderman && ESM_Settings.EndermanMode.equalsIgnoreCase("Slender"))))
		{
			event.entityLiving.setDead();
			return;
		}
		
		if(event.entityLiving instanceof EntityLiving)
		{
			if(((EntityLiving)event.entityLiving).getAttackTarget() != null)
			{
				ESM_PathCapHandler.AddNewAttack(event.entityLiving, ((EntityLiving)event.entityLiving).getAttackTarget());
			}
		}
		
		updateEntityAwareness(event.entityLiving);
		
		if(event.entityLiving instanceof EntityCreeper)
		{
			ESM_CreeperHandler.onLivingUpdate((EntityCreeper)event.entityLiving);
		} else if(event.entityLiving instanceof EntitySkeleton)
		{
			ESM_SkeletonHandler.onLivingUpdate((EntitySkeleton)event.entityLiving);
		} else if(event.entityLiving instanceof EntityBlaze)
		{
			ESM_BlazeHandler.onLivingUpdate((EntityBlaze)event.entityLiving);
		} else if(event.entityLiving instanceof EntityEnderman)
		{
			ESM_EndermanHandler.onLivingUpdate((EntityEnderman)event.entityLiving);
		}
		
		if(event.entityLiving instanceof EntityDragon)
		{
			ESM_DragonHandler.onLivingUpdate((EntityDragon)event.entityLiving);
		}
		
		return;
	}

	@ForgeSubscribe
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		if(ESM_Settings.AllowSleep)
		{
			return;
		}
		
		if (!event.entityPlayer.worldObj.isRemote)
        {
            if (event.entityPlayer.isPlayerSleeping() || !event.entityPlayer.isEntityAlive())
            {
                return;
            }
            
            if (!event.entityPlayer.worldObj.provider.canRespawnHere())
            {
                return;
            }
            
            if (event.entityPlayer.worldObj.isDaytime())
            {
                return;
            }
            
            if (Math.abs(event.entityPlayer.posX - (double)event.x) > 3.0D || Math.abs(event.entityPlayer.posY - (double)event.y) > 2.0D || Math.abs(event.entityPlayer.posZ - (double)event.z) > 3.0D)
            {
                return;
            }
            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = event.entityPlayer.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().getAABB((double)event.x - d0, (double)event.y - d1, (double)event.z - d0, (double)event.x + d0, (double)event.y + d1, (double)event.z + d0));
            
	        if (!list.isEmpty())
            {
                return;
            }
        }
	    
	    event.result = EnumStatus.OTHER_PROBLEM;
		
	    if (event.entityPlayer.isRiding())
	    {
	        event.entityPlayer.mountEntity((Entity)null);
	    }
	    
		event.entityPlayer.setSpawnChunk(new ChunkCoordinates(event.x,event.y,event.z), false);
		event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Spawnpoint set"));
	}
	
	@ForgeSubscribe
	public void onWorldLoad(Load event)
	{
		if(!event.world.isRemote && ESM_Settings.currentWorlds == null)
		{
			MinecraftServer mc = MinecraftServer.getServer();
			ESM_Settings.currentWorlds = mc.worldServers;
			if(ESM.proxy.isClient())
			{
				ESM_Settings.currentWorldConfig = new File(mc.getFile("saves/" + mc.getFolderName()).getAbsolutePath(), "ESM_Options.cfg");
			} else
			{
				ESM_Settings.currentWorldConfig = new File(mc.getFile(mc.getFolderName()).getAbsolutePath(), "ESM_Options.cfg");
			}
			ESM_Settings.LoadWorldConfig();
		}
	}
	
	@ForgeSubscribe
	public void onWorldUnload(Unload event)
	{
		if(!event.world.isRemote)
		{
			MinecraftServer mc = MinecraftServer.getServer();
			
			if(mc.worldServers == null || !mc.isServerRunning())
			{
				ESM_Settings.currentWorlds = null;
			}
		}
	}
	
	@ForgeSubscribe
	public void initMapGen(InitMapGenEvent event)
	{
		/*ESM.log.log(Level.INFO, "Fired InitMapGenEvent with type " + event.type.toString());
		if(event.type.equals(InitMapGenEvent.EventType.SCATTERED_FEATURE))
		{
			if(event.originalGen instanceof MapGenScatteredFeature)
			{
				ESM.log.log(Level.INFO, "Replacing MapGenScatteredFeature!");
				event.newGen = new MapGenScatteredFortress();
			}
		}*/
	}
	
	public static int getPortalTime(Entity entity)
	{
		int time = -1;
		
		Field field = null;
		try
		{
			field = Entity.class.getDeclaredField("portalCounter");
		} catch(NoSuchFieldException | SecurityException e)
		{
			try
			{
				field = Entity.class.getDeclaredField("field_82153_h");
			} catch(NoSuchFieldException | SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return time;
			}
		}
		
		field.setAccessible(true);
		
		try
		{
			time = (int)field.getInt(entity);
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			return time;
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return time;
		}
		
		return time;
	}
	
	public static boolean getInPortal(Entity entity)
	{
		boolean flag = false;
		
		Field field = null;
		try
		{
			field = Entity.class.getDeclaredField("inPortal");
		} catch(NoSuchFieldException | SecurityException e)
		{
			try
			{
				field = Entity.class.getDeclaredField("field_71087_bX");
			} catch(NoSuchFieldException | SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return flag;
			}
		}
		
		field.setAccessible(true);
		
		try
		{
			flag = (boolean)field.getBoolean(entity);
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			return flag;
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return flag;
		}
		
		return flag;
	}
	
	public static void setInPortal(Entity entity, boolean value)
	{
		Field field = null;
		try
		{
			field = Entity.class.getDeclaredField("inPortal");
		} catch(NoSuchFieldException | SecurityException e)
		{
			try
			{
				field = Entity.class.getDeclaredField("field_71087_bX");
			} catch(NoSuchFieldException | SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		field.setAccessible(true);
		
		try
		{
			field.setBoolean(entity, value);
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			return;
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return;
		}
	}
}
