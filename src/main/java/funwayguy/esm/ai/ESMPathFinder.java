package funwayguy.esm.ai;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import org.apache.logging.log4j.Level;
import funwayguy.esm.core.ESM;

public class ESMPathFinder extends PathFinder
{
	static ArrayList<Block> avoidBlocks;
	
    /** should the PathFinder go through wodden door blocks */
    private boolean isWoddenDoorAllowed;
    /** should the PathFinder disregard BlockMovement type materials in its path */
    private boolean isMovementBlockAllowed;
	
	public ESMPathFinder(IBlockAccess blockAccess, boolean allowDoors, boolean movementBlock, boolean pathWater, boolean canDrown)
	{
		super(blockAccess, allowDoors, movementBlock, pathWater, canDrown);
        this.isWoddenDoorAllowed = allowDoors;
        this.isMovementBlockAllowed = movementBlock;
	}

    /**
     * Checks if an entity collides with blocks at a position. Returns 1 if clear, 0 for colliding with any solid block,
     * -1 for water(if avoiding water) but otherwise clear, -2 for lava, -3 for fence, -4 for closed trapdoor, 2 if
     * otherwise clear except for open trapdoor or water(if not avoiding)
     */
	@Override
    public int getVerticalOffset(Entity p_75855_1_, int p_75855_2_, int p_75855_3_, int p_75855_4_, PathPoint p_75855_5_)
    {
		boolean flag = false;
		
		if(fieldPathWater == null)
		{
			try
			{
				fieldPathWater = PathFinder.class.getDeclaredField("field_75863_g");
				fieldPathWater.setAccessible(true);
			} catch(Exception e1)
			{
				try
				{
					fieldPathWater = PathFinder.class.getDeclaredField("isPathingInWater");
					fieldPathWater.setAccessible(true);
				} catch(Exception e2)
				{
	        		ESM.log.log(Level.ERROR, "Unable to get field for pathing in water", e1);
				}
			}
		}
		
		if(fieldPathWater != null)
		{
			try
			{
				flag = fieldPathWater.getBoolean(this);
			} catch(Exception e)
			{
        		ESM.log.log(Level.ERROR, "Unable to read field for pathing in water", e);
				flag = false;
			}
		}
		
        return VerticalOffset(p_75855_1_, p_75855_2_, p_75855_3_, p_75855_4_, p_75855_5_, flag, this.isMovementBlockAllowed, this.isWoddenDoorAllowed);
    }
	
	Field fieldPathWater = null;
	static Method liquidVecMethod = null;
	
