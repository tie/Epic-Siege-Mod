package funwayguy.epicsiegemod.ai.modifiers;

import java.lang.reflect.Field;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntitySkeleton;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.api.ITaskModifier;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ModifierBowAttack implements ITaskModifier
{
	private static Field f_moveSpeedAmp;
	private static Field f_attackCooldown;
	
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		return entityLiving instanceof EntitySkeleton && task.getClass() == EntityAIAttackRangedBow.class;
	}
	
	@Override
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
	{
		EntitySkeleton skeleton = (EntitySkeleton)host;
		
		try
		{
			return new EntityAIAttackRangedBow(skeleton, f_moveSpeedAmp.getDouble(entry), f_attackCooldown.getInt(entry), ESM_Settings.SkeletonDistance);
		} catch(Exception e)
		{
			return new EntityAIAttackRangedBow(skeleton, 1D, 20, ESM_Settings.SkeletonDistance);
		}
	}
	
	static
	{
		try
		{
			f_moveSpeedAmp = EntityAIAttackRangedBow.class.getDeclaredField("field_188500_b");
			f_attackCooldown = EntityAIAttackRangedBow.class.getDeclaredField("field_188501_c");
			f_moveSpeedAmp.setAccessible(true);
			f_attackCooldown.setAccessible(true);
		} catch(Exception e1)
		{
			try
			{
				f_moveSpeedAmp = EntityAIAttackRangedBow.class.getDeclaredField("moveSpeedAmp");
				f_attackCooldown = EntityAIAttackRangedBow.class.getDeclaredField("attackCooldown");
				f_moveSpeedAmp.setAccessible(true);
				f_attackCooldown.setAccessible(true);
			} catch(Exception e2)
			{
				ESM.logger.log(Level.INFO, "Unable to access ranged attack variables", e2);
			}
		}
	}
}
