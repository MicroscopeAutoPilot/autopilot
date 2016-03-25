package rtlib.core.variable;

public interface VariableSetListener<O>
{
	void setEvent(O pCurrentValue, O pNewValue);
}
