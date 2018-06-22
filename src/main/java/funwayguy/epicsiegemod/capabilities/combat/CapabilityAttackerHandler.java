package funwayguy.epicsiegemod.capabilities.combat;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import funwayguy.epicsiegemod.core.ESM;

public class CapabilityAttackerHandler
{
	@CapabilityInject(IAttackerHandler.class)
	public static Capability<IAttackerHandler> ATTACKER_HANDLER_CAPABILITY = null;
	public static ResourceLocation ATTACKER_HANDLER_ID = new ResourceLocation(ESM.MODID + ":attack_handler");
	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IAttackerHandler.class, new Capability.IStorage<IAttackerHandler>()
		{
			@Override
			public NBTBase writeNBT(Capability<IAttackerHandler> capability, IAttackerHandler instance, EnumFacing side)
			{
				return null;
			}

			@Override
			public void readNBT(Capability<IAttackerHandler> capability, IAttackerHandler instance, EnumFacing side, NBTBase nbt)
			{
			}
		}, AttackerHandler::new);
	}
}
