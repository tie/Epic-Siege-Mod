package funwayguy.epicsiegemod.ai.hooks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntitySenses;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class EntitySensesProxy extends EntitySenses
{
    private final EntityLiving entityObj;
    private List<Entity> seenEntities = new ArrayList<>();
    private List<Entity> unseenEntities = new ArrayList<>();
    
	public EntitySensesProxy(EntityLiving entityObjIn)
	{
		super(entityObjIn);
		this.entityObj = entityObjIn;
	}
	
    /**
     * Clears canSeeCachePositive and canSeeCacheNegative.
     */
	@Override
    public void clearSensingCache()
    {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }
	
    /**
     * Checks, whether 'our' entity can see the entity given as argument (true) or not (false), caching the result.
     */
    @Override
    public boolean canSee(Entity entityIn)
    {
        if (this.seenEntities.contains(entityIn))
        {
            return true;
        }
        else if (this.unseenEntities.contains(entityIn))
        {
            return false;
        }
        else
        {
            this.entityObj.world.profiler.startSection("canSee");
            boolean flag = (entityIn.getDistance(entityObj) <= ESM_Settings.Xray) || this.entityObj.canEntityBeSeen(entityIn);
            this.entityObj.world.profiler.endSection();

            if (flag)
            {
                this.seenEntities.add(entityIn);
            }
            else
            {
                this.unseenEntities.add(entityIn);
            }

            return flag;
        }
    }
}
