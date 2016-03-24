package autopilot.finder;

import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasures.FocusMeasure;
import autopilot.utils.math.TheilSenRobustExtremumFinder;

/**
 * Robust focus finder based on the {code TheilSenRobustExtremumFinder}.
 * 
 * @author royer
 */
public class RobustThielSenFocusFinder implements
																			FocusFinderInterface<Double>
{
	private static final int cNumberOfTriplets = 1000;

	private final FocusFinderInterface<Double> mFocusFinder;
	private final double mRefinementRadius;

	private final TheilSenRobustExtremumFinder mTheilSenRobustExtremumFinder;
	private final TDoubleDoubleHashMap mImagePositionToFocusValueMap = new TDoubleDoubleHashMap();

	private boolean isUpToDate = false;
	private double mBestPositionAfterRefinement;

	/**
	 * Constructs a robust Thiel-Sen focus finder from a focus measure enum and
	 * refinement radius parameter.
	 * 
	 * @param pFocusMeasure
	 *          focus measure
	 * @param pRefinementRadius
	 *          refinement radius
	 */
	public RobustThielSenFocusFinder(	final FocusMeasure pFocusMeasure,
																		final double pRefinementRadius)
	{
		this(	new SimpleFocusFinder<Double>(pFocusMeasure),
					pRefinementRadius);
	}

	/**
	 * Constructs a robust Thiel-Sen focus finder from a focus finder
	 * implementation and refinement radius.
	 * 
	 * @param pFocusFinderImplementation
	 *          focus measure implementation
	 * @param pRefinementRadius
	 *          refinement radius
	 */
	public RobustThielSenFocusFinder(	final FocusFinderInterface<Double> pFocusFinderImplementation,
																		final double pRefinementRadius)
	{
		this(	pFocusFinderImplementation,
					pRefinementRadius,
					cNumberOfTriplets);
	}

	/**
	 * Constructs a robust Thiel-Sen focus finder from a focus finder
	 * implementation, refinement radius and number of triplets.
	 * 
	 * @param pRobustFocusFinder
	 *          robust focues finder
	 * @param pRefinementRadius
	 *          refinement radius
	 * @param pNumberOfTriplets
	 *          number of triplets
	 */
	public RobustThielSenFocusFinder(	final FocusFinderInterface<Double> pRobustFocusFinder,
																		final double pRefinementRadius,
																		final int pNumberOfTriplets)
	{
		super();
		mFocusFinder = pRobustFocusFinder;
		mRefinementRadius = pRefinementRadius;
		mTheilSenRobustExtremumFinder = new TheilSenRobustExtremumFinder(pNumberOfTriplets);
		clear();
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getName()
	 */
	@Override
	public String getName()
	{
		return String.format(	"RobustFocusFinder(%s)",
													mFocusFinder.getName());
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#clear()
	 */
	@Override
	public void clear()
	{
		isUpToDate = false;
		mFocusFinder.clear();
		mTheilSenRobustExtremumFinder.reset();
		mImagePositionToFocusValueMap.clear();
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
		final double lFocusValue = mFocusFinder.addImage(	pPosition,
																											pDoubleArrayImage);

		mImagePositionToFocusValueMap.put(pPosition, lFocusValue);

		return lFocusValue;
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#removeImage(java.lang.Object)
	 */
	@Override
	public void removeImage(final Double pPosition)
	{
		isUpToDate = false;
		mImagePositionToFocusValueMap.remove(pPosition);
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getBestPosition()
	 */
	@Override
	public Double getBestPosition()
	{
		ensureUpToDate();
		return mBestPositionAfterRefinement;
	}

	private void ensureUpToDate()
	{
		if (!isUpToDate)
		{
			final Double lBestPosition = mFocusFinder.getBestPosition();

			mTheilSenRobustExtremumFinder.reset();

			mImagePositionToFocusValueMap.forEachEntry(new TDoubleDoubleProcedure()
			{
				@Override
				public final boolean execute(	final double pPosition,
																			final double pFocusValue)
				{
					if (Math.abs(lBestPosition - pPosition) < mRefinementRadius)
					{
						mTheilSenRobustExtremumFinder.enter(pPosition,
																								pFocusValue);
					}
					return true;
				}
			});

			mBestPositionAfterRefinement = mTheilSenRobustExtremumFinder.getResult();
			isUpToDate = true;
		}
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getBestFocusMeasure()
	 */
	@Override
	public double getBestFocusMeasure()
	{
		return mFocusFinder.getBestFocusMeasure();
	}

	/**
	 * @see autopilot.finder.FocusFinderInterface#getFocusAmplitude()
	 */
	@Override
	public double getFocusAmplitude()
	{
		return mFocusFinder.getFocusAmplitude();
	}

}
