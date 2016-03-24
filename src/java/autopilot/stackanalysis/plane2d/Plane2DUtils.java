package autopilot.stackanalysis.plane2d;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import autopilot.stackanalysis.plane2d.xi2reg.Xi2RegPlane2DFitDistribution;
import gnu.trove.list.array.TDoubleArrayList;

public class Plane2DUtils
{

	public static FitResult averageFitResults(	ArrayList<FitResult> pFitResultList,
												boolean lUnionInlierSets,
												boolean pMedian)
	{
		FitResult lAverageFitResult = new FitResult();

		TDoubleArrayList lMS = new TDoubleArrayList();
		TDoubleArrayList lPValue = new TDoubleArrayList();
		TDoubleArrayList lZScore = new TDoubleArrayList();

		TDoubleArrayList lRegParams0 = new TDoubleArrayList();
		TDoubleArrayList lRegParams1 = new TDoubleArrayList();
		TDoubleArrayList lRegParams2 = new TDoubleArrayList();

		if (lUnionInlierSets)
			lAverageFitResult.inliers = new boolean[pFitResultList.get(0).inliers.length];
		else
			lAverageFitResult.inliers = Arrays.copyOf(	pFitResultList.get(0).inliers,
														pFitResultList.get(0).inliers.length);

		for (FitResult lFitResult : pFitResultList)
		{
			lMS.add(lFitResult.ms);
			lPValue.add(lFitResult.pvalue);
			lZScore.add(lFitResult.zscore);

			lRegParams0.add(lFitResult.regparams[0]);
			lRegParams1.add(lFitResult.regparams[1]);
			lRegParams2.add(lFitResult.regparams[2]);

			// System.out.println(toDegrees(atan(lFitResult.regparams[2] /
			// 0.406)));

			if (lUnionInlierSets)
				for (int i = 0; i < lAverageFitResult.inliers.length; i++)
					lAverageFitResult.inliers[i] |= lFitResult.inliers[i];
			else
				for (int i = 0; i < lAverageFitResult.inliers.length; i++)
					lAverageFitResult.inliers[i] &= lFitResult.inliers[i];

		}

		lAverageFitResult.ms = StatUtils.percentile(lMS.toArray(), 50);
		lAverageFitResult.pvalue = StatUtils.percentile(lPValue.toArray(),
														50);
		lAverageFitResult.zscore = StatUtils.percentile(lZScore.toArray(),
														50);

		lAverageFitResult.regparams = new double[3];

		if (pMedian)
		{
			lAverageFitResult.regparams[0] = StatUtils.percentile(	lRegParams0.toArray(),
																	50);
			lAverageFitResult.regparams[1] = StatUtils.percentile(	lRegParams1.toArray(),
																	50);
			lAverageFitResult.regparams[2] = StatUtils.percentile(	lRegParams2.toArray(),
																	50);
		}
		else
		{
			lAverageFitResult.regparams[0] = StatUtils.mean(lRegParams0.toArray());
			lAverageFitResult.regparams[1] = StatUtils.mean(lRegParams1.toArray());
			lAverageFitResult.regparams[2] = StatUtils.mean(lRegParams2.toArray());
		}

		// System.out.println("average="+toDegrees(atan(lAverageFitResult.regparams[2]
		// / 0.406)));

		return lAverageFitResult;
	}

	static public final FitResult fit(	TDoubleArrayList pX,
										TDoubleArrayList pY,
										TDoubleArrayList pZ,
										boolean[] pInliers)
	{
		try
		{
			int lNumberOfInliers = 0;
			for (int i = 0; i < pX.size(); i++)
				if (pInliers[i])
					lNumberOfInliers++;

			final double[] lData = new double[3 * lNumberOfInliers];

			for (int i = 0, j = 0; i < pX.size(); i++)
				if (pInliers[i])
				{
					lData[3 * j + 0] = pZ.get(i);
					lData[3 * j + 1] = pX.get(i);
					lData[3 * j + 2] = pY.get(i);
					j++;
				}

			final long lStartTimeNs = System.nanoTime();

			final OLSMultipleLinearRegression lOLSMultipleLinearRegression = new OLSMultipleLinearRegression();
			lOLSMultipleLinearRegression.setNoIntercept(false);
			lOLSMultipleLinearRegression.newSampleData(	lData,
														lNumberOfInliers,
														2);

			final double[] lEstimateRegressionParameters = lOLSMultipleLinearRegression.estimateRegressionParameters();

			final long lStopTimeNs1 = System.nanoTime();
			final double lElapsedTimeInSeconds1 = 0.001 * 0.001 * 0.001 * (lStopTimeNs1 - lStartTimeNs);
			/*System.out.format("elapsed time for fitting: %g s \n",
												lElapsedTimeInSeconds1);/**/

			final double lZMax = pZ.max();
			final double lZMin = pZ.min();
			final double lZAmplitude = lZMax - lZMin;

			final double lOneMinusRSquaredNormalized = lOLSMultipleLinearRegression.calculateResidualSumOfSquares() / (lNumberOfInliers * lZAmplitude * lZAmplitude);

			final Xi2RegPlane2DFitDistribution mPlane2DFitDistributions = new Xi2RegPlane2DFitDistribution();

			final double lPValue = mPlane2DFitDistributions.getPValue(	pInliers.length,
																		lNumberOfInliers,
																		lOneMinusRSquaredNormalized);

			final double lZScore = mPlane2DFitDistributions.getZScore(	lNumberOfInliers,
																		lOneMinusRSquaredNormalized);

			final long lStopTimeNs2 = System.nanoTime();
			final double lElapsedTimeInSeconds2 = 0.001 * 0.001 * 0.001 * (lStopTimeNs2 - lStartTimeNs);
			/*System.out.format("elapsed time for pvalue: %g s \n",
												lElapsedTimeInSeconds2);/**/

			final FitResult lFitResult = new FitResult();
			lFitResult.ms = lOneMinusRSquaredNormalized;
			lFitResult.pvalue = lPValue;
			lFitResult.zscore = lZScore;
			lFitResult.regparams = lEstimateRegressionParameters;
			lFitResult.inliers = pInliers;
			return lFitResult;
		}
		catch (final InsufficientDataException e)
		{
			return null;
		}
		catch (final SingularMatrixException e)
		{
			return null;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
