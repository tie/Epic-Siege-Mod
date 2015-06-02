package funwayguy.esm.handlers.entities;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import funwayguy.esm.core.ESM_Settings;

public class ESM_CreeperHandler
{
	public static void onEntityJoinWorld(EntityCreeper creeper)
	{
		if(ESM_Settings.CreeperPowered && (ESM_Settings.CreeperPoweredRarity <= 0 || creeper.getRNG().nextInt(ESM_Settings.CreeperPoweredRarity) == 0))
		{
			creeper.getDataWatcher().updateObject(17, Byte.valueOf((byte)1));
			return;
		}
	}
	
	public static void onLivingUpdate(EntityCreeper creeper)
	{
		int fuseTime = getCreeperFuseTime(creeper);
		int radius = getCreeperRadius(creeper);
		
		if(creeper.getAttackTarget() != null && (creeper.getCreeperState() == 1 || creeper.getDistanceToEntity(creeper.getAttackTarget()) < radius * (creeper.getPowered()? 2D : 1D) + 1) && creeper.ridingEntity != null)
		{
			creeper.dismountEntity(creeper.ridingEntity); // Creepers dismount when close to their target
			creeper.ridingEntity.riddenByEntity = null;
			creeper.ridingEntity = null;
		}
		
		if(ESM_Settings.CreeperNapalm && fuseTime <= 1)
		{
            boolean flag = creeper.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
            
			if(creeper.getPowered())
			{
				creeper.worldObj.newExplosion(creeper, creeper.posX, creeper.posY, creeper.posZ, (float)(radius*2), true, flag);
			} else
			{
				creeper.worldObj.newExplosion(creeper, creeper.posX, creeper.posY, creeper.posZ, (float)radius, true, flag);
			}
			creeper.setDead();
		}
	}
	
	public static int getCreeperFuseTime(EntityCreeper creeper)
	{
		return (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityCreeper.class, creeper, "field_82225_f", "fuseTime") - (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityCreeper.class, creeper, "field_70833_d", "timeSinceIgnited");
	}
	
	public static int getCreeperRadius(EntityCreeper creeper)
	{
		int radius = 3;
		
		NBTTagCompound data = creeper.getEntityData();
		
		if(data.hasKey("ExplosionRadius"))
		{
			radius = data.getByte("ExplosionRadius");
		}
		
		return radius;
	}
}
