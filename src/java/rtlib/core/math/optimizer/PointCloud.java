package rtlib.core.math.optimizer;

import gnu.trove.list.array.TDoubleArrayList;

public class PointCloud
{
	private final int mInputDimension, mOutputDimension;
	TDoubleArrayList mPointCloudData;

	public PointCloud(int pInputDimension, int pOutputDimension)
	{
		super();
		mInputDimension = pInputDimension;
		mOutputDimension = pOutputDimension;
	}

	public void addPoint(double[] pInputVector, double[] pOutputVector)
	{
		for (double lValue : pInputVector)
			mPointCloudData.add(lValue);
		for (double lValue : pOutputVector)
			mPointCloudData.add(lValue);
	}

	public void getPoint(int pPointIndex, double[] pPoint)
	{
		mPointCloudData.toArray(pPoint,
								pPointIndex * (mInputDimension + mOutputDimension),
								mInputDimension);
	}

	public double getValue(int pPointIndex, int pValueIndex)
	{
		return mPointCloudData.get(pPointIndex * (mInputDimension + mOutputDimension)
									+ mInputDimension
									+ pValueIndex);
	}

	public void getKClosestPoints(final int pK, int[] pPointIndices)
	{

	}

}
