package rtlib.core.variable.types.longv;

import rtlib.core.variable.VariableInterface;

public interface LongVariableInterface	extends
										VariableInterface<Long>,
										LongInputVariableInterface,
										LongOutputVariableInterface
{

	public void sendUpdatesTo(LongVariable pVariable);

	public void doNotSendUpdatesTo(LongVariable pVariable);

	public void doNotSendAnyUpdates();

	public void syncWith(LongVariable pVariable);

	public void doNotSyncWith(LongVariable pVariable);

}
