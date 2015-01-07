package funwayguy.esm.world.gen;

import funwayguy.esm.core.ESM_Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;

public class FortressHell extends FortressBase
{
	static final Block spineMat = Blocks.quartz_block;
	static final Block baseMat = Blocks.quartz_block;
	static final Block eyeMat = Blocks.iron_bars;
	
	public FortressHell(World par1World, int chunkX, int chunkZ)
	{
		super(par1World, chunkX, chunkZ);
		this.originY += 1;
	}

	@Override
	public boolean buildStructure()
	{
    	if(ESM_Utils.isFortAt(worldObj, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0), 48))
		{
    		return false;
		} else
		{
    		ESM_Utils.addFortToDB(worldObj, this.getXWithOffset(0, 0), this.getZWithOffset(0, 0));
		}
    	
    	this.customFillWithBlocks(worldObj, 0, -3, 0, 6, 8, 6, baseMat, baseMat, false);
    	this.customFillWithBlocks(worldObj, 1, 0, 1, 5, 8, 5, Blocks.air, Blocks.air, false);
    	

    	this.customFillWithBlocks(worldObj, 1, 5, 1, 5, 5, 5, Blocks.netherrack, Blocks.netherrack, false);
    	this.customFillWithBlocks(worldObj, 1, 6, 1, 5, 6, 5, Blocks.fire, Blocks.fire, false);
    	this.customFillWithBlocks(worldObj, 1, 7, 1, 5, 7, 5, baseMat, baseMat, false);
    	this.customFillWithBlocks(worldObj, 2, -2, 2, 4, 7, 4, Blocks.air, Blocks.air, false);

    	this.customPlaceBlockAtCurrentPosition(worldObj, Blocks.air, 0, 0, 8, 0);
    	this.customPlaceBlockAtCurrentPosition(worldObj, Blocks.air, 0, 0, 8, 6);
    	this.customPlaceBlockAtCurrentPosition(worldObj, Blocks.air, 0, 6, 8, 0);
    	this.customPlaceBlockAtCurrentPosition(worldObj, Blocks.air, 0, 6, 8, 6);
    	
    	this.customFillWithBlocks(worldObj, 1, 6, 0, 2, 6, 0, eyeMat, eyeMat, false);
    	this.customFillWithBlocks(worldObj, 4, 6, 0, 5, 6, 0, eyeMat, eyeMat, false);
    	
    	for(int i = 5; i >= 0; i--)
    	{
    		this.buildSpinePiece(3, i, 7 + ((5 - i) * 3));
    	}
    	
    	for(int i = 0; i < 7; i++)
    	{
    		for(int k = 0; k < 5; k++)
    		{
    			this.customFillWithBlocks(worldObj, i, 0 + ((i + k) % 2), k, i, 1 + ((i + k) % 2), k, Blocks.air, Blocks.air, false);
    		}
    	}
    	
    	
    	for(int i = 2; i <= 4; i++)
    	{
    		for(int j = 2; j <= 4; j++)
    		{
    			this.customGenerateSpawner(worldObj, i, -2, j, "Blaze");
    			this.customGenerateSpawner(worldObj, i, 7, j, "Blaze");
    		}
    	}
    	
    	this.customFillWithBlocks(worldObj, 2, -1, 2, 4, -1, 4, Blocks.lava, Blocks.lava, false);
    	
    	this.customPlaceBlockAtCurrentPosition(worldObj, Blocks.dispenser, 0, 3, -1, 3);
    	
    	TileEntity tile = worldObj.getTileEntity(this.getXWithOffset(3, 3), this.getYWithOffset(-1), this.getZWithOffset(3, 3));
    	
    	if(tile != null && tile instanceof TileEntityDispenser)
    	{
    		((TileEntityDispenser)tile).setInventorySlotContents(0, new ItemStack(Blocks.tnt));
    	}
    	
    	this.customPlaceBlockAtCurrentPosition(worldObj, Blocks.trapped_chest, 0, 3, 0, 3);
    	
    	tile = worldObj.getTileEntity(this.getXWithOffset(3, 3), this.getYWithOffset(0), this.getZWithOffset(3, 3));
    	
    	if(tile != null && tile instanceof TileEntityChest)
    	{
    		TileEntityChest chest = (TileEntityChest)tile;
    		
        	for(int i = 0; i <= chest.getSizeInventory(); i++)
        	{
        		switch(worldObj.rand.nextInt(6))
        		{
        			case 0:
        			{
        				chest.setInventorySlotContents(i, new ItemStack(Items.nether_wart, 4));
        				break;
        			}
        			
        			case 1:
        			{
        				chest.setInventorySlotContents(i, new ItemStack(Items.quartz, 8));
        				break;
        			}
        			
        			case 2:
        			{
        				chest.setInventorySlotContents(i, new ItemStack(Items.blaze_rod, 1));
        				break;
        			}
        			
        			case 3:
        			{
        				chest.setInventorySlotContents(i, new ItemStack(Items.glowstone_dust, 16));
        				break;
        			}
        			
        			case 4:
        			{
        				chest.setInventorySlotContents(i, new ItemStack(Items.diamond, 1));
        				break;
        			}
        			
        			case 5:
        			{
        				if(worldObj.rand.nextInt(100) == 0)
        				{
        					chest.setInventorySlotContents(i, new ItemStack(Items.skull, 1, 1));
        				}
        				break;
        			}
        		}
        	}
    	}
    	
		return true;
	}
	
	public void buildSpinePiece(int posX, int posY, int posZ)
	{
		this.customFillWithBlocks(worldObj, posX, posY, posZ, posX, posY, posZ + 2, spineMat, spineMat, false); // Spine base
		this.customFillWithBlocks(worldObj, posX - 1 , posY, posZ + 1, posX + 1, posY, posZ + 1, spineMat, spineMat, false); // Spine cross
		this.customPlaceBlockAtCurrentPosition(worldObj, spineMat, 0, posX - 2, posY - 1, posZ + 1); // Rib 1st curve L
		this.customPlaceBlockAtCurrentPosition(worldObj, spineMat, 0, posX + 2, posY - 1, posZ + 1); // Rib 1st curve R
		this.customFillWithBlocks(worldObj, posX - 3, posY - 4, posZ + 1, posX - 3, posY - 2, posZ + 1, spineMat, spineMat, false); // Rib straight L
		this.customFillWithBlocks(worldObj, posX + 3, posY - 4, posZ + 1, posX + 3, posY - 2, posZ + 1, spineMat, spineMat, false); // Rib straight R
		this.customPlaceBlockAtCurrentPosition(worldObj, spineMat, 0, posX - 2, posY - 5, posZ + 1); // Rib 2nd curve L
		this.customPlaceBlockAtCurrentPosition(worldObj, spineMat, 0, posX + 2, posY - 5, posZ + 1); // Rib 2nd curve R
	}
}
