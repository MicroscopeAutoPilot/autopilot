package autopilot.fmatrix.compiler;

import org.ejml.simple.SimpleMatrix;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.constrains.ConstrainAverage;
import autopilot.fmatrix.constraingraph.constrains.ConstrainConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainDifference;
import autopilot.fmatrix.constraingraph.constrains.ConstrainEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainHarmonic;
import autopilot.fmatrix.constraingraph.constrains.ConstrainIdentity;
import autopilot.fmatrix.constraingraph.constrains.ConstrainNull;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumEquality;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

public class CompileMatrix
{

	/**
	 * Compiles the matrix corresponding to a given constrain graph.
	 * 
	 * @param pConstrainGraph
	 *          constrain graph
	 * @return corresponding matrix
	 */
	public static final SimpleMatrix compile(final ConstrainGraph pConstrainGraph)
	{
		final int lNumberOfRows = pConstrainGraph.getTotalNumberOfLinearConstrains();
		final int lNumberOfColumns = pConstrainGraph.getNumberOfStateVariables();

		int lRowCounter = 0;
		final SimpleMatrix lSimpleMatrix = new SimpleMatrix(lNumberOfRows,
																												lNumberOfColumns);

		lRowCounter = addObservableConstrainsToFocusMatrix(	pConstrainGraph,
																												lRowCounter,
																												lSimpleMatrix);

		lRowCounter = addConstantConstrainsToFocusMatrix(	pConstrainGraph,
																											lRowCounter,
																											lSimpleMatrix);

		return lSimpleMatrix;
	}

	private static int addConstantConstrainsToFocusMatrix(final ConstrainGraph pConstrainGraph,
																												int lRowCounter,
																												final SimpleMatrix lSimpleMatrix)
	{
		for (int i = 0; i < pConstrainGraph.getNumberOfConstantConstrains(); i++)
		{
			final Constrain lConstantConstrainAt = pConstrainGraph.getConstantConstrainAt(i);
			lConstantConstrainAt.getConstrainVariable();

			if (lConstantConstrainAt instanceof ConstrainNull)
			{
				// Do Nothing
			}
			else if (lConstantConstrainAt instanceof ConstrainEquality)
			{
				addDifferenceToRow(	pConstrainGraph,
														lRowCounter,
														lSimpleMatrix,
														lConstantConstrainAt);
			}
			else if (lConstantConstrainAt instanceof ConstrainConstantEquality)
			{
				final StateVariable lVariable1 = lConstantConstrainAt.getVariable1();
				final int lStateVariableIndex1 = pConstrainGraph.getStateVariableIndex(lVariable1);
				final double lValue1 = lVariable1.getPolarity() * lConstantConstrainAt.getWeight();
				lSimpleMatrix.set(lRowCounter, lStateVariableIndex1, lValue1);
			}
			else if (lConstantConstrainAt instanceof ConstrainSumEquality)
			{
				final ConstrainSumEquality lConstrainSumEquality = (ConstrainSumEquality) lConstantConstrainAt;

				for (int v = 0; v < lConstrainSumEquality.getStateVariableList()
																									.size(); v++)
				{
					final StateVariable lStateVariable = lConstrainSumEquality.getVariable(v);
					final int lIndex = pConstrainGraph.getStateVariableIndex(lStateVariable);
					final double lValue = lStateVariable.getPolarity() * lConstrainSumEquality.getWeight();
					lSimpleMatrix.set(lRowCounter,
														lIndex,
														(v % 2 == 0 ? 1 : -1) * lValue);
				}

			}
			else if (lConstantConstrainAt instanceof ConstrainSumConstantEquality)
			{
				final ConstrainSumConstantEquality lConstrainSumConstantEquality = (ConstrainSumConstantEquality) lConstantConstrainAt;

				for (int v = 0; v < lConstrainSumConstantEquality.getStateVariableList()
																													.size(); v++)
				{
					final StateVariable lStateVariable = lConstrainSumConstantEquality.getVariable(v);
					final int lIndex = pConstrainGraph.getStateVariableIndex(lStateVariable);
					final double lValue = lStateVariable.getPolarity() * lConstrainSumConstantEquality.getWeight();
					lSimpleMatrix.set(lRowCounter, lIndex, lValue);
				}

			}
			else if (lConstantConstrainAt instanceof ConstrainAverage)
			{
				final ConstrainAverage lConstrainAverage = (ConstrainAverage) lConstantConstrainAt;
				final StateVariable lStateVariableAverage = lConstrainAverage.getVariable(0);
				final int lStateVariableAverageIndex = pConstrainGraph.getStateVariableIndex(lStateVariableAverage);
				final double lStateVariableAverageValue = -lStateVariableAverage.getPolarity() * lConstrainAverage.getWeight();
				lSimpleMatrix.set(lRowCounter,
													lStateVariableAverageIndex,
													lStateVariableAverageValue);

				final double lNumberOfVariablesToTakeAverage = lConstrainAverage.getStateVariableList()
																																				.size() - 1;
				for (int v = 1; v < lConstrainAverage.getStateVariableList()
																							.size(); v++)
				{
					final StateVariable lStateVariable = lConstrainAverage.getVariable(v);
					final int lStateVariableIndex = pConstrainGraph.getStateVariableIndex(lStateVariable);
					final double lStateVariableValue = lStateVariable.getPolarity() * lConstrainAverage.getWeight()
																							/ lNumberOfVariablesToTakeAverage;

					final double lOldValue = lSimpleMatrix.get(	lRowCounter,
																											lStateVariableIndex);
					lSimpleMatrix.set(lRowCounter,
														lStateVariableIndex,
														lOldValue + lStateVariableValue);
				}

			}
			else if (lConstantConstrainAt instanceof ConstrainHarmonic)
			{
				final ConstrainHarmonic lConstrainHarmonic = (ConstrainHarmonic) lConstantConstrainAt;
				final StateVariable lStateVariableVi = lConstrainHarmonic.getVariable(0);
				final int lStateVariableAverageIndex = pConstrainGraph.getStateVariableIndex(lStateVariableVi);
				final double lStateVariableAverageValue = -lStateVariableVi.getPolarity() * lConstrainHarmonic.getWeight();
				lSimpleMatrix.set(lRowCounter,
													lStateVariableAverageIndex,
													lStateVariableAverageValue);

				final double lNumberOfVariablesToTakeHarmonic = lConstrainHarmonic.getStateVariableList()
																																					.size() - 1;

				for (int v = 0; v < 4; v++)
				{
					final StateVariable lStateVariable = lConstrainHarmonic.getVariable(1 + v);
					final int lStateVariableIndex = pConstrainGraph.getStateVariableIndex(lStateVariable);
					final double lStateVariableValue = ConstrainHarmonic.cFilterCoefs[v] * lStateVariable.getPolarity()
																							* lConstrainHarmonic.getWeight();

					final double lOldValue = lSimpleMatrix.get(	lRowCounter,
																											lStateVariableIndex);
					lSimpleMatrix.set(lRowCounter,
														lStateVariableIndex,
														lOldValue + lStateVariableValue);
				}

			}

			lRowCounter++;
		}
		return lRowCounter;
	}

