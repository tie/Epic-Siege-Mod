package funwayguy.epicsiegemod.capabilities.modified;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface IModifiedHandler
{
	public boolean isModified();
	public void setModified(boolean state);
	
	public void readFromNBT(NBTTagCompound tags);
	public NBTTagCompound writeToNBT(NBTTagCompound tags);
	
	public NBTTagCompound getModificationData(ResourceLocation ID);
}
