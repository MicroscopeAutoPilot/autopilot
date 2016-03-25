package rtlib.core.variable.types.doublev;

import rtlib.core.units.Magnitude;

public interface DoubleInputVariableInterface
{
	void setValue(double pNewValue);

	void setValue(double pValue, Magnitude pMagnitude);
}
