package funwayguy.esm.world.dimensions;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.MapGenScatteredFeature;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ChunkProviderSpace implements IChunkProvider
{
	private byte seaDepth = 76;
	private byte seaLevel = 64;
	private int mineralDepth = 48;
	private float multiplier = 9.5F;
	private float minHeight = 0F;
	private float maxHeight = 1.5F;
	private byte subMaterial = (byte)Block.stone.blockID;
	private byte midMaterial = (byte)Block.whiteStone.blockID;
	private byte topMaterial = (byte)Block.ice.blockID;
	private byte basinMaterial = (byte)Block.gravel.blockID;
	private int exposed1 = Block.stone.blockID;
	private int exposed1Chance = 50;
	private int exposed2 = Block.whiteStone.blockID;
	private int exposed2Chance = 50;

    private Random rand;
    
    private NoiseGeneratorOctaves ESMnoiseGen1;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves ESMnoiseGen2;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves ESMnoiseGen3;

    /** A NoiseGeneratorOctaves used in generating terrain */
    private NoiseGeneratorOctaves ESMnoiseGen4;

    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves ESMnoiseGen5;

    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves ESMnoiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;

    /** Reference to the World object. */
    private World worldObj;

    /** are map structures going to be generated (e.g. strongholds) */
    private final boolean mapFeaturesEnabled;

    /** Holds the overall noise array used in chunk generation */
    private double[] noiseArray;
    private double[] stoneNoise = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

    /** Holds ravine generator */
    private MapGenBase ravineGenerator = new MapGenRavine();

    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] ESMbiomesForGeneration;

    /** A double array that hold terrain noise from noiseGen3 */
    double[] noise3;

    /** A double array that hold terrain noise */
    double[] noise1;

    /** A double array that hold terrain noise from noiseGen2 */
    double[] noise2;

    /** A double array that hold terrain noise from noiseGen5 */
    double[] noise5;

    /** A double array that holds terrain noise from noiseGen6 */
    double[] noise6;

    /**
     * Used to store the 5x5 parabolic field that is used during terrain generation.
     */
    float[] parabolicField;
    int[][] field_73219_j = new int[32][32];

    public ChunkProviderSpace(World par1World, long par2)
    {
        this.worldObj = par1World;
        this.mapFeaturesEnabled = true;
        this.rand = new Random(par2);
        
        this.ESMnoiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.ESMnoiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.ESMnoiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.ESMnoiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
        this.ESMnoiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.ESMnoiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }
	
	public void generateTerrain(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
	{
        byte var4 = 4;
        byte var5 = 16;//
        byte var6 = seaDepth;// Sea level
        int var7 = var4 + 1;
        byte var8 = 17;
        int var9 = var4 + 1;
        this.ESMbiomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.ESMbiomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, var7 + 5, var9 + 5);
        this.noiseArray = this.initializeNoiseField(this.noiseArray, par1 * var4, 0, par2 * var4, var7, var8, var9);
        
        for (int var10 = 0; var10 < var4; ++var10)
        {
            for (int var11 = 0; var11 < var4; ++var11)
            {
                for (int var12 = 0; var12 < var5; ++var12)
                {
                    double var13 = 0.125D;
                    double var15 = this.noiseArray[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 0];
                    double var17 = this.noiseArray[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 0];
                    double var19 = this.noiseArray[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 0];
                    double var21 = this.noiseArray[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 0];
                    double var23 = (this.noiseArray[((var10 + 0) * var9 + var11 + 0) * var8 + var12 + 1] - var15) * var13;
                    double var25 = (this.noiseArray[((var10 + 0) * var9 + var11 + 1) * var8 + var12 + 1] - var17) * var13;
                    double var27 = (this.noiseArray[((var10 + 1) * var9 + var11 + 0) * var8 + var12 + 1] - var19) * var13;
                    double var29 = (this.noiseArray[((var10 + 1) * var9 + var11 + 1) * var8 + var12 + 1] - var21) * var13;
                    
                    for (int var31 = 0; var31 < 8; ++var31)
                    {
                        double var32 = 0.25D;
                        double var34 = var15;
                        double var36 = var17;
                        double var38 = (var19 - var15) * var32;
                        double var40 = (var21 - var17) * var32;
                        for (int var42 = 0; var42 < 4; ++var42)
                        {
                            int var43 = var42 + var10 * 4 << 11 | 0 + var11 * 4 << 7 | var12 * 8 + var31;
                            short var44 = 128;
                            var43 -= var44;
                            double var45 = 0.25D;
                            double var49 = (var36 - var34) * var45;
                            double var47 = var34 - var49;

                            for (int var51 = 0; var51 < 4; ++var51)
                            {
                                if ((var47 += var49) > 0.0D)
                                {
                                    par3ArrayOfByte[var43 += var44] = 0; //Sub terrain material
                                }
                                else if (var12 * multiplier + var31 < var6)
                                {
                                    par3ArrayOfByte[var43 += var44] = subMaterial; //Sea water material
                                }
                                else
                                {
                                    par3ArrayOfByte[var43 += var44] = 0;
                                }
                            }

                            var34 += var38;
                            var36 += var40;
                        }

                        var15 += var23;
                        var17 += var25;
                        var19 += var27;
                        var21 += var29;
                    }
                }
            }
        }
	}

    public void replaceBlocksForBiome(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        byte var5 = 63;
        double var6 = 0.03125D;
        this.stoneNoise = this.ESMnoiseGen4.generateNoiseOctaves(this.stoneNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, var6 * 2.0D, var6 * 2.0D, var6 * 2.0D);

        for (int var8 = 0; var8 < 16; ++var8)
        {
            for (int var9 = 0; var9 < 16; ++var9)
            {
                BiomeGenBase var10 = par4ArrayOfBiomeGenBase[var9 + var8 * 16];
                float var11 = var10.getFloatTemperature();
                int var12 = (int)(this.stoneNoise[var8 + var9 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int var13 = -1;
                byte var14 = topMaterial; // Top Block
                byte var15 = midMaterial; // Filler Block

                for (int var16 = 127; var16 >= 0; --var16)
                {
                    int var17 = (var9 * 16 + var8) * 128 + var16;

                    if (var16 <= 0 + this.rand.nextInt(5))
                    {
                        par3ArrayOfByte[var17] = 0; // Bedrock
                    }
                    else
                    {
                        byte var18 = par3ArrayOfByte[var17];

                        if (var18 == 0)
                        {
                            var13 = -1;
                        }
                        else if (var18 == subMaterial)
                        {
                            if (var13 == -1)
                            {
                                if (var12 <= 0) // Exposed rock
                                {
                                    var14 = 0;
                                    var15 = (byte)Block.whiteStone.blockID;
                                }
                                else if (var16 >= var5 - 4 && var16 <= var5 + 1)
                                {
                                    var14 = topMaterial; // Top Block
                                    var15 = midMaterial; // Filler Block
                                }

                                if (var16 < var5 && var14 == 0)
                                {
                                    if (var11 < 0.15F)
                                    {
                                        var14 = basinMaterial;
                                    }
                                    else
                                    {
                                        var14 = basinMaterial;
                                    }
                                }

                                var13 = var12;

                                if (var16 >= var5 - 1)
                                {
                                    par3ArrayOfByte[var17] = var14;
                                }
                                else
                                {
                                    par3ArrayOfByte[var17] = var15;
                                }
                            }
                            else if (var13 > 0)
                            {
                                --var13;
                                par3ArrayOfByte[var17] = var15;

                                if (var13 == 0 && var15 == Block.sand.blockID)
                                {
                                    var13 = this.rand.nextInt(4);
                                    var15 = (byte)Block.sandStone.blockID;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
	public Chunk loadChunk(int i, int j)
	{
        return this.provideChunk(i, j);
	}
	
	public Chunk provideChunk(int i, int j)
	{
        this.rand.setSeed((long)i * 341873128712L + (long)j * 132897987541L);
        byte[] var3 = new byte[32768];
        this.ESMbiomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.ESMbiomesForGeneration, i * 16, j * 16, 16, 16);
        this.generateTerrain(i, j, var3, this.ESMbiomesForGeneration);
        this.replaceBlocksForBiome(i, j, var3, this.ESMbiomesForGeneration);
        this.caveGenerator.generate(this, this.worldObj, i, j, var3);
        this.ravineGenerator.generate(this, this.worldObj, i, j, var3);
        
        this.scatteredFeatureGenerator.generate(this, this.worldObj, i, j, var3);

        Chunk var4 = new Chunk(this.worldObj, var3, i, j);
        byte[] var5 = var4.getBiomeArray();

        for (int var6 = 0; var6 < var5.length; ++var6)
        {
            var5[var6] = (byte)this.ESMbiomesForGeneration[var6].biomeID;
        }

        var4.generateSkylightMap();
        return var4;
	}

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        if (this.parabolicField == null)
        {
            this.parabolicField = new float[25];

            for (int var8 = -2; var8 <= 2; ++var8)
            {
                for (int var9 = -2; var9 <= 2; ++var9)
                {
                    float var10 = 10.0F / MathHelper.sqrt_float((float)(var8 * var8 + var9 * var9) + 0.2F);
                    this.parabolicField[var8 + 2 + (var9 + 2) * 5] = var10;
                }
            }
        }

        double var44 = 684.412D;
        double var45 = 684.412D;
        this.noise5 = this.ESMnoiseGen5.generateNoiseOctaves(this.noise5, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
        this.noise6 = this.ESMnoiseGen6.generateNoiseOctaves(this.noise6, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
        this.noise3 = this.ESMnoiseGen3.generateNoiseOctaves(this.noise3, par2, par3, par4, par5, par6, par7, var44 / 80.0D, var45 / 160.0D, var44 / 80.0D);
        this.noise1 = this.ESMnoiseGen1.generateNoiseOctaves(this.noise1, par2, par3, par4, par5, par6, par7, var44, var45, var44);
        this.noise2 = this.ESMnoiseGen2.generateNoiseOctaves(this.noise2, par2, par3, par4, par5, par6, par7, var44, var45, var44);
        int var12 = 0;
        int var13 = 0;

        for (int var14 = 0; var14 < par5; ++var14)
        {
            for (int var15 = 0; var15 < par7; ++var15)
            {
                float var16 = 0.0F;
                float var17 = 0.0F;
                float var18 = 0.0F;
                byte var19 = 2;
                BiomeGenBase var20 = this.ESMbiomesForGeneration[var14 + 2 + (var15 + 2) * (par5 + 5)];

                for (int var21 = -var19; var21 <= var19; ++var21)
                {
                    for (int var22 = -var19; var22 <= var19; ++var22)
                    {
                        BiomeGenBase var23 = this.ESMbiomesForGeneration[var14 + var21 + 2 + (var15 + var22 + 2) * (par5 + 5)];
                        float var24 = this.parabolicField[var21 + 2 + (var22 + 2) * 5] / (var23.minHeight + 2.0F);

                        if (var23.minHeight > var20.minHeight)
                        {
                            var24 /= 2.0F;
                        }

                        var16 += maxHeight * var24;//var23.maxHeight * var24;
                        var17 += minHeight * var24;//var23.minHeight * var24;
                        var18 += var24;
                    }
                }

                var16 /= var18;
                var17 /= var18;
                var16 = var16 * 0.9F + 0.1F;
                var17 = (var17 * 4.0F - 1.0F) / 8.0F;
                double var47 = this.noise6[var13] / 8000.0D;

                if (var47 < 0.0D)
                {
                    var47 = -var47 * 0.3D;
                }

                var47 = var47 * 3.0D - 2.0D;

                if (var47 < 0.0D)
                {
                    var47 /= 2.0D;

                    if (var47 < -1.0D)
                    {
                        var47 = -1.0D;
                    }

                    var47 /= 1.4D;
                    var47 /= 2.0D;
                }
                else
                {
                    if (var47 > 1.0D)
                    {
                        var47 = 1.0D;
                    }

                    var47 /= 8.0D;
                }

                ++var13;

                for (int var46 = 0; var46 < par6; ++var46)
                {
                    double var48 = (double)var17;
                    double var26 = (double)var16;
                    var48 += var47 * 0.2D;
                    var48 = var48 * (double)par6 / 16.0D;
                    double var28 = (double)par6 / 2.0D + var48 * 4.0D;
                    double var30 = 0.0D;
                    double var32 = ((double)var46 - var28) * 12.0D * 128.0D / 128.0D / var26;

                    if (var32 < 0.0D)
                    {
                        var32 *= 4.0D;
                    }

                    double var34 = this.noise1[var12] / 512.0D;
                    double var36 = this.noise2[var12] / 512.0D;
                    double var38 = (this.noise3[var12] / 10.0D + 1.0D) / 2.0D;

                    if (var38 < 0.0D)
                    {
                        var30 = var34;
                    }
                    else if (var38 > 1.0D)
                    {
                        var30 = var36;
                    }
                    else
                    {
                        var30 = var34 + (var36 - var34) * var38;
                    }

                    var30 -= var32;

                    if (var46 > par6 - 4)
                    {
                        double var40 = (double)((float)(var46 - (par6 - 4)) / 3.0F);
                        var30 = var30 * (1.0D - var40) + -10.0D * var40;
                    }

                    par1ArrayOfDouble[var12] = var30;
                    ++var12;
                }
            }
        }

        return par1ArrayOfDouble;
    }

	@Override
	public boolean chunkExists(int i, int j)
	{
		return true;
	}

	@Override
	public void populate(IChunkProvider ichunkprovider, int par2, int par3)
	{
        BlockSand.fallInstantly = true;
        int var4 = par2 * 16;
        int var5 = par3 * 16;
        this.rand.setSeed(this.worldObj.getSeed());
        long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)par2 * var7 + (long)par3 * var9 ^ this.worldObj.getSeed());
        boolean var11 = false;
        
        this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);

        int var12;
        int var13;
        int var14;
        
        for(int i = 0; i <= exposed1Chance; i ++)
        {
	        if (!var11 && this.rand.nextInt(2) == 0)
	        {
	            var12 = var4 + this.rand.nextInt(16) + 8;
	            var13 = this.rand.nextInt(seaDepth);
	            var14 = var5 + this.rand.nextInt(16) + 8;
	            if(var13 > seaLevel - 4)
	            {
	            	(new WorldGenLakes(exposed1)).generate(this.worldObj, this.rand, var12, var13, var14);
	            }
	        }
        }
        
        for(int i = 0; i <= exposed2Chance; i ++)
        {
	        if (!var11 && this.rand.nextInt(2) == 0)
	        {
	            var12 = var4 + this.rand.nextInt(16) + 8;
	            var13 = this.rand.nextInt(seaDepth);
	            var14 = var5 + this.rand.nextInt(16) + 8;
	            if(var13 > seaLevel - 4)
	            {
	            	(new WorldGenLakes(exposed2)).generate(this.worldObj, this.rand, var12, var13, var14);
	            }
	        }
        }
        
        WorldGenerator ironGen = new WorldGenMinable(Block.oreIron.blockID, 8);
        WorldGenerator goldGen = new WorldGenMinable(Block.oreGold.blockID, 8);
        WorldGenerator diamondGen = new WorldGenMinable(Block.oreDiamond.blockID, 7);
        WorldGenerator redGen = new WorldGenMinable(Block.oreRedstone.blockID, 7);
        WorldGenerator coalGen = new WorldGenMinable(Block.oreCoal.blockID,12);
        WorldGenerator silverfishGen = new WorldGenMinable(Block.silverfish.blockID,12);
        for (int var05 = 0; var05 < seaLevel; ++var05)
        {
        	if(rand.nextInt(7) == 0)
        	{
	            int var06 = var4 + this.rand.nextInt(16);
	            int var07 = mineralDepth + this.rand.nextInt((seaLevel - mineralDepth));
	            int var08 = var5 + this.rand.nextInt(16);
	            ironGen.generate(this.worldObj, this.rand, var06, var07, var08);
        	}
        }
        for (int var05 = 0; var05 < seaLevel; ++var05)
        {
        	if(rand.nextInt(7) == 0)
        	{
	            int var06 = var4 + this.rand.nextInt(16);
	            int var07 = mineralDepth + this.rand.nextInt((seaLevel - mineralDepth));
	            int var08 = var5 + this.rand.nextInt(16);
	            coalGen.generate(this.worldObj, this.rand, var06, var07, var08);
        	}
        }
        for (int var05 = 0; var05 < seaLevel; ++var05)
        {
        	if(rand.nextInt(3) == 0)
        	{
	            int var06 = var4 + this.rand.nextInt(16);
	            int var07 = mineralDepth + this.rand.nextInt((seaLevel - mineralDepth));
	            int var08 = var5 + this.rand.nextInt(16);
	            silverfishGen.generate(this.worldObj, this.rand, var06, var07, var08);
        	}
        }
        for (int var05 = 0; var05 < seaLevel; ++var05)
        {
        	if(rand.nextInt(15) == 0)
        	{
	            int var06 = var4 + this.rand.nextInt(16);
	            int var07 = mineralDepth + this.rand.nextInt((seaLevel - mineralDepth)/2);
	            int var08 = var5 + this.rand.nextInt(16);
	            goldGen.generate(this.worldObj, this.rand, var06, var07, var08);
        	}
        }
        for (int var05 = 0; var05 < seaLevel; ++var05)
        {
        	if(rand.nextInt(15) == 0)
        	{
	            int var06 = var4 + this.rand.nextInt(16);
	            int var07 = mineralDepth + this.rand.nextInt((seaLevel - mineralDepth)/2);
	            int var08 = var5 + this.rand.nextInt(16);
	            redGen.generate(this.worldObj, this.rand, var06, var07, var08);
        	}
        }
        for (int var05 = 0; var05 < seaLevel; ++var05)
        {
        	if(rand.nextInt(47) == 0)
        	{
	            int var06 = var4 + this.rand.nextInt(16);
	            int var07 = mineralDepth + this.rand.nextInt((seaLevel - mineralDepth)/2);
	            int var08 = var5 + this.rand.nextInt(16);
	            diamondGen.generate(this.worldObj, this.rand, var06, var07, var08);
        	}
        }

        var4 += 8;
        var5 += 8;

        for (var12 = 0; var12 < 16; ++var12)
        {
            for (var13 = 0; var13 < 16; ++var13)
            {
                var14 = this.worldObj.getPrecipitationHeight(var4 + var12, var5 + var13);

                if (this.worldObj.isBlockFreezable(var12 + var4, var14 - 1, var13 + var5))
                {
                    this.worldObj.setBlock(var12 + var4, var14 - 1, var13 + var5, Block.ice.blockID);
                }

                if (this.worldObj.canSnowAt(var12 + var4, var14, var13 + var5))
                {
                    this.worldObj.setBlock(var12 + var4, var14, var13 + var5, Block.snow.blockID);
                }
            }
        }

        BlockSand.fallInstantly = false;
	}
	
	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate)
	{
		return true;
	}
	
	public boolean unloadQueuedChunks()
	{
		return false;
	}
	
	public boolean canSave()
	{
		return true;
	}
	
	public String makeString()
	{
		return "RandomLevelSource";
	}
	
	public List getPossibleCreatures(EnumCreatureType enumcreaturetype, int i, int j, int k)
	{
		BiomeGenBase var5 = this.worldObj.getBiomeGenForCoords(i, k);
        return var5 == null ? null : var5.getSpawnableList(enumcreaturetype);
	}
	
	public ChunkPosition findClosestStructure(World world, String s, int i, int j, int k)
	{
		return null;
	}
	
	public int getLoadedChunkCount()
	{
		return 0;
	}
	
	public void recreateStructures(int i, int j)
	{
		this.scatteredFeatureGenerator.generate(this, this.worldObj, i, j, (byte[])null);
	}
	
	public void saveExtraData() {}
}
