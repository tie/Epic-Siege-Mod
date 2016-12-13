package funwayguy.epicsiegemod.ai.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import com.google.common.base.Predicate;

public class PredicateExplosives implements Predicate<Entity>
{
	Entity host;
	
	public PredicateExplosives(Entity host)
	{
		this.host = host;
	}
	
	@Override
	public boolean apply(Entity input)
	{
		if(input == host)
		{
			return false;
		}
		
		if(input instanceof EntityCreeper)
		{
			return ((EntityCreeper)input).getCreeperState() > 0;
		}
		
		return input instanceof EntityTNTPrimed;
	}	
}
