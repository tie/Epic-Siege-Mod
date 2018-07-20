package funwayguy.epicsiegemod.ai.utils;

import java.lang.reflect.Field;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.core.ESM;

public class CreeperHooks
{
	private static final DataParameter<Boolean> POWERED;
	private static Field blastSize = null;
	
	private EntityCreeper creeper;
	
	public CreeperHooks(EntityCreeper creeper)
	{
		this.creeper = creeper;
	}
	
	public EntityCreeper getCreeper()
	{
		return creeper;
	}
	
	public boolean isPowered()
	{
		return creeper.getDataManager().get(POWERED);
	}
	
	public void setPowered(boolean state)
	{
		creeper.getDataManager().set(POWERED, state);
	}
	
	public int getExplosionSize()
	{
		try
		{
			return blastSize.getInt(creeper);
		} catch(Exception e)
		{
			ESM.logger.log(Level.ERROR, "Unable to get creeper blast radius", e);
			return 3;
		}
	}
	
	public void setExplosionSize(int value)
	{
		try
		{
			blastSize.setInt(creeper, value);
		} catch(Exception e)
		{
			ESM.logger.log(Level.ERROR, "Unable to set creeper blast radius", e);
		}
	}
	
	static
	{
		POWERED = ObfuscationReflectionHelper.getPrivateValue(EntityCreeper.class, null, "field_184714_b", "POWERED");
		
		try
		{
			blastSize = EntityCreeper.class.getDeclaredField("field_82226_g");
			blastSize.setAccessible(true);
		} catch(Exception e1)
		{
			try
			{
				blastSize = EntityCreeper.class.getDeclaredField("explosionRadius");
				blastSize.setAccessible(true);
			} catch(Exception e2)
			{
				ESM.logger.log(Level.ERROR, "Unable to set Creeper hooks");
			}
		}
	}
}
