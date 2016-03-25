package rtlib.core.variable.types.doublev.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static rtlib.core.units.Magnitude.Kilo;
import static rtlib.core.units.Magnitude.Nano;
import static rtlib.core.units.Magnitude.Unit;
import static rtlib.core.units.SIUnit.Meter;
import static rtlib.core.units.SIUnit.MicroMeter;

import org.junit.Test;

import rtlib.core.variable.exceptions.InvalidMagnitudeException;
import rtlib.core.variable.types.doublev.DoubleVariable;

public class DoubleVariableTests
{

	@Test
	public void testSyncWith()
	{
		final DoubleVariable x = new DoubleVariable("x", 0);
		final DoubleVariable y = new DoubleVariable("y", 0);

		x.syncWith(y);
		assertTrue(x.getValue() == 0);
		assertTrue(y.getValue() == 0);

		x.setValue(1);
		assertTrue(x.getValue() == 1);
		assertTrue(y.getValue() == 1);

		y.setValue(2);
		assertTrue(x.getValue() == 2);
		assertTrue(y.getValue() == 2);

	}

	@Test
	public void testUnits()
	{
		final DoubleVariable x = new DoubleVariable("x",
													MicroMeter,
													0);
		final DoubleVariable y = new DoubleVariable("y",
													MicroMeter,
													0);

		x.syncWith(y);
		assertEquals(0, x.getValue(), 0.01);
		assertEquals(0, y.getValue(), 0.01);

		x.setValue(1, Nano);
		assertEquals(1e-3, x.getValue(), 0.01);
		assertEquals(1e-3, y.getValue(), 0.01);

		y.setValue(2, Kilo);
		assertEquals(2e9, x.getValue(), 0.01);
		assertEquals(2e9, y.getValue(), 0.01);
		assertEquals(2, x.getValue(Kilo), 0.01);
		assertEquals(2, y.getValue(Kilo), 0.01);
		assertEquals(2000, y.getValue(Unit), 0.01);

		try
		{
			final DoubleVariable z = new DoubleVariable("y", Meter, 0);
			y.syncWith(z);
			fail();
		}
		catch (final InvalidMagnitudeException e)
		{
			// System.out.println("exception caught as expected");
		}

	}

}
