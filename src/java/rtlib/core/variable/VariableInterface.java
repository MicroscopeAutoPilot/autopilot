package rtlib.core.variable;

public interface VariableInterface<O>
{

	public String getName();

	public void setCurrent();

	public void set(O pValue);

	public O get();

	public void addListener(VariableListener<O> pVariableListener);

	public void removeListener(VariableListener<O> pVariableListener);

	public void addSetListener(VariableSetListener<O> pDoubleVariableListener);

	public void addGetListener(VariableGetListener<O> pDoubleVariableListener);

	public void removeSetListener(VariableSetListener<O> pDoubleVariableListener);

	public void removeGetListener(VariableGetListener<O> pDoubleVariableListener);

	public void removeAllSetListeners();

	public void removeAllGetListeners();

	public void removeAllListeners();

}
