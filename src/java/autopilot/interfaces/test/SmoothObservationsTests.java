package autopilot.interfaces.test;

import org.junit.Test;

import autopilot.fmatrix.vector.ObservationVector;
import autopilot.interfaces.SmoothObservations;

public class SmoothObservationsTests
{

	@Test
	public void testLinearStepSmoothing()
	{
		double a = 0.0;
		double b = 0;
		double u = 0.3;
		double v = -u;

		double[] lArray = new double[6];
		boolean[] lMissingArray = new boolean[6];
		for (int i = 0; i < 6; i++)
		{
			double x = (2.0 * i) / (lArray.length - 1) - 1;
			double y = i * a + b + (x <= 0 ? u : v);
			lArray[i] = y;
			System.out.format("%g\t%g\n", x, y);
		}

		lArray[1] += 0.3;
		lMissingArray[1] = true;

		double[] lLinearStepSmoothing = SmoothObservations.linearStepSmoothing(	lArray,
																				lArray,
																				lMissingArray);

		System.out.println("____________________________________");
		for (int i = 0; i < 6; i++)
		{
			double x = (2.0 * i) / (lArray.length - 1) - 1;
			double y = lLinearStepSmoothing[i];

			System.out.format("%g\t%g\n", x, y);
		}
	}

	@Test
	public void testLinearStepSmoothingRealExample1()
	{

		double[] lArray = new double[]
		{ 1.004, 0.105, -0.097, 0.491, -1.276, -0.901, -0.697, -0.493 };
		testOnArray(lArray);

		lArray = new double[]
		{ -1.679 - 0.669, -0.092, -0.413, 0.886, 0.775, 0.161, 0.904 };
		testOnArray(lArray);
	}

	private void testOnArray(double[] lArray)
	{
		boolean[] lMissingArray = new boolean[8];

		double[] lLinearStepSmoothing = SmoothObservations.linearStepSmoothing(	lArray,
																				lArray,
																				lMissingArray);

		System.out.println("____________________________________");
		for (int i = 0; i < lArray.length; i++)
		{
			double x = (2.0 * i) / (lArray.length - 1) - 1;
			double y = lLinearStepSmoothing[i];

			System.out.format("%g\t%g\n", x, y);
		}
	}

	@Test
	public void testWithObservationVector()
	{
		int nbplanes = 6;
		ObservationVector lObservationVector = new ObservationVector(	1,
																		nbplanes,
																		2,
																		1,
																		1,
																		4);

		double a = 0.0;
		double b = 0;
		double u = 0.3;
		double v = -u;

		for (int i = 0; i < nbplanes; i++)
		{

			double x = (2.0 * i) / (nbplanes - 1) - 1;
			double y = i * a + b + (x <= 0 ? u : v);

			if (i == 1)
				y += 0.5;

			System.out.format("%d\t%g\t%g\n", i, x, y);
			lObservationVector.setObservationIlluminationBeta(	0,
																i,
																0,
																y);

		}

		boolean[] lMissingArray = new boolean[lObservationVector.getNumElements()];

		SmoothObservations.betaAngleSmoothing(	lObservationVector,
												lMissingArray);

		System.out.println("____________________________________");
		for (int i = 0; i < lObservationVector.getNumElements(); i++)
		{
			double y = lObservationVector.getValueAt(i);

			System.out.format("%g\n", y);
		}
	}

}
