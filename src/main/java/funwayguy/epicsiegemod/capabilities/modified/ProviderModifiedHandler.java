package funwayguy.epicsiegemod.capabilities.modified;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class ProviderModifiedHandler implements ICapabilityProvider, INBTSerializable<NBTTagCompound>
{
	private ModifiedHandler handler = new ModifiedHandler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return handler != null && capability == CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability != CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY)
		{
			return null;
		}
		
		return CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY.cast(handler);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		return handler.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		handler.readFromNBT(tag);
	}
}
