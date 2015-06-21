package funwayguy.esm.ai;

import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import funwayguy.esm.core.ESM_Settings;

class ESM_EntityAINearestAttackableTargetSelector implements IEntitySelector
{
	EntityLivingBase owner;
    final IEntitySelector field_111103_c;
    final List<Class<? extends EntityLivingBase>> targetable;
    final ESM_EntityAINearestAttackableTarget field_111102_d;

    public ESM_EntityAINearestAttackableTargetSelector(EntityLivingBase owner, ESM_EntityAINearestAttackableTarget targetAINearestAttackableTarget, IEntitySelector par2IEntitySelector, List<Class<? extends EntityLivingBase>> targetable)
    {
    	this.owner = owner;
        this.field_111102_d = targetAINearestAttackableTarget;
        this.field_111103_c = par2IEntitySelector;
        this.targetable = targetable;
    }

    /**
     * Return whether the specified entity is applicable to this filter.
     */
    public boolean isEntityApplicable(Entity target)
    {
    	if(target == null)
    	{
    		return false;
    	} else if(!ESM_Settings.friendlyFire && owner instanceof IMob && (ESM_Settings.Chaos? owner.getClass() == target.getClass() : target instanceof IMob))
		{
			return false;
		}
    	
    	if(!ESM_Settings.Chaos) // Should we even check if the target is applicable or just allow every little thing to be targeted
    	{
	    	boolean flag = true;
	    	
	    	for(Class<? extends EntityLivingBase> clazz : targetable)
	    	{
	    		if(clazz.isAssignableFrom(target.getClass()))
	    		{
	    			flag = false;
	    			break;
	    		}
	    	}
	    	
	    	if(flag)
	    	{
	    		return false;
	    	}
    	}
    	
        return !(target instanceof EntityLivingBase) ? false : (this.field_111103_c != null && !this.field_111103_c.isEntityApplicable(target) ? false : this.field_111102_d.isSuitableTarget((EntityLivingBase)target, false));
    }
}
