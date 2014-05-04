package funwayguy.esm.handlers.entities;

import java.lang.reflect.Field;

import funwayguy.esm.core.ESM_Settings;
import net.minecraft.entity.monster.EntityBlaze;

public class ESM_BlazeHandler
{
	public static void onEntityJoinWorld(EntityBlaze blaze)
	{
		blaze.getEntityData().setInteger("ESM_FIREBALLS", 0);
	}
	
	public static void onLivingUpdate(EntityBlaze blaze)
	{
		int fireballs = getBlazeFireballs(blaze);
		
		if(fireballs > 1 && fireballs < 5 && blaze.getEntityData().getInteger("ESM_FIREBALLS") < ESM_Settings.BlazeFireballs)
		{
			setBlazeFireballs(blaze, 2);
		} else if(fireballs > 1)
		{
			setBlazeFireballs(blaze, 5);
			blaze.getEntityData().setInteger("ESM_FIREBALLS", 0);
		}
	}
	
	public static int getBlazeFireballs(EntityBlaze blaze)
	{
		int fireballs = -1;
		
		Field field = null;
		try
		{
			field = EntityBlaze.class.getDeclaredField("field_70846_g");
		} catch(NoSuchFieldException e)
		{
			e.printStackTrace();
			return fireballs;
		} catch(SecurityException e)
		{
			e.printStackTrace();
			return fireballs;
		}
		
		field.setAccessible(true);
		
		try
		{
			fireballs = (int)field.getInt(blaze);
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			return fireballs;
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return fireballs;
		}
		
		return fireballs;
	}
	
	public static void setBlazeFireballs(EntityBlaze blaze, int count)
	{
		Field field = null;
		try
		{
			field = EntityBlaze.class.getDeclaredField("field_70846_g");
		} catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch(SecurityException e)
		{
			e.printStackTrace();
		}
		
		field.setAccessible(true);
		
		try
		{
			field.setInt(blaze, count);
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
