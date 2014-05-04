package funwayguy.esm.world.gen;

import net.minecraft.world.World;

public class FortressJungle extends FortressBase
{
	public FortressJungle(World par1World, int chunkX, int chunkZ)
	{
		super(par1World, chunkX, chunkZ);
	}

	@Override
	public boolean buildStructure()
	{
		return false;
	}
}
