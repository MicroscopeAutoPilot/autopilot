package autopilot.fmatrix.constraingraph;

import java.util.ArrayList;

import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.constrains.ConstrainAbsoluteDifferentialInequality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainAverage;
import autopilot.fmatrix.constraingraph.constrains.ConstrainConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainDifference;
import autopilot.fmatrix.constraingraph.constrains.ConstrainEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainHarmonic;
import autopilot.fmatrix.constraingraph.constrains.ConstrainIdentity;
import autopilot.fmatrix.constraingraph.constrains.ConstrainNull;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumEquality;
import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class allows the construction of a constrain graph. You can add state
 * variables, observation variables and constrains.
 * 
 * @author royer
 */

public class ConstrainGraph
{

	ArrayList<StateVariable> mStateVariableList = new ArrayList<StateVariable>();
	ArrayList<ObservableVariable> mObservableVariableList = new ArrayList<ObservableVariable>();
	ArrayList<Constrain> mObservableConstrainList = new ArrayList<Constrain>();
	ArrayList<Constrain> mConstantConstrainList = new ArrayList<Constrain>();
	ArrayList<Constrain> mInequalityConstrainList = new ArrayList<Constrain>();

	public ConstrainGraph()
	{
		super();
	}

	/**
	 * Adds a state variable of a given name to the constrain graph. The observale
	 * variable is created, added and returned.The polarity allows to control the
	 * orientation of the variable compared to other variables.
	 * 
	 * @param pName
	 *          Name of the state variable
	 * @param pVariablePolarity
	 *          Polarity of the state variable
	 * @return state variable
	 */
	public StateVariable addStateVariable(final String pName,
																				final double pVariablePolarity)
	{
		final StateVariable lStateVariable = new StateVariable(	pName,
																														pVariablePolarity);
		mStateVariableList.add(lStateVariable);
		return lStateVariable;
	}

	/**
	 * Returns the state variable at a given index position.
	 * 
	 * @param pIndex
	 *          state variable index
	 * @return state variable
	 */
	public final StateVariable getStateVariableByIndex(int pIndex)
	{
		return mStateVariableList.get(pIndex);
	}

	/**
	 * Returns the state variable index.
	 * 
	 * @param pStateVariable
	 *          State variable
	 * @return Index of the state variable
	 */
	public final int getStateVariableIndex(final StateVariable pStateVariable)
	{
		return mStateVariableList.indexOf(pStateVariable);
	}

	/**
	 * Returns the state variable that bares the given name
	 * 
	 * @param pString
	 *          Name of the state variable
	 * @return State variable
	 */
	public StateVariable getStateVariableByName(final String pString)
	{
		for (final StateVariable lStateVariable : mStateVariableList)
		{
			if (lStateVariable.getName().equals(pString))
			{
				return lStateVariable;
			}
		}
		return null;
	}

	/**
	 * Adds an observable variable of a given name, the observable variable
	 * created and added is returned.
	 * 
	 * @param pName
	 *          variable name
	 * @return observable variable
	 */
	public ObservableVariable addObservableVariable(final String pName)
	{
		final ObservableVariable lObservationVariable = new ObservableVariable(pName);
		mObservableVariableList.add(lObservationVariable);
		return lObservationVariable;
	}

	/**
	 * Returns the observable variable at a given index position.
	 * 
	 * @param pIndex
	 *          observable variable index
	 * @return observable variable
	 */
	public final ObservableVariable getObservableVariableByIndex(int pIndex)
	{
		return mObservableVariableList.get(pIndex);
	}

	/**
	 * Returns the index of a given observable variable.
	 * 
	 * @param pObservableVariable
	 *          observable variable
	 * @return index of observable variable
	 */
	public final int getObservableVariableIndex(final ObservableVariable pObservableVariable)
	{
		return mObservableVariableList.indexOf(pObservableVariable);
	}

