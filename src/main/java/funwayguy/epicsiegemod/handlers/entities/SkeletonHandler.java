package funwayguy.epicsiegemod.handlers.entities;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import funwayguy.epicsiegemod.capabilities.modified.CapabilityModifiedHandler;
import funwayguy.epicsiegemod.capabilities.modified.IModifiedHandler;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class SkeletonHandler
{
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}
		
		if(!(event.getEntity() instanceof EntitySkeleton || event.getEntity() instanceof EntityTippedArrow))
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
		
		if(event.getEntity() instanceof EntitySkeleton && event.getWorld().provider.getDimension() != -1)
		{
			EntitySkeleton skeleton = (EntitySkeleton)event.getEntity();
			
			if(skeleton.getRNG().nextInt(100) < ESM_Settings.WitherSkeletonRarity)
			{
				event.setCanceled(true);
				skeleton.setDead();
				
				EntityWitherSkeleton wSkel = new EntityWitherSkeleton(event.getWorld());
				wSkel.setPosition(skeleton.posX, skeleton.posY, skeleton.posZ);
				event.getWorld().spawnEntity(wSkel);
			}
		} else if(event.getEntity().getClass() == EntityTippedArrow.class)
		{
			EntityTippedArrow arrow = (EntityTippedArrow)event.getEntity();
			if(arrow.shootingEntity instanceof EntityLiving && !(arrow.shootingEntity instanceof EntityPlayer))
			{
				EntityLiving shooter = (EntityLiving)arrow.shootingEntity;
				EntityLivingBase target = shooter.getAttackTarget();
				
				if(target != null)
				{
					replaceArrowAttack(shooter, target, arrow.getDamage(), PotionUtils.getPotionTypeFromNBT(arrow.writeToNBT(new NBTTagCompound())));
					arrow.setDead();
					event.setCanceled(true);
					return;
				}
			}
		}
	}
	
	public static void replaceArrowAttack(EntityLiving shooter, EntityLivingBase targetEntity, double par2, PotionType potions)
	{
		EntityTippedArrow entityarrow = new EntityTippedArrow(shooter.world, shooter);
		ItemStack itemTip = new ItemStack(Items.TIPPED_ARROW);
		PotionUtils.addPotionToItemStack(itemTip, potions);
    	entityarrow.setPotionEffect(itemTip); // Preserve effects (could be modified here)
        double targetDist = shooter.getDistance(targetEntity.posX + (targetEntity.posX - targetEntity.lastTickPosX), targetEntity.getEntityBoundingBox().minY, targetEntity.posZ + (targetEntity.posZ - targetEntity.lastTickPosZ));
        float fireSpeed = (float)((0.00013*(targetDist)*(targetDist)) + (0.02*targetDist) + 1.25);
    	
        double d0 = (targetEntity.posX + (targetEntity.posX - targetEntity.lastTickPosX) * (targetDist/fireSpeed)) - shooter.posX;
        double d1 = targetEntity.getEntityBoundingBox().minY + (double)(targetEntity.height / 3.0F) - entityarrow.posY;
        double d2 = (targetEntity.posZ + (targetEntity.posZ - targetEntity.lastTickPosZ) * (targetDist/fireSpeed)) - shooter.posZ;
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
        
        if (d3 >= 1.0E-7D)
        {
            float f4 = (float)d3 * 0.2F;
        	entityarrow.setThrowableHeading(d0, d1 + (double)f4, d2, fireSpeed, ESM_Settings.SkeletonAccuracy);
        }
    	
        //EntityArrow entityarrow = new EntityArrow(shooter.worldObj, shooter, targetEntity, 1.6F, (float)(14 - shooter.worldObj.difficultySetting * 4));
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, shooter.getHeldItem(EnumHand.MAIN_HAND));
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, shooter.getHeldItem(EnumHand.MAIN_HAND));
        entityarrow.setDamage(par2);
        
        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }
        
        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }
        
        if (shooter.isBurning() || EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, shooter.getHeldItem(EnumHand.MAIN_HAND)) > 0 || (shooter instanceof EntitySkeleton && shooter instanceof EntityWitherSkeleton))
        {
            entityarrow.setFire(100);
        }
        
        shooter.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (shooter.getRNG().nextFloat() * 0.4F + 0.8F));
        
        IModifiedHandler modHandler = entityarrow.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if(modHandler != null)
        {
        	modHandler.setModified(true);
        }
        
        shooter.world.spawnEntity(entityarrow);
	}
}
