package autopilot.maxsearch;

/**
 * Uniform maximum search algorithm. It just takes equi-distant sample x_k
 * within [x_min,x_max], computes f(x_k), and find the arg max.
 * 
 * @author royer
 */
public class UniformMaximumSearch extends MaximumSearchAbstract	implements
																																MaximumSearchInterface
{

	public int mMaximumNumberOfEvaluations;
	private double mInflection = 1;
	protected double[] mY, mX;
	private double mLastBestY;

	/**
	 * Constructs a uniform maximum search algorithm object with an almost
	 * unlimited evaluations allowed.
	 */
	public UniformMaximumSearch()
	{
		this(Integer.MAX_VALUE);
	}

	/**
	 * Constructs a uniform maximum search algorithm object.
	 * 
	 * @param pMaximumNumberOfEvaluations
	 *          max number of evaluations
	 */
	public UniformMaximumSearch(final int pMaximumNumberOfEvaluations)
	{
		mMaximumNumberOfEvaluations = pMaximumNumberOfEvaluations;
	}

	@Override
	public double findMaximum(final Function pFunction,
														double pMin,
														double pMax,
														final double pPrecision)
	{
		pMin = Math.max(pMin, pFunction.getXMin());
		pMax = Math.min(pMax, pFunction.getXMax());

		final double lWidth = pMax - pMin;

		final double lMinimalPrecision = lWidth / (mMaximumNumberOfEvaluations - 1);

		final int lNumberOfPoints = 1 + (int) Math.round(lWidth / Math.max(	pPrecision,
																																				lMinimalPrecision));

		return simpleSearch(pFunction, pMin, pMax, lNumberOfPoints);
	}

	protected double simpleSearch(final Function pFunction,
																final double pMin,
																final double pMax,
																final int pNumberOfPoints)
	{
		mX = new double[pNumberOfPoints];
		for (int i = 0; i < pNumberOfPoints; i++)
		{
			final double xn = (double) i / (pNumberOfPoints - 1);
			final double xntransformed = 2 * (xn - 0.5);
			final double xninflection = Math.signum(xntransformed) * Math.pow(Math.abs(xntransformed),
																																				mInflection);
			final double xninversetransformed = 0.5 + 0.5 * xninflection;
			final double x = pMin + (pMax - pMin) * xninversetransformed;
			mX[i] = x;
		}
		mY = pFunction.f(mX);

		final double xm = argmax(mX, mY);

		return xm;
	}

	private double argmax(final double[] pX, final double[] pY)
	{
		double xm = Double.NaN;
		double ym = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			final double y = pY[i];
			if (y > ym)
			{
				ym = y;
				xm = pX[i];
			}
		}
		mLastBestY = ym;
		return xm;
	}

	public double getInflection()
	{
		return mInflection;
	}

	public void setInflection(final double inflection)
	{
		mInflection = inflection;
	}

	public double getLastBestY()
	{
		return mLastBestY;
	}

}
