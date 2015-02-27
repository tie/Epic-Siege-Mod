package funwayguy.esm.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ESM_EntityAIBuildTrap extends EntityAIBase
{
	EntityLiving builder;
	
	public ESM_EntityAIBuildTrap(EntityLiving entity)
	{
		this.builder = entity;
	}
	
	@Override
	public boolean shouldExecute()
	{
		ItemStack stack = builder.getEquipmentInSlot(0);
		return stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.stone_pressure_plate) && builder.getRNG().nextInt(100) == 0;
	}
	
	@Override
	public boolean continueExecuting()
	{
		return false;
	}
	
	@Override
	public void startExecuting()
	{
		int i = MathHelper.floor_double(builder.posX);
		int j = MathHelper.floor_double(builder.posY);
		int k = MathHelper.floor_double(builder.posZ);
		
		int ii = i + builder.getRNG().nextInt(11)-5;
		int jj = j + builder.getRNG().nextInt(5)-2;
		int kk = k + builder.getRNG().nextInt(11)-5;
		
		if(jj < 10 || jj > 245)
		{
			return;
		}
		
		if(ii != 0 || kk != 0)
		{
			boolean flag = false;
			
			switch(builder.getRNG().nextInt(5))
			{
				case 0:
				{
					flag = BuildTrapA(builder.worldObj, ii, jj, kk);
					break;
				}
				case 1:
				{
					flag = BuildTrapB(builder.worldObj, ii, jj, kk);
					break;
				}
				case 2:
				{
					flag = BuildTrapC(builder.worldObj, ii, jj, kk);
					break;
				}
				case 3:
				{
					flag = BuildTrapD(builder.worldObj, ii, jj, kk);
					break;
				}
				case 4:
				{
					flag = BuildTrapE(builder.worldObj, i, j, k);
					break;
				}
			}
			
			if(flag)
			{
				builder.worldObj.playSoundAtEntity(builder, Block.soundTypeStone.soundName, 1.0F, 1.0F);
				builder.setCurrentItemOrArmor(0, null);
			}
		}
	}
	
	// Note: All traps are generated from the trigger point
	
	/**
	 * Potion trap
	 */
	public static boolean BuildTrapA(World world, int x, int y, int z)
	{
		// Ensure a valid building area exists and that the plate is accessible
		for(int j = -2; j < 2; j++)
		{
			if(world.getBlock(x, y + j, z) != (j >= 0? Blocks.air : Blocks.stone))
			{
				return false;
			}
		}
		
		world.setBlock(x, y - 2, z, Blocks.dispenser, 1, 2);
		world.setBlockMetadataWithNotify(x, y - 2, z, 1, 2);
		world.setBlock(x, y - 1, z, Blocks.stone, 0, 2);
		world.setBlock(x, y, z, Blocks.stone_pressure_plate, 0, 2);
		
		TileEntity tile = world.getTileEntity(x, y - 2, z);
		
		if(tile != null && tile instanceof TileEntityDispenser)
		{
			TileEntityDispenser dispenser = (TileEntityDispenser)tile;
			switch(world.rand.nextInt(4))
			{
				case 0:
				{
					dispenser.setInventorySlotContents(0, new ItemStack(Items.potionitem, 1, 16484)); // Poison II (0:16)
					break;
				}
				case 1:
				{
					dispenser.setInventorySlotContents(0, new ItemStack(Items.potionitem, 1, 16428)); // Harming II
					break;
				}
				case 2:
				{
					dispenser.setInventorySlotContents(0, new ItemStack(Items.potionitem, 1, 16458)); // Slowness (Extended 3:00)
					break;
				}
				case 3:
				{
					dispenser.setInventorySlotContents(0, new ItemStack(Items.potionitem, 1, 16456)); // Weakness (Extended 3:00)
					break;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * TNT + Web Mine
	 */
	public static boolean BuildTrapB(World world, int x, int y, int z)
	{
		// Ensure a valid building area exists and that the plate is accessible
		for(int j = -1; j < 2; j++)
		{
			if(world.getBlock(x, y + j, z) != (j >= 0? Blocks.air : Blocks.gravel))
			{
				return false;
			}
		}
		world.setBlock(x, y - 3, z, Blocks.web, 0, 2);
		world.setBlock(x, y - 2, z, Blocks.tnt, 0, 2);
		world.setBlock(x, y - 1, z, Blocks.gravel, 0, 2);
		world.setBlock(x, y, z, Blocks.stone_pressure_plate, 0, 2);
		
		return true;
	}
	
	/**
	 * Instantaneous Mine
	 */
	public static boolean BuildTrapC(World world, int x, int y, int z)
	{
		// Ensure a valid building area exists and that the plate is accessible
		for(int j = -4; j < 2; j++)
		{
			if(world.getBlock(x, y + j, z) != (j >= 0? Blocks.air : Blocks.stone))
			{
				return false;
			}
		}
		
		world.setBlock(x, y - 4, z, Blocks.stone);
		EntityMinecartTNT tntCart = new EntityMinecartTNT(world);
		tntCart.setPosition((double)x + 0.5D, (double)y - 2.5D, (double)z + 0.5D);
		world.setBlockToAir(x, y - 3, z);
		world.spawnEntityInWorld(tntCart);
		world.setBlock(x, y - 2, z, Blocks.dispenser, 0, 2);
		world.setBlockMetadataWithNotify(x, y - 2, z, 0, 2);
		
		TileEntity tile = world.getTileEntity(x, y - 2, z);
		
		if(tile != null && tile instanceof TileEntityDispenser)
		{
			TileEntityDispenser dispenser = (TileEntityDispenser)tile;
			dispenser.setInventorySlotContents(0, new ItemStack(Items.flint_and_steel));
		}
		
		world.setBlock(x, y - 1, z, Blocks.stone, 0, 2);
		world.setBlock(x, y, z, Blocks.stone_pressure_plate, 0, 2);
		
		return true;
	}
	
	/**
	 * Ore Trap - Floor
	 */
	public static boolean BuildTrapD(World world, int x, int y, int z)
	{
		// Ensure frame exists to hold wires etc
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -4; j < 0; j++)
			{
				for(int k = -1; k <= 1; k++)
				{
					if(world.getBlock(x + i, y + j, z + k) != Blocks.stone && !(i == 0 && j == -1 && k == 0 && world.getBlock(x + i, y + j, z + k) == Blocks.iron_ore))
					{
						return false;
					}
				}
			}
		}
		
		// Check if trigger ore is accessible
		if(world.getBlock(x, y, z) != Blocks.air || world.getBlock(x, y - 1, z) != Blocks.air)
		{
			return false;
		}
		
		world.setBlock(x, y - 1, z, Blocks.iron_ore, 0, 2);
		world.setBlock(x, y - 2, z, Blocks.lever, 8, 2);
		world.setBlock(x - 1, y - 2, z, Blocks.redstone_wire, 0, 3);
		world.setBlock(x, y - 3, z, Blocks.unlit_redstone_torch, 1, 2);
		world.setBlock(x + 1, y - 3, z, Blocks.tnt, 0, 2);
		world.setBlock(x, y - 3, z + 1, Blocks.tnt, 0, 2);
		world.setBlock(x, y - 3, z - 1, Blocks.tnt, 0, 2);
		
		return true;
	}
	
	public static boolean ValidWallTrap(World world, MovingObjectPosition mop, ForgeDirection d)
	{
		if(mop == null || d == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
		{
			return false;
		}
		
		int x = mop.blockX;
		int y = mop.blockY;
		int z = mop.blockZ;
		
		Block block = world.getBlock(x, y, z);
		
		if(block != Blocks.stone && !(block instanceof BlockOre))
		{
			return false;
		}
		
		if(d == ForgeDirection.NORTH || d == ForgeDirection.SOUTH)
		{
			for(int i = -1; i <= 1; i++)
			{
				for(int j = -1; j <= 2; j++)
				{
					for(int k = 0; k <= 1; k++)
					{
						Block tmpB = world.getBlock(x + i, y + j, z + (k * d.offsetZ));
						if(!(tmpB instanceof BlockOre) && tmpB != Blocks.stone)
						{
							return false;
						}
					}
				}
			}
		} else if(d == ForgeDirection.EAST || d == ForgeDirection.WEST)
		{
			for(int i = 0; i <= 1; i++)
			{
				for(int j = -1; j <= 2; j++)
				{
					for(int k = -1; k <= 1; k++)
					{
						Block tmpB = world.getBlock(x + (i * d.offsetX), y + j, z + k);
						if(!(tmpB instanceof BlockOre) && tmpB != Blocks.stone)
						{
							return false;
						}
					}
				}
			}
		} else
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Ore Trap - Wall
	 */
	public static boolean BuildTrapE(World world, int x, int y, int z)
	{
		ForgeDirection direction = null;
		MovingObjectPosition mop = AIUtils.RayCastBlocks(world, Vec3.createVectorHelper(x, y + 1, z), Vec3.createVectorHelper(x, y + 1, z + 5), false);
		if(direction == null && mop != null && ValidWallTrap(world, mop, ForgeDirection.SOUTH))
		{
			direction = ForgeDirection.SOUTH;
		}
		mop = mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK? mop : AIUtils.RayCastBlocks(world, Vec3.createVectorHelper(x, y + 1, z), Vec3.createVectorHelper(x, y + 1, z - 5), false);
		if(direction == null && mop != null && ValidWallTrap(world, mop, ForgeDirection.NORTH))
		{
			direction = ForgeDirection.NORTH;
		}
		mop = mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK? mop : AIUtils.RayCastBlocks(world, Vec3.createVectorHelper(x, y + 1, z), Vec3.createVectorHelper(x + 5, y + 1, z), false);
		if(direction == null && mop != null && ValidWallTrap(world, mop, ForgeDirection.EAST))
		{
			direction = ForgeDirection.EAST;
		}
		mop = mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK? mop : AIUtils.RayCastBlocks(world, Vec3.createVectorHelper(x, y + 1, z), Vec3.createVectorHelper(x - 5, y + 1, z), false);
		if(direction == null && mop != null && ValidWallTrap(world, mop, ForgeDirection.WEST))
		{
			direction = ForgeDirection.WEST;
		}
		
		if(mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || direction == null)
		{
			return false;
		}
		
		if(direction == ForgeDirection.SOUTH || direction == ForgeDirection.NORTH)
		{
			int d = direction.offsetZ;
			x = mop.blockX ;
			y = mop.blockY;
			z = mop.blockZ;
			
			world.setBlock(x, y, z, Blocks.iron_ore);
			world.setBlock(x, y + 1, z, Blocks.iron_ore);
			world.setBlock(x, y, z + (1 * d), Blocks.redstone_torch, d == 1? 3 : 4, 2);
			world.setBlock(x, y + 2, z + (1 * d), Blocks.unlit_redstone_torch, 5, 2);
			world.setBlock(x + 1, y + 2, z + (1 * d), Blocks.tnt);
			world.setBlock(x - 1, y + 2, z + (1 * d), Blocks.tnt);
		} else if(direction == ForgeDirection.EAST || direction == ForgeDirection.WEST)
		{
			int d = direction.offsetX;
			x = mop.blockX ;
			y = mop.blockY;
			z = mop.blockZ;
			
			world.setBlock(x, y, z, Blocks.iron_ore);
			world.setBlock(x, y + 1, z, Blocks.iron_ore);
			world.setBlock(x + (1 * d), y, z, Blocks.redstone_torch, d == 1? 3 : 4, 2);
			world.setBlock(x + (1 * d), y + 2, z, Blocks.unlit_redstone_torch, 5, 2);
			world.setBlock(x + (1 * d), y + 2, z + 1, Blocks.tnt);
			world.setBlock(x + (1 * d), y + 2, z - 1, Blocks.tnt);
		} else
		{
			return false;
		}
		
		return true;
	}
}