	private static int addObservableConstrainsToFocusMatrix(final ConstrainGraph pConstrainGraph,
																													int lRowCounter,
																													final SimpleMatrix lSimpleMatrix)
	{
		for (int i = 0; i < pConstrainGraph.getNumberOfObservableConstrains(); i++)
		{
			final Constrain lObservableConstrainAt = pConstrainGraph.getObservableConstrainAt(i);
			lObservableConstrainAt.getConstrainVariable();

			if (lObservableConstrainAt instanceof ConstrainIdentity)
			{
				final ConstrainIdentity lConstrainIdentity = (ConstrainIdentity) lObservableConstrainAt;
				if (!lConstrainIdentity.isMissingObservation())
				{
					final StateVariable lStateVariable = lConstrainIdentity.getVariable1();
					final int lIndex = pConstrainGraph.getStateVariableIndex(lStateVariable);
					final double lValue = lStateVariable.getPolarity() * lConstrainIdentity.getWeight();

					lSimpleMatrix.set(lRowCounter, lIndex, lValue);
				}
			}
			else if (lObservableConstrainAt instanceof ConstrainDifference)
			{
				final ConstrainDifference lConstrainDifference = (ConstrainDifference) lObservableConstrainAt;
				if (!lConstrainDifference.isMissingObservation())
				{
					addDifferenceToRow(	pConstrainGraph,
															lRowCounter,
															lSimpleMatrix,
															lObservableConstrainAt);
				}
			}

			lRowCounter++;
		}
		return lRowCounter;
	}

	private static void addDifferenceToRow(	final ConstrainGraph pConstrainGraph,
																					final int lRowCounter,
																					final SimpleMatrix lSimpleMatrix,
																					final Constrain lObservableConstrainAt)
	{
		final StateVariable lVariable1 = lObservableConstrainAt.getVariable1();
		final StateVariable lVariable2 = lObservableConstrainAt.getVariable2();

		final int lStateVariableIndex1 = pConstrainGraph.getStateVariableIndex(lVariable1);
		final int lStateVariableIndex2 = pConstrainGraph.getStateVariableIndex(lVariable2);

		final double lValue1 = lObservableConstrainAt.getWeight();
		final double lValue2 = -lVariable1.getPolarity() * lVariable2.getPolarity()
														* lObservableConstrainAt.getWeight();

		lSimpleMatrix.set(lRowCounter, lStateVariableIndex1, lValue1);
		lSimpleMatrix.set(lRowCounter, lStateVariableIndex2, lValue2);
	}
}
