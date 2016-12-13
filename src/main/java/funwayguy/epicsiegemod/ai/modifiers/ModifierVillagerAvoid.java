package funwayguy.epicsiegemod.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import funwayguy.epicsiegemod.api.ITaskModifier;

public class ModifierVillagerAvoid implements ITaskModifier
{
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		return entityLiving instanceof EntityVillager && task.getClass() == EntityAIAvoidEntity.class;
	}
	
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
	{
		// Can't search for interface IMob.class
		return new EntityAIAvoidEntity((EntityVillager)host, EntityMob.class, 12F, 0.6D, 0.6D);
	}
}
