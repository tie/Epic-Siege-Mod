package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;

public class ESM_EntityAIWander extends EntityAIBase
{
    private final EntityAIWander wander;
    private final EntityCreature creature;
    private int delay = 0;
    
    public ESM_EntityAIWander(EntityCreature entity, EntityAIWander wander)
    {
        this.wander = wander;
        this.creature = entity;
        
        this.setMutexBits(wander.getMutexBits());
    }
    
    @Override
    public boolean shouldExecute()
    {
        if(delay > 0)
        {
            delay--;
            return false;
        }
        
        // Passengers override AI pathing. This wast the vanilla "bug" that needed fixing
        return wander.shouldExecute() && creature.getNavigator().noPath() && creature.getAttackTarget() == null && creature.getPassengers().size() > 0;
    }
    
    @Override
    public boolean shouldContinueExecuting()
    {
        return wander.shouldContinueExecuting();
    }
    
    @Override
    public void startExecuting()
    {
        delay = 20; // May only update wander once per second
        wander.startExecuting();
    }
}
