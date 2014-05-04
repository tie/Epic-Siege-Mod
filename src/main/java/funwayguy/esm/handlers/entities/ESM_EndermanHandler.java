package funwayguy.esm.handlers.entities;

import java.lang.reflect.Field;
import java.util.UUID;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.core.ESM_Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ESM_EndermanHandler
{
    public static final AttributeModifier speedBoostMod = (new AttributeModifier(UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0"), "Attacking speed boost", 6.199999809265137D, 0)).setSaved(false);
    public static final AttributeModifier speedHaltMod = (new AttributeModifier(UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A2"), "Staring speed halt", -1.0D, 2)).setSaved(false);
	
    public static void onEntityJoinWorld(EntityEnderman enderman)
	{
	}
	
	public static void onLivingUpdate(EntityEnderman enderman)
	{
		if(enderman.getEntityToAttack() != null && enderman.getEntityToAttack() instanceof EntityLivingBase)
		{
			EntityLivingBase target = (EntityLivingBase)enderman.getEntityToAttack();
			
			if(ESM_Settings.EndermanMode.equalsIgnoreCase("Slender") && enderman.getEntityData().getBoolean("ESM_LOOKED_AWAY"))
			{
				target.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
				target.addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 0));
				target.addPotionEffect(new PotionEffect(Potion.hunger.id, 100, 0));
				
	            AttributeInstance attributeinstance = enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
	            attributeinstance.removeModifier(speedBoostMod);
	            attributeinstance.removeModifier(speedHaltMod);
	            
	            if(shouldAttackTarget(enderman, target) && enderman.getDistanceSqToEntity(target) < 8.0D)
	            {
	            	if(enderman.getRNG().nextInt(20) == 0 && ESM_Settings.EndermanPlayerTele)
	            	{
	            		teleportTargetRandomly(target);
	            	} else
	            	{
	            		teleportTargetRandomly(enderman);
	            	}
					enderman.worldObj.playSoundAtEntity(enderman, "ambient.cave.cave", 1.0F, 0.5F);
	            } else if(enderman.getRNG().nextInt(100) == 0)
				{
					enderman.worldObj.playSoundAtEntity(enderman, "ambient.cave.cave", 1.0F, 0.5F);
				}
			} if(ESM_Settings.EndermanMode.equalsIgnoreCase("Slender"))
			{
				if(!shouldAttackTarget(enderman, target))
				{
					enderman.getEntityData().setBoolean("ESM_LOOKED_AWAY", true);
				} else
				{
		            AttributeInstance attributeinstance = enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		            attributeinstance.removeModifier(speedHaltMod);
		            attributeinstance.applyModifier(speedHaltMod);
				}
			}
		} else
		{
			enderman.getEntityData().setBoolean("ESM_LOOKED_AWAY", false);
            AttributeInstance attributeinstance = enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            attributeinstance.removeModifier(speedHaltMod);
            
			EntityLivingBase target = getValidTarget(enderman);
			
			if(target != null)
			{
				enderman.setTarget(target);
			}
		}
	}
	
	public static EntityLivingBase getValidTarget(EntityEnderman enderman)
	{
        EntityLivingBase target = ESM_Utils.GetNearestValidTarget(enderman);//enderman.worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);

        if (target != null)
        {
            if(shouldAttackTarget(enderman, target))
            {
                setAggressive(enderman, true);
                
                int stare = getStareTimer(enderman);
                if (stare == 0)
                {
                    enderman.worldObj.playSoundAtEntity(target, "mob.endermen.stare", 1.0F, 1.0F);
                }
                
                setStareTimer(enderman, stare + 1);
                if (stare++ == 5)
                {
                    setStareTimer(enderman, 0);
                    enderman.setScreaming(true);
                    return target;
                }
            }
            else
            {
                setStareTimer(enderman, 0);
            }
        }

        return null;
	}
	
	public static boolean shouldAttackTarget(EntityEnderman enderman, EntityLivingBase target)
	{
        ItemStack itemstack = target.getCurrentItemOrArmor(4);

        if (itemstack != null && itemstack.itemID == Block.pumpkin.blockID)
        {
            return false;
        }
        else
        {
            Vec3 vec3 = target.getLook(1.0F).normalize();
            Vec3 vec31 = enderman.worldObj.getWorldVec3Pool().getVecFromPool(enderman.posX - target.posX, enderman.boundingBox.minY + (double)(enderman.height / 2.0F) - (target.posY + (double)target.getEyeHeight()), enderman.posZ - target.posZ);
            double d0 = vec31.lengthVector();
            vec31 = vec31.normalize();
            double d1 = vec3.dotProduct(vec31);
            if (d1 > 0.75D - 0.025D / d0 && ESM_Settings.EndermanMode.equalsIgnoreCase("Slender"))
            {
                return enderman.canEntityBeSeen(target);
            }
            else if(d1 > 1.0D - 0.025D / d0)
            {
                return enderman.canEntityBeSeen(target);
            } else
            {
                return false;
            }
        }
	}
	
	public static void setAggressive(EntityEnderman enderman, boolean state)
	{
		Field field = null;
		try
		{
			field = EntityEnderman.class.getDeclaredField("isAggressive");
		} catch(NoSuchFieldException | SecurityException e)
		{
			try
			{
				field = EntityEnderman.class.getDeclaredField("field_104003_g");
			} catch(NoSuchFieldException | SecurityException e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		field.setAccessible(true);
		
		try
		{
			field.setBoolean(enderman, state);
		} catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			return;
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return;
		}
		
		return;
	}
	
	public static int getStareTimer(EntityEnderman enderman)
	{
		return enderman.getEntityData().getInteger("ESM_STARETIMER");
	}
	
	public static void setStareTimer(EntityEnderman enderman, int stare)
	{
		enderman.getEntityData().setInteger("ESM_STARETIMER", stare);
	}

    /**
     * Teleport the enderman's current target to a random nearby position
     */
    public static boolean teleportTargetRandomly(EntityLivingBase target)
    {
        double d = target.posX + (target.getRNG().nextDouble() - 0.5D) * 64D;
        double d1 = target.posY + (double)(target.getRNG().nextInt(64) - 32);
        double d2 = target.posZ + (target.getRNG().nextDouble() - 0.5D) * 64D;
        return teleportTargetTo(target, d, d1, d2);
    }

    /**
     * Teleport the target
     */
    public static boolean teleportTargetTo(EntityLivingBase target, double par1, double par3, double par5)
    {
        double d = target.posX;
        double d1 = target.posY;
        double d2 = target.posZ;
        target.posX = par1;
        target.posY = par3;
        target.posZ = par5;
        boolean flag = false;
        int i = MathHelper.floor_double(target.posX);
        int j = MathHelper.floor_double(target.posY);
        int k = MathHelper.floor_double(target.posZ);

        if (target.worldObj.blockExists(i, j, k))
        {
            boolean flag1;

            for (flag1 = false; !flag1 && j > 0;)
            {
                int i1 = target.worldObj.getBlockId(i, j - 1, k);

                if (i1 != 0 && Block.blocksList[i1].blockMaterial.blocksMovement())
                {
                	flag1 = true;
                }
                else
                {
                    --target.posY;
                    --j;
                }
            }

            if (flag1)
            {
            	target.setPosition(target.posX, target.posY, target.posZ);

                if (target.worldObj.getCollidingBoundingBoxes(target, target.boundingBox).size() == 0 && !target.worldObj.isAnyLiquid(target.boundingBox))
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
        	target.setPosition(d, d1, d2);
            return false;
        }

        int l = 128;

        for (int j1 = 0; j1 < l; j1++)
        {
            double d3 = (double)j1 / ((double)l - 1.0D);
            float f = (target.getRNG().nextFloat() - 0.5F) * 0.2F;
            float f1 = (target.getRNG().nextFloat() - 0.5F) * 0.2F;
            float f2 = (target.getRNG().nextFloat() - 0.5F) * 0.2F;
            double d4 = d + (target.posX - d) * d3 + (target.getRNG().nextDouble() - 0.5D) * (double)target.width * 2D;
            double d5 = d1 + (target.posY - d1) * d3 + target.getRNG().nextDouble() * (double)target.height;
            double d6 = d2 + (target.posZ - d2) * d3 + (target.getRNG().nextDouble() - 0.5D) * (double)target.width * 2D;
            target.worldObj.spawnParticle("portal", d4, d5, d6, f, f1, f2);
        }

        target.worldObj.playSoundEffect(d, d1, d2, "mob.endermen.portal", 1.0F, 1.0F);
        target.worldObj.playSoundAtEntity(target, "mob.endermen.portal", 1.0F, 1.0F);
        return true;
    }
}
