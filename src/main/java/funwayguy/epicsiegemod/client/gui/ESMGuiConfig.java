package funwayguy.epicsiegemod.client.gui;

import java.util.ArrayList;
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
	
	public static ArrayList<IConfigElement> getCategories(Configuration config)
	{
		ArrayList<IConfigElement> cats = new ArrayList<IConfigElement>();
		
		for(String s : config.getCategoryNames())
		{
			cats.add(new ConfigElement(config.getCategory(s)));
		}
		
		return cats;
	}
}
