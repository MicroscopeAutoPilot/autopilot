package autopilot.stackanalysis.plane2d.xi2reg;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import autopilot.stackanalysis.plane2d.FitResult;
import autopilot.stackanalysis.plane2d.Plane2DUtils;

public class Xi2RegPlane2DFitOptimizationTask	implements
												Callable<FitResult>
{
	private final int mIndex;
	private final TDoubleArrayList mX;
	private final TDoubleArrayList mY;
	private final TDoubleArrayList mZ;
	private final int mMinimumNumberOfInliers;
	private final ThreadLocalRandom mThreadLocalRandom;

	public Xi2RegPlane2DFitOptimizationTask(int pIndex,
											TDoubleArrayList pX,
											TDoubleArrayList pY,
											TDoubleArrayList pZ,
											int pMinimumNumberOfInliers)
	{
		mIndex = pIndex;
		mX = pX;
		mY = pY;
		mZ = pZ;
		mMinimumNumberOfInliers = pMinimumNumberOfInliers;
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

		final double lRND = mThreadLocalRandom.nextDouble();
		final int lNumberOfInliers = (int) max(	mMinimumNumberOfInliers,
												(mIndex + lRND * lInliersSet.length) % lInliersSet.length);

		if (mIndex % 2 != 0)
			generateNearestNeighboorRandomSet(	mIndex,
												mX,
												mY,
												mZ,
												lInliersSet,
												lNumberOfInliers);
		else
			generateRandomSet(lInliersSet, lNumberOfInliers);

		final FitResult lRandomFit = Plane2DUtils.fit(mX,
														mY,
														mZ,
														lInliersSet);

		FitResult lFitResult = null;
		if (lRandomFit != null)
		{
			format(	"%d before opt lRandomFit.countInliers()=%d \n",
					mIndex,
					lRandomFit.countInliers());

			lFitResult = optimize(lRandomFit, mX, mY, mZ);

			format(	"%d after opt lRandomFit.countInliers()=%d \n",
					mIndex,
					lFitResult.countInliers());
		}

		format("finished optimization task\n");

		return lFitResult;
	}

	private FitResult optimize(	final FitResult pBestFitResultYet,
								TDoubleArrayList pX,
								TDoubleArrayList pY,
								TDoubleArrayList pZ)
	{
		format("############################################################\n");

		FitResult lCurrentBestFit = pBestFitResultYet;
		for (int r = 0; r < 2 * pBestFitResultYet.inliers.length; r++)
		{
			final FitResult lAdd = proposeMove(	true,
												lCurrentBestFit,
												pX,
												pY,
												pZ);
			final FitResult lRemove = proposeMove(	false,
													lCurrentBestFit,
													pX,
													pY,
													pZ);

			if (lAdd.betterThan(lCurrentBestFit) || lRemove.betterThan(lCurrentBestFit))
			{
				if (lAdd.betterThan(lRemove))
				{
					format("choice: adding point -> %s \n", lAdd);
					lCurrentBestFit = lAdd;
				}
				else if (lRemove.betterThan(lAdd))
				{
					format("choice: removing point -> %s \n", lRemove);
					lCurrentBestFit = lRemove;
				}
			}
			else
				break;
		}

		format(	"FINAL inlier set: %s \n",
				Arrays.toString(lCurrentBestFit.inliers));

		return lCurrentBestFit;
	}

	private FitResult proposeMove(	boolean pAdd,
									FitResult pBestFitResultYet,
									TDoubleArrayList pX,
									TDoubleArrayList pY,
									TDoubleArrayList pZ)
	{
		format("___________________________________________________________________\n");

		format(	"inlier set: %s \n",
				Arrays.toString(pBestFitResultYet.inliers));

		format(	"last:  p-value= %g, zscore=%s, ms= %g  \n",
				pBestFitResultYet.pvalue,
				pBestFitResultYet.zscore,
				pBestFitResultYet.ms);

		int lBestInlierIndex = -1;
		double lBestDistance = pAdd ? Double.POSITIVE_INFINITY : 0;

		for (int i = 0; i < pBestFitResultYet.inliers.length; i++)
			if (pAdd ^ pBestFitResultYet.inliers[i])
			{
				final double x = pX.get(i);
				final double y = pY.get(i);
				final double z = pZ.get(i);

				final double dist = pBestFitResultYet.distanceTo(	x,
																	y,
																	z);

				if ((pAdd && dist < lBestDistance) || (!pAdd && dist > lBestDistance))
				{
					lBestDistance = dist;
					lBestInlierIndex = i;
				}
			}

		if (lBestInlierIndex >= 0)
		{
			final boolean[] lNewInliers = new boolean[pBestFitResultYet.inliers.length];
			for (int i = 0; i < lNewInliers.length; i++)
				lNewInliers[i] = pBestFitResultYet.inliers[i];
			lNewInliers[lBestInlierIndex] = pAdd;

			final FitResult lNewFit = Plane2DUtils.fit(	pX,
															pY,
															pZ,
															lNewInliers);

			if (lNewFit != null)
			{
				if (lNewFit.pvalue == 0)
					format("PVALUE IS ZERO!!!");

				format(	"new:  p-value= %g, zscore=%s, ms= %g  \n",
						lNewFit.pvalue,
						lNewFit.zscore,
						lNewFit.ms);

				if (lNewFit.betterThan(pBestFitResultYet))
				{
					format(	"proposal: %s datapoint #%d \n",
							pAdd ? "adding" : "removing",
							lBestInlierIndex);
					return lNewFit;
				}
			}
		}

		return pBestFitResultYet;
	}

	private void generateNearestNeighboorRandomSet(	int pStartIndex,
													TDoubleArrayList pX,
													TDoubleArrayList pY,
													TDoubleArrayList pZ,
													boolean[] pInliers,
													int pNumberOfInliers)
	{
		for (int i = 0; i < pInliers.length; i++)
			pInliers[i] = false;

		int lIndex = pStartIndex % pInliers.length;
		pInliers[lIndex] = true;

		int lAddedCount = 1;
		while (lAddedCount < pNumberOfInliers)
		{

			double lSmallestDistance = Double.POSITIVE_INFINITY;
			int lNewIndex = -1;
			for (int i = 0; i < pInliers.length; i++)
				if (i != lIndex && !pInliers[i])
				{
					final double lDistance = distance(	pX,
														pY,
														pZ,
														lIndex,
														i);
					if (lDistance < lSmallestDistance)
					{
						lSmallestDistance = lDistance;
						lNewIndex = i;
					}
				}
			if (lNewIndex == -1)
				break;
			pInliers[lNewIndex] = true;
			lAddedCount++;

			lIndex = pickRandom(pInliers);
			if (lIndex == -1)
				break;
		}

	}

	private int pickRandom(boolean[] pInliers)
	{
		int lIndex = (int) (mThreadLocalRandom.nextDouble() * pInliers.length);

		int i = 0;
		while (!pInliers[lIndex])
		{
			lIndex++;
			if (lIndex >= pInliers.length)
				lIndex = 0;
			i++;
			if (i > pInliers.length)
				return -1;
		}

		return lIndex;
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

	private double distance(TDoubleArrayList pX,
							TDoubleArrayList pY,
							TDoubleArrayList pZ,
							int pA,
							int pB)
	{
		final double xa = pX.get(pA);
		final double ya = pY.get(pA);
		final double za = pZ.get(pA);

		final double xb = pX.get(pB);
		final double yb = pY.get(pB);
		final double zb = pZ.get(pB);

		final double lDistance = sqrt((xa - xb) * (xa - xb)
										+ (ya - yb)
										* (ya - yb)
										+ (za - zb)
										* (za - zb));

		return lDistance;
	}

	private static void format(String format, Object... args)
	{
		// System.out.format(format, args);
	}

}
