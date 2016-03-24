package autopilot.interfaces;

import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import autopilot.fmatrix.vector.ObservationVector;

public class SmoothObservations
{

	protected static final double cSharpness = 0.001;

	public static void betaAngleSmoothing(	ObservationVector pObservationVector,
											boolean[] pMissingObservations)
	{
		int lNumberOfWavelengths = pObservationVector.getNumberOfWavelengths();
		int lNumberOfPlanes = pObservationVector.getNumberOfPlanes();
		int lNumberOfLightsheets = pObservationVector.getNumberOfLightsheets();

		boolean[] lMissingArray = new boolean[lNumberOfPlanes];
		double[] lBetaArray = new double[lNumberOfPlanes];

		for (int w = 0; w < lNumberOfWavelengths; w++)
			for (int l = 0; l < lNumberOfLightsheets; l++)
			{

				for (int p = 0; p < lNumberOfPlanes; p++)
				{
					Double beta = pObservationVector.getObservationIlluminationBeta(w,
																					p,
																					l);
					if (beta != null && !Double.isNaN(beta))
					{
						lBetaArray[p] = beta;

						int lObservationIlluminationAlphaIndex = pObservationVector.getObservationIlluminationBetaIndex(w,
																														p,
																														l);
						lMissingArray[p] = pMissingObservations[lObservationIlluminationAlphaIndex];
					}
					else
					{
						lBetaArray[p] = 0;
						lMissingArray[p] = true;
					}
				}

				linearStepSmoothing(lBetaArray,
									lBetaArray,
									lMissingArray);

				for (int p = 0; p < lNumberOfPlanes; p++)
					pObservationVector.setObservationIlluminationBeta(	w,
																		p,
																		l,
																		lBetaArray[p]);

			}
	}

	public static double[] linearStepSmoothing(	double[] pOrgArray,
												double[] pDestArray,
												boolean[] pMissingArray)
	{
		ParametricUnivariateFunction lParametricUnivariateFunction = new ParametricUnivariateFunction()
		{

			@Override
			public double value(double pX, double... pParameters)
			{
				double a = pParameters[0];
				double b = pParameters[1];
				double c = pParameters[2];

				return a * pX
						+ b
						+ c
						* pX
						/ sqrt(cSharpness + pX * pX);
			}

			@Override
			public double[] gradient(double pX, double... pParameters)
			{
				double da = pX;
				double db = 1;
				double dc = pX / sqrt(cSharpness + pX * pX);

				return new double[]
				{ da, db, dc };
			}
		};

		SimpleCurveFitter lSimpleCurveFitter = SimpleCurveFitter.create(lParametricUnivariateFunction,
																		new double[]
																		{	0,
																			0,
																			0 });

		ArrayList<WeightedObservedPoint> lPointList = new ArrayList<>();
		for (int i = 0; i < pOrgArray.length; i++)
		{
			double x = (2.0 * i) / (pOrgArray.length - 1) - 1;
			double y = pOrgArray[i];
			double w = (pMissingArray[i]||(y==0)) ? 0 : 1;
			WeightedObservedPoint lWeightedObservedPoint = new WeightedObservedPoint(	w,
																						x,
																						y);
			lPointList.add(lWeightedObservedPoint);
			// System.out.format("%g\t%g\n", x, y);
		}

		double[] lFit = lSimpleCurveFitter.fit(lPointList);
		//System.out.println("fit:" + Arrays.toString(lFit));

		for (int i = 0; i < pOrgArray.length; i++)
		{
			double x = (2.0 * i) / (pOrgArray.length - 1) - 1;
			double y = lParametricUnivariateFunction.value(x, lFit);
			pDestArray[i] = y;
			// System.out.format("%g\t%g\n", x, y);
		}

		return pDestArray;

	}

}
