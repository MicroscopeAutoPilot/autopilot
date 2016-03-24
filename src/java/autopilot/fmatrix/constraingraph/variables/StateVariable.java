package autopilot.fmatrix.constraingraph.variables;

/**
 * State variables represent - as their name indicates - the state of one of the
 * system's degrees of freedom.
 * 
 * @author royer
 */
public class StateVariable extends Variable
{

	final double mPolarity;

	/**
	 * Constructs a state variable from a given name and polarity. the polarity is
	 * used to control the relative orientation of different state variables.
	 * 
	 * @param pName
	 *          state variable name
	 * @param pVariablePolarity
	 *          state variable polarity
	 */
	public StateVariable(	final String pName,
												final double pVariablePolarity)
	{
		super(pName);
		mPolarity = pVariablePolarity;
	}

	/**
	 * Returns the polarity of the state variable.
	 * 
	 * @return state variable polarity.
	 */
	public final double getPolarity()
	{
		return mPolarity;
	}

	@Override
	public String toString()
	{
		return String.format(	"StateVariable [getName()=%s, getValue()=%s, mPolarity=%s]",
													getName(),
													getValue(),
													mPolarity);
	}
}
