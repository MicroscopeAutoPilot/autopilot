package autopilot.fmatrix.vector.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import autopilot.fmatrix.vector.StateVector;

public class StateVectorTests
{

	private void println(Object pObject)
	{
		// System.out.println(pObject);
	}

	@Test
	public void testArrayConversion()
	{
		final StateVector v = new StateVector(1, 1, 1, 4, 1, 4);

		final double[] array = new double[v.getNumElements()];
		for (int i = 0; i < v.getNumElements(); i++)
		{
			array[i] = i;
		}

		println(v);
		v.getFrom(array);
		println(v);

		for (int i = 0; i < v.getNumElements(); i++)
		{
			final double lValue = v.getValueAt(i);
			assertEquals(i, lValue, 0);
			v.setValueAt(i, lValue + 1);
		}
		println(v);

		v.copyTo(array);
		println(Arrays.toString(array));

		for (int i = 0; i < v.getNumElements(); i++)
		{
			final double lValue = array[i];
			assertEquals(i, lValue - 1, 0);
		}
		println(v);

	}

	@Test
	public void testSingleColorSinglePlane()
	{
		final StateVector state = new StateVector(1, 1, 2, 3, 1, 4);

		assertEquals(1 * 1 * (2 + 3 * 4), state.getNumElements(), 0);

		state.setDetectionZ(0, 0, 0, 1);
		state.setDetectionZ(0, 0, 1, 2);

		state.setIlluminationZ(0, 0, 0, 3);
		state.setIlluminationZ(0, 0, 1, 4);
		state.setIlluminationZ(0, 0, 2, 5);

		state.setIlluminationX(0, 0, 0, 6);
		state.setIlluminationX(0, 0, 1, 7);
		state.setIlluminationX(0, 0, 2, 8);

		state.setIlluminationAlpha(0, 0, 0, 9);
		state.setIlluminationAlpha(0, 0, 1, 10);
		state.setIlluminationAlpha(0, 0, 2, 11);

		state.setIlluminationBeta(0, 0, 0, 12);
		state.setIlluminationBeta(0, 0, 1, 13);
		state.setIlluminationBeta(0, 0, 2, 14);

		assertEquals(1, state.getDetectionZ(0, 0, 0), 0);
		assertEquals(2, state.getDetectionZ(0, 0, 1), 0);

		assertEquals(3, state.getIlluminationZ(0, 0, 0), 0);
		assertEquals(4, state.getIlluminationZ(0, 0, 1), 0);
		assertEquals(5, state.getIlluminationZ(0, 0, 2), 0);

		assertEquals(6, state.getIlluminationX(0, 0, 0), 0);
		assertEquals(7, state.getIlluminationX(0, 0, 1), 0);
		assertEquals(8, state.getIlluminationX(0, 0, 2), 0);

		assertEquals(9, state.getIlluminationAlpha(0, 0, 0), 0);
		assertEquals(10, state.getIlluminationAlpha(0, 0, 1), 0);
		assertEquals(11, state.getIlluminationAlpha(0, 0, 2), 0);

		assertEquals(12, state.getIlluminationBeta(0, 0, 0), 0);
		assertEquals(13, state.getIlluminationBeta(0, 0, 1), 0);
		assertEquals(14, state.getIlluminationBeta(0, 0, 2), 0);

		for (int i = 0; i < state.getNumElements(); i++)
		{
			assertEquals(state.getValueAt(i), i + 1, 0);
		}

	}

