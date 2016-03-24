package autopilot.stackanalysis.plane2d.theilsen;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import autopilot.stackanalysis.plane2d.FitResult;
import autopilot.stackanalysis.plane2d.Plane2DUtils;

public class TheilSen2DFitOptimizationTask	implements
											Callable<FitResult>
{
	private final int mIndex;
	private final TDoubleArrayList mX;
	private final TDoubleArrayList mY;
	private final TDoubleArrayList mZ;
	private final ThreadLocalRandom mThreadLocalRandom;

	public TheilSen2DFitOptimizationTask(	int pIndex,
											TDoubleArrayList pX,
											TDoubleArrayList pY,
											TDoubleArrayList pZ)
	{
		mIndex = pIndex;
		mX = pX;
		mY = pY;
		mZ = pZ;
		mThreadLocalRandom = ThreadLocalRandom.current();

	}

	@Override
	public FitResult call() throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		format("starting optimization task\n");

		final boolean[] lInliersSet = new boolean[mX.size()];

		for (int i = 0; i < mIndex; i++)
			mThreadLocalRandom.nextLong();

		final int lNumberOfInliers = 6;
		
		generateRandomSet(lInliersSet, lNumberOfInliers);

		final FitResult lRandomFit = Plane2DUtils.fit(	mX,
														mY,
														mZ,
														lInliersSet);

		return lRandomFit;
	}

	

	private void generateRandomSet(	boolean[] pInliers,
									int pNumberOfInliers)
	{
		for (int i = 0; i < pInliers.length; i++)
			pInliers[i] = false;

		for (int i = 0; i < pNumberOfInliers; i++)
		{
			int lIndex;
			while (pInliers[lIndex = (int) floor(mThreadLocalRandom.nextDouble() * pInliers.length)])
				;
			pInliers[lIndex] = true;
		}
	}

	
	private static void format(String format, Object... args)
	{
		// System.out.format(format, args);
	}

}
