package funwayguy.epicsiegemod.core;

public class DimSettings
{
	public double dmgMult;
	public double spdMult;
	public double hpMult;
	public double knockResist;
	
	public DimSettings(double hpMult, double dmgMult, double spdMult, double knockResist)
	{
		this.hpMult = hpMult -1D;
		this.dmgMult = dmgMult - 1D;
		this.spdMult = spdMult - 1D;
		this.knockResist = knockResist -1D;
	}
}
