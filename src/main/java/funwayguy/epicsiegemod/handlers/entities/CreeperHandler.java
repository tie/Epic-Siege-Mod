package funwayguy.epicsiegemod.handlers.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.apache.logging.log4j.Level;
import funwayguy.epicsiegemod.capabilities.modified.CapabilityModifiedHandler;
import funwayguy.epicsiegemod.capabilities.modified.IModifiedHandler;
import funwayguy.epicsiegemod.client.ESMSounds;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;
import funwayguy.epicsiegemod.handlers.MainHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CreeperHandler
{
	private static Field f_isFlaming = null;
	private static Field f_explosionSize = null;
	private static Field f_POWERED = null;
	
	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void onSpawn(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote || !(event.getEntity() instanceof EntityCreeper))
		{
			return;
		}
		
		IModifiedHandler handler;
		
		if(event.getEntity().hasCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null))
		{
			handler = event.getEntity().getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
			
			if(handler.isModified())
			{
				return;
			} else
			{
				handler.setModified(true);
			}
		} else
		{
			return; // This handler needs to be present
		}
		
		EntityCreeper creeper = (EntityCreeper)event.getEntity();
		
		if(event.getWorld().rand.nextInt(100) < ESM_Settings.CreeperPoweredRarity)
		{
			try
			{
				creeper.getDataManager().set((DataParameter<Boolean>)f_POWERED.get(creeper), true);
			} catch(Exception e)
			{
				ESM.logger.log(Level.ERROR, "Unable to set creeper powered state", e);
			}
		}
	}
	
	@SubscribeEvent
	public void onExplode(ExplosionEvent.Start event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		EntityLivingBase entity = event.getExplosion().getExplosivePlacedBy();
		
		if(entity instanceof EntityCreeper && f_isFlaming != null)
		{
			if(ESM_Settings.CreeperNapalm)
			{
				try
				{
					f_isFlaming.set(event.getExplosion(), true);
				} catch(Exception e)
				{
					ESM.logger.log(Level.ERROR, "Failed to set creeper blast to flaming", e);
				}
			}
			
			if(ESM_Settings.CenaCreeper && entity.getCustomNameTag().equalsIgnoreCase("John Cena"))
			{
				try
				{
					f_explosionSize.set(event.getExplosion(), f_explosionSize.getFloat(event.getExplosion()) * 3F);
				} catch(Exception e)
				{
					ESM.logger.log(Level.ERROR, "John Cena misfired", e);
				}
				
				Vec3d vec = event.getExplosion().getPosition();
				event.getWorld().playSound(null, new BlockPos(vec), ESMSounds.sndCenaEnd, SoundCategory.HOSTILE, 1F, 1F);
			}
		}
	}
	
	static
	{
		try
		{
			f_isFlaming = Explosion.class.getDeclaredField("field_77286_a");
			f_explosionSize = Explosion.class.getDeclaredField("field_77280_f");
			f_POWERED = EntityCreeper.class.getDeclaredField("field_184714_b");
			MainHandler.f_modifiers.set(f_explosionSize, f_explosionSize.getModifiers() & ~Modifier.FINAL);
			f_isFlaming.setAccessible(true);
			f_explosionSize.setAccessible(true);
			f_POWERED.setAccessible(true);
		} catch(Exception e1)
		{
			try
			{
				f_isFlaming = Explosion.class.getDeclaredField("isFlaming");
				f_explosionSize = Explosion.class.getDeclaredField("explosionSize");
				f_POWERED = EntityCreeper.class.getDeclaredField("POWERED");
				MainHandler.f_modifiers.set(f_explosionSize, f_explosionSize.getModifiers() & ~Modifier.FINAL);
				f_isFlaming.setAccessible(true);
				f_explosionSize.setAccessible(true);
				f_POWERED.setAccessible(true);
			} catch(Exception e2)
			{
				ESM.logger.log(Level.ERROR, "Failed to set Creeper hooks", e2);
			}
		}
	}
}
