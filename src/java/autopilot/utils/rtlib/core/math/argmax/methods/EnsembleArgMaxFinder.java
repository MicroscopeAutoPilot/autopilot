package autopilot.utils.rtlib.core.math.argmax.methods;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import autopilot.utils.rtlib.core.math.argmax.ArgMaxFinder1DInterface;

public class EnsembleArgMaxFinder implements ArgMaxFinder1DInterface
{
	private static final Executor sExecutor = Executors.newCachedThreadPool();
	private static final int cTimeOutInSeconds = 1;

	private final ArrayList<ArgMaxFinder1DInterface> mArgMaxFinder1DInterfaceList = new ArrayList<ArgMaxFinder1DInterface>();
	private final Median mMedian;

	private final boolean mDebug = false;

	public EnsembleArgMaxFinder()
	{
		super();
		mMedian = new Median();
	}

	public void add(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		mArgMaxFinder1DInterfaceList.add(pArgMaxFinder1DInterface);
	}

	private class ArgMaxCallable implements Callable<Double>
	{
		private final double[] mX;
		private final double[] mY;
		private final ArgMaxFinder1DInterface mArgMaxFinder1DInterface;

		public ArgMaxCallable(	ArgMaxFinder1DInterface pArgMaxFinder1DInterface,
								double[] pX,
								double[] pY)
		{
			mArgMaxFinder1DInterface = pArgMaxFinder1DInterface;
			mX = pX;
			mY = pY;
		}

		@Override
		public Double call() throws Exception
		{
			final long lStartTimeInNs = System.nanoTime();
			final Double lArgMax = mArgMaxFinder1DInterface.argmax(	mX,
																	mY);
			final long lStopTimeInNs = System.nanoTime();
			/*double lElapsedtimeInSeconds = Magnitude.nano2unit(lStopTimeInNs - lStartTimeInNs);
			System.out.format("elapsed time: %g for %s \n",
													lElapsedtimeInSeconds,
													mArgMaxFinder1DInterface.toString());/**/

			return lArgMax;
		}

		@Override
		public String toString()
		{
			return mArgMaxFinder1DInterface.toString();
		}

	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		println("pX=" + Arrays.toString(pX));
		println("pY=" + Arrays.toString(pY));
		if (constant(pY))
			return null;

		final ArrayList<FutureTask<Double>> lTaskList = new ArrayList<FutureTask<Double>>();
		final HashMap<FutureTask<Double>, ArgMaxCallable> lTaskToCallableMap = new HashMap<FutureTask<Double>, ArgMaxCallable>();

		for (final ArgMaxFinder1DInterface lArgMaxFinder1DInterface : mArgMaxFinder1DInterfaceList)
		{
			final ArgMaxCallable lArgMaxCallable = new ArgMaxCallable(	lArgMaxFinder1DInterface,
																		pX,
																		pY);
			final FutureTask<Double> lArgMaxFutureTask = new FutureTask<Double>(lArgMaxCallable);
			sExecutor.execute(lArgMaxFutureTask);
			lTaskList.add(lArgMaxFutureTask);
			lTaskToCallableMap.put(lArgMaxFutureTask, lArgMaxCallable);
		}

		final TDoubleArrayList lArgMaxList = new TDoubleArrayList();
		for (final FutureTask<Double> lArgMaxFutureTask : lTaskList)
		{
			try
			{
				final Double lArgMax = lArgMaxFutureTask.get(	cTimeOutInSeconds,
																TimeUnit.SECONDS);
				if (lArgMax != null)
				{
					if (mDebug)
						System.out.println("class: " + lTaskToCallableMap.get(lArgMaxFutureTask)
											+ "\n\t\targmax="
											+ lArgMax);
					lArgMaxList.add(lArgMax);
				}
			}
			catch (final Throwable e)
			{
				if (mDebug)
					e.printStackTrace();
			}
		}

		final double lArgMaxMedian = mMedian.evaluate(lArgMaxList.toArray());

		return lArgMaxMedian;
	}

	private boolean constant(double[] pY)
	{
		for (int i = 0; i < pY.length; i++)
			if (pY[i] != pY[0])
				return false;
		return true;
	}

	@Override
	public String toString()
	{
		return String.format(	"EnsembleArgMaxFinder [mArgMaxFinder1DInterfaceList=%s]",
								mArgMaxFinder1DInterfaceList);
	}

	private void println(String pString)
	{

	}

}
