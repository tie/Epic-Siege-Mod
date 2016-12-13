package funwayguy.epicsiegemod.ai;

import funwayguy.epicsiegemod.ai.utils.CreeperHooks;
import funwayguy.epicsiegemod.core.ESM_Settings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;

public class ESM_EntityAICreeperSwell extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityCreeper creeper;
    /** The creeper's attack target. This is used for the changing of the creeper's state. */
    EntityLivingBase attackTarget;
    CreeperHooks creeperHooks;
    boolean detLocked = false;
    
    public ESM_EntityAICreeperSwell(EntityCreeper creeper)
    {
        this.creeper = creeper;
        this.creeperHooks = new CreeperHooks(creeper);
        if(!ESM_Settings.CreeperChargers)
        {
        	this.setMutexBits(1);
        }
    }
    
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase target = this.creeper.getAttackTarget();
        int blastSize = creeperHooks.getExplosionSize(); // Powered state is ignored for now
        return this.creeper.getCreeperState() > 0 || canBreachEntity(target) || (target != null && this.creeper.getDistanceSqToEntity(target) < blastSize * blastSize);
    }
    
    public boolean canBreachEntity(EntityLivingBase target)
    {
    	if(ESM_Settings.CreeperBreaching && creeper.ticksExisted > 60 && target != null && !creeper.isRiding() && !creeper.hasPath() && creeper.getDistanceToEntity(target) < 64)
        {
        	return true;
        }
    	
    	return false;
    }
    
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	//this.creeper.getNavigator().clearPathEntity(); // Can interfere with breaching
        this.attackTarget = this.creeper.getAttackTarget();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.attackTarget = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
    	if(detLocked)
    	{
    		creeper.setCreeperState(1);
    		return;
    	}
    	
    	int blastSize = creeperHooks.getExplosionSize() * (creeperHooks.isPowered()? 2 : 1);
    	boolean breaching = false;
    	
    	if(canBreachEntity(attackTarget))
    	{
    		breaching = true;
    	}
    	
        if (this.attackTarget == null)
        {
            this.creeper.setCreeperState(-1);
        }
        else if (this.creeper.getDistanceSqToEntity(this.attackTarget) > blastSize * blastSize && !breaching)
        {
            this.creeper.setCreeperState(-1);
        }
        else if (!this.creeper.getEntitySenses().canSee(this.attackTarget) && !breaching)
        {
            this.creeper.setCreeperState(-1);
        }
        else
        {
        	detLocked = true;
            this.creeper.setCreeperState(1);
        }
    }
}
