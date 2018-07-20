package funwayguy.epicsiegemod.ai.modifiers;

import funwayguy.epicsiegemod.ai.ESM_EntityAIWander;
import funwayguy.epicsiegemod.api.ITaskModifier;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;

// This doesn't actually modify the wander itself but rather adds a wrapper with some extra latency prevention measures
public class ModifierWander implements ITaskModifier
{
    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task)
    {
        return entityLiving instanceof EntityCreature && task instanceof EntityAIWander;
    }
    
    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry)
    {
        return new ESM_EntityAIWander((EntityCreature)host, (EntityAIWander)entry);
    }
}
