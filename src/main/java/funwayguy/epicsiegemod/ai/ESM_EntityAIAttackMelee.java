package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ESM_EntityAIAttackMelee extends EntityAIBase
{
    private EntityLiving attacker;
    /** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
    int attackTick;
    /** The speed with which the mob will approach the target */
    private double speedTowardsTarget;
    /** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
    private boolean longMemory;
    /** The PathEntity of our entity. */
    private Path entityPathEntity;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int failedPathFindingPenalty = 0;
    //private boolean canPenalize = false;
    private boolean strafeClockwise = false;

    public ESM_EntityAIAttackMelee(EntityLiving creature, double speedIn, boolean useLongMemory)
    {
        this.attacker = creature;
        //this.worldObj = creature.world;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
            //if (canPenalize)
            {
                if (--this.delayCounter <= 0)
                {
                    this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
                    this.delayCounter = Math.max(4 + this.attacker.getRNG().nextInt(7), (int)this.attacker.getDistance(entitylivingbase) - 16);
                    return this.entityPathEntity != null;
                }
                else
                {
                    return true;
                }
            }
            //this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
            //return this.entityPathEntity != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        return entitylivingbase != null && (entitylivingbase.isEntityAlive() && (!this.longMemory ? !this.attacker.getNavigator().noPath() : ((!(attacker instanceof EntityCreature) || ((EntityCreature)this.attacker).isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase))) && (!(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative()))));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget * (this.attacker.getCustomNameTag().equalsIgnoreCase("Vash505")? 1.25F : 1F));
        this.delayCounter = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative()))
        {
            this.attacker.setAttackTarget(null);
        }

        this.attacker.getNavigator().clearPath();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        
        if(entitylivingbase == null)
        {
            return;
        }
        
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
        double d1 = this.func_179512_a(entitylivingbase);
        --this.delayCounter;

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F))
        {
            this.targetX = entitylivingbase.posX;
            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
            boolean onTarget = false;
            
            if (this.attacker.getNavigator().getPath() != null)
            {
                PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                
                if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < (d0 > 1024 ? 16 : 1)) // If further than 32 blocks, reduce the accuracy to within a chunk
                {
                    failedPathFindingPenalty = 0; // Because we're not updating the path, the existing delay will suffice
                    onTarget = true; // Path is still on target and doesn't require updating
                }
                else
                {
                    // Path is off target. Update and delay next round
                    failedPathFindingPenalty += Math.max(10, attacker.getDistance(entitylivingbase) - 6);
                }
            }
            else
            {
                // New path needs to be created. Add delay inbetween attempts
                failedPathFindingPenalty +=Math.max(10, attacker.getDistance(entitylivingbase) - 6);
            }
            
            this.delayCounter += failedPathFindingPenalty;
            
            if(!onTarget && !this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget * (this.attacker.getCustomNameTag().equalsIgnoreCase("Vash505")? 1.25F : 1F)))
            {
                this.delayCounter += 15;
            }
        }
        
        if(ESM_Settings.attackEvasion && d0 < 8*8)
        {
        	float sSpd = this.attacker.getCustomNameTag().equalsIgnoreCase("Vash505")? 1F : 0.5F;
            this.attacker.getMoveHelper().strafe(sSpd, this.strafeClockwise ? sSpd : -sSpd);
            this.attacker.faceEntity(entitylivingbase, 30.0F, 30.0F);
        }
        
        this.attackTick = Math.max(this.attackTick - 1, 0);
        
        if (d0 <= d1 && this.attackTick <= 0)
        {
        	this.strafeClockwise = this.attacker.getRNG().nextBoolean();
            this.attackTick = 10 + this.attacker.getRNG().nextInt(10);
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            if(this.attacker instanceof IAnimals)
            {
            	entitylivingbase.attackEntityFrom(DamageSource.causeMobDamage(this.attacker), 1F);
            } else
            {
            	this.attacker.attackEntityAsMob(entitylivingbase);
            }
        }
    }

    private double func_179512_a(EntityLivingBase attackTarget)
    {
        return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
    }
}
