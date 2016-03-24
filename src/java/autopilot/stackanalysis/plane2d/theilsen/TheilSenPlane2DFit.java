package autopilot.stackanalysis.plane2d.theilsen;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import autopilot.stackanalysis.plane2d.FitResult;
import autopilot.stackanalysis.plane2d.Plane2DFitInterface;
import autopilot.stackanalysis.plane2d.Plane2DUtils;
import gnu.trove.list.array.TDoubleArrayList;

public class TheilSenPlane2DFit implements Plane2DFitInterface
{
	private final ExecutorService mExecutorService;

	TDoubleArrayList mX = new TDoubleArrayList();
	TDoubleArrayList mY = new TDoubleArrayList();
	TDoubleArrayList mZ = new TDoubleArrayList();

	private int mMinimumNumberOfInliers;

	public TheilSenPlane2DFit()
	{
		this(Executors.newFixedThreadPool(Runtime.getRuntime()
													.availableProcessors()));
	}

	public TheilSenPlane2DFit(ExecutorService pExecutorService)
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
		int lNumberOfFits = 256;

		final ArrayList<FutureTask<FitResult>> lFutureTaskList = new ArrayList<FutureTask<FitResult>>();
		for (int i = 0; i < lNumberOfFits; i++)
		{
			final TheilSen2DFitOptimizationTask lPlane2DFitOptimizationTask = new TheilSen2DFitOptimizationTask(	i,
																														mX,
																														mY,
																														mZ);
			final FutureTask<FitResult> lFutureTask = new FutureTask<FitResult>(lPlane2DFitOptimizationTask);
			mExecutorService.execute(lFutureTask);
			lFutureTaskList.add(lFutureTask);
		}

		ArrayList<FitResult> lFitResultList = new ArrayList<FitResult>();

		for (final FutureTask<FitResult> lFutureTask : lFutureTaskList)
		{
			try
			{
				final FitResult lFitResult = lFutureTask.get();

				if (lFitResult == null)
					continue;

				lFitResultList.add(lFitResult);

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

		FitResult lAverageFitResults = Plane2DUtils.averageFitResults(	lFitResultList,
																		true,
																		true);

		return lAverageFitResults;

	}

}
