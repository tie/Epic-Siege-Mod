package funwayguy.esm.ai;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.core.ESM_Utils;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;

public class ESM_EntityAINearestAttackableTarget extends ESM_EntityAITarget
{
    private final Class<?> targetClass;
    private final int targetChance;
    private int searchDelay = 0;

    /** Instance of EntityAINearestAttackableTargetSorter. */
    private final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;

    /**
     * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
     * restrictions)
     */
    private final IEntitySelector targetEntitySelector;
    private EntityLivingBase targetEntity;

    public ESM_EntityAINearestAttackableTarget(EntityCreature par1EntityCreature, Class<?> par2Class, int par3, boolean par4)
    {
        this(par1EntityCreature, par2Class, par3, par4, false);
    }

    public ESM_EntityAINearestAttackableTarget(EntityCreature par1EntityCreature, Class<?> par2Class, int par3, boolean par4, boolean par5)
    {
        this(par1EntityCreature, par2Class, par3, par4, par5, (IEntitySelector)null);
    }

    public ESM_EntityAINearestAttackableTarget(EntityCreature par1EntityCreature, Class<?> par2Class, int par3, boolean par4, boolean par5, IEntitySelector par6IEntitySelector)
    {
        super(par1EntityCreature, par4, par5);
        this.targetClass = par2Class;
        this.targetChance = par3;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(par1EntityCreature);
        this.setMutexBits(1);
        this.targetEntitySelector = new ESM_EntityAINearestAttackableTargetSelector(this, par6IEntitySelector);
    }
    
    public void resetTask()
    {
    	super.resetTask();
    	this.searchDelay = 0;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @SuppressWarnings("unchecked")
	public boolean shouldExecute()
    {
    	if(searchDelay > 0)
    	{
    		searchDelay--;
    		return false;
    	} else
    	{
    		searchDelay = ESM_Settings.Awareness/2;
    	}
    	
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
        {
            return false;
        } else if(targetClass == EntityVillager.class && (!(taskOwner instanceof EntityZombie) || taskOwner instanceof EntityPigZombie) && !ESM_Settings.VillagerTarget)
    	{
    		return false;
    	} else if(targetClass == EntityCreature.class && !ESM_Settings.Chaos)
    	{
    		return false;
    	} else
        {
            double d0 = this.getTargetDistance();
            List<?> list = this.taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(d0, d0, d0), this.targetEntitySelector);
            Collections.sort(list, this.theNearestAttackableTargetSorter);

            if (list.isEmpty())
            {
                return false;
            }
            else
            {
                //this.targetEntity = (EntityLivingBase)list.get(0);
                //return true;
            	Iterator<?> var2 = list.iterator();
            	while (var2.hasNext())
                {
                    EntityLivingBase entity = (EntityLivingBase)var2.next();
                    
                    if(taskOwner.getDistanceToEntity(entity) > this.getTargetDistance())
                    {
                    	continue;
                    }
                    
                    int pathCount = ESM_Utils.getAIPathCount(this.taskOwner.worldObj, entity);
                    
                	if(pathCount < ESM_Settings.TargetCap || ESM_Settings.TargetCap == -1 || ESM_Utils.isCloserThanOtherAttackers(this.taskOwner.worldObj, taskOwner, entity))
                    {
                		this.targetEntity = entity;
                		return true;
                	}
                }
            	
            	return false;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	this.searchDelay = 0;
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}
