package funwayguy.esm.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class ESM_EntityAIBrainController extends EntityAIBase
{
	static EntityLiving globalHost;
	public static NeatBrain brain;
	EntityLiving host;
	
	public ESM_EntityAIBrainController(EntityLiving entityLiving)
	{
		this.host = entityLiving;
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(brain == null)
		{
			globalHost = host;
			brain = new NeatBrain(host);
			brain.PlayTop();
		} else if(globalHost == null || globalHost.isDead)
		{
			globalHost = host;
			brain.TransferBrain(host);
		}
		
		return globalHost == host;
	}
	
	@Override
	public void updateTask()
	{
		brain.TickBrain();
	}
	
}
