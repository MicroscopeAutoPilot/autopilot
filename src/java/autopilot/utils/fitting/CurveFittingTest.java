package autopilot.utils.fitting;

import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.junit.Test;

public class CurveFittingTest
{

	@Test
	public void test()
	{
		ParametricUnivariateFunction lParametricUnivariateFunction = new ParametricUnivariateFunction()
		{

			@Override
			public double value(double pX, double... pParameters)
			{
				double a = pParameters[0];
				double b = pParameters[1];
				double c = pParameters[2];

				return a * pX + b + c * pX / sqrt(0.01 + pX * pX);
			}

			@Override
			public double[] gradient(double pX, double... pParameters)
			{
				double da = pX;
				double db = 1;
				double dc = pX / sqrt(0.01 + pX * pX);

				return new double[]
				{ da, db, dc };
			}
		};

		SimpleCurveFitter lSimpleCurveFitter = SimpleCurveFitter.create(lParametricUnivariateFunction,
																		new double[]
																		{	0,
																			0,
																			0 });

		double a = 0.0;
		double b = 0;
		double u = 0.3;
		double v = -u;

		ArrayList<WeightedObservedPoint> lPointList = new ArrayList<>();
		for (double i = -1; i <= 1; i += 0.3)
		{
			double x = i;
			double y = i * a + b + (i <= 0 ? u : v);
			System.out.format("%g\t%g\n",x,y);
			WeightedObservedPoint lWeightedObservedPoint = new WeightedObservedPoint(	1,
																						x,
																						y);
			lPointList.add(lWeightedObservedPoint);
		}

		double[] lFit = lSimpleCurveFitter.fit(lPointList);

		System.out.println("fit:" + Arrays.toString(lFit));

	}
}
