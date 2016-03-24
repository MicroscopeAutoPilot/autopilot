package autopilot.stackanalysis.plane2d;

import gnu.trove.list.array.TDoubleArrayList;

public interface Plane2DFitInterface
{

	void addPoint(double pX, double pY, double pZ);

	void clear();

	TDoubleArrayList getListX();

	TDoubleArrayList getListY();

	TDoubleArrayList getListZ();

	int getNumberOfDataPoints();

	FitResult fit();

	int getMinimumNumberOfInliers();

	void setMinimumNumberOfInliers(int pMinimumNumberOfInliers);

}