	/**
	 * Return the observable variable that bares a given name.
	 * 
	 * @param pString
	 *          observable variable name
	 * @return observable variable
	 */
	public ObservableVariable getObservableVariableByName(final String pString)
	{
		for (final ObservableVariable lObservableVariable : mObservableVariableList)
		{
			if (lObservableVariable.getName().equals(pString))
			{
				return lObservableVariable;
			}
		}
		return null;
	}

	/**
	 * Adds an identity constrain to the constrain graph. Identity constrains
	 * areof the form: V = O. Where V is a state variable and O an observable.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pObservableVariable
	 *          observable variable
	 * @param pStateVariable
	 *          state variable
	 * @return added constrain
	 */
	protected ConstrainIdentity addIdentityConstrain(	final String pConstrainName,
																										final ObservableVariable pObservableVariable,
																										final StateVariable pStateVariable)
	{
		final ConstrainIdentity lConstrainIdentity = new ConstrainIdentity(	pConstrainName,
																																				pObservableVariable,
																																				pStateVariable);
		mObservableConstrainList.add(lConstrainIdentity);
		return lConstrainIdentity;
	}

	/**
	 * Adds a difference constrain to the constrain graph. Difference constrains
	 * are of the form: V1 - V2 = O. Where V1 and V2 are two state variables and O
	 * is an observable.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pO
	 *          observable variable
	 * @param pV1
	 *          state variable V1
	 * @param pV2
	 *          state variable V2
	 * @return added constrain
	 */
	public ConstrainDifference addDifferenceConstrain(final String pConstrainName,
																										final ObservableVariable pO,
																										final StateVariable pV1,
																										final StateVariable pV2)
	{
		final ConstrainDifference lDifferenceConstrain = new ConstrainDifference(	pConstrainName,
																																							pO,
																																							pV1,
																																							pV2);
		mObservableConstrainList.add(lDifferenceConstrain);
		return lDifferenceConstrain;
	}

	/**
	 * Adds a constant equality constrain to the constrain graph. Constant
	 * equality constrains are of the form: V = C. Where V is a state variable and
	 * C is a constant.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pV
	 *          state variable
	 * @param pValue
	 *          value the state variable has to be equal to
	 * @return added constrain
	 */
	public ConstrainConstantEquality addEqualityConstrain(final String pConstrainName,
																												final StateVariable pV,
																												final double pValue)
	{
		final ConstantVariable lConstantVariable = new ConstantVariable(pConstrainName + "Constant",
																																		pValue);
		final ConstrainConstantEquality lConstantEqualityConstrain = new ConstrainConstantEquality(	pConstrainName,
																																																pV,
																																																lConstantVariable);
		mConstantConstrainList.add(lConstantEqualityConstrain);
		return lConstantEqualityConstrain;
	}

	/**
	 * Adds a equality constrain to the constrain graph. Equality constrains are
	 * of the form of the form: V1 = V2.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pV1
	 *          state variable V1
	 * @param pV2
	 *          state variable V2
	 * @return added constrain
	 */
	public ConstrainEquality addEqualityConstrain(final String pConstrainName,
																								final StateVariable pV1,
																								final StateVariable pV2)
	{
		final ConstrainEquality lEqualityConstrain = new ConstrainEquality(	pConstrainName,
																																				pV1,
																																				pV2);
		mConstantConstrainList.add(lEqualityConstrain);
		return lEqualityConstrain;
	}

	/**
	 * Adds 'sum constant equality' constrain to the constrain graph. Sum constant
	 * equality' constrains are of the form: V1 + V2 + ... + Vk = C
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pConstantValue
	 *          value that the sum has to be equal to.
	 * @param pStateVariables
	 *          list/array of state variables
	 * @return added constrain
	 */
	public ConstrainSumConstantEquality addSumConstantEqualityConstrain(final String pConstrainName,
																																			final double pConstantValue,
																																			final StateVariable... pStateVariables)
	{
		final ConstrainSumConstantEquality lConstrainSumConstantEquality = new ConstrainSumConstantEquality(pConstrainName,
																																																				pConstantValue,
																																																				pStateVariables);
		mConstantConstrainList.add(lConstrainSumConstantEquality);
		return lConstrainSumConstantEquality;
	}

