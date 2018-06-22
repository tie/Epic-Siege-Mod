package funwayguy.epicsiegemod.ai;

import com.google.common.base.Function;
//import com.google.common.base.Predicate;
import funwayguy.epicsiegemod.ai.utils.PredicateTargetBasic;
import funwayguy.epicsiegemod.capabilities.combat.CapabilityAttackerHandler;
import funwayguy.epicsiegemod.capabilities.combat.IAttackerHandler;
import funwayguy.epicsiegemod.core.ESM_Settings;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ESM_EntityAINearestAttackableTarget extends ESM_EntityAITarget
{
	private EntityLiving taskOwner;
    private final List<Predicate<EntityLivingBase>> targetChecks = new ArrayList<>();
    private final int targetChance;
    private final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    private java.util.function.Predicate<? super EntityLivingBase> targetEntitySelector;
    private EntityLivingBase targetEntity;
    private final FunctionEntity visFunc;

    public ESM_EntityAINearestAttackableTarget(EntityLiving host, boolean checkSight)
    {
        this(host, checkSight, false);
    }

    public ESM_EntityAINearestAttackableTarget(EntityLiving host, boolean checkSight, boolean onlyNearby)
    {
        this(host, 10, checkSight, onlyNearby, null);
    }

    public ESM_EntityAINearestAttackableTarget(EntityLiving host, int chance, boolean checkSight, boolean onlyNearby, final Predicate <? super EntityLivingBase> targetSelector)
    {
        super(host, checkSight, onlyNearby);
        this.taskOwner = host;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(host);
        this.setMutexBits(1);
        this.visFunc = new FunctionEntity(host);
        this.targetEntitySelector = (Predicate<EntityLivingBase>)p_apply_1_ -> p_apply_1_ != null && ((targetSelector == null || targetSelector.test(p_apply_1_)) && (EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) && ESM_EntityAINearestAttackableTarget.this.isSuitableTarget(p_apply_1_, false)));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	if(this.taskOwner.ticksExisted % 10 != 0) // Rate limiting
		{
			return false;
		} else if(this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
        {
            return false;
        }
        
        List<EntityLivingBase> list = this.taskOwner.world.getEntitiesWithinAABB(EntityLivingBase.class, this.func_188511_a(this.getTargetDistance()), this.targetEntitySelector::test);
        
        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            list.sort(this.theNearestAttackableTargetSorter);
            this.targetEntity = list.get(0);
            return true;
        }
    }

    private AxisAlignedBB func_188511_a(double p_188511_1_)
    {
        return this.taskOwner.getEntityBoundingBox().grow(p_188511_1_, 16.0D, p_188511_1_);
    }
    
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
    
    @Override
    public boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles)
    {
    	if(!super.isSuitableTarget(target, includeInvincibles))
    	{
    		return false;
    	}
    	
    	if(target.hasCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null))
    	{
    		IAttackerHandler ah = target.getCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null);
    		
    		if(ah != null && !ah.canAttack(target, taskOwner)) // Normally used for attacker count but can be used for additional restrictions
    		{
    			return false;
    		}
    	}
    	
    	Double visObj = visFunc.apply(target);
    	if(visObj != null && taskOwner.getDistance(target) > this.getTargetDistance() * visObj)
    	{
    		return false; // Target has reduced visibility and is out of range
    	}
    	
    	boolean flag = false;
    	
    	for(Predicate<EntityLivingBase> p : targetChecks)
    	{
    		if(p.test(target))
    		{
    			flag = true;
    			break;
    		}
    	}
    	
    	if(!flag && ESM_Settings.attackPets && target instanceof IEntityOwnable)
    	{
    		IEntityOwnable pet = (IEntityOwnable)target;
    		
    		if(pet.getOwner() instanceof EntityPlayer)
    		{
    			flag = true;
    		}
    	}
    	
    	return flag;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
	public void addTarget(Class<? extends EntityLivingBase> target)
    {
    	targetChecks.add(new PredicateTargetBasic(target));
    }
    
    public static class FunctionEntity implements Function<EntityLivingBase,Double>
    {
    	EntityLivingBase host;
    	
    	private FunctionEntity(EntityLivingBase host)
    	{
    		this.host = host;
    	}
    	
		@Override
		public Double apply(EntityLivingBase input)
		{
			double visibility = 1D;
			
            ItemStack itemstack = input.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            
			if(itemstack.getItem() == Items.SKULL)
			{
				int i = itemstack.getItemDamage();
				boolean flag0 = host instanceof EntitySkeleton && i == 0;
				boolean flag1 = host instanceof EntityZombie && i == 2;
				boolean flag2 = host instanceof EntityCreeper && i == 4;
				
				if (flag0 || flag1 || flag2)
				{
					visibility *= 0.5D;
				}
			} else if(itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN))
			{
				if(host instanceof EntityEnderman)
				{
					return 0D;
				}
			}
            
            if(input.isSneaking())
            {
            	visibility *= 0.8D;
            }
            
            if(input.isInvisible())
            {
            	double av = 0.1D;
            	int total = 0;
            	int num = 0;
            	
            	Iterable<ItemStack> armor = input.getArmorInventoryList();
            	
				for(ItemStack a : armor)
				{
					total ++;
					
					if(a != null)
					{
						num ++;
					}
				}
				
				if(total > 0)
				{
					av = Math.max(0.1D, (double)total/(double)num);
				}
            	
            	visibility *= av;
            }
            
            return visibility;
		}
    }
}
