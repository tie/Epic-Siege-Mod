package funwayguy.epicsiegemod.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import funwayguy.epicsiegemod.core.ESM;

public class ESMSounds
{
	public static SoundEvent sndCenaStart = new SoundEvent(new ResourceLocation(ESM.MODID + ":cena_creeper.start"));
	public static SoundEvent sndCenaEnd = new SoundEvent(new ResourceLocation(ESM.MODID + ":cena_creeper.end"));
	
	public static void registerSounds()
	{
		ForgeRegistries.SOUND_EVENTS.register(sndCenaStart.setRegistryName(sndCenaStart.getSoundName()));
		ForgeRegistries.SOUND_EVENTS.register(sndCenaEnd.setRegistryName(sndCenaEnd.getSoundName()));
	}
}