	@Test
	public void testThreeColorTwoPlanes()
	{
		final StateVector state = new StateVector(3, 2, 1, 1, 1, 4);

		assertEquals(3 * 2 * (1 + 4), state.getNumElements(), 0);

		state.setDetectionZ(0, 0, 0, 1);
		state.setIlluminationZ(0, 0, 0, 2);
		state.setIlluminationX(0, 0, 0, 3);
		state.setIlluminationAlpha(0, 0, 0, 4);
		state.setIlluminationBeta(0, 0, 0, 5);

		state.setDetectionZ(0, 1, 0, 6);
		state.setIlluminationZ(0, 1, 0, 7);
		state.setIlluminationX(0, 1, 0, 8);
		state.setIlluminationAlpha(0, 1, 0, 9);
		state.setIlluminationBeta(0, 1, 0, 10);

		state.setDetectionZ(1, 0, 0, 11);
		state.setIlluminationZ(1, 0, 0, 12);
		state.setIlluminationX(1, 0, 0, 13);
		state.setIlluminationAlpha(1, 0, 0, 14);
		state.setIlluminationBeta(1, 0, 0, 15);

		state.setDetectionZ(1, 1, 0, 16);
		state.setIlluminationZ(1, 1, 0, 17);
		state.setIlluminationX(1, 1, 0, 18);
		state.setIlluminationAlpha(1, 1, 0, 19);
		state.setIlluminationBeta(1, 1, 0, 20);

		state.setDetectionZ(2, 0, 0, 21);
		state.setIlluminationZ(2, 0, 0, 22);
		state.setIlluminationX(2, 0, 0, 23);
		state.setIlluminationAlpha(2, 0, 0, 24);
		state.setIlluminationBeta(2, 0, 0, 25);

		state.setDetectionZ(2, 1, 0, 26);
		state.setIlluminationZ(2, 1, 0, 27);
		state.setIlluminationX(2, 1, 0, 28);
		state.setIlluminationAlpha(2, 1, 0, 29);
		state.setIlluminationBeta(2, 1, 0, 30);

		assertEquals(1, state.getDetectionZ(0, 0, 0), 0);
		assertEquals(2, state.getIlluminationZ(0, 0, 0), 0);
		assertEquals(3, state.getIlluminationX(0, 0, 0), 0);
		assertEquals(4, state.getIlluminationAlpha(0, 0, 0), 0);
		assertEquals(5, state.getIlluminationBeta(0, 0, 0), 0);

		assertEquals(6, state.getDetectionZ(0, 1, 0), 0);
		assertEquals(7, state.getIlluminationZ(0, 1, 0), 0);
		assertEquals(8, state.getIlluminationX(0, 1, 0), 0);
		assertEquals(9, state.getIlluminationAlpha(0, 1, 0), 0);
		assertEquals(10, state.getIlluminationBeta(0, 1, 0), 0);

		assertEquals(11, state.getDetectionZ(1, 0, 0), 0);
		assertEquals(12, state.getIlluminationZ(1, 0, 0), 0);
		assertEquals(13, state.getIlluminationX(1, 0, 0), 0);
		assertEquals(14, state.getIlluminationAlpha(1, 0, 0), 0);
		assertEquals(15, state.getIlluminationBeta(1, 0, 0), 0);

		assertEquals(16, state.getDetectionZ(1, 1, 0), 0);
		assertEquals(17, state.getIlluminationZ(1, 1, 0), 0);
		assertEquals(18, state.getIlluminationX(1, 1, 0), 0);
		assertEquals(19, state.getIlluminationAlpha(1, 1, 0), 0);
		assertEquals(20, state.getIlluminationBeta(1, 1, 0), 0);

		assertEquals(21, state.getDetectionZ(2, 0, 0), 0);
		assertEquals(22, state.getIlluminationZ(2, 0, 0), 0);
		assertEquals(23, state.getIlluminationX(2, 0, 0), 0);
		assertEquals(24, state.getIlluminationAlpha(2, 0, 0), 0);
		assertEquals(25, state.getIlluminationBeta(2, 0, 0), 0);

		assertEquals(26, state.getDetectionZ(2, 1, 0), 0);
		assertEquals(27, state.getIlluminationZ(2, 1, 0), 0);
		assertEquals(28, state.getIlluminationX(2, 1, 0), 0);
		assertEquals(29, state.getIlluminationAlpha(2, 1, 0), 0);
		assertEquals(30, state.getIlluminationBeta(2, 1, 0), 0);

		println(state);

		for (int i = 0; i < state.getNumElements(); i++)
		{
			assertEquals(state.getValueAt(i), i + 1, 0);
		}

	}
}
