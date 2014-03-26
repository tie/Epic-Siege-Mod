package funwayguy.esm.world.dimensions.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.gen.structure.ComponentScatteredFeatureSwampHut;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureScatteredFeatureStart;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenScatteredFortress extends MapGenStructure
{
    private static List biomelist = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland});

    /** contains possible spawns for scattered features */
    private List scatteredFortSpawnList;

    /** the maximum distance between scattered features */
    private int maxDistanceBetweenScatteredForts;

    /** the minimum distance between scattered features */
    private int minDistanceBetweenScatteredForts;

    public MapGenScatteredFortress()
    {
        this.scatteredFortSpawnList = new ArrayList();
        this.maxDistanceBetweenScatteredForts = 32;
        this.minDistanceBetweenScatteredForts = 8;
    }

    public MapGenScatteredFortress(Map par1Map)
    {
        this();
        Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();

            if (((String)entry.getKey()).equals("distance"))
            {
                this.minDistanceBetweenScatteredForts = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.maxDistanceBetweenScatteredForts, this.minDistanceBetweenScatteredForts + 1);
            }
        }
    }

    public String func_143025_a()
    {
        return "Temple";
    }

    protected boolean canSpawnStructureAtCoords(int par1, int par2)
    {
        int k = par1;
        int l = par2;

        if (par1 < 0)
        {
            par1 -= this.maxDistanceBetweenScatteredForts - 1;
        }

        if (par2 < 0)
        {
            par2 -= this.maxDistanceBetweenScatteredForts - 1;
        }

        int i1 = par1 / this.maxDistanceBetweenScatteredForts;
        int j1 = par2 / this.maxDistanceBetweenScatteredForts;
        Random random = this.worldObj.setRandomSeed(i1, j1, 14357617);
        i1 *= this.maxDistanceBetweenScatteredForts;
        j1 *= this.maxDistanceBetweenScatteredForts;
        i1 += random.nextInt(this.maxDistanceBetweenScatteredForts - this.minDistanceBetweenScatteredForts);
        j1 += random.nextInt(this.maxDistanceBetweenScatteredForts - this.minDistanceBetweenScatteredForts);

        if (k == i1 && l == j1)
        {
            BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(k * 16 + 8, l * 16 + 8);
            Iterator iterator = biomelist.iterator();

            while (iterator.hasNext())
            {
                BiomeGenBase biomegenbase1 = (BiomeGenBase)iterator.next();

                if (biomegenbase == biomegenbase1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int par1, int par2)
    {
        return new StructureScatteredFeatureStart(this.worldObj, this.rand, par1, par2);
    }

    public boolean func_143030_a(int par1, int par2, int par3)
    {
        StructureStart structurestart = this.func_143028_c(par1, par2, par3);

        if (structurestart != null && structurestart instanceof StructureScatteredFeatureStart && !structurestart.components.isEmpty())
        {
            StructureComponent structurecomponent = (StructureComponent)structurestart.components.getFirst();
            return structurecomponent instanceof ComponentScatteredFeatureSwampHut;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns possible spawns for scattered features
     */
    public List getScatteredFeatureSpawnList()
    {
        return this.scatteredFortSpawnList;
    }
}
