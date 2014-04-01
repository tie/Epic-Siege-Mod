package funwayguy.esm.ai;

import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.handlers.entities.ESM_CreeperHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;

public class ESM_EntityAICreeperSwell extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityCreeper swellingCreeper;

    /**
     * The creeper's attack target. This is used for the changing of the creeper's state.
     */
    EntityLivingBase creeperAttackTarget;
    
    double detDist = 9.0D;
    boolean breachLock = false;

    public ESM_EntityAICreeperSwell(EntityCreeper par1EntityCreeper)
    {
        this.swellingCreeper = par1EntityCreeper;
    	detDist = (double)ESM_CreeperHandler.getCreeperRadius(swellingCreeper) + 0.5D;
    	detDist = detDist * detDist;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.swellingCreeper.getAttackTarget();
        return this.swellingCreeper.getCreeperState() > 0 || entitylivingbase != null && this.swellingCreeper.getDistanceSqToEntity(entitylivingbase) <= detDist;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.swellingCreeper.getNavigator().clearPathEntity();
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.creeperAttackTarget = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
    	if(breachLock)
    	{
            this.swellingCreeper.setCreeperState(1);
            return;
    	}
    	
    	boolean enableBreach = !this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget) && ESM_Settings.CreeperBreaching && swellingCreeper.getNavigator().noPath();
    	
        if (this.creeperAttackTarget == null)
        {
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > (detDist * 2))
        {
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget) && !enableBreach)
        {
            this.swellingCreeper.setCreeperState(-1);
        }
        else
        {
        	if(enableBreach)
        	{
        		breachLock = true;
        	}
            this.swellingCreeper.setCreeperState(1);
        }
    }
}
