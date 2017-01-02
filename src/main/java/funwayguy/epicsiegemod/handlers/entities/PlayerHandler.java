package funwayguy.epicsiegemod.handlers.entities;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class PlayerHandler
{
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.getEntity().worldObj.isRemote || !(event.getEntity() instanceof EntityPlayer) || !(event.getEntity().worldObj instanceof WorldServer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer)event.getEntity();
		
		boolean hard = false;
		int day = (int)(player.worldObj.getWorldTime()/24000);
		
		if(!hard && ESM_Settings.hardDay != 0 && day != 0 && day%ESM_Settings.hardDay == 0)
		{
			hard = true;
		}
		
		Random rand = player.getRNG();
		
		if(hard && rand.nextInt(10) == 0 && player.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && player.worldObj.getGameRules().getBoolean("doMobSpawning") && player.worldObj.loadedEntityList.size() < 512)
		{
			int x = MathHelper.floor_double(player.posX) + rand.nextInt(48) - 24;
			int y = MathHelper.floor_double(player.posY) + rand.nextInt(48) - 24;
			int z = MathHelper.floor_double(player.posZ) + rand.nextInt(48) - 24;
			BlockPos spawnPos = new BlockPos(x, y, z);
			
			if(player.worldObj.getClosestPlayer(x, y, z, 8D, false) == null && WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, player.worldObj, spawnPos))
			{
                SpawnListEntry spawnlistentry = ((WorldServer)player.worldObj).getSpawnListEntryForTypeAt(EnumCreatureType.MONSTER, spawnPos);
                
                if(spawnlistentry != null)
                {
	                try
	                {
	                	EntityLiving entityliving = (EntityLiving)spawnlistentry.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {player.worldObj});
	
	                    entityliving.setLocationAndAngles((double)x, (double)y, (double)z, rand.nextFloat() * 360.0F, 0.0F);
	
	                    Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, player.worldObj, x, y, z);
	                    if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere()))
	                    {
	                    	player.worldObj.spawnEntityInWorld(entityliving);
	                    }
	                }
	                catch (Exception exception)
	                {
	                    exception.printStackTrace();
	                }
                }
			}
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(ESM_Settings.ResistanceCoolDown > 0)
		{
			event.player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, ESM_Settings.ResistanceCoolDown, 5));
		}
	}
	
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event)
	{
		if(ESM_Settings.ResistanceCoolDown > 0)
		{
			event.player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, ESM_Settings.ResistanceCoolDown, 5));
		}
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(ESM_Settings.ResistanceCoolDown > 0)
		{
			event.player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, ESM_Settings.ResistanceCoolDown, 5));
		}
	}
	
	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		if(ESM_Settings.AllowSleep || event.getEntityPlayer().worldObj.isRemote)
		{
			return;
		}
		
        if (event.getEntityPlayer().isPlayerSleeping() || !event.getEntityPlayer().isEntityAlive())
        {
            return;
        }
        
        if (!event.getEntityPlayer().worldObj.provider.canRespawnHere())
        {
            return;
        }
        
        if (event.getEntityPlayer().worldObj.isDaytime())
        {
            return;
        }
        
        if (Math.abs(event.getEntityPlayer().posX - (double)event.getPos().getX()) > 3.0D || Math.abs(event.getEntityPlayer().posY - (double)event.getPos().getY()) > 2.0D || Math.abs(event.getEntityPlayer().posZ - (double)event.getPos().getZ()) > 3.0D)
        {
            return;
        }
        double d0 = 8.0D;
        double d1 = 5.0D;
        List<?> list = event.getEntityPlayer().worldObj.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double)event.getPos().getX() - d0, (double)event.getPos().getY() - d1, (double)event.getPos().getZ() - d0, (double)event.getPos().getX() + d0, (double)event.getPos().getY() + d1, (double)event.getPos().getZ() + d0));
        
        if (!list.isEmpty())
        {
            return;
        }
	    
	    event.setResult(SleepResult.OTHER_PROBLEM);
		
	    if (event.getEntityPlayer().isRiding())
	    {
	        event.getEntityPlayer().dismountRidingEntity();
	    }
	    
		event.getEntityPlayer().setSpawnChunk(event.getPos(), false, event.getEntityPlayer().dimension);
		event.getEntityPlayer().addChatMessage(new TextComponentString("Spawnpoint set"));
	}
}
