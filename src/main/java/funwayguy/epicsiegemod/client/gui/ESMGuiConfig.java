package funwayguy.epicsiegemod.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import funwayguy.epicsiegemod.core.ESM;
import funwayguy.epicsiegemod.handlers.ConfigHandler;

public class ESMGuiConfig extends GuiConfig
{
	public ESMGuiConfig(GuiScreen parent)
	{
		super(parent, getCategories(ConfigHandler.config), ESM.MODID, false, false, ESM.NAME);
	}
	
	private static List<IConfigElement> getCategories(Configuration config)
	{
		List<IConfigElement> cats = new ArrayList<>();
		
		for(String s : config.getCategoryNames())
		{
			cats.add(new ConfigElement(config.getCategory(s)));
		}
		
		return cats;
	}
}
