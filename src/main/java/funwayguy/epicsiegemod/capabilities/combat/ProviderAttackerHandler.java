package funwayguy.epicsiegemod.capabilities.combat;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ProviderAttackerHandler implements ICapabilityProvider
{
	private AttackerHandler handler = new AttackerHandler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return handler != null && capability == CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability != CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY)
		{
			return null;
		}
		
		return CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY.cast(handler);
	}
}
