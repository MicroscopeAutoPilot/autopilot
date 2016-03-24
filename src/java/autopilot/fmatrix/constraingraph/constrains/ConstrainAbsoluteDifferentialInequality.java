package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: |ΔV| <= C. Where V is a state
 * variable and C is a constant.
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainAbsoluteDifferentialInequality extends
																										Constrain
{

	/**
	 * Constructs a {@link ConstrainAbsoluteDifferentialInequality} constrain
	 * given a constant C and a state variable V.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pConstantVariable
	 *          constant variable
	 * @param pStateVariable
	 *          state variable
	 */
	public ConstrainAbsoluteDifferentialInequality(	final String pConstrainName,
																									final ConstantVariable pConstantVariable,
																									final StateVariable pStateVariable)
	{
		super(pConstrainName);
		setConstrainVariable(pConstantVariable);
		addStateVariable(pStateVariable);
	}

}
