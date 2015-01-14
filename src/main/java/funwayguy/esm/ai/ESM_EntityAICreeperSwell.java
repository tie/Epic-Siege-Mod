package funwayguy.esm.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.handlers.entities.ESM_CreeperHandler;

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
        MovingObjectPosition mop = GetMovingObjectPosition(this.swellingCreeper, false);
    	
    	boolean enableBreach = entitylivingbase != null && ESM_Settings.CreeperBreaching && !swellingCreeper.hasPath() && mop != null && mop.typeOfHit == MovingObjectType.BLOCK;
        return this.swellingCreeper.getCreeperState() > 0 || enableBreach || (entitylivingbase != null && this.swellingCreeper.getDistanceSqToEntity(entitylivingbase) <= detDist);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        //this.swellingCreeper.getNavigator().clearPathEntity();
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
    	
    	boolean enableBreach = this.creeperAttackTarget != null && !this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget) && ESM_Settings.CreeperBreaching && swellingCreeper.getNavigator().noPath();
    	
        if (this.creeperAttackTarget == null)
        {
        	System.out.println("No target");
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > (detDist * 2) && !enableBreach)
        {
        	System.out.println("Too far can't breach");
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget) && !enableBreach)
        {
        	System.out.println("Can't see and can't breach");
            this.swellingCreeper.setCreeperState(-1);
        }
        else
        {
        	if(enableBreach)
        	{
        		//breachLock = true;
        	}
            this.swellingCreeper.setCreeperState(1);
        }
    }

    public static MovingObjectPosition GetMovingObjectPosition(EntityLivingBase entityLiving, boolean liquids)
    {
        float f = 1.0F;
        float f1 = entityLiving.prevRotationPitch + (entityLiving.rotationPitch - entityLiving.prevRotationPitch) * f;
        float f2 = entityLiving.prevRotationYaw + (entityLiving.rotationYaw - entityLiving.prevRotationYaw) * f;
        double d0 = entityLiving.prevPosX + (entityLiving.posX - entityLiving.prevPosX) * (double)f;
        double d1 = entityLiving.prevPosY + (entityLiving.posY - entityLiving.prevPosY) * (double)f + (double)entityLiving.getEyeHeight();
        double d2 = entityLiving.prevPosZ + (entityLiving.posZ - entityLiving.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 1.0D; // Ray Distance
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return entityLiving.worldObj.func_147447_a(vec3, vec31, liquids, !liquids, false);
    }
}
