package rtlib.core.variable.exceptions;

import rtlib.core.variable.types.doublev.DoubleVariable;

public class InvalidMagnitudeException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public InvalidMagnitudeException(	final DoubleVariable pFromVariable,
										final DoubleVariable pToVariable)
	{
		super(String.format("Variable %s cannot be synced with variable %s because the magnitudes %s and %s are different",
							pFromVariable.toString(),
							pToVariable.toString(),
							pFromVariable.getMagnitude(),
							pToVariable.getMagnitude()));
	}
}
