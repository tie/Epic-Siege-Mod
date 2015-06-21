package funwayguy.esm.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.Level;
import funwayguy.esm.core.ESM;

public class NeatBrain
{
	EntityLiving entityLiving;
	Random rand;
	double minDist = 9999;
	double maxDist = -1;
	
	HashMap<String, Boolean> actions;
	final int viewRadius = 16;
	final int inputSize;
	
	int population = 200;
	float deltaDisjoint = 2.0F;
	float deltaWeights = 0.4F;
	float deltaThreshold = 1.0F;
	
	int staleSpecies = 15;
	
	float mutateConnectionChance = 0.25F;
	float preturbChance = 0.9F;
	float crossoverChance = 0.75F;
	float linkMutationChance = 2.0F;
	float nodeMutationChance = 0.5F;
	float biasMutationChance = 0.4F;
	float stepSize = .01F;
	float disableMutationChance = 0.4F;
	float enableMutationChance = 0.2F;
	
	int maxNodes = 1000;
	int timeout = 60;
	
	Pool curPool;
	
	public NeatBrain(EntityLiving entityLiving)
	{
		this.entityLiving = entityLiving;
		this.rand = entityLiving.getRNG();
		
		int tmp = viewRadius * 2;
		inputSize = tmp * tmp * tmp;
		
		actions = new HashMap<String, Boolean>();
		actions.put("NORTH", false);
		actions.put("SOUTH", false);
		actions.put("EAST", false);
		actions.put("WEST", false);
		actions.put("JUMP", false);
		
		Load();
		
		if(curPool == null)
		{
			InitPool();
		}
		
		Save();
	}
	
	public void TransferBrain(EntityLiving entityLiving)
	{
		this.entityLiving = entityLiving;
		this.rand = entityLiving.getRNG();
	}
	
	public void TickBrain()
	{
		if(entityLiving == null || entityLiving.isDead)
		{
			// Brain needs transfer to new host
			return;
		}
		
		Entity target = entityLiving.getAttackTarget();
		
		if(target == null)
		{
			// No target. Brain is idle
			return;
		}
		
		try
		{
			Species sp = curPool.species.get(curPool.curSpecies);
			Genome gn = sp.genomes.get(curPool.curGenome);
			
			if(curPool.curFrame%10 == 0)
			{
				this.Evaluate();
			}
			
			double dx = (actions.get("EAST")? 1D : 0D) + (actions.get("WEST")? -1D : 0D);
			double dz = (actions.get("NORTH")? 1D : 0D) + (actions.get("SOUTH")? -1D : 0D);
			double spd = entityLiving.getAIMoveSpeed();
			entityLiving.moveEntity(dx * spd, 0D, dz * spd);
			if(actions.get("JUMP"))
			{
				entityLiving.getJumpHelper().setJumping();;
			}
			
			if(maxDist < 0D)
			{
				maxDist = entityLiving.getDistanceToEntity(target);
			}
			
			if(target != null && entityLiving.getDistanceToEntity(target) < this.minDist)
			{
				minDist = entityLiving.getDistanceToEntity(target);
				timeout = 60;
			} else
			{
				timeout--;
			}
			
			if(timeout <= Math.min(0D, (maxDist - minDist) * -20D))
			{
				System.out.println("Brain genome timed out at " + timeout);
				int fitness = MathHelper.ceiling_double_int((maxDist - minDist)/curPool.curFrame);
				if(fitness == 0)
				{
					fitness = -1;
				}
				
				gn.fitness = fitness;
				
				if(fitness > curPool.maxFitness)
				{
					curPool.maxFitness = fitness;
				}
				
				curPool.curSpecies = 0;
				curPool.curGenome = 0;
				
				while(FitnessMeasured())
				{
					NextGenome();
				}
				System.out.println("Starting run: Generation " + curPool.generation + " on species " + curPool.curSpecies + "/" + curPool.species.size() + " genome " + curPool.curGenome + "/" + sp.genomes.size());
				InitRun();
				//Save(); // Causes way too much overhead to do it here
			}
		} catch(Exception e)
		{
			ESM.log.log(Level.ERROR, "An error occured while ticking the NEAT brain:", e);
		}
		
		curPool.curFrame++;
	}
	
