package autopilot.utils.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.vecmath.Point2d;

public class TheilSenRobustExtremumFinder
{

	private final Random mRandom = new Random();

	private final ThreePointParabolaArgmax mThreePointPerfectEstimator = new ThreePointParabolaArgmax();

	private final ArrayList<Point2d> mPointList = new ArrayList<Point2d>();

	private final boolean mMaximum;
	private double mNumberOfTriplets;

	public TheilSenRobustExtremumFinder()
	{
		this(true);
	}

	public TheilSenRobustExtremumFinder(final boolean pMaximum)
	{
		this(pMaximum, 16);
	}

	public TheilSenRobustExtremumFinder(final int pNumberOfTriplets)
	{
		this(true, pNumberOfTriplets);
	}

	public TheilSenRobustExtremumFinder(final boolean pMaximum,
																			final int pNumberOfTriplets)
	{
		mMaximum = pMaximum;
		mNumberOfTriplets = pNumberOfTriplets;
	}

	public void reset()
	{
		mPointList.clear();
	}

	public void enter(final double pX, final double pY)
	{
		final Point2d lPoint = new Point2d(pX, pY);
		mPointList.add(lPoint);
	}

	public Double getResult()
	{

		Collections.sort(mPointList, new Comparator<Point2d>()
		{
			@Override
			public int compare(final Point2d pA, final Point2d pB)
			{
				return Double.compare(pA.x, pB.x);
			}
		});

		final int lNumberOfPoints = mPointList.size();
		final int lIterations = getNumberOfIterations(lNumberOfPoints);

		final Median lMedian = new Median();

		for (int i = 0; i < lIterations; i++)
		{

			int cxi = pickRandomPointBetween(0, lNumberOfPoints);
			int lxi = -1;
			int uxi = -1;

			// System.out.format("original cxi=%d \n", cxi);
			if (cxi < lNumberOfPoints / 3)
			{
				// System.out.format("%d < lNumberOfPoints / 3 \n", cxi);
				lxi = cxi;
				cxi = pickRandomPointBetween(lxi + 1, lNumberOfPoints);
				if (cxi < lxi + 1 + (lNumberOfPoints - lxi - 1) / 2)
				{
					uxi = pickRandomPointBetween(cxi + 1, lNumberOfPoints);
				}
				else
				{
					uxi = cxi;
					cxi = pickRandomPointBetween(lxi + 1, uxi);
				}

			}
			else if (cxi >= 2 * lNumberOfPoints / 3)
			{
				// System.out.format("%d >= (2 * lNumberOfPoints) / 3 \n", cxi);
				uxi = cxi;
				cxi = pickRandomPointBetween(0, uxi);

				if (cxi < uxi / 2)
				{
					lxi = cxi;
					cxi = pickRandomPointBetween(lxi + 1, uxi);
				}
				else
				{
					lxi = pickRandomPointBetween(0, cxi);
				}

			}
			else
			{
				// System.out.format("else \n");
				lxi = pickRandomPointBetween(0, cxi);
				uxi = pickRandomPointBetween(cxi + 1, lNumberOfPoints);
			}

			final double xl = mPointList.get(lxi).x;
			final double yl = mPointList.get(lxi).y;
			final double xc = mPointList.get(cxi).x;
			final double yc = mPointList.get(cxi).y;
			final double xu = mPointList.get(uxi).x;
			final double yu = mPointList.get(uxi).y;

			mThreePointPerfectEstimator.findExtremum(xl, xc, xu, yl, yc, yu);

			if (mThreePointPerfectEstimator.reliable && !(mMaximum ^ mThreePointPerfectEstimator.signPositive))
			{
				final double lExtremumPositionX = mThreePointPerfectEstimator.xe;
				if (!Double.isNaN(lExtremumPositionX) && !Double.isInfinite(lExtremumPositionX))
				{
					lMedian.enter(lExtremumPositionX);
				}
				else
				{
				}
				// System.out.println(lExtremumPositionX);
			}

		}

		final Double lExtremumPositionXMedian = lMedian.getStatistic();

		return lExtremumPositionXMedian;
	}

	private int pickRandomPointBetween(final int pBegin, final int pEnd)
	{
		final int i = pBegin + (int) (mRandom.nextDouble() * (pEnd - pBegin));
		return i;
	}

	public void setNumberOfIterations(final int pNumberOfIterations)
	{
		mNumberOfTriplets = pNumberOfIterations;
	}

	public int getNumberOfIterations(final int pNumberOfIterations)
	{
		return (int) mNumberOfTriplets;
	}

}
