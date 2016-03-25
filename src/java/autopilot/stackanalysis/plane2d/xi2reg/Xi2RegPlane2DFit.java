package autopilot.stackanalysis.plane2d.xi2reg;

import static java.lang.Math.atan;
import static java.lang.Math.log;
import static java.lang.Math.toDegrees;

import autopilot.utils.rtlib.core.cpu.AvailableThreads;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import autopilot.stackanalysis.plane2d.FitResult;
import autopilot.stackanalysis.plane2d.Plane2DUtils;
import autopilot.stackanalysis.plane2d.Plane2DFitInterface;

public class Xi2RegPlane2DFit implements Plane2DFitInterface 
{
	private final ExecutorService mExecutorService;
	private int mMinimumNumberOfInliers;
	
	private TDoubleArrayList mX = new TDoubleArrayList();
	private TDoubleArrayList mY = new TDoubleArrayList();
	private TDoubleArrayList mZ = new TDoubleArrayList();


	public Xi2RegPlane2DFit()
	{
		this(Executors.newFixedThreadPool(AvailableThreads.getNumberOfThreads()));
	}

	public Xi2RegPlane2DFit(ExecutorService pExecutorService)
	{
		super();
		mExecutorService = pExecutorService;
	}
	
	@Override
	public int getMinimumNumberOfInliers()
	{
		return mMinimumNumberOfInliers;
	}

	@Override
	public void setMinimumNumberOfInliers(int pMinimumNumberOfInliers)
	{
		mMinimumNumberOfInliers = pMinimumNumberOfInliers;
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#addPoint(double, double, double)
	 */
	@Override
	public void addPoint(double pX, double pY, double pZ)
	{
		mX.add(pX);
		mY.add(pY);
		mZ.add(pZ);
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#clear()
	 */
	@Override
	public void clear()
	{
		mX.clear();
		mY.clear();
		mZ.clear();
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#getListX()
	 */
	@Override
	public TDoubleArrayList getListX()
	{
		return mX;
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#getListY()
	 */
	@Override
	public TDoubleArrayList getListY()
	{
		return mY;
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#getListZ()
	 */
	@Override
	public TDoubleArrayList getListZ()
	{
		return mZ;
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#getNumberOfDataPoints()
	 */
	@Override
	public int getNumberOfDataPoints()
	{
		return mX.size();
	}

	/* (non-Javadoc)
	 * @see autopilot.stackanalysis.plane2d.Plane2DFitInterface#fit()
	 */
	@Override
	public FitResult fit()
	{
		return fit(4);
	}


	public FitResult fit(	int pRepeats,
							double pJitterLevel,
							int pSearchLevel)
	{
		ArrayList<FitResult> lFitResultList = new ArrayList<>(pRepeats);
		for (int i = 0; i < pRepeats; i++)
		{
			TDoubleArrayList lZ = jitter(pJitterLevel, mZ);
			FitResult lFitResult = fit(pSearchLevel, mX, mY, lZ);
			lFitResultList.add(lFitResult);
		}
	
		return Plane2DUtils.averageFitResults(lFitResultList,false,false);
	}

	private TDoubleArrayList jitter(double pJitterLevel,
									TDoubleArrayList pZ)
	{
		double lVariance = StatUtils.variance(pZ.toArray());
		double lJitterAmplitude = lVariance*pJitterLevel;
				
		TDoubleArrayList lZ = new TDoubleArrayList(pZ.size());
		for(int i=0; i<pZ.size(); i++)
		{
			double lNewValue = pZ.getQuick(i)+lJitterAmplitude*(2*Math.random()-1);
			lZ.add(lNewValue);
		}
		
		return lZ;
	}


	public FitResult fit(int pSearchLevel)
	{
		return fit(pSearchLevel, mX, mY, mZ);
	}


	public FitResult fit(	int pSearchLevel,
							TDoubleArrayList pX,
							TDoubleArrayList pY,
							TDoubleArrayList pZ)
	{
		format("started 2d fit\n");

		final boolean[] lInlierSet = new boolean[pX.size()];
		generateFullSet(lInlierSet);

		FitResult lLastBestFit = Plane2DUtils.fit(pX, pY, pZ, lInlierSet);
		if (lLastBestFit == null)
			return null;

		final int lNumberOfLocalSearches = pSearchLevel * lInlierSet.length;

		format("number of datapoints: " + pX.size());
		format("min number of inliers: " + getMinimumNumberOfInliers());

		// for (int r = 0; r < 4; r++)
		{
			final ArrayList<FutureTask<FitResult>> lFutureTaskList = new ArrayList<FutureTask<FitResult>>();
			for (int i = 0; i < lNumberOfLocalSearches; i++)
			{
				final Xi2RegPlane2DFitOptimizationTask lPlane2DFitOptimizationTask = new Xi2RegPlane2DFitOptimizationTask(	i,
																												pX,
																												pY,
																												pZ,
																												getMinimumNumberOfInliers());
				final FutureTask<FitResult> lFutureTask = new FutureTask<FitResult>(lPlane2DFitOptimizationTask);
				mExecutorService.execute(lFutureTask);
				lFutureTaskList.add(lFutureTask);
				/*try
				{
					lFutureTask.get();
				}
				catch (InterruptedException | ExecutionException e)
				{
					e.printStackTrace();
				}/**/
			}

			for (final FutureTask<FitResult> lFutureTask : lFutureTaskList)
			{
				try
				{
					final FitResult lFitResult = lFutureTask.get();

					if (lFitResult == null)
						continue;

					if (lFitResult.countInliers() >= getMinimumNumberOfInliers())
					{
						if (lFitResult.betterThan(lLastBestFit))
						{
							format("    better: " + lFitResult);
							lLastBestFit = lFitResult;
						}
						else
							format("NOT better: " + lFitResult);
					}
					/*else
						System.out.println("not enough inliers: " + lFitResult);/**/

				}
				catch (final ExecutionException e)
				{
					e.printStackTrace();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}
		}

		format("Result=" + lLastBestFit);

		final int lNumberOfInliers = lLastBestFit.countInliers();
		format("nb inliers=" + lNumberOfInliers);

		if (lNumberOfInliers < getMinimumNumberOfInliers())
			return null;

		format("finished 2d fit\n");

		return lLastBestFit;
	}

	private static void generateFullSet(boolean[] pInliers)
	{
		for (int i = 0; i < pInliers.length; i++)
			pInliers[i] = true;
	}





	private static void format(String format, Object... args)
	{
		// System.out.format(format, args);
	}



}
