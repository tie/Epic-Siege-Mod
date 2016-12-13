package funwayguy.epicsiegemod.handlers.entities;

import funwayguy.epicsiegemod.core.ESM_Settings;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpiderHandler
{
	@SubscribeEvent
	public void onAttacked(LivingHurtEvent event)
	{
		if(event.getEntity().worldObj.isRemote)
		{
			return;
		}
		
		// Start pile of if statement checks
		if(event.getEntityLiving() instanceof EntityPlayer && event.getSource() != null)
		{
			if(event.getSource().getEntity() instanceof EntitySpider && event.getEntityLiving().getRNG().nextInt(100) < ESM_Settings.SpiderWebChance)
			{
				if(event.getEntityLiving().worldObj.getBlockState(event.getEntityLiving().getPosition()).getMaterial().isReplaceable())
				{
					event.getEntityLiving().worldObj.setBlockState(event.getEntityLiving().getPosition(), Blocks.WEB.getDefaultState());
				}
			}
		}
	}
}
