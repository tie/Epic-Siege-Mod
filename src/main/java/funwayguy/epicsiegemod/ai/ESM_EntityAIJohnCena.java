package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import funwayguy.epicsiegemod.ai.utils.CreeperHooks;
import funwayguy.epicsiegemod.client.ESMSounds;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ESM_EntityAIJohnCena extends EntityAIBase
{
    /** The creeper that is swelling. */
    EntityCreeper creeper;
    /** The creeper's attack target. This is used for the changing of the creeper's state. */
    EntityLivingBase attackTarget;
    CreeperHooks creeperHooks;
    boolean detLocked = false;
    int blastSize = -1;
    
    public ESM_EntityAIJohnCena(EntityCreeper creeper)
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
        return this.creeper.getCreeperState() > 0 || canBreachEntity(target) || (target != null && this.creeper.getDistanceSqToEntity(target) < blastSize * blastSize);
    }
    
    @Override
    public boolean shouldContinueExecuting()
    {
    	return true;
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
		creeper.playSound(ESMSounds.sndCenaStart, 1F, 1F);
        this.attackTarget = this.creeper.getAttackTarget();
        this.creeper.setCustomNameTag("John Cena");
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
		creeper.setCreeperState(1);
		return;
    }
}
