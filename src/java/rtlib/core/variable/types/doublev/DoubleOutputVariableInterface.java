package rtlib.core.variable.types.doublev;

import rtlib.core.units.Magnitude;

public interface DoubleOutputVariableInterface
{
	double getValue();

	double getValue(Magnitude pMagnitude);
}
