package funwayguy.esm.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;
import funwayguy.esm.core.ESM_Settings;

public class ESM_EntityAIAttackEvasion extends EntityAIBase
{
    public final IEntitySelector field_98218_a = new IEntitySelector()
    {
        /**
         * Return whether the specified entity is applicable to this filter.
         */
        public boolean isEntityApplicable(Entity p_82704_1_)
        {
            return p_82704_1_.isEntityAlive() && ESM_EntityAIAttackEvasion.this.theEntity.getEntitySenses().canSee(p_82704_1_);
        }
    };
    /** The entity we are attached to */
    private EntityCreature theEntity;
    private double farSpeed;
    private double nearSpeed;
    private Entity closestLivingEntity;
    private float distanceFromEntity;
    /** The PathEntity of our entity */
    private PathEntity entityPathEntity;
    /** The PathNavigate of our entity */
    private PathNavigate entityPathNavigate;

    public ESM_EntityAIAttackEvasion(EntityCreature creature, float distance, double farSpeed, double nearSpeed)
    {
        this.theEntity = creature;
        this.distanceFromEntity = distance;
        this.farSpeed = farSpeed;
        this.nearSpeed = nearSpeed;
        this.entityPathNavigate = creature.getNavigator();
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if(!ESM_Settings.attackEvasion)
    	{
    		return false;
    	}
    	

		@SuppressWarnings("unchecked")
		List<EntityMob> attackers = this.theEntity.worldObj.selectEntitiesWithinAABB(EntityMob.class, this.theEntity.boundingBox.expand(16D, 16D, 16D), this.field_98218_a);
		if(attackers.size() > 1)
		{
			return false;
		}
		
        @SuppressWarnings("unchecked")
		List<EntityPlayer> list = this.theEntity.worldObj.selectEntitiesWithinAABB(EntityPlayer.class, this.theEntity.boundingBox.expand((double)this.distanceFromEntity, (double)this.distanceFromEntity, (double)this.distanceFromEntity), this.field_98218_a);

        if (list.isEmpty())
        {
            return false;
        }
        
        Iterator<EntityPlayer> iterator = list.iterator();

        this.closestLivingEntity = null;
        
        while(iterator.hasNext())
        {
        	EntityPlayer player = iterator.next();
        	
        	if(player != null && player.isEntityAlive() && !player.capabilities.disableDamage)
        	{
                double dist = 5D;
                Vec3 vectorA = Vec3.createVectorHelper(player.posX, player.posY + player.eyeHeight, player.posZ);
                Vec3 vectorB = player.getLookVec();
                Vec3 vectorC = vectorA.addVector(vectorB.xCoord * dist, vectorB.yCoord * dist, vectorB.zCoord * dist);
                Entity rayEntity = AIUtils.RayCastEntities(player.worldObj, vectorA, vectorC, player);
                
                if(rayEntity != null)
                {
                	closestLivingEntity = player;
            		break;
                }
        	}
        }
        
        if(this.closestLivingEntity == null)
        {
        	return false;
        }

        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 8, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

        if (vec3 == null)
        {
            return false;
        }
        else if (this.closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity))
        {
            return false;
        }
        else
        {
            this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3);
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entityPathNavigate.noPath() && this.closestLivingEntity != null && this.closestLivingEntity.isEntityAlive();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	this.theEntity.setAttackTarget(null);
        this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.closestLivingEntity = null;
    }

    /**
     * Determine if this AI Task is interruptible by a higher (= lower value) priority task.
     */
    public boolean isInterruptible()
    {
        return false;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0D)
        {
            this.theEntity.getNavigator().setSpeed(this.nearSpeed);
        }
        else
        {
            this.theEntity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}