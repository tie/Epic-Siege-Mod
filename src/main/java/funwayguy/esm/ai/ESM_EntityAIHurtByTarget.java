package funwayguy.esm.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

public class ESM_EntityAIHurtByTarget extends ESM_EntityAITarget
{
    boolean entityCallsForHelp;
    private int field_142052_b;

    public ESM_EntityAIHurtByTarget(EntityCreature creature, boolean callsForHelp)
    {
        super(creature, false);
        this.entityCallsForHelp = callsForHelp;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int i = this.taskOwner.func_142015_aE();
        return i != this.field_142052_b && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.field_142052_b = this.taskOwner.func_142015_aE();

        if (this.entityCallsForHelp)
        {
            double d0 = this.getTargetDistance();
            @SuppressWarnings("unchecked")
			List<EntityCreature> list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getBoundingBox(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand(d0, 10.0D, d0));
            Iterator<EntityCreature> iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityCreature entitycreature = iterator.next();
                
                if(entitycreature == null || !entitycreature.isEntityAlive())
                {
                	continue;
                }

                if (this.taskOwner != entitycreature && entitycreature.getAttackTarget() == null && (entitycreature.getTeam() == null || !entitycreature.isOnSameTeam(this.taskOwner.getAITarget())))
                {
                    entitycreature.setAttackTarget(this.taskOwner.getAITarget());
                }
            }
        }

        super.startExecuting();
    }
}