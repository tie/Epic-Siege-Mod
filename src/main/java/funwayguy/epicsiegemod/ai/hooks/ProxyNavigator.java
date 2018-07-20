package funwayguy.epicsiegemod.ai.hooks;

import funwayguy.epicsiegemod.core.ESM;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ProxyNavigator extends PathNavigateGround
{
    public ProxyNavigator(EntityLiving entitylivingIn, World worldIn)
    {
        super(entitylivingIn, worldIn);
    }
    
    @Override
    public void clearPath()
    {
        //ESM.logger.info("Cleared path", new Exception());
        super.clearPath();
    }
    
    @Override
    public boolean setPath(@Nullable Path pathentityIn, double speedIn)
    {
        boolean flag = super.setPath(pathentityIn, speedIn);
        
        if(!flag)
        {
            //ESM.logger.error("Failed to set path", new Exception());
        }
        
        return flag;
    }
}
