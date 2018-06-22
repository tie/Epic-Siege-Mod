package funwayguy.epicsiegemod.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityZombie;
import funwayguy.epicsiegemod.ai.ESM_EntityAIZombieAttack;
import funwayguy.epicsiegemod.api.ITaskModifier;

public class ModifierZombieAttack implements ITaskModifier
{
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		return task != null && task.getClass() == EntityAIZombieAttack.class && entityLiving instanceof EntityZombie;
	}
	
	@Override
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
	{
		try
		{
			return new ESM_EntityAIZombieAttack((EntityZombie)host, ModifierAttackMelee.f_speed.getDouble(entry), true);
		} catch(Exception e)
		{
			return new ESM_EntityAIZombieAttack((EntityZombie)host, 1D, true);
		}
	}
}
