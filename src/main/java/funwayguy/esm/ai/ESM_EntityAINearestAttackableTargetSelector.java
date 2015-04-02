package funwayguy.esm.ai;

import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import funwayguy.esm.core.ESM_Settings;

class ESM_EntityAINearestAttackableTargetSelector implements IEntitySelector
{
	EntityLivingBase owner;
    final IEntitySelector field_111103_c;
    final List<Class<? extends EntityLivingBase>> targetable;
    final ESM_EntityAINearestAttackableTarget field_111102_d;

    ESM_EntityAINearestAttackableTargetSelector(EntityLivingBase owner, ESM_EntityAINearestAttackableTarget targetAINearestAttackableTarget, IEntitySelector par2IEntitySelector, List<Class<? extends EntityLivingBase>> targetable)
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
    	}
    	
    	boolean flag = true;
    	
    	for(Class<? extends EntityLivingBase> clazz : targetable)
    	{
    		if(clazz.isAssignableFrom(target.getClass()))
    		//if(target.getClass().isAssignableFrom(clazz))
    		{
    			flag = false;
    			break;
    		}
    	}
    	
    	if(flag)
    	{
    		return false;
    	} else if(target instanceof EntityVillager && (!(owner instanceof EntityZombie) || owner instanceof EntityPigZombie) && !ESM_Settings.VillagerTarget) // You aren't permitted to target this unless enabled
    	{
    		return false;
    	} else if(target instanceof EntityCreature && !ESM_Settings.Chaos) // You aren't permitted to target this unless enabled
    	{
    		return false;
    	}
    	
        return !(target instanceof EntityLivingBase) ? false : (this.field_111103_c != null && !this.field_111103_c.isEntityApplicable(target) ? false : this.field_111102_d.isSuitableTarget((EntityLivingBase)target, false));
    }
}
