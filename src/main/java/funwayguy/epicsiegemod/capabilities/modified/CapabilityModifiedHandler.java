package funwayguy.epicsiegemod.capabilities.modified;

import java.util.concurrent.Callable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import funwayguy.epicsiegemod.core.ESM;

public class CapabilityModifiedHandler
{
	@CapabilityInject(IModifiedHandler.class)
	public static Capability<IModifiedHandler> MODIFIED_HANDLER_CAPABILITY = null;
	public static ResourceLocation MODIFIED_HANDLER_ID = new ResourceLocation(ESM.MODID + ":modified_handler");
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IModifiedHandler.class, new Capability.IStorage<IModifiedHandler>()
		{
			@Override
			public NBTBase writeNBT(Capability<IModifiedHandler> capability, IModifiedHandler instance, EnumFacing side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<IModifiedHandler> capability, IModifiedHandler instance, EnumFacing side, NBTBase nbt)
			{
			}
		}, new Callable<IModifiedHandler>()
		{
			@Override
			public IModifiedHandler call() throws Exception
			{
				return new ModifiedHandler();
			}
		});
	}
}
