package autopilot.fmatrix.vector.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import autopilot.fmatrix.vector.ObservationVector;

public class ObservationVectorTests
{
	private void println(Object pObject)
	{
		// System.out.println(pObject);
	}

	@Test
	public void testSimple()
	{
		final ObservationVector state = new ObservationVector(1,
																													1,
																													1,
																													1,
																													1,
																													4);

		state.setObservationZ(0, 0, 0, 0, 1);
		state.setObservationIlluminationX(0, 0, 0, 2);
		state.setObservationIlluminationAlpha(0, 0, 0, 3);
		state.setObservationIlluminationBeta(0, 0, 0, 4);

		assertEquals(4, state.getNumElements());

		println(state);

		for (int i = 0; i < state.getNumElements(); i++)
		{
			assertEquals(state.getValueAt(i), i + 1, 0);
		}

	}

	@Test
	public void test2()
	{
		final ObservationVector state = new ObservationVector(2,
																													2,
																													3,
																													1,
																													1,
																													4);

		int counter = 0;
		for (int w = 0; w < 2; w++)
		{
			for (int p = 0; p < 2; p++)
			{
				state.setObservationZ(w, p, 0, 0, ++counter);
				state.setObservationZ(w, p, 1, 0, ++counter);
				state.setObservationZ(w, p, 2, 0, ++counter);
				state.setObservationIlluminationX(w, p, 0, ++counter);
				state.setObservationIlluminationAlpha(w, p, 0, ++counter);
				state.setObservationIlluminationBeta(w, p, 0, ++counter);
			}
		}

		counter = 0;
		for (int w = 0; w < 2; w++)
		{
			for (int p = 0; p < 2; p++)
			{
				assertEquals(++counter, state.getObservationZ(w, p, 0, 0), 0);
				assertEquals(++counter, state.getObservationZ(w, p, 1, 0), 0);
				assertEquals(++counter, state.getObservationZ(w, p, 2, 0), 0);
				assertEquals(	++counter,
											state.getObservationIlluminationX(w, p, 0),
											0);
				assertEquals(	++counter,
											state.getObservationIlluminationAlpha(w, p, 0),
											0);
				assertEquals(	++counter,
											state.getObservationIlluminationBeta(w, p, 0),
											0);
			}
		}

		assertEquals(counter, state.getNumElements());

		println(state);

		for (int i = 0; i < state.getNumElements(); i++)
		{
			assertEquals(state.getValueAt(i), i + 1, 0);
		}

	}
}
