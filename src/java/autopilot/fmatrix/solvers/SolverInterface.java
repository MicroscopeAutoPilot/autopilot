package autopilot.fmatrix.solvers;

import org.ejml.data.DenseMatrix64F;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

/**
 * Interface for all solvers
 * 
 * @author royer
 */
public interface SolverInterface
{
	/**
	 * Solves the problem described by the constrain graph, the current state
	 * vector, the observation vector (compiled from measurements and constrains).
	 * 
	 * @param pConstrainGraph
	 *          constrain graph
	 * @param pCurrentStateVector
	 *          previous state vector
	 * @param pObservationVector
	 *          observation vector
	 * @param pNewStateVector
	 *          contains the solution: the new state vector
	 */
	public void solve(final ConstrainGraph pConstrainGraph,
										final StateVector pCurrentStateVector,
										final ObservationVector pObservationVector,
										final StateVector pNewStateVector);

	/**
	 * Solves for the correction vector. Adding the correction vector to the
	 * current state vector gives you the new state vector.
	 * 
	 * @param pConstrainGraph
	 *          constrain graph
	 * @param pCurrentStateVector
	 *          current state vector
	 * @param pObservationVector
	 *          observation vector
	 * @return correction vector
	 */
	public DenseMatrix64F delta(final ConstrainGraph pConstrainGraph,
															final StateVector pCurrentStateVector,
															final ObservationVector pObservationVector);

	/**
	 * Sets the logging mode
	 * 
	 * @param pStdOut
	 *          true if stdout is used
	 * @param pLogFile
	 *          true if logfile used
	 */
	public void setLogging(boolean pStdOut, boolean pLogFile);

	/**
	 * Returns code informing on any warnings or describing the quality of
	 * solution.
	 * 
	 * @return return code.
	 */
	public int getReturnCode();

}
