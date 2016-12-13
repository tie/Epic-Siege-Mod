package funwayguy.epicsiegemod.capabilities.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public interface IAttackerHandler
{
	boolean canAttack(EntityLivingBase target, EntityLiving attacker);
	void addAttacker(EntityLivingBase target, EntityLiving attacker);
	int getAttackers();
	void updateAttackers(EntityLivingBase target);
}
