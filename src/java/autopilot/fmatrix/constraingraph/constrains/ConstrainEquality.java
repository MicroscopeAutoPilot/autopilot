package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: V1 = V2
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainEquality extends ConstrainDifference
{

	/**
	 * Constructs a {@link ConstrainEquality} constrain given two state variables
	 * V1 and V2.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pV1
	 *          first variable
	 * @param pV2
	 *          second variable
	 */
	public ConstrainEquality(	final String pConstrainName,
														final StateVariable pV1,
														final StateVariable pV2)
	{
		super(pConstrainName);
		final ConstantVariable lConstrainVariable = new ConstantVariable(	pConstrainName + "ConstantValue",
																																			0);
		setConstrainVariable(lConstrainVariable);
		addStateVariable(pV1);
		addStateVariable(pV2);
	}

	/*@Override
	public String toString()
	{
		return String.format(	"%s==%s",
													getVariable1().getName(),
													getVariable2().getName());
	}/**/

}
