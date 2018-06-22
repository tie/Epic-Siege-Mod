package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import funwayguy.epicsiegemod.ai.utils.CreeperHooks;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ESM_EntityAICreeperSwell extends EntityAIBase
{
    /** The creeper that is swelling. */
    private EntityCreeper creeper;
    /** The creeper's attack target. This is used for the changing of the creeper's state. */
    private EntityLivingBase attackTarget;
    private CreeperHooks creeperHooks;
    private boolean detLocked = false;
    private int blastSize = -1;
    
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
        
        if(blastSize < 0)
        {
        	blastSize = creeperHooks.getExplosionSize(); // Powered state is ignored for now
        }
        
        return this.creeper.getCreeperState() > 0 || canBreachEntity(target) || (target != null && this.creeper.getDistanceSq(target) < blastSize * blastSize);
    }
    
    private boolean canBreachEntity(EntityLivingBase target)
    {
        return ESM_Settings.CreeperBreaching && creeper.ticksExisted > 60 && target != null && !creeper.isRiding() && !creeper.hasPath() && creeper.getDistance(target) < 64;
    
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
    	
    	int finalBlastSize = blastSize * (creeperHooks.isPowered()? 2 : 1);
    	boolean breaching = false;
    	
    	if(canBreachEntity(attackTarget))
    	{
    		breaching = true;
    	}
    	
        if (this.attackTarget == null)
        {
            this.creeper.setCreeperState(-1);
        }
        else if (this.creeper.getDistanceSq(this.attackTarget) > finalBlastSize * finalBlastSize && !breaching)
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
