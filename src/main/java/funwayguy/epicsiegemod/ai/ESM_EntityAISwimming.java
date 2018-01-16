package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;

public class ESM_EntityAISwimming extends EntityAIBase
{
	private EntityLiving host;
	
	public ESM_EntityAISwimming(EntityLiving host)
	{
		this.host = host;
		this.setMutexBits(4);
		((PathNavigateGround)host.getNavigator()).setCanSwim(true);
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(!host.isInWater() && !host.isInLava())
		{
			return false;
		}
		
		BlockPos pos = host.getPosition();
		Path path = host.getNavigator().getPath();
		EntityLivingBase target = host.getAttackTarget();
		
		if(host.getAir() < 150) // Past 50% air, swim up!
		{
			return true;
		} else if(path != null && path.getFinalPathPoint() != null && path.getFinalPathPoint().y < pos.getY())
		{
			return false;
		} else if(target != null && target.getPosition().getY() < pos.getY() && host.getDistanceToEntity(target) < 8F)
		{
			return false;
		}
		
		return true;
	}
	
    public void updateTask()
    {
        host.getJumpHelper().setJumping();
    }
}
