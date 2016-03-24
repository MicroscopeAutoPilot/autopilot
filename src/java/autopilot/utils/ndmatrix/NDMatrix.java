package autopilot.utils.ndmatrix;

import java.util.ArrayList;
import java.util.Arrays;

public class NDMatrix<O>
{
	ArrayList<O> mMatrix;
	private final int[] mDimensions;

	public NDMatrix(final int... pDimensions)
	{
		super();
		mDimensions = pDimensions;
		final int lLength = getSize();
		mMatrix = new ArrayList<O>(lLength);
		for (int i = 0; i < lLength; i++)
		{
			mMatrix.add(null);
		}
	}

	public int getSize()
	{
		int lLength = 1;
		for (final int lDimension : mDimensions)
		{
			lLength *= lDimension;
		}

		return lLength;
	}

	public int getIndex(final int... pCoordinate)
	{
		int lIndex = 0;
		int lFactor = 1;
		for (int i = 0; i < mDimensions.length; i++)
		{
			lFactor *= i == 0 ? 1 : mDimensions[i - 1];
			lIndex += pCoordinate[i] * lFactor;
		}
		return lIndex;
	}

	private int[] clip(final int[] pCoordinate)
	{
		for (int i = 0; i < mDimensions.length; i++)
		{
			if (pCoordinate[i] < 0)
			{
				pCoordinate[i] = 0;
			}
			else if (pCoordinate[i] >= mDimensions[i])
			{
				pCoordinate[i] = mDimensions[i] - 1;
			}
		}
		return pCoordinate;
	}

	private boolean isInside(final int[] pCoordinate)
	{
		for (int i = 0; i < mDimensions.length; i++)
		{
			if (pCoordinate[i] < 0)
			{
				return false;
			}
			else if (pCoordinate[i] >= mDimensions[i])
			{
				return false;
			}
		}
		return true;
	}

	public int[] getDimensions()
	{
		return mDimensions;
	}

	public int getDimension(final int pIndex)
	{
		return mDimensions[pIndex];
	}

	public void set(final O pItem, final int... pCoordinate)
	{
		final int lIndex = getIndex(pCoordinate);
		mMatrix.set(lIndex, pItem);
	}

	public O get(final int... pCoordinate)
	{
		final int lIndex = getIndex(pCoordinate);
		final O lO = mMatrix.get(lIndex);
		return lO;
	}

	public O clipAndGet(int... pCoordinate)
	{
		pCoordinate = clip(pCoordinate);
		return get(pCoordinate);
	}

	public O nullOrGet(final int... pCoordinate)
	{
		if (!isInside(pCoordinate))
			return null;

		return get(pCoordinate);
	}

	@Override
	public String toString()
	{
		return String.format(	"NDMatrix [mDimensions=%s, mMatrix=%s]",
													Arrays.toString(mDimensions),
													mMatrix);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(mDimensions);
		result = prime * result
							+ (mMatrix == null ? 0 : mMatrix.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final NDMatrix other = (NDMatrix) obj;
		if (!Arrays.equals(mDimensions, other.mDimensions))
		{
			return false;
		}
		if (mMatrix == null)
		{
			if (other.mMatrix != null)
			{
				return false;
			}
		}
		else if (!mMatrix.equals(other.mMatrix))
		{
			return false;
		}
		return true;
	}

}
