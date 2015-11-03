package funwayguy.esm.ai;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class ESMPathNavigator extends PathNavigate
{
    private EntityLiving theEntity;
    private boolean canSwim;
    private World worldObj;
    /** Specifically, if a wooden door block is even considered to be passable by the pathfinder */
    private boolean canPassOpenWoodenDoors = true;
    /** If door blocks are considered passable even when closed */
    private boolean canPassClosedWoodenDoors;
    /** If water blocks are avoided (at least by the pathfinder) */
    private boolean avoidsWater;
	
	public ESMPathNavigator(EntityLiving entityLiving, World world)
	{
		super(entityLiving, world);
		this.theEntity = entityLiving;
		this.worldObj = world;
	}
	
	/**
	 * Inherits most of the main options from the original navigator
	 * @param navigator
	 */
	public void inherit(PathNavigate navigator)
	{
		if(navigator == null)
		{
			return;
		}
		
		this.setAvoidSun((Boolean)ObfuscationReflectionHelper.getPrivateValue(PathNavigate.class, navigator, "field_75509_f", "noSunPathfind"));
		this.setAvoidsWater(navigator.getAvoidsWater());
		this.setBreakDoors(navigator.getCanBreakDoors());
		this.setSpeed((Double)ObfuscationReflectionHelper.getPrivateValue(PathNavigate.class, navigator, "field_75511_d", "speed"));
		this.setEnterDoors((Boolean)ObfuscationReflectionHelper.getPrivateValue(PathNavigate.class, navigator, "field_75518_j", "canPassOpenWoodenDoors"));
		this.setCanSwim((Boolean)ObfuscationReflectionHelper.getPrivateValue(PathNavigate.class, navigator, "field_75517_m", "canSwim"));
	}

    /**
     * Returns the path to the given coordinates
     */
    public PathEntity getPathToXYZ(double p_75488_1_, double p_75488_3_, double p_75488_5_)
    {
        return !this.canNavigate() ? null : this.getEntityPathToXYZ(this.worldObj, this.theEntity, MathHelper.floor_double(p_75488_1_), (int)p_75488_3_, MathHelper.floor_double(p_75488_5_), this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
    }

    public PathEntity getEntityPathToXYZ(World world, Entity p_72844_1_, int p_72844_2_, int p_72844_3_, int p_72844_4_, float p_72844_5_, boolean p_72844_6_, boolean p_72844_7_, boolean p_72844_8_, boolean p_72844_9_)
    {
        world.theProfiler.startSection("pathfind");
        int l = MathHelper.floor_double(p_72844_1_.posX);
        int i1 = MathHelper.floor_double(p_72844_1_.posY);
        int j1 = MathHelper.floor_double(p_72844_1_.posZ);
        int k1 = (int)(p_72844_5_ + 8.0F);
        int l1 = l - k1;
        int i2 = i1 - k1;
        int j2 = j1 - k1;
        int k2 = l + k1;
        int l2 = i1 + k1;
        int i3 = j1 + k1;
        ChunkCache chunkcache = new ChunkCache(world, l1, i2, j2, k2, l2, i3, 0);
        PathEntity pathentity = (new ESMPathFinder(chunkcache, p_72844_6_, p_72844_7_, p_72844_8_, p_72844_9_)).createEntityPathTo(p_72844_1_, p_72844_2_, p_72844_3_, p_72844_4_, p_72844_5_);
        world.theProfiler.endSection();
        return pathentity;
    }

    /**
     * Returns the path to the given EntityLiving
     */
	@Override
    public PathEntity getPathToEntityLiving(Entity p_75494_1_)
    {
        return !this.canNavigate() ? null : this.getPathEntityToEntity(this.worldObj, this.theEntity, p_75494_1_, this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
    }

    public PathEntity getPathEntityToEntity(World world, Entity p_72865_1_, Entity p_72865_2_, float p_72865_3_, boolean p_72865_4_, boolean p_72865_5_, boolean p_72865_6_, boolean p_72865_7_)
    {
    	world.theProfiler.startSection("pathfind");
        int i = MathHelper.floor_double(p_72865_1_.posX);
        int j = MathHelper.floor_double(p_72865_1_.posY + 1.0D);
        int k = MathHelper.floor_double(p_72865_1_.posZ);
        int l = (int)(p_72865_3_ + 16.0F);
        int i1 = i - l;
        int j1 = j - l;
        int k1 = k - l;
        int l1 = i + l;
        int i2 = j + l;
        int j2 = k + l;
        ChunkCache chunkcache = new ChunkCache(world, i1, j1, k1, l1, i2, j2, 0);
        PathEntity pathentity = (new ESMPathFinder(chunkcache, p_72865_4_, p_72865_5_, p_72865_6_, p_72865_7_)).createEntityPathTo(p_72865_1_, p_72865_2_, p_72865_3_);
        world.theProfiler.endSection();
        return pathentity;
    }

    /**
     * If on ground or swimming and can swim
     */
    private boolean canNavigate()
    {
        return this.theEntity.onGround || this.canSwim && this.isInLiquid() || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
    }

    /**
     * Returns true if the entity is in water or lava, false otherwise
     */
    private boolean isInLiquid()
    {
        return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
    }

    /**
     * Sets if the entity can swim
     */
	@Override
    public void setCanSwim(boolean flag)
    {
		super.setCanSwim(flag);
        this.canSwim = flag;
    }

    public void setAvoidsWater(boolean flag)
    {
    	super.setAvoidsWater(flag);
        this.avoidsWater = flag;
    }

    public void setBreakDoors(boolean flag)
    {
    	super.setBreakDoors(flag);
        this.canPassClosedWoodenDoors = flag;
    }

    /**
     * Sets if the entity can enter open doors
     */
    public void setEnterDoors(boolean flag)
    {
    	super.setEnterDoors(flag);
        this.canPassOpenWoodenDoors = flag;
    }
}