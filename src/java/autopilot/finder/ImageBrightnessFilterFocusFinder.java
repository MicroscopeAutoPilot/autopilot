package autopilot.finder;

import gnu.trove.map.hash.TDoubleDoubleHashMap;
import autopilot.image.DoubleArrayImage;
import autopilot.utils.math.Percentile;

/**
 * Simple focus finder using image brightness as a rejection filter to discard
 * images that are too dark (no fluorescence signal). The threshold is defined
 * as percentile of the brightens of all images added.
 * 
 * @author royer
 */
public class ImageBrightnessFilterFocusFinder	implements
																							FocusFinderInterface<Double>
{
	private final FocusFinderInterface<Double> mFocusFinder;
	private final double mImageNormalizedAverageBrightnessPercentile;

	private final TDoubleDoubleHashMap mImagePositionToAverageBrightnessMap = new TDoubleDoubleHashMap();
	private double mOffset;
	private double mFactor;
	private double mNormalizedAverageBrightnessThreshold;

	private double mCurrentBestPosition;
	private boolean mUpToDate = false;

	private final boolean mDebug = false;

	/**
	 * Returns a focus finder factory for a given base factory and normalized
	 * average brightness percentile.
	 * 
	 * @param pFocusFinderFactory
	 *          base factory
	 * @param pImageNormalizedAverageBrightnessPercentile
	 *          normalized average brightness percentile
	 * @return factory
	 */
	public static final FocusFinderFactoryInterface<Double> getFactory(	final FocusFinderFactoryInterface<Double> pFocusFinderFactory,
																																			final double pImageNormalizedAverageBrightnessPercentile)
	{
		final FocusFinderFactoryInterface<Double> lFactory = new FocusFinderFactoryInterface<Double>()
		{

			@Override
			public FocusFinderInterface<Double> instantiate()
			{
				final FocusFinderInterface<Double> lFocusFinder = pFocusFinderFactory.instantiate();
				return new ImageBrightnessFilterFocusFinder(lFocusFinder,
																										pImageNormalizedAverageBrightnessPercentile);
			}
		};

		return lFactory;
	}

	/**
	 * Constructs an image brightnes filter focus finder given a focus finder and
	 * normalized average brightness percentile.
	 * 
	 * @param pFocusFinder
	 *          focus finder
	 * @param pImageNormalizedAverageBrightnessPercentile
	 *          normalized average brightness percentile
	 */
	public ImageBrightnessFilterFocusFinder(final FocusFinderInterface<Double> pFocusFinder,
																					final double pImageNormalizedAverageBrightnessPercentile)
	{
		super();
		mFocusFinder = pFocusFinder;
		mImageNormalizedAverageBrightnessPercentile = pImageNormalizedAverageBrightnessPercentile;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getName()
	 */
	@Override
	public String getName()
	{
		return String.format(	"ImageBrightnessFilterFocusFinder(%s,%g)",
													mFocusFinder.getName(),
													mImageNormalizedAverageBrightnessPercentile);
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#clear()
	 */
	@Override
	public void clear()
	{
		mFocusFinder.clear();
		mImagePositionToAverageBrightnessMap.clear();
		mUpToDate = false;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#addImage(java.lang.Object,
	 *      autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double addImage(	final Double pPosition,
													final DoubleArrayImage pDoubleArrayImage)
	{
		mUpToDate = false;

		mFocusFinder.addImage(pPosition, pDoubleArrayImage);

		final double lAverageBrightness = computeAverageBrightness(pDoubleArrayImage);

		mImagePositionToAverageBrightnessMap.put(	pPosition,
																							lAverageBrightness);

		return lAverageBrightness;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#removeImage(java.lang.Object)
	 */
	@Override
	public void removeImage(final Double pPosition)
	{
		mUpToDate = false;
		mFocusFinder.removeImage(pPosition);
		mImagePositionToAverageBrightnessMap.remove(pPosition);
	}

	/**
	 * Returns th average brightness of the image.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @return average brightness
	 */
	private double computeAverageBrightness(final DoubleArrayImage pDoubleArrayImage)
	{
		final double lAverageBrightness = pDoubleArrayImage.normL1() / pDoubleArrayImage.getLength();
		return lAverageBrightness;
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

	private void ensureUpToDate()
	{
		if (!mUpToDate)
		{
			computeNormalizationParametersAndPercentile();

			final double[] lPositions = mImagePositionToAverageBrightnessMap.keys();

			for (final double lPosition : lPositions)
			{
				final double lAverageBrightness = mImagePositionToAverageBrightnessMap.get(lPosition);
				final double lNormalizedBrightness = getNormalizedAverageBrightness(lAverageBrightness);
				if (isBrightEnough(lNormalizedBrightness))
				{
					if (mDebug)
					{
						System.out.format("accepted image at: %g because average brightness (nab=%g) high enough \n",
															lPosition,
															lNormalizedBrightness);
					}
				}
				else
				{
					mFocusFinder.removeImage(lPosition);
					if (mDebug)
					{
						System.out.format("rejected image at: %g because average brightness (nab=%g) too low! \n",
															lPosition,
															lNormalizedBrightness);
					}

				}
			}

			mCurrentBestPosition = mFocusFinder.getBestPosition();
			mUpToDate = true;
		}
	}

	private void computeNormalizationParametersAndPercentile()
	{
		final double[] lAverageBrightnessArray = mImagePositionToAverageBrightnessMap.values();

		{
			double lMin = Double.POSITIVE_INFINITY;
			double lMax = Double.NEGATIVE_INFINITY;
			for (final double lAverageBrightness : lAverageBrightnessArray)
			{
				lMin = Math.min(lMin, lAverageBrightness);
				lMax = Math.max(lMax, lAverageBrightness);
			}

			mOffset = lMin;
			mFactor = 1 / (lMax - lMin);
		}

		{
			final Percentile lPercentile = new Percentile(mImageNormalizedAverageBrightnessPercentile);
			for (final double lAverageBrightness : lAverageBrightnessArray)
			{
				final double lNormalizedAverageBrightness = getNormalizedAverageBrightness(lAverageBrightness);
				lPercentile.enter(lNormalizedAverageBrightness);
			}

			mNormalizedAverageBrightnessThreshold = lPercentile.getStatistic();
		}

	}

	private final double getNormalizedAverageBrightness(final double pAverageBrightness)
	{
		final double lNormalizedAverageBrightness = mFactor * (pAverageBrightness - mOffset);
		return lNormalizedAverageBrightness;
	}

	protected boolean isBrightEnough(final double pNormalizedAverageBrightness)
	{
		return pNormalizedAverageBrightness > mNormalizedAverageBrightnessThreshold;
	}

	@Override
	public double getBestFocusMeasure()
	{
		ensureUpToDate();
		return mFocusFinder.getBestFocusMeasure();
	}

	@Override
	public double getFocusAmplitude()
	{
		ensureUpToDate();
		return mFocusFinder.getFocusAmplitude();
	}

}
