package funwayguy.esm.handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import com.google.common.base.Stopwatch;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import funwayguy.esm.ai.GenericEntitySelector;
import funwayguy.esm.client.gui.ESMGuiConfig;
import funwayguy.esm.core.DimSettings;
import funwayguy.esm.core.ESM;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.core.ESM_Utils;
import funwayguy.esm.entities.EntityESMGhast;
import funwayguy.esm.handlers.entities.ESM_BlazeHandler;
import funwayguy.esm.handlers.entities.ESM_CreeperHandler;
import funwayguy.esm.handlers.entities.ESM_EndermanHandler;
import funwayguy.esm.handlers.entities.ESM_SkeletonHandler;
import funwayguy.esm.handlers.entities.ESM_ZombieHandler;

public class ESM_EventManager
{	
	static float curBossMod = 0F;
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.world.isRemote || event.entity instanceof EntityPlayer)
		{
			return;
		}
		
		if(ESM_Settings.Apocalypse && (event.entity instanceof IMob || event.entity instanceof EntityMob) && !(event.entity instanceof EntityZombie || event.entity instanceof EntityPlayer || (event.entity instanceof EntityEnderman && ESM_Settings.EndermanSlender)))
		{
			event.entity.setDead();
			event.setCanceled(true);
			return;
		}
		
		if(event.entity instanceof EntityLiving && (event.entity instanceof IMob || ESM_Settings.ambiguous_AI) && !ESM_Settings.AIExempt.contains(EntityList.getEntityString(event.entity)))
		{
			ESM_Utils.replaceAI((EntityLiving)event.entity, true);
			if(event.entity instanceof EntityMob || (event.entity instanceof EntitySpider && !event.world.isDaytime()))
			{
				searchForTarget((EntityCreature)event.entity);
			}
		}
		
		if(event.entity.getEntityData().getBoolean("ESM_MODIFIED") || ESM_Settings.AIExempt.contains(EntityList.getEntityString(event.entity)))
		{
			event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
			return;
		}
		
		if(event.entity instanceof EntityLivingBase && (event.entity instanceof IMob || ESM_Settings.ambiguous_AI) && !ESM_Settings.AIExempt.contains(EntityList.getEntityString(event.entity)))
		{
			EntityLivingBase entityLiving = (EntityLivingBase)event.entity;
			DimSettings dimSet = ESM_Settings.dimSettings.get(event.world.provider.dimensionId);
			
			if(dimSet == null && curBossMod > 0F)
			{
				dimSet = new DimSettings(1F, 1F, 1F, 1F);
			}
			
			if(dimSet != null)
			{
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("ESM_TWEAK_1", dimSet.hpMult + curBossMod, 1));
					entityLiving.setHealth(entityLiving.getMaxHealth());
				}
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.movementSpeed) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(new AttributeModifier("ESM_TWEAK_2", dimSet.spdMult + curBossMod, 1));
				}
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.attackDamage) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(new AttributeModifier("ESM_TWEAK_3", dimSet.dmgMult + curBossMod, 1));
				}
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("ESM_TWEAK_4", dimSet.dmgMult + curBossMod, 1));
				}
			}
		}
		
		if(event.entity.getClass() == EntityGhast.class)
		{
			event.setCanceled(true);
			EntityESMGhast newGhast = new EntityESMGhast(event.world);
			newGhast.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
			NBTTagCompound oldTags = new NBTTagCompound();
			event.entity.writeToNBT(oldTags);
			newGhast.readFromNBT(oldTags);
			event.world.spawnEntityInWorld(newGhast);
			newGhast.riddenByEntity = event.entity.riddenByEntity;
			if(newGhast.riddenByEntity != null)
			{
				newGhast.riddenByEntity.mountEntity(newGhast);
			}
			event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
			event.entity.setDead();
			event.setCanceled(true);
			return;
		} else if(event.entity instanceof EntityCreeper)
		{
			ESM_CreeperHandler.onEntityJoinWorld((EntityCreeper)event.entity);
		} else if(event.entity instanceof EntitySpider)
		{
		} else if(event.entity instanceof EntitySkeleton)
		{
			ESM_SkeletonHandler.onEntityJoinWorld((EntitySkeleton)event.entity);
		} else if(event.entity instanceof EntityZombie)
		{
			if(ESM_Settings.ZombieDiggers && event.world.rand.nextFloat() < 0.1F)
			{
				((EntityZombie)event.entity).setCanPickUpLoot(true);
				
				if(event.world.rand.nextFloat() < 0.1F)
				{
					((EntityZombie)event.entity).setCurrentItemOrArmor(0, new ItemStack(Items.diamond_pickaxe));
				} else
				{
					((EntityZombie)event.entity).setCurrentItemOrArmor(0, new ItemStack(Items.iron_pickaxe));
				}
			} else if(ESM_Settings.DemolitionZombies && event.world.rand.nextFloat() < 0.1F)
			{
				((EntityZombie)event.entity).setCurrentItemOrArmor(0, new ItemStack(Blocks.tnt));
			}
		} else if(event.entity.getClass() == EntityArrow.class) // Changed because other people like replacing arrows and not saying they did
		{
			EntityArrow arrow = (EntityArrow)event.entity;
			if(arrow.shootingEntity instanceof EntityLiving && arrow.shootingEntity instanceof IMob)
			{
				EntityLiving shooter = (EntityLiving)arrow.shootingEntity;
				EntityLivingBase target = shooter.getAttackTarget();
				
				if(target != null)
				{
					replaceArrowAttack(shooter, target, arrow.getDamage());
					event.setCanceled(true);
					event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
					event.entity.setDead();
					event.setCanceled(true);
					return;
				}
			}
		} else if(event.entity instanceof EntityPotion)
		{
			EntityPotion potion = (EntityPotion)event.entity;
			
			PotionEffect effect = null;
			
			if(ESM_Settings.customPotions.length > 0)
			{
				String[] type = ESM_Settings.customPotions[event.world.rand.nextInt(ESM_Settings.customPotions.length)].split(":");
				
				if(type.length == 3)
				{
					try
					{
						effect = new PotionEffect(Integer.parseInt(type[0]), Integer.parseInt(type[1]), Integer.parseInt(type[2]));
					} catch(Exception e)
					{
						effect = null;
					}
				}
			}
			
			if(potion.getThrower() instanceof EntityWitch && effect != null)
			{
				NBTTagList nbtList = new NBTTagList();
				nbtList.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
				
				ItemStack effectStack = new ItemStack(Items.potionitem);
				NBTTagCompound itemTags = new NBTTagCompound();
				itemTags.setTag("CustomPotionEffects", nbtList);
				effectStack.setTagCompound(itemTags);
				
				ObfuscationReflectionHelper.setPrivateValue(EntityPotion.class, potion, effectStack, "field_70197_d", "potionDamage");
			}
		} else if(event.entity instanceof EntityBlaze)
		{
			ESM_BlazeHandler.onEntityJoinWorld((EntityBlaze)event.entity);
		} else if(event.entity instanceof EntitySmallFireball)
		{
			EntitySmallFireball fireball = (EntitySmallFireball)event.entity;
			if(fireball.shootingEntity instanceof EntityBlaze)
			{
				fireball.shootingEntity.getEntityData().setInteger("ESM_FIREBALLS", fireball.shootingEntity.getEntityData().getInteger("ESM_FIREBALLS") + 1);
			}
		} else if(event.entity instanceof EntityEnderman)
		{
			ESM_EndermanHandler.onEntityJoinWorld((EntityEnderman)event.entity);
		}
		
		if((ESM_Settings.MobBombAll || (ESM_Settings.MobBombs != null && ESM_Settings.MobBombs.contains(EntityList.getEntityString(event.entity)) != ESM_Settings.MobBombInvert)) && event.entity.riddenByEntity == null && event.entity instanceof IMob && !event.isCanceled() && !event.entity.isDead && event.world.loadedEntityList.size() < 512)
		{
			event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
			
			if(ESM_Settings.MobBombRarity <= 0 || event.world.rand.nextInt(ESM_Settings.MobBombRarity) == 0)
			{
				Entity passenger;
				
				if(!ESM_Settings.CrystalBombs)
				{
					passenger = new EntityCreeper(event.entity.worldObj);
				} else
				{
					passenger = new EntityEnderCrystal(event.entity.worldObj);
				}
				
				passenger.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
				
				if(passenger instanceof EntityLiving)
				{
					((EntityLiving)passenger).onSpawnWithEgg((IEntityLivingData)null);
				}
				
				event.entity.worldObj.spawnEntityInWorld(passenger);
				passenger.mountEntity(event.entity);
			}
		}
		
		if(event.entity instanceof IMob && !(event.entity instanceof IBossDisplayData) && event.entity instanceof EntityLivingBase && ESM_Settings.PotionMobs > event.world.rand.nextInt(100) && ESM_Settings.PotionMobEffects != null && ESM_Settings.PotionMobEffects.length > 0)
		{
			int id = ESM_Settings.PotionMobEffects[event.world.rand.nextInt(ESM_Settings.PotionMobEffects.length)];
			if(Potion.potionTypes[id] != null)
			{
				((EntityLivingBase)event.entity).addPotionEffect(new PotionEffect(id, 999999));
			}
		}
		
		event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
	}
	
	@SubscribeEvent
	public void onExplode(ExplosionEvent.Start event)
	{
		EntityLivingBase source = event.explosion.getExplosivePlacedBy();
		
		if(source instanceof EntityCreeper)
		{
			if(ESM_Settings.CreeperNapalm)
			{
				event.explosion.isFlaming = true;
			}
			
			if(((EntityCreeper)source).getCustomNameTag().equals("John Cena"))
			{
				event.explosion.explosionSize *= 3F;
				event.world.playSoundAtEntity(source, "esm:cena_creeper.end", 1.0F, 1.0F);
			}
		}
	}
	
	public static void replaceArrowAttack(EntityLiving shooter, EntityLivingBase targetEntity, double par2)
	{
    	EntityArrow entityarrow;
        double targetDist = shooter.getDistance(targetEntity.posX + (targetEntity.posX - targetEntity.lastTickPosX), targetEntity.boundingBox.minY, targetEntity.posZ + (targetEntity.posZ - targetEntity.lastTickPosZ));
        float fireSpeed = (float)((0.00013*(targetDist)*(targetDist)) + (0.02*targetDist) + 1.25);
    	
    	if(ESM_Settings.SkeletonDistance <= 0)
    	{
    		entityarrow = new EntityArrow(shooter.worldObj, shooter, targetEntity, 1.6F, ESM_Settings.SkeletonAccuracy);
    	} else
    	{
    		entityarrow = new EntityArrow(shooter.worldObj, shooter, targetEntity, fireSpeed, ESM_Settings.SkeletonAccuracy);
    	}
    	
        double d0 = (targetEntity.posX + (targetEntity.posX - targetEntity.lastTickPosX) * (targetDist/fireSpeed)) - shooter.posX;
        double d1 = targetEntity.boundingBox.minY + (double)(targetEntity.height / 3.0F) - entityarrow.posY;
        double d2 = (targetEntity.posZ + (targetEntity.posZ - targetEntity.lastTickPosZ) * (targetDist/fireSpeed)) - shooter.posZ;
        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        
        if (d3 >= 1.0E-7D)
        {
            float f4 = (float)d3 * 0.2F;
        	entityarrow.setThrowableHeading(d0, d1 + (double)f4, d2, fireSpeed, ESM_Settings.SkeletonAccuracy);
        }
    	
        //EntityArrow entityarrow = new EntityArrow(shooter.worldObj, shooter, targetEntity, 1.6F, (float)(14 - shooter.worldObj.difficultySetting * 4));
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, shooter.getHeldItem());
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, shooter.getHeldItem());
        entityarrow.setDamage(par2);

        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, shooter.getHeldItem()) > 0 || (shooter instanceof EntitySkeleton && ((EntitySkeleton)shooter).getSkeletonType() == 1))
        {
            entityarrow.setFire(100);
        }

        shooter.playSound("random.bow", 1.0F, 1.0F / (shooter.getRNG().nextFloat() * 0.4F + 0.8F));
        entityarrow.getEntityData().setBoolean("ESM_MODIFIED", true);
        shooter.worldObj.spawnEntityInWorld(entityarrow);
	}
	
	@SubscribeEvent
	public void onEntityAttacked(LivingHurtEvent event)
	{
		if(event.entity.worldObj.isRemote)
		{
			return;
		}
		
		if(!ESM_Settings.friendlyFire && event.source != null && event.source.getEntity() != null && event.entityLiving instanceof IMob && (ESM_Settings.Chaos? event.entityLiving.getClass() == event.source.getEntity().getClass() : event.source.getEntity() instanceof IMob))
		{
			event.setCanceled(true);
			return;
		}
		
		if(!(event.entityLiving instanceof EntityPlayer) && event.entityLiving.ridingEntity != null && event.source == DamageSource.inWall)
		{
			event.entityLiving.dismountEntity(event.entityLiving.ridingEntity);
			event.entityLiving.ridingEntity.riddenByEntity = null;
			event.entityLiving.ridingEntity = null;
		}
		
		if(event.entityLiving instanceof EntityPlayer && event.source.getEntity() instanceof IMob)
		{
			int day = (int)(event.entityLiving.worldObj.getWorldTime()/24000);
			
			if(ESM_Settings.hardDay != 0 && day != 0 && day%ESM_Settings.hardDay == 0)
			{
				event.ammount *= 2F;
			}
		}
		
		if(event.source != null && event.source.getEntity() instanceof EntitySpider && ESM_Settings.SpiderWebChance > event.entityLiving.getRNG().nextInt(100))
		{
			int i = MathHelper.floor_double(event.entityLiving.posX);
			int j = MathHelper.floor_double(event.entityLiving.posY);
			int k = MathHelper.floor_double(event.entityLiving.posZ);
			event.entityLiving.worldObj.setBlock(i, j, k, Blocks.web);
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		if(event.entity.worldObj.isRemote)
		{
			return;
		}
		
		ESM_PathCapHandler.RemoveTarget(event.entityLiving);
		
		if(event.entityLiving instanceof EntityLiving)
		{
			EntityLivingBase target = ((EntityLiving)event.entityLiving).getAttackTarget();
			
			if(target != null)
			{
				ESM_PathCapHandler.UpdateAttackers(target);
			}
		}
		
		if(event.entity instanceof EntityPlayer)
		{
			if(event.source.getSourceOfDamage() instanceof EntityZombie && ESM_Settings.ZombieInfectious)
			{
				EntityZombie zombie = new EntityZombie(event.entity.worldObj);
				zombie.setPosition(event.entity.posX, event.entity.posY, event.entity.posZ);
				zombie.setCanPickUpLoot(true);
				zombie.setCustomNameTag(event.entity.getCommandSenderName() + " (" + StatCollector.translateToLocal("entity.Zombie.name") + ")");
				zombie.getEntityData().setBoolean("ESM_MODIFIED", true);
				event.entity.worldObj.spawnEntityInWorld(zombie);
			}
		}
		
		if(event.entityLiving instanceof IBossDisplayData)
		{
			curBossMod += ESM_Settings.bossModifier;
		}
	}
	
	static Method methodAI;
	
	public static boolean usesAI(EntityLivingBase entityLiving)
	{
		if(methodAI == null)
		{
			try
			{
				methodAI = EntityLivingBase.class.getMethod("func_70650_aV");
			} catch(Exception e1)
			{
				try
				{
					methodAI = EntityLivingBase.class.getMethod("isAIEnabled");
				} catch(Exception e2)
				{
					return false;
				}
			}
		}
		
		try
		{
			return (Boolean)methodAI.invoke(entityLiving);
		} catch(Exception e)
		{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void searchForTarget(EntityCreature entity)
	{
		if(ESM_Settings.neutralMobs || usesAI(entity) || (entity instanceof EntityEnderman) || (entity instanceof EntityTameable && ((EntityTameable)entity).isTamed()))
		{
			entity.getEntityData().setInteger("ESM_TARGET_COOLDOWN", 0);
			return;
		}
		
		int cooldown = entity.getEntityData().getInteger("ESM_TARGET_COOLDOWN");
		
		if(cooldown > 0)
		{
			entity.getEntityData().setInteger("ESM_TARGET_COOLDOWN", cooldown - 1);
			return;
		} else
		{
			entity.getEntityData().setInteger("ESM_TARGET_COOLDOWN", 60);
		}
		
		EntityLivingBase target = entity.getAITarget();
		target = target != null && target.isEntityAlive()? target : entity.getAttackTarget();
		target = target != null && target.isEntityAlive()? target : (entity.getEntityToAttack() instanceof EntityLivingBase? (EntityLivingBase)entity.getEntityToAttack() : target);
		
		if(target != null && target.isEntityAlive())
		{
			if(ESM_Settings.TargetCap >= 0 && ESM_PathCapHandler.attackMap.get(target) != null && ESM_PathCapHandler.attackMap.get(target).size() > ESM_Settings.TargetCap && !ESM_Utils.isCloserThanOtherAttackers(entity.worldObj, entity, target))
			{
				entity.getNavigator().clearPathEntity();
				entity.setAttackTarget(null);
				entity.setTarget(null);
				entity.setRevengeTarget(null);
				return;
			}
			
			if(entity.getDistanceToEntity(target) > ESM_Settings.Awareness)
			{
				entity.getNavigator().clearPathEntity();
				entity.setAttackTarget(null);
				entity.setTarget(null);
				entity.setRevengeTarget(null);
				return;
			}
			
			if(!entity.hasPath() && ESM_Settings.forcePath)
			{
				// This entity may be auto-invalidating its current target/path
				entity.setPathToEntity(entity.worldObj.getPathEntityToEntity(entity, target, ESM_Settings.Awareness, true, false, false, true));
				entity.getEntityData().setInteger("ESM_TARGET_COOLDOWN", 10);
			}
			
			// In case the target hasn't been applied to both variables yet we just re-set both of them.
			entity.setTarget(target);
			entity.setAttackTarget(target);
			entity.setRevengeTarget(target);
			return;
		}
		
		EntityLivingBase closestTarget = null;
		ArrayList<EntityLiving> targets = new ArrayList<EntityLiving>();
		
		targets.addAll(entity.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, entity.boundingBox.expand(ESM_Settings.Awareness, ESM_Settings.Awareness, ESM_Settings.Awareness), new GenericEntitySelector(entity)));
		Collections.sort(targets, new EntityAINearestAttackableTarget.Sorter(entity));
		
		for(int i = 0; i < targets.size(); i++)
		{
			EntityLivingBase subject = targets.get(i);
			
			if(ESM_Settings.TargetCap < 0 || ESM_PathCapHandler.attackMap.get(subject) == null || ESM_PathCapHandler.attackMap.get(subject).size() < ESM_Settings.TargetCap || ESM_Utils.isCloserThanOtherAttackers(entity.worldObj, entity, subject))
			{
				closestTarget = subject;
				break; // List is sorted, no need to continue looping through everything
			}
		}
		
		entity.setTarget(closestTarget);
		entity.setAttackTarget(closestTarget);
		entity.setRevengeTarget(closestTarget);
		
		if(closestTarget != null)
		{
			entity.setPathToEntity(entity.worldObj.getPathEntityToEntity(entity, closestTarget, ESM_Settings.Awareness, true, false, false, true));
			ESM_PathCapHandler.AddNewAttack(entity, closestTarget);
		}
	}
	
	Stopwatch timer = Stopwatch.createUnstarted();
	int ticks = 0;
	float TPS = 0;
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event)
	{
		if(event.phase == TickEvent.Phase.END)
		{
			if(!timer.isRunning())
			{
				timer.start();
			}
			
			ticks++;
			
			if(ticks >= 100)
			{
				timer.stop();
				
				TPS = (float)ticks/timer.elapsed(TimeUnit.MILLISECONDS)*1000F;
				
				// Debugging TPS counter
				//ESM.log.log(Level.INFO, "TPms: " + TPS + " (" + ticks + "/" + (timer.elapsed(TimeUnit.MILLISECONDS)/1000F) + ")");
				ticks = 0;
				
				timer.reset();
				timer.start();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.entity.worldObj.isRemote || event.entityLiving instanceof EntityPlayer)
		{
			return;
		}
		
		if(ESM_Settings.Apocalypse && !(event.entityLiving instanceof EntityPlayer || event.entityLiving instanceof EntityZombie || (event.entityLiving instanceof EntityEnderman && ESM_Settings.EndermanSlender)))
		{
			event.entityLiving.setDead();
			return;
		}
		
		if(ESM_Settings.AIExempt.contains(EntityList.getEntityString(event.entity)))
		{
			return;
		}
		
		if(event.entityLiving instanceof EntityLiving && event.entityLiving.ticksExisted == 1)
		{
			ESM_Utils.replaceAI((EntityLiving)event.entity);
		}
		
		if(event.entityLiving instanceof EntityLiving && ((EntityLiving)event.entityLiving).getAttackTarget() != null)
		{
			ESM_PathCapHandler.AddNewAttack(event.entityLiving, ((EntityLiving)event.entityLiving).getAttackTarget());
		} else if(event.entityLiving.getAITarget() != null)
		{
			ESM_PathCapHandler.AddNewAttack(event.entityLiving, event.entityLiving.getAITarget());
		} else if(event.entityLiving instanceof EntityCreature && ((EntityCreature)event.entityLiving).getEntityToAttack() != null && ((EntityCreature)event.entityLiving).getEntityToAttack() instanceof EntityLivingBase)
		{
			ESM_PathCapHandler.AddNewAttack(event.entityLiving, (EntityLivingBase)((EntityCreature)event.entityLiving).getEntityToAttack());
		}
		
		if(ESM_Settings.Awareness != 16 && event.entityLiving instanceof IMob && event.entityLiving instanceof EntityCreature && !(event.entityLiving instanceof EntitySpider && event.entityLiving.worldObj.isDaytime()))
		{
			searchForTarget((EntityCreature)event.entityLiving);
		}
		

		boolean hard = ESM_Settings.moreSpawning;
		int day = (int)(event.entityLiving.worldObj.getWorldTime()/24000);
		
		if(!hard && ESM_Settings.hardDay != 0 && day != 0 && day%ESM_Settings.hardDay == 0)
		{
			hard = true;
		}
		
		if(event.entityLiving instanceof EntityZombie)
		{
			ESM_ZombieHandler.onLivingUpdate((EntityZombie)event.entityLiving);
		} else if(event.entityLiving instanceof EntityCreeper)
		{
			ESM_CreeperHandler.onLivingUpdate((EntityCreeper)event.entityLiving);
		} else if(event.entityLiving instanceof EntitySkeleton)
		{
			ESM_SkeletonHandler.onLivingUpdate((EntitySkeleton)event.entityLiving);
		} else if(event.entityLiving instanceof EntityBlaze)
		{
			ESM_BlazeHandler.onLivingUpdate((EntityBlaze)event.entityLiving);
		} else if(event.entityLiving instanceof EntityEnderman)
		{
			ESM_EndermanHandler.onLivingUpdate((EntityEnderman)event.entityLiving);
		} else if(hard && event.entityLiving.getRNG().nextInt(10) == 0 && event.entityLiving.worldObj.difficultySetting != EnumDifficulty.PEACEFUL && event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("doMobSpawning") && event.entityLiving instanceof EntityPlayer && event.entityLiving.worldObj instanceof WorldServer && event.entityLiving.worldObj.loadedEntityList.size() < 512)
		{
			int x = MathHelper.floor_double(event.entityLiving.posX) + event.entityLiving.getRNG().nextInt(48) - 24;
			int y = MathHelper.floor_double(event.entityLiving.posY) + event.entityLiving.getRNG().nextInt(48) - 24;
			int z = MathHelper.floor_double(event.entityLiving.posZ) + event.entityLiving.getRNG().nextInt(48) - 24;
			
			if(event.entityLiving.worldObj.getClosestPlayer(x, y, z, 8D) == null && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, event.entityLiving.worldObj, x, y, z))
			{
                SpawnListEntry spawnlistentry = ((WorldServer)event.entityLiving.worldObj).spawnRandomCreature(EnumCreatureType.monster, x, y, z);
                
                if(spawnlistentry != null)
                {
	                try
	                {
	                	EntityLiving entityliving = (EntityLiving)spawnlistentry.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {event.entityLiving.worldObj});
	
	                    entityliving.setLocationAndAngles((double)x, (double)y, (double)z, event.entityLiving.getRNG().nextFloat() * 360.0F, 0.0F);
	
	                    Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, event.entityLiving.worldObj, x, y, z);
	                    if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere()))
	                    {
	                    	event.entityLiving.worldObj.spawnEntityInWorld(entityliving);
	                    }
	                }
	                catch (Exception exception)
	                {
	                    exception.printStackTrace();
	                }
                }
			}
		}
		
		return;
	}
	
	@SubscribeEvent
	public void onEnderTeleport(EnderTeleportEvent event)
	{
		AxisAlignedBB bounds = event.entityLiving.getCollisionBox(event.entityLiving);
		bounds = bounds != null? bounds : event.entityLiving.getBoundingBox();
		
		if(bounds != null && !event.entityLiving.worldObj.getEntitiesWithinAABB(EntityPlayer.class, bounds.expand(5D, 5D, 5D)).isEmpty())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event)
	{
		if(ESM_Settings.ResistanceCoolDown > 0)
		{
			event.player.addPotionEffect(new PotionEffect(Potion.resistance.id, ESM_Settings.ResistanceCoolDown, 5));
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerRespawnEvent event)
	{
		if(ESM_Settings.ResistanceCoolDown > 0)
		{
			event.player.addPotionEffect(new PotionEffect(Potion.resistance.id, ESM_Settings.ResistanceCoolDown, 5));
		}
	}

	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		if(ESM_Settings.AllowSleep || event.entityPlayer.worldObj.isRemote)
		{
			return;
		}
		
		if (!event.entityPlayer.worldObj.isRemote)
        {
            if (event.entityPlayer.isPlayerSleeping() || !event.entityPlayer.isEntityAlive())
            {
                return;
            }
            
            if (!event.entityPlayer.worldObj.provider.canRespawnHere())
            {
                return;
            }
            
            if (event.entityPlayer.worldObj.isDaytime())
            {
                return;
            }
            
            if (Math.abs(event.entityPlayer.posX - (double)event.x) > 3.0D || Math.abs(event.entityPlayer.posY - (double)event.y) > 2.0D || Math.abs(event.entityPlayer.posZ - (double)event.z) > 3.0D)
            {
                return;
            }
            double d0 = 8.0D;
            double d1 = 5.0D;
            List<?> list = event.entityPlayer.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox((double)event.x - d0, (double)event.y - d1, (double)event.z - d0, (double)event.x + d0, (double)event.y + d1, (double)event.z + d0));
            
	        if (!list.isEmpty())
            {
                return;
            }
        }
	    
	    event.result = EnumStatus.OTHER_PROBLEM;
		
	    if (event.entityPlayer.isRiding())
	    {
	        event.entityPlayer.mountEntity((Entity)null);
	    }
	    
		event.entityPlayer.setSpawnChunk(new ChunkCoordinates(event.x,event.y,event.z), false);
		event.entityPlayer.addChatMessage(new ChatComponentText("Spawnpoint set"));
	}
	
	@SubscribeEvent
	public void onWorldLoad(Load event)
	{
		if(!event.world.isRemote && (ESM_Settings.currentWorlds == null || ESM_Settings.worldDir == null))
		{
			MinecraftServer server = MinecraftServer.getServer();
			
			if(server.isServerRunning())
			{
				ESM_Settings.currentWorlds = server.worldServers;
				if(ESM.proxy.isClient())
				{
					ESM_Settings.worldDir = server.getFile("saves/" + server.getFolderName());
				} else
				{
					ESM_Settings.worldDir = server.getFile(server.getFolderName());
				}
				ESM_Settings.LoadWorldConfig();
				try
				{
					NBTTagCompound wmTag = CompressedStreamTools.read(new File(ESM_Settings.worldDir, "ESM.dat"));
					if(wmTag != null)
					{
						curBossMod = wmTag.getFloat("WitherModifier");
					} else
					{
						curBossMod = 0F;
					}
				} catch(IOException e)
				{
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(Unload event)
	{
		if(!event.world.isRemote)
		{
			MinecraftServer mc = MinecraftServer.getServer();
			
			if(!mc.isServerRunning())
			{
				ESM_PathCapHandler.attackMap.clear();
				ESM_Settings.currentWorlds = null;
				ESM_Settings.worldDir = null;
				curBossMod = 0F;
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(Save event)
	{
		try
		{
			NBTTagCompound wmTag = new NBTTagCompound();
			wmTag.setFloat("BossModifier", curBossMod);
			CompressedStreamTools.write(wmTag, new File(ESM_Settings.worldDir, "ESM.dat"));
		} catch(Exception e)
		{
		}
		/*if(ESM_EntityAIBrainController.brain != null)
		{
			ESM_EntityAIBrainController.brain.Save();
		}*/
	}
	
	public static int getPortalTime(Entity entity)
	{
		return ObfuscationReflectionHelper.getPrivateValue(Entity.class, entity, "field_82153_h", "portalCounter");
	}
	
	public static boolean getInPortal(Entity entity)
	{
		return ObfuscationReflectionHelper.getPrivateValue(Entity.class, entity, "field_71087_bX", "inPortal");
	}
	
	public static void setInPortal(Entity entity, boolean value)
	{
		ObfuscationReflectionHelper.setPrivateValue(Entity.class, entity, value, "field_71087_bX", "inPortal");
	}
	
	public static boolean isNearSpawner(World world, int x, int y, int z)
	{
		for(int i = x - 5; i < x + 5; i++)
		{
			for(int j = y - 5; j < y + 5; j++)
			{
				for(int k = z - 5; k < z + 5; k++)
				{
					if(!world.getChunkProvider().chunkExists(i >> 4, k >> 4))
					{
						continue;
					}
					
					if(world.getBlock(i, j, k) == Blocks.mob_spawner)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID.equals(ESM_Settings.ID))
		{
			for(Configuration config : ESMGuiConfig.tempConfigs)
			{
				config.save();
			}
			
			ESM_Utils.UpdateBiomeSpawns();
		}
	}
	
	@SubscribeEvent
	public void allowDespawn(LivingSpawnEvent.AllowDespawn event)
	{
		if(!ESM_Settings.keepLoaded)
		{
			return;
		}
		
		if(event.entityLiving instanceof EntityCreature)
		{
			EntityCreature creature = (EntityCreature)event.entityLiving;
			
			if(creature.getEntityToAttack() != null && creature.getEntityToAttack() instanceof EntityPlayer && !creature.getEntityToAttack().isDead)
			{
				event.setResult(Result.DENY);
				return;
			}
		} else if(event.entityLiving instanceof EntityESMGhast)
		{
			EntityESMGhast ghast = (EntityESMGhast)event.entityLiving;
			
			if(ghast.targetedEntity != null && ghast.targetedEntity instanceof EntityPlayer && !ghast.targetedEntity.isDead)
			{
				event.setResult(Result.DENY);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void allowSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		BiomeGenBase biome = event.world.getBiomeGenForCoords((int)event.x, (int)event.z);
		
		if(event.entityLiving instanceof EntityGhast && ESM_Settings.GhastDimensionBlacklist.contains(event.world.provider.dimensionId) && !ESM_Utils.nativeGhastBiomes.contains(biome))
		{
			event.setResult(Result.DENY);
			return;
		} else if(event.entityLiving instanceof EntityBlaze && ESM_Settings.BlazeDimensionBlacklist.contains(event.world.provider.dimensionId) && !ESM_Utils.nativeBlazeBiomes.contains(biome))
		{
			event.setResult(Result.DENY);
			return;
		} else if(event.entityLiving instanceof EntityMob && event.getResult() != Result.DENY)
		{
			boolean flag = false;
			int day = (int)(event.world.getWorldTime()/24000);
			
			if(ESM_Settings.hardDay != 0 && day != 0 && day%ESM_Settings.hardDay == 0)
			{
				flag = true;
			}
			
			if(!flag && ESM_Settings.timedDifficulty > 0 && event.world.getTotalWorldTime()/(ESM_Settings.timedDifficulty*24000D) < event.world.rand.nextFloat())
			{
				event.setResult(Result.DENY);
				return;
			} else if(!ESM_Settings.moreSpawning && !flag)
			{
				return;
			}
			
	        int i = MathHelper.floor_double(event.entityLiving.posX);
	        int j = MathHelper.floor_double(event.entityLiving.boundingBox.minY);
	        int k = MathHelper.floor_double(event.entityLiving.posZ);
	        
	        int l = event.world.getBlockLightValue(i, j, k);
	        
            if (event.world.isThundering())
            {
                int i1 = event.world.skylightSubtracted;
                event.world.skylightSubtracted = 10;
                l = event.world.getBlockLightValue(i, j, k);
                event.world.skylightSubtracted = i1;
            }
            
            if(flag) // Hard day! Ignoring lighting D:
            {
            	l = 0;
            }
            
			if(event.world.checkNoEntityCollision(event.entityLiving.boundingBox) && event.world.getCollidingBoundingBoxes(event.entityLiving, event.entityLiving.boundingBox).isEmpty() && !event.world.isAnyLiquid(event.entityLiving.boundingBox) && ((EntityMob)event.entityLiving).getBlockPathWeight(i, j, k) >= 0.0F && l <= 7)
			{
				event.setResult(Result.ALLOW);
			}
		}
	}
}
