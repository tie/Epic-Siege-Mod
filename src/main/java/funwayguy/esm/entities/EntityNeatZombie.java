package funwayguy.esm.entities;

import funwayguy.esm.ai.ESM_EntityAIBrainController;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

public class EntityNeatZombie extends EntityZombie
{
	public EntityNeatZombie(World world)
	{
		super(world);
		this.tasks.taskEntries.clear();
		this.tasks.addTask(0, new ESM_EntityAIBrainController(this));
	}
}
