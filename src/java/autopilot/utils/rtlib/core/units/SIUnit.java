package autopilot.utils.rtlib.core.units;

public enum SIUnit
{

	ArbitraryUnit(Magnitude.Unit, "AU"),
	Meter(Magnitude.Unit, "m"),
	Gram(Magnitude.Unit, "g"),
	Second(Magnitude.Unit, "s"),
	Kelvin(Magnitude.Unit, "m"),
	Mole(Magnitude.Kilo, "mol"),
	Candela(Magnitude.Unit, "cd"),
	Ampere(Magnitude.Unit, "A"),

	MilliMeter(Magnitude.Milli, "mm"),
	MicroMeter(Magnitude.Micro, "um"),
	NanoMeter(Magnitude.Nano, "nm"),
	Kilogram(Magnitude.Kilo, "Kg"),
	MilliSecond(Magnitude.Milli, "ms"),
	MicroSecond(Magnitude.Micro, "us");

	private final Magnitude mMagnitude;
	private final String mAbbreviation;

	SIUnit(Magnitude pMagnitude, String pAbbreviation)
	{
		mMagnitude = pMagnitude;
		mAbbreviation = pAbbreviation;
	}

	public Magnitude getMagnitude()
	{
		return mMagnitude;
	}

	public String getAbbreviation()
	{
		return mAbbreviation;
	}

	public boolean isBaseUnit()
	{
		return mMagnitude == Magnitude.Unit;
	}
}
