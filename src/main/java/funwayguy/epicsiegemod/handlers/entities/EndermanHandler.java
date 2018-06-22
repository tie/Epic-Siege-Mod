package funwayguy.epicsiegemod.handlers.entities;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class EndermanHandler
{
	@SubscribeEvent
	public void onEnderTeleport(EnderTeleportEvent event)
	{
		if(event.getEntity().world.isRemote || !(event.getEntityLiving() instanceof EntityEnderman))
		{
			return;
		}
		
		EntityEnderman enderman = (EntityEnderman)event.getEntityLiving();
		
		if(ESM_Settings.EndermanPlayerTele && enderman.getAttackTarget() != null && enderman.getRNG().nextFloat() < 0.5F)
		{
			if(enderman.getAttackTarget().getDistance(enderman) <= 2F)
			{
				event.setCanceled(true);
				enderman.getAttackTarget().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
			}
		}
	}
}