	public void Save()
	{
		if(curPool == null)
		{
			return;
		}
		
		try
		{
			CompressedStreamTools.write(curPool.saveNBT(), MinecraftServer.getServer().getFile("neat_brain.dat"));
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void Load()
	{
		try
		{
			NBTTagCompound tags = CompressedStreamTools.read(MinecraftServer.getServer().getFile("neat_brain.dat"));
			if(tags == null)
			{
				return;
			}
			Pool pool = new Pool(this);
			pool.readNBT(tags);
			
			if(pool.species.size() > 0)
			{
				curPool = pool;
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// ===== BRAIN UTILITIES =====
	
	public int[] GetInputs()
	{
		int[] inputs = new int[inputSize];
		int ex = MathHelper.floor_double(entityLiving.posX);
		int ey = MathHelper.floor_double(entityLiving.posY);
		int ez = MathHelper.floor_double(entityLiving.posZ);
		int viewD = viewRadius*2;
		
		for(int i = 0; i < inputs.length; i++)
		{
			int x = ex + i%viewD - viewRadius;
			int y = ey + i/(viewD^2) - viewRadius;
			int z = ez + (i%(viewD^2))/viewD - viewRadius;
			
			Block block = entityLiving.worldObj.getBlock(x, y, z);
			
			if(block.getCollisionBoundingBoxFromPool(entityLiving.worldObj, x, y, z) != null)
			{
				inputs[i] = -1;
			} else
			{
				inputs[i] = 0;
			}
		}
		
		Entity tar = entityLiving.getAttackTarget();
		if(tar != null)
		{
			int tx = MathHelper.floor_double(tar.posX) - (int)(entityLiving.posX - viewRadius);
			int ty = MathHelper.floor_double(tar.posY) - (int)(entityLiving.posY - viewRadius);
			int tz = MathHelper.floor_double(tar.posZ) - (int)(entityLiving.posZ - viewRadius);
			tx = MathHelper.clamp_int(tx, 0, viewD*2 - 1);
			ty = MathHelper.clamp_int(ty, 0, viewD*2 - 1);
			tz = MathHelper.clamp_int(tz, 0, viewD*2 - 1);
			
			int idx = ty*(viewD^2) + tz*viewD + tx;
			inputs[idx] = 1;
		}
		
		return inputs;
	}
	
	public double Sigmoid(double x)
	{
		return 2D/(1 + Math.exp(-4.9D*x)) - 1D;
	}
	
	public Genome Crossover(Genome a, Genome b)
	{
		if(b.fitness > a.fitness)
		{
			Genome tmp = a;
			a = b;
			b = tmp;
		}
		
		Genome child = new Genome(this);
		HashMap<Integer, Gene> innovMap = new HashMap<Integer, Gene>();
		
		for(int i = 0; i < b.genes.size(); i++)
		{
			Gene g = b.genes.get(i);
			innovMap.put(g.innovation, g);
		}
		
		for(int i = 0; i < a.genes.size(); i++)
		{
			Gene g1 = a.genes.get(i);
			Gene g2 = innovMap.get(g1.innovation);
			if(g2 != null && g2.enable && rand.nextBoolean())
			{
				child.genes.add(g2.copy());
			} else
			{
				child.genes.add(g1.copy());
			}
		}
		
		child.maxNeuron = Math.max(a.maxNeuron, b.maxNeuron);
		
		child.InheritRates(a);
		
		return child;
	}
	
	public int RandomNeuron(ArrayList<Gene> genes, boolean nonInput)
	{
		ArrayList<Integer> valid = new ArrayList<Integer>();
		
		if(!nonInput)
		{
			for(int i = 0; i < this.inputSize; i++)
			{
				valid.add(i);
			}
		}
		
		for(int i = 0; i < this.actions.size(); i++)
		{
			valid.add(this.maxNodes + i);
		}
		
		for(Gene g : genes)
		{
			if(!nonInput || g.into >= this.inputSize)
			{
				valid.add(g.into);
			}
			if(!nonInput || g.out >= this.inputSize)
			{
				valid.add(g.out);
			}
		}
		
		return valid.get(rand.nextInt(valid.size()));
	}
	
	public boolean ContainsLink(ArrayList<Gene> genes, Gene link)
	{
		for(Gene g : genes)
		{
			if(g.into == link.into && g.out == link.out)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public float Disjoint(ArrayList<Gene> genes1, ArrayList<Gene> genes2)
	{
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(Gene g : genes1)
		{
			if(!tmp.contains(g.innovation))
			{
				tmp.add(g.innovation);
			}
		}
		
		for(Gene g : genes2)
		{
			if(!tmp.add(g.innovation))
			{
				tmp.add(g.innovation);
			} else
			{
				tmp.remove((Integer)g.innovation);
			}
		}
		
		return tmp.size() / (float)Math.max(genes1.size(), genes2.size());
	}
	
	public float Weights(ArrayList<Gene> genes1, ArrayList<Gene> genes2)
	{
		HashMap<Integer, Gene> tmp = new HashMap<Integer, Gene>();
		for(Gene g : genes2)
		{
			tmp.put(g.innovation, g);
		}
		
		float sum = 0;
		int coincident = 0;
		for(Gene g : genes1)
		{
			if(tmp.containsKey(g.innovation))
			{
				Gene g1 = tmp.get(g.innovation);
				sum += Math.abs(g.weight - g1.weight);
				coincident++;
			}
		}
		
		return sum / (float)coincident;
	}
	
	public boolean SameSpecies(Genome a, Genome b)
	{
		float dd = this.deltaDisjoint * this.Disjoint(a.genes, b.genes);
		float dw = this.deltaWeights * this.Weights(a.genes, b.genes);
		return dd + dw < this.deltaThreshold;
	}
	
	public void RankGlobally()
	{
		ArrayList<Genome> global = new ArrayList<Genome>();
		for(Species sp : this.curPool.species)
		{
			for(Genome gn : sp.genomes)
			{
				global.add(gn);
			}
		}
		
		Collections.sort(global, new GenomeComparator());
		
		for(int i = 0; i < global.size(); i++)
		{
			global.get(i).globalRank = i;
		}
	}
	
	public int TotalAverageFitness()
	{
		int total = 0;
		for(Species sp : this.curPool.species)
		{
			total += sp.avgFitness;
		}
		return total;
	}
	
	public void CullSpecies(boolean cutToOne)
	{
		for(Species sp : this.curPool.species)
		{
			Collections.sort(sp.genomes, new GenomeComparator());
			
			int remaining = cutToOne? 1 : (int)Math.ceil(sp.genomes.size()/2D);
			
			while(sp.genomes.size() > remaining)
			{
				sp.genomes.remove(sp.genomes.size() - 1);
			}
		}
	}
	
	public void RemoveStaleSpecies()
	{
		ArrayList<Species> survived = new ArrayList<Species>();
		
		for(Species sp : this.curPool.species)
		{
			Collections.sort(sp.genomes, new GenomeComparator());
			
			if(sp.genomes.get(0).fitness > sp.topFitness)
			{
				sp.topFitness = sp.genomes.get(0).fitness;
				sp.staleness = 0;
			} else
			{
				sp.staleness++;
			}
			
			if(sp.staleness < this.staleSpecies || sp.topFitness >= this.curPool.maxFitness)
			{
				survived.add(sp);
			}
		}
		
		this.curPool.species = survived;
	}
	
	public void RemoveWeakSpecies()
	{
		ArrayList<Species> survived = new ArrayList<Species>();
		
		int sum = TotalAverageFitness();
		for(Species sp : curPool.species)
		{
			int breed = (int)Math.floor(sp.avgFitness / (sum * (double)this.population));
			if(breed >= 1)
			{
				survived.add(sp);
			}
		}
		
		curPool.species = survived;
	}
	
	public void AddToSpecies(Genome child)
	{
		for(Species sp : curPool.species)
		{
			if(SameSpecies(child, sp.genomes.get(0)))
			{
				sp.genomes.add(child);
				return;
			}
		}
		
		Species sp = new Species(this);
		sp.genomes.add(child);
		curPool.species.add(sp);
	}
	
	public void NewGeneration()
	{
		System.out.println("Creating new generation...");
		CullSpecies(false);
		RankGlobally();
		RemoveStaleSpecies();
		RankGlobally();
		for(Species sp : curPool.species)
		{
			sp.CalcAverageFitness();
		}
		RemoveWeakSpecies();
		if(curPool.species.size() <= 0)
		{
			System.out.println("Pool has no suitable parents for new generation! Starting over...");
			InitPool();
		}
		int sum = TotalAverageFitness();
		ArrayList<Genome> children = new ArrayList<Genome>();
		for(Species sp : curPool.species)
		{
			int breed = (int)Math.floor(sp.avgFitness / (sum * (double)this.population)) - 1;
			for(int i = 0; i < breed; i++)
			{
				children.add(sp.BreedChild());
			}
		}
		CullSpecies(true);
		while(children.size() + curPool.species.size() < this.population)
		{
			Species sp = curPool.species.get(rand.nextInt(curPool.species.size()));
			children.add(sp.BreedChild());
		}
		
		for(Genome gn : children)
		{
			AddToSpecies(gn);
		}
		
		curPool.generation++;
		
		Save();
	}
	
	public void ClearControls()
	{
		for(String key : actions.keySet())
		{
			actions.put(key, false);
		}
	}
	
	public void InitPool()
	{
		curPool = new Pool(this);
		for(int i = 0; i < population; i++)
		{
			AddToSpecies(Genome.Basic(this));
		}
		InitRun();
	}
	
	public void InitRun()
	{
		minDist = 9999;
		maxDist = -1;
		curPool.curFrame = 0;
		ClearControls();
		Species sp = curPool.species.get(curPool.curSpecies);
		Genome gn = sp.genomes.get(curPool.curGenome);
		gn.network = new Network(this);
		gn.network.Generate(gn);
		Evaluate();
	}
	
	public void Evaluate()
	{
		Species sp = curPool.species.get(curPool.curSpecies);
		Genome gn = sp.genomes.get(curPool.curGenome);
		
		int[] inputs = GetInputs();
		gn.network.Evaluate(inputs);
	}
	
	public void NextGenome()
	{
		curPool.curGenome++;
		
		if(curPool.curGenome >= curPool.species.get(curPool.curSpecies).genomes.size())
		{
			curPool.curGenome = 0;
			curPool.curSpecies++;
			if(curPool.curSpecies >= curPool.species.size())
			{
				NewGeneration();
				curPool.curSpecies = 0;
			}
		}
	}
	
	public boolean FitnessMeasured()
	{
		Species sp = curPool.species.get(curPool.curSpecies);
		Genome gn = sp.genomes.get(curPool.curGenome);
		return gn.fitness != 0;
	}
	
	public void PlayTop()
	{
		int maxFit = 0;
		int maxSp = 0;
		int maxGn = 0;
		
		for(int i = 0; i < curPool.species.size(); i++)
		{
			Species sp = curPool.species.get(i);
			
			for(int j = 0; j < sp.genomes.size(); j++)
			{
				Genome gn = sp.genomes.get(j);
				if(gn.fitness > maxFit)
				{
					maxFit = gn.fitness;
					maxSp = i;
					maxGn = j;
				}
			}
		}
		
		curPool.curSpecies = maxSp;
		curPool.curGenome = maxGn;
		curPool.maxFitness = maxFit;
		InitRun();
		curPool.curFrame++;
	}
	
	public static class GenomeComparator implements Comparator<Genome>
	{
		@Override
		public int compare(Genome g1, Genome g2)
		{
			if(g1.fitness < g2.fitness)
			{
				return 1;
			} else if(g1.fitness > g2.fitness)
			{
				return -1;
			} else
			{
				return 0;
			}
		}
	}
	
	public static class GeneComparator implements Comparator<Gene>
	{
		@Override
		public int compare(Gene g1, Gene g2)
		{
			if(g1.out < g2.out)
			{
				return 1;
			} else if(g1.out > g2.out)
			{
				return -1;
			} else
			{
				return 0;
			}
		}
		
	}
	
	// =====  BRAIN CLASSES  =====
	
	public static class Pool
	{
		final NeatBrain brain;
		
		ArrayList<Species> species = new ArrayList<Species>();
		public int generation = 0;
		public int innovation = 0;
		public int curSpecies = 0;
		public int curGenome = 0;
		public int curFrame = 0;
		public int maxFitness = 0;
		
		public Pool(NeatBrain brain)
		{
			this.brain = brain;
			innovation = brain.actions.size();
		}
		
		public int NewInnovation()
		{
			innovation++;
			return innovation;
		}
		
		public NBTTagCompound saveNBT()
		{
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger("generation", generation);
			tags.setInteger("innovation", innovation);
			tags.setInteger("curSpecies", curSpecies);
			tags.setInteger("curGenome", curGenome);
			tags.setInteger("curFrame", curFrame);
			tags.setInteger("maxFitness", maxFitness);
			
			NBTTagList spList = new NBTTagList();
			for(Species sp : species)
			{
				spList.appendTag(sp.saveNBT());
			}
			tags.setTag("species", spList);
			
			return tags;
		}
		
		public void readNBT(NBTTagCompound tags)
		{
			generation = tags.getInteger("generation");
			innovation = tags.getInteger("innovation");
			curSpecies = Math.max(1, tags.getInteger("curSpecies"));
			curGenome = Math.max(1, tags.getInteger("curGenome"));
			curFrame = tags.getInteger("curFrame");
			maxFitness = tags.getInteger("maxFitness");
			
			species.clear();
			NBTTagList psList = tags.getTagList("species", 10);
			for(int i = 0; i < psList.tagCount(); i++)
			{
				Species sp = new Species(brain);
				sp.readNBT(psList.getCompoundTagAt(i));
				species.add(sp);
			}
		}
	}
	
	public static class Species
	{
		final NeatBrain brain;
		
		public ArrayList<Genome> genomes = new ArrayList<Genome>();
		public int topFitness = 0;
		public int staleness = 0;
		public int avgFitness = 0;
		
		public Species(NeatBrain brain)
		{
			this.brain = brain;
		}
		
		public void CalcAverageFitness()
		{
			int total = 0;
			for(Genome gn : genomes)
			{
				total += gn.globalRank;
			}
			avgFitness = total / genomes.size();
		}
		
		public Genome BreedChild()
		{
			System.out.println("Breeding child...");
			Genome child;
			if(brain.rand.nextFloat() < brain.crossoverChance)
			{
				Genome g1 = genomes.get(brain.rand.nextInt(genomes.size()));
				Genome g2 = genomes.get(brain.rand.nextInt(genomes.size()));
				child = brain.Crossover(g1, g2);
			} else
			{
				Genome g = genomes.get(brain.rand.nextInt(genomes.size()));
				child = g.copy();
			}
			
			child.Mutate();
			return child;
		}
		
		public NBTTagCompound saveNBT()
		{
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger("topFitness", topFitness);
			tags.setInteger("staleness", staleness);
			tags.setInteger("avgFitness", avgFitness);
			
			NBTTagList gnList = new NBTTagList();
			for(Genome gn : genomes)
			{
				gnList.appendTag(gn.saveNBT());
			}
			tags.setTag("genomes", gnList);
			
			return tags;
		}
		
		public void readNBT(NBTTagCompound tags)
		{
			topFitness = tags.getInteger("topFitness");
			staleness = tags.getInteger("staleness");
			avgFitness = tags.getInteger("avgFitness");
			
			genomes.clear();
			NBTTagList gnList = tags.getTagList("genomes", 10);
			for(int i = 0; i < gnList.tagCount(); i++)
			{
				Genome gn = new Genome(brain);
				gn.readNBT(gnList.getCompoundTagAt(i));
				genomes.add(gn);
			}
		}
	}
	
	public static class Genome
	{
		final NeatBrain brain;
		
		public ArrayList<Gene> genes = new ArrayList<Gene>();
		public int fitness = 0;
		public int adjFitness = 0;
		public Network network;
		public int maxNeuron = 0;
		public int globalRank = 0;
		
		public float rateConnection;
		public float rateLink;
		public float rateBias;
		public float rateNode;
		public float rateEnable;
		public float rateDisable;
		public float rateStep;
		
		public Genome(NeatBrain brain)
		{
			this.brain = brain;
			network = new Network(brain);
			rateConnection = brain.mutateConnectionChance;
			rateLink = brain.linkMutationChance;
			rateBias = brain.biasMutationChance;
			rateNode = brain.nodeMutationChance;
			rateEnable = brain.enableMutationChance;
			rateDisable = brain.disableMutationChance;
			rateStep = brain.stepSize;
		}
		
		public static Genome Basic(NeatBrain brain)
		{
			Genome gn = new Genome(brain);
			gn.maxNeuron = brain.inputSize;
			gn.Mutate();
			return gn;
		}
		
		public void PointMutate()
		{
			for(Gene g : genes)
			{
				if(brain.rand.nextFloat() < brain.preturbChance)
				{
					g.weight = g.weight + brain.rand.nextFloat()*rateStep*2F - rateStep;
				} else
				{
					g.weight = brain.rand.nextFloat()*4F - 2F;
				}
			}
		}
		
		public void LinkMutate(boolean forceBias)
		{
			int n1 = brain.RandomNeuron(genes, false);
			int n2 = brain.RandomNeuron(genes, true);
			
			Gene link = new Gene(brain);
			
			if(n1 < brain.inputSize && n2 < brain.inputSize)
			{
				return;
			}
			
			if(n2 < brain.inputSize)
			{
				int tmp = n1;
				n1 = n2;
				n2 = tmp;
			}
			
			link.into = n1;
			link.out = n2;
			if(forceBias)
			{
				link.into = brain.inputSize;
			}
			
			if(brain.ContainsLink(genes, link))
			{
				return;
			}
			
			link.innovation = brain.curPool.NewInnovation();
			link.weight = brain.rand.nextFloat()*4F - 2F;
			genes.add(link);
		}
		
		public void NodeMutate()
		{
			if(genes.size() <= 0)
			{
				return;
			}
			
			this.maxNeuron++;
			Gene g = genes.get(brain.rand.nextInt(genes.size()));
			if(g == null || !g.enable)
			{
				return;
			}
			g.enable = false;
			
			Gene g1 = g.copy();
			g1.out = maxNeuron;
			g1.weight = 1F;
			g1.innovation = brain.curPool.NewInnovation();
			g1.enable = true;
			genes.add(g1);
			
			Gene g2 = g.copy();
			g2.into = maxNeuron;
			g2.innovation = brain.curPool.NewInnovation();
			g2.enable = true;
			genes.add(g2);
		}
		
		public void SetMutate(boolean state)
		{
			ArrayList<Gene> tmp = new ArrayList<Gene>();
			for(Gene g : genes)
			{
				if(g != null && g.enable != state)
				{
					tmp.add(g);
				}
			}
			
			if(tmp.size() <= 0)
			{
				return;
			}
			
			tmp.get(brain.rand.nextInt(tmp.size())).enable = state;
		}
		
		public void Mutate()
		{
			rateConnection *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			rateLink *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			rateBias *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			rateNode *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			rateEnable *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			rateDisable *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			rateStep *= (brain.rand.nextBoolean()? 0.95F : 1.05263F);
			
			if(brain.rand.nextFloat() < rateConnection)
			{
				this.PointMutate();
			}
			
			for(float i = rateLink; i > 0F; i -= 1F)
			{
				if(brain.rand.nextFloat() < i)
				{
					this.LinkMutate(false);
				}
			}
			
			for(float i = rateBias; i > 0F; i -= 1F)
			{
				if(brain.rand.nextFloat() < i)
				{
					this.LinkMutate(true);
				}
			}
			
			for(float i = rateNode; i > 0F; i -= 1F)
			{
				if(brain.rand.nextFloat() < i)
				{
					this.NodeMutate();
				}
			}
			
			for(float i = rateEnable; i > 0F; i -= 1F)
			{
				if(brain.rand.nextFloat() < i)
				{
					this.SetMutate(true);
				}
			}
			
			for(float i = rateDisable; i > 0F; i -= 1F)
			{
				if(brain.rand.nextFloat() < i)
				{
					this.SetMutate(false);
				}
			}
		}
		
		public Genome copy()
		{
			Genome gn = new Genome(brain);
			
			gn.maxNeuron = maxNeuron;
			
			for(Gene g : genes)
			{
				gn.genes.add(g.copy());
			}
			
			gn.InheritRates(this);
			
			return gn;
		}
		
		public void InheritRates(Genome gn)
		{
			rateConnection = gn.rateConnection;
			rateLink = gn.rateLink;
			rateBias = gn.rateBias;
			rateNode = gn.rateNode;
			rateEnable = gn.rateEnable;
			rateDisable = gn.rateDisable;
			rateStep = gn.rateStep;
		}
		
		public NBTTagCompound saveNBT()
		{
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger("fitness", fitness);
			tags.setInteger("adjFitness", adjFitness);
			tags.setInteger("maxNeuron", maxNeuron);
			tags.setInteger("globalRank", globalRank);
			tags.setTag("network", network.saveNBT());
			
			tags.setFloat("rateConnection", rateConnection);
			tags.setFloat("rateLink", rateLink);
			tags.setFloat("rateBias", rateBias);
			tags.setFloat("rateNode", rateNode);
			tags.setFloat("rateEnable", rateEnable);
			tags.setFloat("rateDisable", rateDisable);
			tags.setFloat("rateStep", rateStep);
			
			NBTTagList gList = new NBTTagList();
			for(Gene g : genes)
			{
				gList.appendTag(g.saveNBT());
			}
			tags.setTag("genes", gList);
			return tags;
		}
		
		@SuppressWarnings("unchecked")
		public void readNBT(NBTTagCompound tags)
		{
			fitness = tags.getInteger("fitness");
			adjFitness = tags.getInteger("adjFitness");
			maxNeuron = tags.getInteger("maxNeuron");
			globalRank = tags.getInteger("globalRank");
			network = new Network(brain);
			network.readNBT(tags.getCompoundTag("network"));
			
			rateConnection = tags.getFloat("rateConnection");
			rateLink = tags.getFloat("rateLink");
			rateBias = tags.getFloat("rateBias");
			rateNode = tags.getFloat("rateNode");
			rateEnable = tags.getFloat("rateEnable");
			rateDisable = tags.getFloat("rateDisable");
			rateStep = tags.getFloat("rateStep");
			
			genes.clear();
			NBTTagList gList = tags.getTagList("genes", 10);
			for(int i = 0; i < gList.tagCount(); i++)
			{
				Gene g = new Gene(brain);
				g.readNBT(gList.getCompoundTagAt(i));
				genes.add(g);
				
			}
		}
	}
	
	public static class Gene
	{
		final NeatBrain brain;
		
		public int into = 0;
		public int out = 0;
		public float weight = 0F;
		public boolean enable = true;
		public int innovation = 0;
		
		public Gene(NeatBrain brain)
		{
			this.brain = brain;
		}
		
		public Gene copy()
		{
			Gene g = new Gene(brain);
			g.readNBT(saveNBT());
			return g;
		}
		
		public NBTTagCompound saveNBT()
		{
			NBTTagCompound tags = new NBTTagCompound();
			tags.setInteger("into", into);
			tags.setInteger("ou", out);
			tags.setFloat("weight", weight);
			tags.setBoolean("enable", enable);
			tags.setInteger("innovation", innovation);
			return tags;
		}
		
		public void readNBT(NBTTagCompound tags)
		{
			into = tags.getInteger("into");
			out = tags.getInteger("out");
			weight = tags.getFloat("weight");
			enable = tags.getBoolean("enable");
			innovation = tags.getInteger("innovation");
		}
	}
	
	public static class Neuron
	{
		final NeatBrain brain;
		
		public ArrayList<Gene> incoming = new ArrayList<Gene>();
		public float value = 0F;
		
		public Neuron(NeatBrain brain)
		{
			this.brain = brain;
		}
		
		public NBTTagCompound saveNBT()
		{
			NBTTagCompound tags = new NBTTagCompound();
			tags.setFloat("value", value);
			
			NBTTagList iList = new NBTTagList();
			for(Gene g : incoming)
			{
				iList.appendTag(g.saveNBT());
			}
			tags.setTag("incoming", iList);
			
			return tags;
		}
		
		public void readNBT(NBTTagCompound tags)
		{
			value = tags.getFloat("value");
			
			incoming.clear();
			NBTTagList iList = tags.getTagList("incoming", 10);
			for(int i = 0; i < iList.tagCount(); i++)
			{
				Gene g = new Gene(brain);
				g.readNBT(iList.getCompoundTagAt(i));
				incoming.add(g);
			}
		}
	}
	
	public static class Network
	{
		final NeatBrain brain;
		
		Neuron[] neurons;
		
		public Network(NeatBrain brain)
		{ 
			this.brain = brain;
			neurons = new Neuron[brain.inputSize + brain.maxNodes + brain.actions.size()];
		}
		
		public Network Generate(Genome genome)
		{
			for(int i = 0; i < brain.inputSize; i++)
			{
				neurons[i] = new Neuron(brain);
			}
			
			for(int i = 0; i < brain.actions.size(); i++)
			{
				neurons[brain.maxNodes + i] = new Neuron(brain);
			}
			
			Collections.sort(genome.genes, new GeneComparator());
			
			for(int i = 0; i < genome.genes.size(); i++)
			{
				Gene g = genome.genes.get(i);
				
				if(g.enable)
				{
					if(neurons[g.out] == null)
					{
						neurons[g.out] = new Neuron(brain);
					}
					
					Neuron n = neurons[g.out];
					n.incoming.add(g);
					
					if(neurons[g.into] == null)
					{
						neurons[g.into] = new Neuron(brain);
					}
				}
			}
			
			return this; // For in-line instantiation
		}
		
		public void Evaluate(int[] inputs)
		{
			if(inputs.length != brain.inputSize)
			{
				System.out.println("ERROR: NEAT brain network recieved incorrect number of inputs!");
				return;
			}
			
			for(int i = 0; i < inputs.length; i++)
			{
				if(neurons[i] != null)
				{
					neurons[i].value = inputs[i];
				}
			}
			
			for(Neuron n : neurons)
			{
				if(n == null)
				{
					continue;
				}
				float sum = 0F;
				
				for(int i = 0; i < n.incoming.size(); i++)
				{
					Gene gIn = n.incoming.get(i);
					Neuron nOther = neurons[gIn.into];
					if(nOther != null)
					{
						sum = sum + gIn.weight * nOther.value;
					}
				}
				
				if(n.incoming.size() > 0)
				{
					n.value = (float)brain.Sigmoid(sum);
				}
			}
			
			String[] actNames = brain.actions.keySet().toArray(new String[0]);
			for(int i = 0; i < brain.actions.size(); i++)
			{
				String key = actNames[i];
				Neuron n = neurons[brain.maxNodes + i];
				
				if(n != null && n.value > 0)
				{
					brain.actions.put(key, true);
				} else
				{
					brain.actions.put(key, false);
				}
			}
		}
		
		public NBTTagCompound saveNBT()
		{
			NBTTagCompound tags = new NBTTagCompound();
			
			NBTTagList nList = new NBTTagList();
			for(int i = 0; i < neurons.length; i++)
			{
				Neuron n = neurons[i];
				if(n != null)
				{
					NBTTagCompound nTag = n.saveNBT();
					nTag.setInteger("netIdx", i);
					nList.appendTag(nTag);
				}
			}
			tags.setTag("network", nList);
			return tags;
		}
		
		public void readNBT(NBTTagCompound tags)
		{
			Arrays.fill(neurons, null);
			NBTTagList nList = new NBTTagList();
			for(int i = 0; i < nList.tagCount(); i++)
			{
				NBTTagCompound nTag = nList.getCompoundTagAt(i);
				Neuron n = new Neuron(brain);
				n.readNBT(nTag);
				neurons[nTag.getInteger("netIdx")] = n;
			}
		}
	}
}
