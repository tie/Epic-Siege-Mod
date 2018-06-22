package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.monster.EntitySpider;

public class ESM_EntityAISpiderTarget extends ESM_EntityAINearestAttackableTarget
{
	private final EntitySpider spider;
	
	public ESM_EntityAISpiderTarget(EntitySpider spider)
	{
		super(spider, true);
		this.spider = spider;
	}
	
	@Override
    public boolean shouldExecute()
    {
        float f = this.spider.getBrightness();
        return !(f >= 0.5F) && super.shouldExecute();
    }
}
