package autopilot.finder;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.procedure.TObjectDoubleProcedure;
import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;
import autopilot.measures.FocusMeasures.FocusMeasure;

/**
 * This focus finder implementation is the simplest one. It just finds the image
 * with highest focus measure value. More elaborate focus finders derive from
 * this class.
 * 
 * @author royer
 * 
 * @param <O>
 *          object representing position
 */
public class SimpleFocusFinder<O> implements FocusFinderInterface<O>
{
	private FocusMeasure mMeasure;
	private FocusMeasureInterface mMeasureImplementation;

	private DoubleArrayImage mWorkingDoubleArrayImage;

	protected final TObjectDoubleHashMap<O> mImagePositionToFocusValueMap = new TObjectDoubleHashMap<O>();

	protected O mBestPosition = null;
	protected double mBestValue = Double.NEGATIVE_INFINITY;
	private double mMinFocusMeasure = Double.POSITIVE_INFINITY;
	private double mMaxFocusMeasure = Double.NEGATIVE_INFINITY;

	protected boolean mUpToDate = false;

	/**
	 * Constructs a simple focus finder from a focus measure enum.
	 * 
	 * @param pMeasure
	 *          focus measure
	 */
	public SimpleFocusFinder(final FocusMeasure pMeasure)
	{
		super();
		mMeasure = pMeasure;
		clear();
	}

	/**
	 * Constructs a simple focus measure from a focus measure implementation.
	 * 
	 * @param pFocusMeasureImplementation
	 *          focus measure implementation
	 */
	public SimpleFocusFinder(final FocusMeasureInterface pFocusMeasureImplementation)
	{
		super();
		mMeasureImplementation = pFocusMeasureImplementation;
		clear();
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getName()
	 */
	@Override
	public String getName()
	{
		if (mMeasure != null)
		{
			return String.format("SimpleFocusFinder(%s)", mMeasure.name());
		}
		else
		{
			return String.format(	"SimpleFocusFinder(%s)",
														mMeasureImplementation.getClass()
																									.getName()
																									.toString());
		}
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#clear()
	 */
	@Override
	public void clear()
	{
		mUpToDate = false;
		mBestPosition = null;
		mBestValue = Double.NEGATIVE_INFINITY;
		mImagePositionToFocusValueMap.clear();
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#addImage(java.lang.Object,
	 *      autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double addImage(	final O pPosition,
													final DoubleArrayImage pDoubleArrayImage)
	{
		mUpToDate = false;

		if (mWorkingDoubleArrayImage == null || mWorkingDoubleArrayImage.getWidth() != pDoubleArrayImage.getWidth()
				|| mWorkingDoubleArrayImage.getHeight() != pDoubleArrayImage.getHeight())
		{
			mWorkingDoubleArrayImage = new DoubleArrayImage(pDoubleArrayImage.getWidth(),
																											pDoubleArrayImage.getHeight());
		}

		double lFocusValue;
		if (mMeasure != null)
		{
			lFocusValue = FocusMeasures.computeFocusMeasure(mMeasure,
																											pDoubleArrayImage,
																											mWorkingDoubleArrayImage);
		}
		else
		{
			mWorkingDoubleArrayImage.copyFrom(pDoubleArrayImage);
			lFocusValue = mMeasureImplementation.computeFocusMeasure(mWorkingDoubleArrayImage);
		}

		if (!Double.isNaN(lFocusValue) & !Double.isInfinite(lFocusValue))
		{
			addPoint(pPosition, lFocusValue);
		}
		else
		{
			System.err.format("WARNING: Erroneous value: %g  detected for position %s \n",
												lFocusValue,
												pPosition.toString());
		}

		return lFocusValue;
	}

	/**
	 * Adds a focus measure value for a given position.
	 * 
	 * @param pPosition
	 *          position
	 * @param lFocusValue
	 *          focus measure value
	 */
	public void addPoint(final O pPosition, final double lFocusValue)
	{
		mUpToDate = false;
		mImagePositionToFocusValueMap.put(pPosition, lFocusValue);
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#removeImage(java.lang.Object)
	 */
	@Override
	public void removeImage(final O pPosition)
	{
		mUpToDate = false;
		mImagePositionToFocusValueMap.remove(pPosition);
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getBestPosition()
	 */
	@Override
	public O getBestPosition()
	{
		ensureUpToDate();
		return mBestPosition;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getBestFocusMeasure()
	 */
	@Override
	public double getBestFocusMeasure()
	{
		ensureUpToDate();
		return mBestValue;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getFocusAmplitude()
	 */
	@Override
	public double getFocusAmplitude()
	{
		ensureUpToDate();
		return mMaxFocusMeasure - mMinFocusMeasure;
	}

	/**
	 * Returns the internal hash map used to store the image to focus measure map.
	 * 
	 * @return hashmap
	 */
	public TObjectDoubleHashMap<O> getInternalMap()
	{
		return mImagePositionToFocusValueMap;
	}

	/**
	 * Returns a String that represents the internal map used to store the image
	 * to focus measure map.
	 * 
	 * @return internal map
	 */
	public String getInternalMapAsTable()
	{
		final StringBuilder lStringBuilder = new StringBuilder();
		mImagePositionToFocusValueMap.forEachEntry(new TObjectDoubleProcedure<O>()
		{
			@Override
			public boolean execute(final O pObject, final double pFocusValue)
			{
				lStringBuilder.append(pObject.toString());
				lStringBuilder.append("\t");
				lStringBuilder.append(pFocusValue);
				lStringBuilder.append("\n");
				return true;
			}
		});
		return lStringBuilder.toString();
	}

	/**
	 * Calls to this method ensres that the computations required for the methods
	 * getBestPosition(), getbestFocusMeasure(), etc... are up to date.
	 */
	protected void ensureUpToDate()
	{
		if (!mUpToDate)
		{
			mMinFocusMeasure = Double.POSITIVE_INFINITY;
			mMaxFocusMeasure = Double.NEGATIVE_INFINITY;
			mBestValue = Double.NEGATIVE_INFINITY;

			mImagePositionToFocusValueMap.forEachEntry(new TObjectDoubleProcedure<O>()
			{

				@Override
				public final boolean execute(	final O pPosition,
																			final double pFocusFalue)
				{
					if (pFocusFalue > mBestValue)
					{
						mBestValue = pFocusFalue;
						mBestPosition = pPosition;
					}

					mMinFocusMeasure = Math.min(mMinFocusMeasure, pFocusFalue);
					mMaxFocusMeasure = Math.max(mMaxFocusMeasure, pFocusFalue);

					return true;
				}

			});

			mUpToDate = true;
		}

	}

}
