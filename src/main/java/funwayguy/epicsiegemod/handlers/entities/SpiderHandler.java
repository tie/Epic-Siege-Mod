package funwayguy.epicsiegemod.handlers.entities;

import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class SpiderHandler
{
	@SubscribeEvent
	public void onAttacked(LivingHurtEvent event)
	{
		if(event.getEntity().world.isRemote)
		{
			return;
		}
		
		if(event.getEntityLiving() instanceof EntityPlayer && event.getSource() != null)
		{
			if(event.getSource().getEntity() instanceof EntitySpider && event.getEntityLiving().getRNG().nextInt(100) < ESM_Settings.SpiderWebChance)
			{
				if(event.getEntityLiving().world.getBlockState(event.getEntityLiving().getPosition()).getMaterial().isReplaceable())
				{
					event.getEntityLiving().world.setBlockState(event.getEntityLiving().getPosition(), Blocks.WEB.getDefaultState());
				}
			}
		}
	}
}
