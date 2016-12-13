package funwayguy.epicsiegemod.ai.modifiers;

import java.lang.reflect.Field;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.ai.ESM_EntityAIAttackRanged;
import funwayguy.epicsiegemod.api.ITaskModifier;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ModifierRangedAttack implements ITaskModifier
{
	private static Field f_entityMoveSpeed;
	private static Field f_attackIntervalMin;
	private static Field f_maxRangedAttackTime;
	
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		if(entityLiving instanceof IRangedAttackMob && task.getClass() == EntityAIAttackRanged.class)
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
	{
		try
		{
			return new ESM_EntityAIAttackRanged((IRangedAttackMob)host, f_entityMoveSpeed.getDouble(entry), f_attackIntervalMin.getInt(entry), f_maxRangedAttackTime.getInt(entry), ESM_Settings.SkeletonDistance);
		} catch(Exception e)
		{
			ESM.logger.log(Level.ERROR, "Unable to replace ranged attack", e);
			return new ESM_EntityAIAttackRanged((IRangedAttackMob)host, 1, 1, 1, ESM_Settings.SkeletonDistance);
		}
	}
	
	static
	{
		try
		{
			f_entityMoveSpeed = EntityAIAttackRanged.class.getDeclaredField("field_75321_e");
			f_attackIntervalMin = EntityAIAttackRanged.class.getDeclaredField("field_96561_g");
			f_maxRangedAttackTime = EntityAIAttackRanged.class.getDeclaredField("field_75325_h");
			f_entityMoveSpeed.setAccessible(true);
			f_attackIntervalMin.setAccessible(true);
			f_maxRangedAttackTime.setAccessible(true);
		} catch(Exception e1)
		{
			try
			{
				f_entityMoveSpeed = EntityAIAttackRanged.class.getDeclaredField("entityMoveSpeed");
				f_attackIntervalMin = EntityAIAttackRanged.class.getDeclaredField("attackIntervalMin");
				f_maxRangedAttackTime = EntityAIAttackRanged.class.getDeclaredField("maxRangedAttackTime");
				f_entityMoveSpeed.setAccessible(true);
				f_attackIntervalMin.setAccessible(true);
				f_maxRangedAttackTime.setAccessible(true);
			} catch(Exception e2)
			{
				ESM.logger.log(Level.INFO, "Unable to access ranged attack variables", e2);
			}
		}
	}
}
