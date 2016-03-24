package autopilot.stackanalysis;

import gnu.trove.list.array.TDoubleArrayList;

public class ZPlaneAnalysisResult
{
	public int mTileWidth;
	public int mTileHeight;
	public int mTileStrideX;
	public int mTileStrideY;

	public double mZ;
	public TDoubleArrayList mXList = new TDoubleArrayList();
	public TDoubleArrayList mYList = new TDoubleArrayList();
	public TDoubleArrayList mVList = new TDoubleArrayList();
	public double mMetric;

	public void add(double pI, double pJ, double pValue)
	{
		mXList.add(pI);
		mYList.add(pJ);
		mVList.add(pValue);
	}

	@Override
	public String toString()
	{
		return String.format(	"ZPlaneAnalysisResult [mZ=%s, mXList=%s, mYList=%s, mVList=%s, mMetric=%s]",
													mZ,
													mXList,
													mYList,
													mVList,
													mMetric);
	}

}
