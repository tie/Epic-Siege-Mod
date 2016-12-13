package funwayguy.epicsiegemod.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public interface ITaskModifier
{
	/**
	 * Returns true if this entity's AI should be replaced/removed
	 */
	boolean isValid(EntityLiving entityLiving, EntityAIBase task);
	
	/**
	 * Returns a replacement AI or null to remove without replacement
	 */
	EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry);
}
