package funwayguy.esm.world.gen;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class FortressBase
{
	public World worldObj;
	public int originX;
	public int originY;
	public int originZ;
	public int orientation;
	
	public FortressBase(World par1World, int chunkX, int chunkZ)
	{
    	this.worldObj = par1World;
    	this.originX = chunkX * 16;
    	this.originZ = chunkZ * 16;
    	this.originY = par1World.getChunkFromChunkCoords(chunkX, chunkZ).getHeightValue(8, 8);
    	this.orientation = par1World.rand.nextInt(4);
	}
	
	public abstract boolean buildStructure();
    
    protected void customFillWithBlocks(World par1World, int par3, int par4, int par5, int par6, int par7, int par8, int par9, int par10, boolean par11)
    {
        for (int var12 = par4; var12 <= par7; ++var12)
        {
            for (int var13 = par3; var13 <= par6; ++var13)
            {
                for (int var14 = par5; var14 <= par8; ++var14)
                {
                    if (!par11 || this.customGetBlockIdAtCurrentPosition(worldObj, var13, var12, var14) != 0)
                    {
                        if (var12 != par4 && var12 != par7 && var13 != par3 && var13 != par6 && var14 != par5 && var14 != par8)
                        {
                            this.customPlaceBlockAtCurrentPosition(worldObj, par10, 0, var13, var12, var14);
                        }
                        else
                        {
                            this.customPlaceBlockAtCurrentPosition(worldObj, par9, 0, var13, var12, var14);
                        }
                    }
                }
            }
        }
    }
    
    protected void customPlaceBlockAtCurrentPosition(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        int var8 = this.getXWithOffset(par4, par6);
        int var9 = this.getYWithOffset(par5);
        int var10 = this.getZWithOffset(par4, par6);
        
        par1World.setBlock(var8, var9, var10, par2, par3, 2);
    }
    
    protected int customGetBlockIdAtCurrentPosition(World par1World, int par2, int par3, int par4)
    {
        int var6 = this.getXWithOffset(par2, par4);
        int var7 = this.getYWithOffset(par3);
        int var8 = this.getZWithOffset(par2, par4);
        return par1World.getBlockId(var6, var7, var8);
    }
    
    protected int customGetBlockSkyLightAtCurrentPosition(World par1World, int par2, int par3, int par4)
    {
        int var6 = this.getXWithOffset(par2, par4);
        int var7 = this.getYWithOffset(par3);
        int var8 = this.getZWithOffset(par2, par4);
        Chunk chunk = par1World.getChunkFromBlockCoords(var6, var8);
        return chunk.getSavedLightValue(EnumSkyBlock.Sky, var6 & 0xf, var7, var8 & 0xf);
    }
    
    protected boolean customGenerateStructureChestContents(World par1World, int par4, int par5, int par6)
    {
        int var9 = this.getXWithOffset(par4, par6);
        int var10 = this.getYWithOffset(par5);
        int var11 = this.getZWithOffset(par4, par6);

        par1World.setBlock(var9, var10, var11, Block.chest.blockID , 0, 2);
        TileEntityChest var12 = (TileEntityChest)par1World.getBlockTileEntity(var9, var10, var11);
        
        if (var12 != null)
        {
        	for(int i = 0; i <= var12.getSizeInventory(); i++)
        	{
        		var12.setInventorySlotContents(i, new ItemStack(Item.expBottle, 1, 0));
        	}
        }
        
        var12.setInventorySlotContents(10, new ItemStack(Block.blockEmerald, 1, 0));
        var12.setInventorySlotContents(13, new ItemStack(Block.blockEmerald, 1, 0));
        var12.setInventorySlotContents(16, new ItemStack(Block.blockEmerald, 1, 0));
        
        return true;
    }
    
    protected void customGenerateSpawner(World par1World, int par3, int par4, int par5, String par6)
    {
        int var3 = this.getXWithOffset(par3, par5);
        int var4 = this.getYWithOffset(par4);
        int var5 = this.getZWithOffset(par3, par5);
        
        par1World.setBlock(var3, var4, var5, Block.mobSpawner.blockID);
        TileEntityMobSpawner var7 = (TileEntityMobSpawner)par1World.getBlockTileEntity(var3, var4, var5);

        if (var7 != null)
        {
            var7.getSpawnerLogic().setMobID(par6);
        }
    }

    protected void customPlaceIronDoorAtCurrentPosition(World par1World, int par4, int par5, int par6, int par7)
    {
        int var8 = this.getXWithOffset(par4, par6);
        int var9 = this.getYWithOffset(par5);
        int var10 = this.getZWithOffset(par4, par6);
        
        ItemDoor.placeDoorBlock(worldObj, var8, var9, var10, par7, Block.doorIron);
    }
    
    protected int getXWithOffset(int x, int z)
    {
    	return x + originX;
    }
    
    protected int getYWithOffset(int y)
    {
    	return y + originY;
    }
    
    protected int getZWithOffset(int x, int z)
    {
    	return z + originZ;
    }
}
