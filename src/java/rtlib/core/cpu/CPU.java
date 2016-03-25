package rtlib.core.cpu;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class CPU
{
	static private Unsafe cUnsafe;

	static
	{
		Field f;
		try
		{
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			cUnsafe = (Unsafe) f.get(null);
		}
		catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static ThreadLocal<double[]> cLoadAverageThreadLocal = new ThreadLocal<double[]>();

	public static final double[] getLoadAverages()
	{
		double[] lLoadAverages = cLoadAverageThreadLocal.get();
		if (lLoadAverages == null)
		{
			lLoadAverages = new double[3];
			cLoadAverageThreadLocal.set(lLoadAverages);
		}
		cUnsafe.getLoadAverage(lLoadAverages, 3);
		return lLoadAverages;
	}

}
