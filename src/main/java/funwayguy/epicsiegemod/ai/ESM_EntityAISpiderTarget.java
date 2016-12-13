package funwayguy.epicsiegemod.ai;

import net.minecraft.entity.monster.EntitySpider;

public class ESM_EntityAISpiderTarget extends ESM_EntityAINearestAttackableTarget
{
	EntitySpider spider;
	
	public ESM_EntityAISpiderTarget(EntitySpider spider)
	{
		super(spider, true);
		this.spider = spider;
	}
	
	@Override
    public boolean shouldExecute()
    {
        float f = this.spider.getBrightness(1.0F);
        return f >= 0.5F ? false : super.shouldExecute();
    }
}
