package rtlib.core.variable;

import rtlib.core.variable.types.objectv.ObjectVariable;

public interface ObjectVariableInterface<O> extends
											VariableInterface<O>
{

	public void sendUpdatesTo(ObjectVariable<O> pVariable);

	public void doNotSendUpdatesTo(ObjectVariable<O> pVariable);

	public void doNotSendAnyUpdates();

	public void syncWith(ObjectVariable<O> pVariable);

	public void doNotSyncWith(ObjectVariable<O> pVariable);
	/**/
}
