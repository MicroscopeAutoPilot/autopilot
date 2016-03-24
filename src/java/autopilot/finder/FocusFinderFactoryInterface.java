package autopilot.finder;

/**
 * Interface for all Focus finder factory classes.
 * 
 * @author royer
 * 
 * @param <O>
 *          position type
 */
public interface FocusFinderFactoryInterface<O>
{
	FocusFinderInterface<O> instantiate();
}
