package rtlib.core.units;

public enum Magnitude
{

	Yotta(1e+24),
	Zetta(1e+24),
	Exa(1e+18),
	Peta(1e+15),
	Tera(1e+12),
	Giga(1e+9),
	Mega(1e+6),
	Kilo(1e+3),
	Hecto(1e+2),
	Deca(1e+1),
	Unit(1),
	Deci(1e-1),
	Centi(1e-2),
	Milli(1e-3),
	Micro(1e-6),
	Nano(1e-9),
	Pico(1e-12),
	Femto(1e-15),
	Atto(1e-18),
	Zepto(1e-21),
	Yocto(1e-24);

	private final double mMagnitude;

	Magnitude(double pMagnitude)
	{
		mMagnitude = pMagnitude;
	}

	public double getMagnitude()
	{
		return mMagnitude;
	}

	public double convertFrom(double pValue, Magnitude pMagnitude)
	{
		final double lConvertedValue = pValue * (pMagnitude.getMagnitude() / getMagnitude());
		return lConvertedValue;
	}

	public static double unit2giga(final double x)
	{
		return Giga.convertFrom(x, Unit);
	}

	public static final double pico2milli(final double x)
	{
		return Milli.convertFrom(x, Pico);
	}

	public static final double nano2unit(final double x)
	{
		return Unit.convertFrom(x, Nano);
	}

	public static final double milli2pico(final double x)
	{
		return Pico.convertFrom(x, Milli);
	}

	public static final double unit2nano(final double x)
	{
		return Nano.convertFrom(x, Unit);
	}

	public static final double milli2nano(final double x)
	{
		return Nano.convertFrom(x, Milli);
	}

	public static double unit2micro(final double x)
	{
		return Micro.convertFrom(x, Unit);
	}

	public static final double nano2milli(final double x)
	{
		return Milli.convertFrom(x, Nano);
	}

	public static double micro2unit(final double x)
	{
		return Unit.convertFrom(x, Micro);
	}

	public static final double nano2micro(final double x)
	{
		return Micro.convertFrom(x, Nano);
	}

	public static final double micro2milli(final double x)
	{
		return Milli.convertFrom(x, Micro);
	}

	public static double milli2unit(final double x)
	{
		return Unit.convertFrom(x, Milli);
	}

	public static final double micro2nano(final double x)
	{
		return Nano.convertFrom(x, Micro);
	}

	public static final double milli2micro(final double x)
	{
		return Micro.convertFrom(x, Milli);
	}

	public static double unit2milli(final double x)
	{
		return Milli.convertFrom(x, Unit);
	}

}
