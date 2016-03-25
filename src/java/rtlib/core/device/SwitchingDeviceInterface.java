package rtlib.core.device;

import rtlib.core.variable.types.booleanv.BooleanVariable;

public interface SwitchingDeviceInterface
{
	int getNumberOfSwitches();

	BooleanVariable getSwitchingVariable(int pSwitchIndex);
}