	/**
	 * Adds sum equality constrain to the constrain graph. Sum equality constrains
	 * are of the form: VA1 + VB1 = VA2 + VB2
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pVA1
	 *          state variable VA1
	 * @param pVA2
	 *          state variable VA2
	 * @param pVB1
	 *          state variable VB1
	 * @param pVB2
	 *          state variable VB1
	 * @return added constrain
	 */
	public ConstrainSumEquality addSumEqualityConstrain(final String pConstrainName,
																											final StateVariable pVA1,
																											final StateVariable pVA2,
																											final StateVariable pVB1,
																											final StateVariable pVB2)
	{
		final ConstrainSumEquality lConstrainSumEquality = new ConstrainSumEquality(pConstrainName,
																																								pVA1,
																																								pVA2,
																																								pVB1,
																																								pVB2);
		mConstantConstrainList.add(lConstrainSumEquality);
		return lConstrainSumEquality;
	}

	/**
	 * Adds average constrain to the constrain graph. Average constrains are of
	 * the form: Vavg = (V1 + V2 + ... +Vk)/k.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pStateVariableAverage
	 *          state variable that has to be an average
	 * @param pStateVariables
	 *          state variables that have to be averaged
	 * @return average constrain
	 */
	public ConstrainAverage addAverageConstrain(final String pConstrainName,
																							final StateVariable pStateVariableAverage,
																							final StateVariable... pStateVariables)
	{
		final ConstrainAverage lConstrainAverage = new ConstrainAverage(pConstrainName,
																																		pStateVariableAverage,
																																		pStateVariables);
		mConstantConstrainList.add(lConstrainAverage);
		return lConstrainAverage;
	}

	/**
	 * Adds an harmonic constrain to the constrain graph. Harmonic constrains are
	 * usefull to 'fill-in' missing information assuming a constant second order
	 * derivative. Harmonic constrains are of the form: form: V(i) = -(1/6)*V(i-2)
	 * + (4/6)*V(i-1) + (4/6)*V(i+1) -(1/6)*V(i+2). Where V(i-2), V(i-1), V(i+1),
	 * V(i+2) variables with know values and V(i) needs to be constrained using a
	 * harmonic constrain. The 'harmonic' naming comes from harmonic surfaces that
	 * are solutions to Laplace's equation.
	 * (https://en.wikipedia.org/wiki/Harmonic_function)
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pStateVariableVi
	 *          state variable that will be 'filled-in'
	 * @param pStateVariables
	 *          state variables that have to be averaged
	 * @return harmonic constrain
	 */
	public ConstrainHarmonic addHarmonicConstrain(final String pConstrainName,
																								final StateVariable pStateVariableVi,
																								final StateVariable... pStateVariables)
	{
		final ConstrainHarmonic lConstrainHarmonic = new ConstrainHarmonic(	pConstrainName,
																																				pStateVariableVi,
																																				pStateVariables);
		mConstantConstrainList.add(lConstrainHarmonic);
		return lConstrainHarmonic;
	}

	/**
	 * Adds a null constrain. This corresponds to an empty row in the matrix.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @return Null constrain
	 */
	public ConstrainNull addNullConstrain(final String pConstrainName)
	{
		final ConstrainNull lConstrainNull = new ConstrainNull(pConstrainName);
		mConstantConstrainList.add(lConstrainNull);
		return lConstrainNull;
	}

