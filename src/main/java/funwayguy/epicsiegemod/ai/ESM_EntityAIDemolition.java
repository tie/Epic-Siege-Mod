package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;

public class ESM_EntityAIDemolition extends EntityAIBase
{
	public EntityLiving host;
	private int delay = 0;
	
	public ESM_EntityAIDemolition(EntityLiving host)
	{
		this.host = host;
	}
	
	@Override
	public boolean shouldExecute()
	{
		delay -= 1;
		
		if(delay > 0)
		{
			return false;
		}
		
		delay = 0;
		
		boolean flag = (host.getHeldItemMainhand() != null && host.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.TNT)) || (host.getHeldItemOffhand() != null && host.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.TNT));
		
		return flag && host.getAttackTarget() != null && host.getAttackTarget().getDistanceToEntity(host) < 4F;
	}
	
	@Override
	public boolean shouldContinueExecuting()
	{
		return false;
	}
	
	@Override
	public void startExecuting()
	{
		delay = 200;
		EntityTNTPrimed tnt = new EntityTNTPrimed(host.world, host.posX, host.posY, host.posZ, host);
		host.world.spawnEntity(tnt);
        host.world.playSound((EntityPlayer)null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}
