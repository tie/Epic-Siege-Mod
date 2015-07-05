package funwayguy.esm.handlers.entities;

import net.minecraft.entity.monster.EntityCreeper;
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
		if(creeper.getCreeperState() == 1 && creeper.ridingEntity != null)
		{
			creeper.dismountEntity(creeper.ridingEntity);
			creeper.ridingEntity.riddenByEntity = null;
			creeper.ridingEntity = null;
		}
	}
}
