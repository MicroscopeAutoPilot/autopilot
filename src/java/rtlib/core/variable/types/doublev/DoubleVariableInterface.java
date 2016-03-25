package rtlib.core.variable.types.doublev;

import rtlib.core.units.Magnitude;
import rtlib.core.units.SIUnit;
import rtlib.core.variable.VariableInterface;

public interface DoubleVariableInterface extends
										VariableInterface<Double>,
										DoubleInputVariableInterface,
										DoubleOutputVariableInterface
{

	public SIUnit getUnit();

	public Magnitude getMagnitude();

	public void sendUpdatesTo(DoubleVariable pVariable);

	public void doNotSendUpdatesTo(DoubleVariable pVariable);

	public void doNotSendAnyUpdates();

	public void syncWith(DoubleVariable pVariable);

	public void doNotSyncWith(DoubleVariable pVariable);

}
