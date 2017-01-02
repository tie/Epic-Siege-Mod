package funwayguy.epicsiegemod.handlers.entities;

import java.io.File;
import java.util.UUID;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import funwayguy.epicsiegemod.capabilities.modified.CapabilityModifiedHandler;
import funwayguy.epicsiegemod.capabilities.modified.IModifiedHandler;
import funwayguy.epicsiegemod.core.DimSettings;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.core.ESM_Settings;

public class GeneralEntityHandler
{
	private final ResourceLocation DIM_MODIFIER = new ResourceLocation(ESM.MODID, "general_spawn");
	private UUID attMod1 = UUID.fromString("74dcd479-97f3-4a04-b84a-0ffab0863a4f");
	private UUID attMod2 = UUID.fromString("2e1a9c33-bbd9-4daf-a723-e598e41ddeb9");
	private UUID attMod3 = UUID.fromString("7dd7b301-055b-4bf1-b94a-2a47a6338ca1");
	private UUID attMod4 = UUID.fromString("321eab99-4946-4375-a693-c0dce3706b6d");
	
	private static float curBossMod = 0F;
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote || event.getEntity().isDead || event.isCanceled())
		{
			return;
		}
		
		if(event.getEntity() instanceof EntityMob && !ESM_Settings.AIExempt.contains(EntityList.getEntityString(event.getEntity())))
		{
			EntityLivingBase entityLiving = (EntityLivingBase)event.getEntity();
			IModifiedHandler modHandler = entityLiving.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
			DimSettings dimSet = ESM_Settings.dimSettings.get(event.getWorld().provider.getDimension());
			
			if(modHandler == null)
			{
				return;
			}
			
			if(dimSet == null && curBossMod > 0F && ESM_Settings.bossModifier != 0F)
			{
				dimSet = new DimSettings(1D, 1D, 1D, 1D);
			}
			
			if(dimSet != null && !modHandler.getModificationData(DIM_MODIFIER).getBoolean("hasModifiers"))
			{
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(attMod1, "ESM_TWEAK_1", dimSet.hpMult + curBossMod, 1));
					entityLiving.setHealth(entityLiving.getMaxHealth());
				}
				
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(attMod2, "ESM_TWEAK_2", dimSet.spdMult + curBossMod, 1));
				}
				
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier(attMod3, "ESM_TWEAK_3", dimSet.dmgMult + curBossMod, 1));
				}
				
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier(attMod4, "ESM_TWEAK_4", dimSet.dmgMult + curBossMod, 1));
				}
				
				modHandler.getModificationData(DIM_MODIFIER).setBoolean("hasModifiers", true);
			}
			
			if(!modHandler.getModificationData(DIM_MODIFIER).getBoolean("checkMobBomb") && (ESM_Settings.MobBombAll || ESM_Settings.MobBombs.contains(EntityList.getEntityString(entityLiving))) && entityLiving.getPassengers().size() == 0 && entityLiving.worldObj.loadedEntityList.size() < 512)
			{
				if(ESM_Settings.MobBombRarity <= 0 || entityLiving.getRNG().nextInt(ESM_Settings.MobBombRarity) == 0)
				{
					EntityLiving passenger = new EntityCreeper(entityLiving.worldObj);
					
					passenger.setLocationAndAngles(entityLiving.posX, entityLiving.posY, entityLiving.posZ, entityLiving.rotationYaw, 0.0F);
					
					if(passenger instanceof EntityLiving)
					{
						((EntityLiving)passenger).onInitialSpawn(entityLiving.worldObj.getDifficultyForLocation(new BlockPos(entityLiving)), (IEntityLivingData)null);
					}
					
					entityLiving.worldObj.spawnEntityInWorld(passenger);
					passenger.startRiding(entityLiving);
				}
				
				modHandler.getModificationData(DIM_MODIFIER).setBoolean("checkMobBomb", true);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityKilled(LivingDeathEvent event)
	{
		if(event.getEntity().worldObj.isRemote || event.getEntity().isNonBoss())
		{
			return;
		}
		
		curBossMod += ESM_Settings.bossModifier;
	}
	
	private File worldDir = null;
	
	@SubscribeEvent
	public void onWorldLoad(Load event)
	{
		if(event.getWorld().isRemote || worldDir != null)
		{
			return;
		}
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		if(server.isServerRunning())
		{
			if(ESM.proxy.isClient())
			{
				worldDir = server.getFile("saves/" + server.getFolderName());
			} else
			{
				worldDir = server.getFile(server.getFolderName());
			}
			
			try
			{
				NBTTagCompound wmTag = CompressedStreamTools.read(new File(worldDir, "ESM.dat"));
				if(wmTag != null)
				{
					curBossMod = wmTag.getFloat("BossModifier");
				} else
				{
					curBossMod = 0F;
				}
			} catch(Exception e)
			{
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(Unload event)
	{
		if(event.getWorld().isRemote)
		{
			return;
		}

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		if(!server.isServerRunning())
		{
			curBossMod = 0F;
			worldDir = null;
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(Save event)
	{
		if(event.getWorld().isRemote || worldDir == null)
		{
			return;
		}
		
		try
		{
			NBTTagCompound wmTag = new NBTTagCompound();
			wmTag.setFloat("BossModifier", curBossMod);
			CompressedStreamTools.write(wmTag, new File(worldDir, "ESM.dat"));
		} catch(Exception e)
		{
		}
	}
}
