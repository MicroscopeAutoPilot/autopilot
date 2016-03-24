package autopilot.fmatrix.constraingraph.variables;

/**
 * In a constrain graph observable variables are directly observed (i.e.
 * defocus) and thus result from an actual measurement. Importantly, observable
 * variables cannot be computed from the values of other variables.
 * 
 * @author royer
 */
public class ObservableVariable extends Variable
{

	private boolean mMissing = false;

	/**
	 * Constructs an observable variable from a given name.
	 * 
	 * @param pName
	 *          variable name
	 */
	public ObservableVariable(final String pName)
	{
		super(pName);
	}

	/**
	 * Sets the 'missing information/measurement' flag for the observable
	 * variable.
	 * 
	 * @param missing
	 *          true if the variable value is missing
	 */
	public void setMissing(final boolean missing)
	{
		mMissing = missing;
	}

	/**
	 * Returns true if the 'missing information/measurement' flag is active.
	 * 
	 * @return true if missing information/measurement.
	 */
	public final boolean isMissing()
	{
		return mMissing;
	}

	@Override
	public String toString()
	{
		return String.format(	"ObservationVariable [getName()=%s, getValue()=%s, mMissing=%s]",
													getName(),
													getValue(),
													isMissing());
	}

}
