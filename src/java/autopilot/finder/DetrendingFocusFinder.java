package autopilot.finder;

import gnu.trove.procedure.TObjectDoubleProcedure;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures.FocusMeasure;

/**
 * Focus finder that can remove linear trends from focus measures. Some focus
 * measure have linear response superimposed to the usual gaussian like focus
 * response. this focus finder is capable of detecting this linear response and
 * removing it.
 * 
 * @author royer
 */
public class DetrendingFocusFinder extends SimpleFocusFinder<Double>
{

	private boolean mDetrendingActive = true;
	private double mMinFocusPosition, mMaxFocusPosition,
			mFocusValueAtMinPosition, mFocusValueAtMaxPosition;

	/**
	 * Constructs a detrending focus finder given a focus measure enum.
	 * 
	 * @param pMeasure
	 *          focus measure
	 */
	public DetrendingFocusFinder(final FocusMeasure pMeasure)
	{
		super(pMeasure);
	}

	/**
	 * Constructs a detrending focus finder from a focus measure implementation.
	 * 
	 * @param pFocusMeasureImplementation
	 *          focus measure.
	 */
	public DetrendingFocusFinder(final FocusMeasureInterface pFocusMeasureImplementation)
	{
		super(pFocusMeasureImplementation);
	}

	/**
	 * @see autopilot.finder.SimpleFocusFinder#ensureUpToDate()
	 */
	@Override
	protected void ensureUpToDate()
	{
		if (!mDetrendingActive)
		{
			super.ensureUpToDate();
			return;
		}

		if (!mUpToDate)
		{

			mMinFocusPosition = Double.POSITIVE_INFINITY;
			mMaxFocusPosition = Double.NEGATIVE_INFINITY;

			mImagePositionToFocusValueMap.forEachEntry(new TObjectDoubleProcedure<Double>()
			{

				@Override
				public final boolean execute(	final Double pPosition,
																			final double pFocusFalue)
				{
					if (pPosition < mMinFocusPosition)
					{
						mMinFocusPosition = pPosition;
						mFocusValueAtMinPosition = pFocusFalue;
					}

					if (pPosition > mMaxFocusPosition)
					{
						mMaxFocusPosition = pPosition;
						mFocusValueAtMaxPosition = pFocusFalue;
					}

					return true;
				}

			});

			final double lTrendSlope = (mFocusValueAtMaxPosition - mFocusValueAtMinPosition) / (mMaxFocusPosition - mMinFocusPosition);

			mBestValue = Double.NEGATIVE_INFINITY;

			mImagePositionToFocusValueMap.forEachEntry(new TObjectDoubleProcedure<Double>()
			{

				@Override
				public final boolean execute(	final Double pPosition,
																			final double pFocusValue)
				{
					final double lTrendCorrection = (pPosition - mMinFocusPosition) * lTrendSlope;
					final double lDetrendedValue = pFocusValue - lTrendCorrection;

					System.out.format("%g\t%g\t%g\n",
														pPosition,
														pFocusValue,
														lDetrendedValue);
					if (lDetrendedValue > mBestValue)
					{
						mBestValue = lDetrendedValue;
						mBestPosition = pPosition;
					}

					return true;
				}
			});

			mUpToDate = true;
		}

	}

	public boolean isDetrendingActive()
	{
		return mDetrendingActive;
	}

	public void setDetrendingActive(final boolean detrendingActive)
	{
		mDetrendingActive = detrendingActive;
	}

}
