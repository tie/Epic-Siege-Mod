package funwayguy.epicsiegemod.handlers.entities;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class ZombieHandler
{
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		if(event.getEntity().world.isRemote)
		{
			return;
		}
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			if(event.getSource().getSourceOfDamage() instanceof EntityZombie && ESM_Settings.ZombieInfectious)
			{
				EntityZombie zombie = new EntityZombie(event.getEntity().world);
				zombie.setPosition(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
				zombie.setCanPickUpLoot(true);
				zombie.setCustomNameTag(event.getEntity().getName() + " (" + event.getSource().getSourceOfDamage().getName() + ")");
				zombie.getEntityData().setBoolean("ESM_MODIFIED", true);
				event.getEntity().world.spawnEntity(zombie);
			}
		}
	}
}
