package funwayguy.esm.handlers;

import java.util.ArrayList;
import java.util.EnumSet;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.EntityLivingBase;

public class ESM_ServerTickHandler implements ITickHandler
{
	public static ArrayList<EntityLivingBase> needsTargetUpdate = new ArrayList<EntityLivingBase>();

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel()
	{
		return null;
	}
}
