package rtlib.core.variable.types.longv.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.core.variable.types.doublev.DoubleVariable;

public class DoubleVariableTests
{

	@Test
	public void DoubleVariableTest()
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

}
