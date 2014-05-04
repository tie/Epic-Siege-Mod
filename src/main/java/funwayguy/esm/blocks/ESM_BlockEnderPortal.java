package funwayguy.esm.blocks;

import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.core.ESM_Utils;

public class ESM_BlockEnderPortal extends BlockEndPortal
{
    public ESM_BlockEnderPortal(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null && !par1World.isRemote)
        {
        	if(ESM_Settings.NewEnd)
        	{
        		ESM_Utils.transferDimensions(ESM_Settings.SpaceDimID, par5Entity, false);
        	} else
        	{
        		par5Entity.travelToDimension(1);
        	}
        }
    }
}
