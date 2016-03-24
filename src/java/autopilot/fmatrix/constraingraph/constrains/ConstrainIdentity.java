package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: V = O. Where V is a state
 * variable and O an observable.
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainIdentity extends Constrain
{

	/**
	 * Constructs a {@link ConstrainIdentity} constrain given an observable O and
	 * a state variable V.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pObservableVariable
	 *          observable variable
	 * @param pStateVariable
	 *          state variable
	 */
	public ConstrainIdentity(	final String pConstrainName,
														final ObservableVariable pObservableVariable,
														final StateVariable pStateVariable)
	{
		super(pConstrainName);
		setConstrainVariable(pObservableVariable);
		addStateVariable(pStateVariable);
	}

}
