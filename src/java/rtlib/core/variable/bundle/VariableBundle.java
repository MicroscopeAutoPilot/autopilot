package rtlib.core.variable.bundle;

import java.util.Collection;
import java.util.HashMap;

import rtlib.core.variable.NamedVariable;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class VariableBundle extends NamedVariable<VariableBundle>
{

	HashMap<String, VariableInterface<?>> mVariableNameToVariableMap = new HashMap<String, VariableInterface<?>>();

	public VariableBundle(final String pBundleName)
	{
		super(pBundleName);
	}

	@Override
	public VariableBundle get()
	{
		return this;
	}

	protected Collection<VariableInterface<?>> getAllVariables()
	{
		return mVariableNameToVariableMap.values();
	}

	public <O> void addVariable(final VariableInterface<O> pVariable)
	{
		mVariableNameToVariableMap.put(pVariable.getName(), pVariable);
	}

	public <O> void removeVariable(final VariableInterface<O> pVariable)
	{
		mVariableNameToVariableMap.remove(pVariable);
	}

	public void removeAllVariables()
	{
		mVariableNameToVariableMap.clear();
	}

	@SuppressWarnings("unchecked")
	public <O> VariableInterface<O> getVariable(final String pVariableName)
	{
		return (VariableInterface<O>) mVariableNameToVariableMap.get(pVariableName);
	}

	public <O> void sendUpdatesTo(	final String pVariableName,
									final VariableInterface<O> pToVariable)
	{
		final VariableInterface<O> lFromVariable = getVariable(pVariableName);

		final ObjectVariable<O> lFromDoubleVariable = (ObjectVariable<O>) lFromVariable;
		final ObjectVariable<O> lToDoubleVariable = (ObjectVariable<O>) pToVariable;

		lFromDoubleVariable.sendUpdatesTo(lToDoubleVariable);

	}

	public <O> void doNotSendUpdatesTo(	final String pVariableName,
										final VariableInterface<O> pToVariable)
	{
		final VariableInterface<O> lFromVariable = getVariable(pVariableName);

		final ObjectVariable<O> lFromDoubleVariable = (ObjectVariable<O>) lFromVariable;
		final ObjectVariable<O> lToDoubleVariable = (ObjectVariable<O>) pToVariable;

		lFromDoubleVariable.doNotSendUpdatesTo(lToDoubleVariable);

	}

	public <O> void getUpdatesFrom(	final String pVariableName,
									final VariableInterface<O> pFromVariable)
	{
		final VariableInterface<O> lToVariable = getVariable(pVariableName);

		final ObjectVariable<O> lTo_DoubleVariable = (ObjectVariable<O>) lToVariable;
		final ObjectVariable<O> lFrom_DoubleVariable = (ObjectVariable<O>) pFromVariable;

		lFrom_DoubleVariable.sendUpdatesTo(lTo_DoubleVariable);

	}

	public <O> void doNotGetUpdatesFrom(final String pVariableName,
										final VariableInterface<O> pFromVariable)
	{
		final VariableInterface<O> lToVariable = getVariable(pVariableName);

		final ObjectVariable<O> lTo_DoubleVariable = (ObjectVariable<O>) lToVariable;
		final ObjectVariable<O> lFrom_DoubleVariable = (ObjectVariable<O>) pFromVariable;

		lFrom_DoubleVariable.doNotSendUpdatesTo(lTo_DoubleVariable);

	}

	public <O> void syncWith(	final String pVariableName,
								final VariableInterface<O> pVariable)
	{
		this.sendUpdatesTo(pVariableName, pVariable);
		this.getUpdatesFrom(pVariableName, pVariable);
	}

	public <O> void doNotSyncWith(	final String pVariableName,
									final VariableInterface<O> pVariable)
	{
		this.doNotSendUpdatesTo(pVariableName, pVariable);
		this.doNotGetUpdatesFrom(pVariableName, pVariable);
	}

	@Override
	public String toString()
	{
		return String.format(	"VariableBundle(%s,%s)",
								getName(),
								mVariableNameToVariableMap);
	}

}
