package funwayguy.esm.handlers.entities;

import java.lang.reflect.Field;
import funwayguy.esm.core.ESM_Settings;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;

public class ESM_CreeperHandler
{
	public static void onEntityJoinWorld(EntityCreeper creeper)
	{
		if(ESM_Settings.CreeperPowered && ESM_Settings.CreeperPoweredRarity <= 0)
		{
			creeper.getDataWatcher().updateObject(17, Byte.valueOf((byte)1));
			return;
		} else if(ESM_Settings.CreeperPowered && ESM_Settings.CreeperPoweredRarity > 0)
		{
			if(creeper.getRNG().nextInt(ESM_Settings.CreeperPoweredRarity) == 0)
			{
				creeper.getDataWatcher().updateObject(17, Byte.valueOf((byte)1));
				return;
			} else
			{
				return;
			}
		}
	}
	
	public static void onLivingUpdate(EntityCreeper creeper)
	{
		int fuseTime = getCreeperFuseTime(creeper);
		int radius = getCreeperRadius(creeper);
		
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
		int fuseTime = 999;
		
		Field field1 = null;
		Field field2 = null;
		try
		{
			field1 = EntityCreeper.class.getDeclaredField("timeSinceIgnited");
			field2 = EntityCreeper.class.getDeclaredField("fuseTime");
		} catch(Exception e)
		{
			try
			{
				field1 = EntityCreeper.class.getDeclaredField("field_70833_d");
				field2 = EntityCreeper.class.getDeclaredField("field_82225_f");
			} catch(Exception e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return fuseTime;
			}
		}
		
		field1.setAccessible(true);
		field2.setAccessible(true);
		
		try
		{
			fuseTime = (int)(field2.getInt(creeper) - field1.getInt(creeper));
		} catch(Exception e)
		{
			e.printStackTrace();
			return fuseTime;
		}
		
		return fuseTime;
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
