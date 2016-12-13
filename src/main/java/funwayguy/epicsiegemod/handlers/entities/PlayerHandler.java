package funwayguy.epicsiegemod.handlers.entities;

import java.util.List;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class PlayerHandler
{
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
