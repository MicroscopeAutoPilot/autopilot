package autopilot.maxsearch;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TDoubleDoubleHashMap;

/**
 * Function implementation that wraps another function and provides caching
 * functionality.
 * 
 * @author royer
 */
public class FunctionCache implements Function
{
	private final Function mFunction;
	private final TDoubleDoubleHashMap mCache = new TDoubleDoubleHashMap();
	private final double mEpsilon;

	/**
	 * Constructs a function cache from a given function and epsilon value. the
	 * parameter epsilon controls how close a value should be to be coalescesd
	 * with an existing cache entry.
	 * 
	 * @param pFunction
	 * @param pEpsilon
	 */
	FunctionCache(final Function pFunction, final double pEpsilon)
	{
		super();
		mFunction = pFunction;
		mEpsilon = pEpsilon;
	}

	/**
	 * Clears the cache.
	 */
	public void clear()
	{
		mCache.clear();
	}

	/**
	 * Returns the number of entries in the cache.
	 * 
	 * @return number of entries
	 */
	public int getCacheSize()
	{
		return mCache.size();
	};

	/**
	 * @see autopilot.maxsearch.Function#f(double)
	 */
	@Override
	public double f(final double pX)
	{
		if (isInCache(pX))
		{
			final double lY = queryCache(pX);
			return lY;
		}
		else
		{
			final double lY = mFunction.f(pX);
			mCache.put(pX, lY);
			return lY;
		}
	}

	/**
	 * @see autopilot.maxsearch.Function#f(double[])
	 */
	@Override
	public double[] f(final double... pX)
	{
		// This a very fancy optimization: we figure out hat is already in the
		// cache,
		// and only call the array function evaluation on the non cached items.
		// This way we make sure that if the underlying f(double[]) is somehow
		// optimized,
		// then we still take advantage of it...

		final double[] y = new double[pX.length];
		final TDoubleArrayList lMissingX = new TDoubleArrayList();

		int i = 0;
		for (final double x : pX)
		{
			if (isInCache(x))
			{
				y[i] = queryCache(x);
			}
			else
			{
				lMissingX.add(x);
			}
			i++;
		}

		if (lMissingX.size() > 0)
		{
			final double[] lMissingXArray = lMissingX.toArray();
			final double[] lMissingYArray = mFunction.f(lMissingXArray);

			i = 0;
			int j = 0;
			for (final double x : pX)
			{
				if (!isInCache(x))
				{
					y[i] = lMissingYArray[j++];
					mCache.put(x, y[i]);
				}
				i++;
			}
		}

		return y;
	}

	/**
	 * Queries the cache for an existing entry.
	 * 
	 * @param pX
	 *          x
	 * @return cached value for f(x)
	 */
	private double queryCache(final double pX)
	{
		if (mCache.containsKey(pX))
		{
			return mCache.get(pX);
		}
		else if (mEpsilon > 0)
		{
			final double[] lKeys = mCache.keys();
			boolean found = false;
			double yfound = 0;
			for (final double x : lKeys)
			{
				if (Math.abs(x - pX) < mEpsilon)
				{
					yfound = mCache.get(x);
					found = true;
					break;
				}
			}
			if (found)
			{
				return yfound;
			}
			else
			{
				throw new IndexOutOfBoundsException("Not in Cache!");
				// return Double.NaN;
			}
		}
		else
		{
			throw new RuntimeException("Could not find key!");
		}
	}

	/**
	 * Checks if there is an entry in the cache for x
	 * 
	 * @param pX
	 *          x value
	 * @return true if f(x) is in the cache.
	 */
	public boolean isInCache(final double pX)
	{
		if (mCache.containsKey(pX))
		{
			return true;
		}
		else if (mEpsilon > 0)
		{
			final double[] lKeys = mCache.keys();
			for (final double x : lKeys)
			{
				if (Math.abs(x - pX) < mEpsilon)
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see autopilot.maxsearch.Function#getXMin()
	 */
	@Override
	public double getXMin()
	{
		return mFunction.getXMin();
	}

	/**
	 * @see autopilot.maxsearch.Function#getXMax()
	 */
	@Override
	public double getXMax()
	{
		return mFunction.getXMax();
	}

	/**
	 * Retuns all x values stored in the cache
	 * 
	 * @return array of x values
	 */
	public double[] getAllCachedX()
	{
		return mCache.keys();
	}

	/**
	 * Returns the internal map containing cached x and f(x) pairs.
	 * 
	 * @return hashmap
	 */
	public TDoubleDoubleHashMap getCacheMap()
	{
		return mCache;
	}

	/**
	 * Wraps a caching function around a function f
	 * 
	 * @param pFunction
	 *          function to be wrapped
	 * @param pEpsilon
	 *          epsilon parameter
	 * @return cached function
	 */
	public static FunctionCache wrap(	final Function pFunction,
																		final double pEpsilon)
	{
		return new FunctionCache(pFunction, pEpsilon);
	}

	@Override
	public String toString()
	{
		return String.format("FunctionCache [mFunction=%s]", mFunction);
	}

}
