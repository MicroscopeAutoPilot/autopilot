package autopilot.finder;

import java.util.ArrayList;

import autopilot.image.DoubleArrayImage;
import autopilot.utils.ArrayMatrix;
import autopilot.utils.math.Median;
import autopilot.utils.math.Statistic;

/**
 * The Region voting focus finder uses a voting scheme to decide on the sharpest
 * image.
 * 
 * @author royer
 */
public class RegionVotingFocusFinder implements
																		FocusFinderInterface<Double>
{
	private final FocusFinderFactoryInterface<Double> mFocusFinderFactoryInterface;
	private final int mMatrixSize;

	private final ArrayMatrix<FocusFinderInterface<Double>> mFocusFinderInterfaceMatrix = new ArrayMatrix<FocusFinderInterface<Double>>();
	private final double mAmplitudeIndexThreshold;
	private Statistic<Double> mStatistic = new Median();

	private boolean mQuadraticBias = false;
	private double mMinPosition;
	private double mMaxPosition;

	private boolean isUpToDate = false;

	private final StringBuilder mPositionAmplitudeDebugInfo = new StringBuilder();
	private Double mCurrentBestPosition;

	/**
	 * Constructs a region voting focus finder from a focus finder factory, image
	 * tile matrix size, and amplitude index threshold.
	 * 
	 * @param pFocusFinderFactoryInterface
	 *          focus finder factory interface
	 * @param pMatrixSize
	 *          image tile matrix size
	 * @param pAmplitudeIndexThreshold
	 *          amplitude index threshold
	 */
	public RegionVotingFocusFinder(	final FocusFinderFactoryInterface<Double> pFocusFinderFactoryInterface,
																	final int pMatrixSize,
																	final double pAmplitudeIndexThreshold)
	{
		super();
		mFocusFinderFactoryInterface = pFocusFinderFactoryInterface;
		mMatrixSize = pMatrixSize;
		mAmplitudeIndexThreshold = pAmplitudeIndexThreshold;

		clear();
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getName()
	 */
	@Override
	public String getName()
	{
		return String.format(	"RegionVotingFocusFinder(%s,%d,%g)",
													mFocusFinderFactoryInterface.instantiate()
																											.getName(),
													mMatrixSize,
													mAmplitudeIndexThreshold);
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#clear()
	 */
	@Override
	public void clear()
	{
		isUpToDate = false;
		for (int y = 0; y < mMatrixSize; y++)
		{
			final ArrayList<FocusFinderInterface<Double>> lRow = new ArrayList<FocusFinderInterface<Double>>();
			for (int x = 0; x < mMatrixSize; x++)
			{
				final FocusFinderInterface<Double> lFocusFinder = mFocusFinderFactoryInterface.instantiate();
				lRow.add(lFocusFinder);
			}
			mFocusFinderInterfaceMatrix.add(lRow);
		}
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#addImage(java.lang.Object,
	 *      autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double addImage(	final Double pPosition,
													final DoubleArrayImage pDoubleArrayImage)
	{
		isUpToDate = false;
		final ArrayMatrix<DoubleArrayImage> lExtractedTilesMatrix = pDoubleArrayImage.extractTiles(	mMatrixSize,
																																																mMatrixSize);
		double sum = 0;
		int count = 0;
		for (int y = 0; y < mMatrixSize; y++)
		{
			for (int x = 0; x < mMatrixSize; x++)
			{
				final FocusFinderInterface<Double> lFocusFinder = mFocusFinderInterfaceMatrix.get(y)
																																											.get(x);
				final DoubleArrayImage lDoubleArrayImage = lExtractedTilesMatrix.get(y)
																																				.get(x);

				sum += lFocusFinder.addImage(pPosition, lDoubleArrayImage);
				count++;
			}
		}

		final double lAverage = sum / count;
		return lAverage;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#removeImage(java.lang.Object)
	 */
	@Override
	public void removeImage(final Double pPosition)
	{
		isUpToDate = false;
		for (int y = 0; y < mMatrixSize; y++)
		{
			for (int x = 0; x < mMatrixSize; x++)
			{
				final FocusFinderInterface<Double> lFocusFinder = mFocusFinderInterfaceMatrix.get(y)
																																											.get(x);
				lFocusFinder.removeImage(pPosition);
			}
		}
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getBestPosition()
	 */
	@Override
	public Double getBestPosition()
	{
		ensureUpToDate();
		return mCurrentBestPosition;
	}

	/**
	 * Computes the quadratic bias value for a given focus finder.
	 * 
	 * @param lFocusFinder
	 *          focus finder
	 * @return quadratic bias
	 */
	public double getQuadraticBiasValue(final FocusFinderInterface<Double> lFocusFinder)
	{
		final double lBestFocusPosition = lFocusFinder.getBestPosition();
		final double lBestPositionIndex = (lBestFocusPosition - mMinPosition) / (getMaxPosition() - mMinPosition);
		final double lQuadraticBiasValue = 4 * lBestPositionIndex
																				* (1 - lBestPositionIndex);
		return lQuadraticBiasValue;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getBestFocusMeasure()
	 */
	@Override
	public double getBestFocusMeasure()
	{
		throw new UnsupportedOperationException("This Focus Finder does not define a single scalar focus measure");
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getFocusAmplitude()
	 */
	@Override
	public double getFocusAmplitude()
	{
		throw new UnsupportedOperationException("This Focus Finder does not define a single scalar focus measure");
	}

	private void ensureUpToDate()
	{
		if (!isUpToDate)
		{
			double lMinAmplitude = Double.POSITIVE_INFINITY;
			double lMaxAmplitude = Double.NEGATIVE_INFINITY;

			for (int y = 0; y < mMatrixSize; y++)
			{
				for (int x = 0; x < mMatrixSize; x++)
				{
					final FocusFinderInterface<Double> lFocusFinder = mFocusFinderInterfaceMatrix.get(y)
																																												.get(x);

					double lFocusAmplitude = lFocusFinder.getFocusAmplitude();

					if (mQuadraticBias)
					{
						final double lQuadraticBiasValue = getQuadraticBiasValue(lFocusFinder);
						lFocusAmplitude *= lQuadraticBiasValue;
					}

					lMinAmplitude = Math.min(lMinAmplitude, lFocusAmplitude);
					lMaxAmplitude = Math.max(lMaxAmplitude, lFocusAmplitude);
				}
			}

			mPositionAmplitudeDebugInfo.setLength(0);
			mStatistic.reset();

			for (int y = 0; y < mMatrixSize; y++)
			{
				for (int x = 0; x < mMatrixSize; x++)
				{
					final FocusFinderInterface<Double> lFocusFinder = mFocusFinderInterfaceMatrix.get(y)
																																												.get(x);

					double lFocusAmplitude = lFocusFinder.getFocusAmplitude();
					if (mQuadraticBias)
					{
						final double lQuadraticBiasValue = getQuadraticBiasValue(lFocusFinder);
						lFocusAmplitude *= lQuadraticBiasValue;
					}

					final double lWidthAmplitude = lMaxAmplitude - lMinAmplitude;
					// if (lWidthAmplitude > 0)
					{
						final double lAmplitudeIndex = (lFocusAmplitude - lMinAmplitude) / lWidthAmplitude;

						if (lAmplitudeIndex > mAmplitudeIndexThreshold)
						{
							final double lFocusPosition = lFocusFinder.getBestPosition();
							mStatistic.enter(lFocusPosition);

							mPositionAmplitudeDebugInfo.append(String.format(	"%g\t%g\n",
																																lFocusPosition,
																																lAmplitudeIndex));
						}
					}
				}
			}

			mCurrentBestPosition = mStatistic.getStatistic();
			isUpToDate = true;
		}
	}

	private void setStatistic(final Statistic<Double> pStatistic)
	{
		isUpToDate &= mStatistic != pStatistic;
		mStatistic = pStatistic;
	}

	/**
	 * Returns the state of the quadratic bias flag.
	 * 
	 * @return quadratic bias
	 */
	public boolean isQuadraticBias()
	{
		return mQuadraticBias;
	}

	/**
	 * Sets the state of the quadratic bias state.
	 * 
	 * @param pQuadraticBias
	 *          quadratic bias
	 */
	public void setQuadraticBias(final boolean pQuadraticBias)
	{
		isUpToDate &= mQuadraticBias != pQuadraticBias;
		mQuadraticBias = pQuadraticBias;
	}

	/**
	 * Returns min position
	 * 
	 * @return min position
	 */
	public double getMinPosition()
	{
		return mMinPosition;
	}

	/**
	 * Sets the min position.
	 * 
	 * @param pMinPosition
	 *          min position
	 */
	public void setMinPosition(final double pMinPosition)
	{
		isUpToDate &= mMinPosition != pMinPosition;
		mMinPosition = pMinPosition;
	}

	/**
	 * Returns the max position.
	 * 
	 * @return max position
	 */
	public double getMaxPosition()
	{
		return mMaxPosition;
	}

	/**
	 * Sets the max position.
	 * 
	 * @param pMaxPosition
	 *          max position.
	 */
	public void setMaxPosition(final double pMaxPosition)
	{
		isUpToDate &= mMaxPosition != pMaxPosition;
		mMaxPosition = pMaxPosition;
	}

	/**
	 * Returns the position amplitude debug information.
	 * 
	 * @return debug info as String
	 */
	public String getPositionAmplitudeDebugInfo()
	{
		return mPositionAmplitudeDebugInfo.toString();
	}

}
