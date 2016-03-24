package autopilot.maxsearch;

/**
 * Golden ratio search.
 * 
 * @author royer
 */
public class GoldenRatioMaximumSearch extends MaximumSearchAbstract	implements
																																		MaximumSearchInterface
{
	private static final double phi = (1 + Math.sqrt(5)) / 2;
	private static final double resphi = 2 - phi;
	private final int mMaxNumberOfEvaluations;

	/**
	 * Constructs a Golden ratio search maximum search with unlimited number of
	 * evaluations.
	 */
	public GoldenRatioMaximumSearch()
	{
		this(Integer.MAX_VALUE);
	}

	/**
	 * Constructs a Golden ratio search maximum search with a given maximal number
	 * of evaluations.
	 * 
	 * @param pMaxNumberOfEvaluations
	 *          maximal number of evaluations
	 */
	public GoldenRatioMaximumSearch(final int pMaxNumberOfEvaluations)
	{
		mMaxNumberOfEvaluations = pMaxNumberOfEvaluations;
	}

	/**
	 * @see autopilot.maxsearch.MaximumSearchAbstract#findMaximum(autopilot.maxsearch.Function,
	 *      double, double, double)
	 */
	@Override
	public double findMaximum(final Function pFunction,
														double pMin,
														double pMax,
														final double pPrecision)
	{
		pMin = Math.max(pMin, pFunction.getXMin());
		pMax = Math.min(pMax, pFunction.getXMax());

		final double lCenter = 0.5 * (pMin + pMax);
		return goldenSectionSearch(	pFunction,
																pMin,
																lCenter,
																pMax,
																pPrecision,
																mMaxNumberOfEvaluations);
	}

	// Taken from http://en.wikipedia.org/wiki/Golden_section_search
	// pMin and pMax are the current bounds; the minimum is between them.
	// pCenter is a center point
	// f(x) is some mathematical function elsewhere defined
	private final double goldenSectionSearch(	final Function pFunction,
																						final double pMin,
																						final double pCenter,
																						final double pMax,
																						final double pPrecision,
																						final int pMaximuNumberOfEvaluations)
	{
		if (Math.abs(pMax - pMin) < pPrecision || pMaximuNumberOfEvaluations <= 0)
		{
			return (pMax + pMin) / 2;
		}

		double x;
		if (pMax - pCenter > pCenter - pMin)
		{
			x = pCenter + resphi * (pMax - pCenter);
		}
		else
		{
			x = pCenter - resphi * (pCenter - pMin);
		}

		if (pFunction.f(x) > pFunction.f(pCenter))
		{
			if (pMax - pCenter > pCenter - pMin)
			{
				return goldenSectionSearch(	pFunction,
																		pCenter,
																		x,
																		pMax,
																		pPrecision,
																		pMaximuNumberOfEvaluations - 1);
			}
			else
			{
				return goldenSectionSearch(	pFunction,
																		pMin,
																		x,
																		pCenter,
																		pPrecision,
																		pMaximuNumberOfEvaluations - 1);
			}
		}
		else
		{
			if (pMax - pCenter > pCenter - pMin)
			{
				return goldenSectionSearch(	pFunction,
																		pMin,
																		pCenter,
																		x,
																		pPrecision,
																		pMaximuNumberOfEvaluations - 1);
			}
			else
			{
				return goldenSectionSearch(	pFunction,
																		x,
																		pCenter,
																		pMax,
																		pPrecision,
																		pMaximuNumberOfEvaluations - 1);
			}
		}
	}

}
