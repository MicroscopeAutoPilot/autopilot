package rtlib.core.variable.exceptions;

import rtlib.core.variable.VariableInterface;

public class InvalidVariableTypeException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public InvalidVariableTypeException(final VariableInterface<?> pFromVariable,
										final VariableInterface<?> pToVariable)
	{
		super(String.format("Variable %s is incompatible with variable %s.",
							pFromVariable.toString(),
							pToVariable.toString()));
	}
}