	/**
	 * Adds an absolute differential inequality constrain on a state variable
	 * (|ΔV| <= C).
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pConstantVariable
	 *          constant variable
	 * @param pStateVariable
	 *          state variable
	 * @return absolute differential inequality constrain
	 */
	public ConstrainAbsoluteDifferentialInequality addAbsoluteDifferentialInequalityConstrain(final String pConstrainName,
																																														final ConstantVariable pConstantVariable,
																																														final StateVariable pStateVariable)
	{
		final ConstrainAbsoluteDifferentialInequality lConstrainAbsoluteDifferentialInequality = new ConstrainAbsoluteDifferentialInequality(	pConstrainName,
																																																																					pConstantVariable,
																																																																					pStateVariable);
		mInequalityConstrainList.add(lConstrainAbsoluteDifferentialInequality);
		return lConstrainAbsoluteDifferentialInequality;
	}

	/**
	 * Returns the index of the given constrain (observable or not).
	 * 
	 * @param pConstrain
	 *          constrain.
	 * @return index of constrain
	 */
	public final int getConstrainIndex(final Constrain pConstrain)
	{
		int lIndexOf = mObservableConstrainList.indexOf(pConstrain);
		if (lIndexOf >= 0)
		{
			return lIndexOf;
		}
		lIndexOf = mConstantConstrainList.indexOf(pConstrain);
		return lIndexOf;
	}

	/**
	 * Returns list of observable constrains.
	 * 
	 * @return observable constrain list.
	 */
	public ArrayList<Constrain> getObservableConstrains()
	{
		return mObservableConstrainList;
	}

	/**
	 * Returns list of constant constrains.
	 * 
	 * @return constant constrain list.
	 */
	public ArrayList<Constrain> getConstantConstrains()
	{
		return mConstantConstrainList;
	}

	/**
	 * Returns list of inequality constrains.
	 * 
	 * @return constant constrain list.
	 */
	public ArrayList<Constrain> getInequalityConstrains()
	{
		return mInequalityConstrainList;
	}

	/**
	 * Returns observable constrain at a given index.
	 * 
	 * @param pIndex
	 *          index of constrain
	 * @return observable constrain
	 */
	public Constrain getObservableConstrainAt(final int pIndex)
	{
		return mObservableConstrainList.get(pIndex);
	}

	/**
	 * Returns constant constrain at a given index.
	 * 
	 * @param pIndex
	 *          index of constrain
	 * @return constant constrain
	 */
	public Constrain getConstantConstrainAt(final int pIndex)
	{
		return mConstantConstrainList.get(pIndex);
	}

	/**
	 * Returns the number of state variables in the constrain graph.
	 * 
	 * @return number of state variables.
	 */
	public final int getNumberOfStateVariables()
	{
		return mStateVariableList.size();
	}

	/**
	 * Returns the number of observable variables in the constrain graph.
	 * 
	 * @return number of observable variables.
	 */
	public int getNumberOfObservableVariables()
	{
		return mObservableVariableList.size();
	}

	/**
	 * Returns the number of observable constrains in the constrain graph.
	 * 
	 * @return number of observable constrains.
	 */
	public int getNumberOfObservableConstrains()
	{
		return mObservableConstrainList.size();
	}

	/**
	 * Return the number of constant constrains.
	 * 
	 * @return number of constant constrains
	 */
	public int getNumberOfConstantConstrains()
	{
		return mConstantConstrainList.size();
	}

	/**
	 * Return the number of inequality constrains.
	 * 
	 * @return number of constant constrains
	 */
	public int getNumberOfInequalityConstrains()
	{
		return mInequalityConstrainList.size();
	}

	/**
	 * Returns the total number of linear constrains (observable and constant
	 * constrains). Inequality constrains do not enter in the matrix formulation
	 * and are hence non-linear as they require non-linear solvers to solve the
	 * system.
	 * 
	 * @return total number of constrains
	 */
	public int getTotalNumberOfLinearConstrains()
	{
		return getNumberOfObservableConstrains() + getNumberOfConstantConstrains();
	}

}
