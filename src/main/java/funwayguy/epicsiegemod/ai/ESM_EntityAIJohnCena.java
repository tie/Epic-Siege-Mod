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
    private EntityCreeper creeper;
    private CreeperHooks creeperHooks;
    private int blastSize = -1;
    
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
        
        return this.creeper.getCreeperState() > 0 || canBreachEntity(target) || (target != null && this.creeper.getDistanceSq(target) < blastSize * blastSize);
    }
    
    @Override
    public boolean shouldContinueExecuting()
    {
    	return true;
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
		creeper.playSound(ESMSounds.sndCenaStart, 1F, 1F);
        this.creeper.setCustomNameTag("John Cena");
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
		creeper.setCreeperState(1);
    }
}
