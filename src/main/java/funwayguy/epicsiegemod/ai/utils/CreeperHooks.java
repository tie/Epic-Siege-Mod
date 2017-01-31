package funwayguy.epicsiegemod.ai.utils;

import java.lang.reflect.Field;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.core.ESM;

public class CreeperHooks
{
	public static final DataParameter<Boolean> POWERED;
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
	
	public void setPowered()
	{
		creeper.getDataManager().set(POWERED, true);
	}
	
	public int getExplosionSize()
	{
		try
		{
			return blastSize.getInt(creeper);
		} catch(Exception e)
		{
			return 3;
		}
	}
	
	public void setExplosionSize(int value)
	{
		try
		{
			blastSize.setInt(creeper, value);
		} catch(Exception e){}
	}
	
	static
	{
		POWERED = ObfuscationReflectionHelper.getPrivateValue(EntityCreeper.class, null, "field_184714_b", "POWERED");
		
		try
		{
			blastSize = EntityCreeper.class.getDeclaredField("field_82226_g");
		} catch(Exception e1)
		{
			try
			{
				blastSize = EntityCreeper.class.getDeclaredField("explosionRadius");
			} catch(Exception e2)
			{
				ESM.logger.log(Level.ERROR, "Unable to set Creeper hooks");
			}
		}
	}
}
