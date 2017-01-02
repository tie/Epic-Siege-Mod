package funwayguy.epicsiegemod.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.monster.EntityCreeper;
import funwayguy.epicsiegemod.ai.ESM_EntityAICreeperSwell;
import funwayguy.epicsiegemod.ai.ESM_EntityAIJohnCena;
import funwayguy.epicsiegemod.api.ITaskModifier;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ModifierCreeperSwell implements ITaskModifier
{
	@Override
	public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
	{
		return entityLiving instanceof EntityCreeper && task.getClass() == EntityAICreeperSwell.class;
	}
	
	@Override
	public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
	{
		if(host.worldObj.rand.nextInt(100) < ESM_Settings.CenaCreeperRarity)
		{
			return new ESM_EntityAIJohnCena((EntityCreeper)host);
		} else
		{
			return new ESM_EntityAICreeperSwell((EntityCreeper)host);
		}
	}
	
}
