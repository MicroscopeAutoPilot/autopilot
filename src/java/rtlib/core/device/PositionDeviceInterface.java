package rtlib.core.device;

import rtlib.core.variable.types.doublev.DoubleVariable;

public interface PositionDeviceInterface
{
	DoubleVariable getPositionVariable();

	int getPosition();

	void setPosition(int pPosition);

	int[] getValidPositions();
}