    public static int VerticalOffset(Entity entity, int x, int y, int z, PathPoint point, boolean pathWater, boolean moveBlock, boolean allowDoors)
    {
    	if(liquidVecMethod == null)
    	{
	    	try
	    	{
	    		liquidVecMethod = BlockLiquid.class.getDeclaredMethod("func_149800_f", IBlockAccess.class, int.class, int.class, int.class);
	    		liquidVecMethod.setAccessible(true);
	    	} catch(Exception e1)
	    	{
	    		try
	    		{
	        		liquidVecMethod = BlockLiquid.class.getDeclaredMethod("getFlowVector", IBlockAccess.class, int.class, int.class, int.class);
	        		liquidVecMethod.setAccessible(true);
	    		} catch(Exception e2)
	    		{
	        		ESM.log.log(Level.ERROR, "Unable to get method for liquid flow vector", e1);
	    		}
	    	}
    	}
    	
        boolean flag3 = false;

        for (int l = x; l < x + point.xCoord; ++l)
        {
            for (int i1 = y; i1 < y + point.yCoord; ++i1)
            {
                for (int j1 = z; j1 < z + point.zCoord; ++j1)
                {
                    Block block = entity.worldObj.getBlock(l, i1, j1);
                    
                    if(avoidBlocks.contains(block))
                	{
                		return -2;
                    } else if (block.getMaterial() != Material.air)
                    {
                        if (block == Blocks.trapdoor)
                        {
                            flag3 = true;
                        }
                        else if(!(block instanceof BlockLiquid))
                        {
                        	if (!allowDoors && (block instanceof BlockDoor ||block instanceof BlockFenceGate) && block.getMaterial().isToolNotRequired())
                            {
                                return 0;
                            }
                        }
                        else
                        {
                        	BlockLiquid liquid = (BlockLiquid)block;
                        	
                        	Vec3 flowDir = Vec3.createVectorHelper(0, 0, 0);
                        	
                        	if(liquidVecMethod != null)
                        	{
	                        	try
	                        	{
	                        		flowDir = (Vec3)liquidVecMethod.invoke(liquid, entity.worldObj, x, y, z);
	                        	} catch(Exception e)
	                        	{
	                        		ESM.log.log(Level.ERROR, "Unable to get liquid flow vector for pathing", e);
	                        	}
                        	}
                        	
                        	if(flowDir.xCoord != 0 || flowDir.zCoord != 0) // Avoid moving water traps
                        	{
                        		return -2;
                        	} else if (pathWater)
                            {
                                return -1;
                            }

                            flag3 = true;
                        }

                        int k1 = block.getRenderType();
                        
                        if (entity.worldObj.getBlock(l, i1, j1).getRenderType() == 9)
                        {
                            int j2 = MathHelper.floor_double(entity.posX);
                            int l1 = MathHelper.floor_double(entity.posY);
                            int i2 = MathHelper.floor_double(entity.posZ);

                            if (entity.worldObj.getBlock(j2, l1, i2).getRenderType() != 9 && entity.worldObj.getBlock(j2, l1 - 1, i2).getRenderType() != 9)
                            {
                                return -3;
                            }
                        } else if(CanFit(entity, block, l, i1, j1))
                        {
                        	continue;
                        } else if (!block.getBlocksMovement(entity.worldObj, l, i1, j1) && (!moveBlock || !((block instanceof BlockDoor || block instanceof BlockFenceGate) && block.getMaterial().isToolNotRequired())))
                        {
                            if (k1 == 11 || k1 == 32)
                            {
                                return -3;
                            }

                            if (block == Blocks.trapdoor)
                            {
                                return -4;
                            }

                            Material material = block.getMaterial();

                            if (material != Material.lava)
                            {
                                return 0;
                            } else if (!entity.handleLavaMovement())
                            {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return flag3 ? 2 : 1;
    }
    
    public static boolean CanFit(Entity entity, Block block, int x, int y, int z)
    {
    	if(block == null || block.getCollisionBoundingBoxFromPool(entity.worldObj, x, y, z) == null)
    	{
    		return true;
    	} else if(entity == null || entity.boundingBox == null)
    	{
    		return false;
    	}
    	
    	AxisAlignedBB eBounds = entity.boundingBox;
    	AxisAlignedBB bBounds = block.getCollisionBoundingBoxFromPool(entity.worldObj, x, y, z);
    	
    	double BW = Math.max(bBounds.maxX - bBounds.minX, bBounds.maxZ - bBounds.minZ);
    	double BH1 = bBounds.maxY - (double)y;
    	double BH2 = bBounds.maxY - bBounds.minY;
    	
    	double EW = Math.max(eBounds.maxX - eBounds.minX, eBounds.maxZ - eBounds.minZ);
    	double EH = eBounds.maxY - eBounds.minY;
    	
    	if(BW <= 0 || BH2 <= 0)
    	{
    		return true;
    	}
    	
    	if(BW > 0 && EW >= 1)
    	{
    		return false;
    	}
    	
    	return BW + EW <= 1D || BH1 <= 0.5F || BH2 + EH <= 1F;
    }
    
    static
    {
    	avoidBlocks = new ArrayList<Block>();
    	avoidBlocks.add(Blocks.stone_pressure_plate);
    	avoidBlocks.add(Blocks.wooden_pressure_plate);
    	avoidBlocks.add(Blocks.tripwire);
    	avoidBlocks.add(Blocks.tripwire_hook);
    	avoidBlocks.add(Blocks.heavy_weighted_pressure_plate);
    	avoidBlocks.add(Blocks.light_weighted_pressure_plate);
    	
    	@SuppressWarnings("unchecked")
		Iterator<Block> iterator = Block.blockRegistry.iterator();
    	
    	while(iterator.hasNext())
    	{
    		Block block = iterator.next();
    		
    		if(block != null && (block.getMaterial() == Material.fire || block.getMaterial() == Material.lava || block.getMaterial() == Material.cactus || block.getMaterial() == Material.web || block.getMaterial() == Material.portal))
    		{
    			avoidBlocks.add(block);
    		}
    	}
    }
}
