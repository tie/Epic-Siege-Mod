package funwayguy.esm.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ESM_EntityAIAvoidDetonations extends EntityAIBase
{
    public final IEntitySelector selector = new ExplosiveEntitySelector();
    public final Comparator<Entity> comparator;
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

    @SuppressWarnings("unchecked")
	public ESM_EntityAIAvoidDetonations(EntityCreature p_i1616_1_, float p_i1616_3_, double p_i1616_4_, double p_i1616_6_)
    {
        this.theEntity = p_i1616_1_;
        this.distanceFromEntity = p_i1616_3_;
        this.farSpeed = p_i1616_4_;
        this.nearSpeed = p_i1616_6_;
        this.entityPathNavigate = p_i1616_1_.getNavigator();
        this.setMutexBits(1);
        this.comparator = (Comparator<Entity>)new EntityAINearestAttackableTarget.Sorter(this.theEntity);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @SuppressWarnings("unchecked")
	public boolean shouldExecute()
    {
    	if(this.theEntity instanceof EntityCreeper)
    	{
    		EntityCreeper me = (EntityCreeper)this.theEntity;
    		if(me.getCreeperState() == 1)
    		{
    			return false;
    		}
    	}
    	
    	if(this.closestLivingEntity == null || !this.closestLivingEntity.isEntityAlive())
    	{
	        List<Entity> list = this.theEntity.worldObj.selectEntitiesWithinAABB(Entity.class, this.theEntity.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity), selector);
	
	        if (list.isEmpty())
	        {
	            return false;
	        }
	        
	        Collections.sort(list, comparator);
	
	        this.closestLivingEntity = list.get(0);
    	}

        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, MathHelper.ceiling_double_int(this.distanceFromEntity + 4D), 4, Vec3.createVectorHelper(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

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
            return this.entityPathEntity != null;
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
        if (this.theEntity.getDistanceToEntity(this.closestLivingEntity) < distanceFromEntity/2D)
        {
            this.theEntity.getNavigator().setSpeed(this.nearSpeed);
        }
        else
        {
            this.theEntity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}