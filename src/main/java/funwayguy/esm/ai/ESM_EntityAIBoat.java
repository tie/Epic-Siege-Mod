package funwayguy.esm.ai;

import java.util.ArrayList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityBoat;

public class ESM_EntityAIBoat extends EntityAIBase
{
	boolean usedBoat = false;
	EntityLiving host;
	
	public ESM_EntityAIBoat(EntityLiving host)
	{
		this.host = host;
		usedBoat = host.getRNG().nextInt(4) == 0; // Only 25% of mobs will actually have a spare boat on hand, otherwise we will try and hijack one
	}
	
	@Override
	public boolean shouldExecute()
	{
		if(host.ridingEntity instanceof EntityBoat)
		{
			return true;
		} else if(host.handleWaterMovement() && host.getAttackTarget() != null && host.getDistanceToEntity(host.getAttackTarget()) > 16) // Only start if we really need to catch up to someone
		{
			@SuppressWarnings("unchecked")
			ArrayList<EntityBoat> nearBoats = (ArrayList<EntityBoat>)host.worldObj.getEntitiesWithinAABB(EntityBoat.class, host.boundingBox.expand(3D, 3D, 3D));
			
			for(EntityBoat b : nearBoats)
			{
				if(!b.onGround && b.riddenByEntity == null)
				{
					host.mountEntity(b);
					return true;
				}
			}
			
			if(!usedBoat)
			{
				usedBoat = true;
				EntityBoat boat = new EntityBoat(host.worldObj);
				boat.setPosition(host.posX, host.posY, host.posZ);
				host.worldObj.spawnEntityInWorld(boat);
				host.mountEntity(boat);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void updateTask()
	{
		if(!(host.ridingEntity instanceof EntityBoat))
		{
			return;
		}
		
		EntityBoat boat = (EntityBoat)host.ridingEntity;
		
		if(boat.onGround || (boat.isCollidedHorizontally && boat.motionX <= 0.25F && boat.motionZ <= 0.25F) || (host.getAttackTarget() != null && host.getDistanceToEntity(host.getAttackTarget()) <= 4))
		{
			host.dismountEntity(boat);
			boat.riddenByEntity = null;
			host.ridingEntity = null;
			boat.setVelocity(0D, 0D, 0D); // Try to stop the boat from moving away
			return;
		}
		
		host.moveForward = 1F;
	}
}
