package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: V = C. Where V is a state
 * variable and C is a constant.
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainConstantEquality extends Constrain
{

	/**
	 * Constructs a constant equality constrain for a given state variable and
	 * constant.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pStateVariable
	 *          state variable
	 * @param pConstantVariable
	 *          constant variable
	 */
	public ConstrainConstantEquality(	final String pConstrainName,
																		final StateVariable pStateVariable,
																		final ConstantVariable pConstantVariable)
	{
		super(pConstrainName);
		addStateVariable(pStateVariable);
		setConstrainVariable(pConstantVariable);
	}

	public final double getValue()
	{
		return getConstrainVariable().getValue();
	}

	/*@Override
	public String toString()
	{
		return String.format(	"%s==%g",
													getVariable1().getName(),
													getValue());
	}/**/
}
