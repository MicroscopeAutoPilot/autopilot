package autopilot.fmatrix.compiler;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.constrains.ConstrainAverage;
import autopilot.fmatrix.constraingraph.constrains.ConstrainConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainDifference;
import autopilot.fmatrix.constraingraph.constrains.ConstrainEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainHarmonic;
import autopilot.fmatrix.constraingraph.constrains.ConstrainIdentity;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumEquality;
import autopilot.fmatrix.constraingraph.variables.StateVariable;
import autopilot.fmatrix.vector.StateVector;

public class CompileObservationVector
{
	/**
	 * Compiles the observation vector from a constrain graph, the current state
	 * vector, and the constrain-less observation vector.
	 * 
	 * @param pConstrainGraph
	 *          constrain graph
	 * @param pCurrentStateVector
	 *          current state vector
	 * @param pConstrainLessObservationVector
	 *          observations vector (only the pure observations without constant
	 *          constrains entries)
	 * @return matrix
	 */
	public static final SimpleMatrix compile(	final ConstrainGraph pConstrainGraph,
																						final StateVector pCurrentStateVector,
																						final DenseMatrix64F pConstrainLessObservationVector)
	{
		final int lNumberOfRows = pConstrainGraph.getTotalNumberOfLinearConstrains();
		final int lNumberOfColumns = 1;

		int lRowCounter = 0;
		final SimpleMatrix lSimpleMatrix = new SimpleMatrix(lNumberOfRows,
																												lNumberOfColumns);

		lRowCounter = addObservableConstrainsToObservationVector(	pConstrainGraph,
																															pConstrainLessObservationVector,
																															lRowCounter,
																															lSimpleMatrix);

		addConstantConstrainsToObservationVector(	pConstrainGraph,
																							pCurrentStateVector,
																							lRowCounter,
																							lSimpleMatrix);

		return lSimpleMatrix;
	}

	private static void addConstantConstrainsToObservationVector(	final ConstrainGraph pFocusMatrixModel,
																																final StateVector pCurrentStateVector,
																																int lRowCounter,
																																final SimpleMatrix lSimpleMatrix)
	{
		final int lNumberOfConstantConstrains = pFocusMatrixModel.getNumberOfConstantConstrains();
		for (int i = 0; i < lNumberOfConstantConstrains; i++)
		{
			final Constrain lConstantConstrainAt = pFocusMatrixModel.getConstantConstrainAt(i);

			if (lConstantConstrainAt instanceof ConstrainEquality)
			{
				final ConstrainEquality lConstrainEquality = (ConstrainEquality) lConstantConstrainAt;
				final StateVariable lStateVariable1 = lConstrainEquality.getVariable1();
				final StateVariable lStateVariable2 = lConstrainEquality.getVariable2();
				final int lIndex1 = pFocusMatrixModel.getStateVariableIndex(lStateVariable1);
				final int lIndex2 = pFocusMatrixModel.getStateVariableIndex(lStateVariable2);

				final double lValue1 = lConstrainEquality.getWeight();
				final double lValue2 = -lStateVariable1.getPolarity() * lStateVariable2.getPolarity()
																* lConstrainEquality.getWeight();

				double lValue = 0;
				if (lConstantConstrainAt.isAbsolute())
				{
					lValue += -(lValue1 * pCurrentStateVector.getValueAt(lIndex1) + lValue2 * pCurrentStateVector.getValueAt(lIndex2));
				}
				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}
			else if (lConstantConstrainAt instanceof ConstrainConstantEquality)
			{
				final ConstrainConstantEquality lConstrainConstantEquality = (ConstrainConstantEquality) lConstantConstrainAt;
				final double lValue = lConstrainConstantEquality.getValue() * lConstrainConstantEquality.getWeight();
				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}
			else if (lConstantConstrainAt instanceof ConstrainSumEquality)
			{
				final ConstrainSumEquality lConstrainSumEquality = (ConstrainSumEquality) lConstantConstrainAt;

				double lValue = 0;

				if (lConstantConstrainAt.isAbsolute())
				{
					for (int v = 0; v < lConstrainSumEquality.getStateVariableList()
																										.size(); v++)
					{
						final StateVariable lStateVariable = lConstrainSumEquality.getVariable(v);
						final int lIndex = pFocusMatrixModel.getStateVariableIndex(lStateVariable);
						final double lStateVariableValue = pCurrentStateVector.getValueAt(lIndex);
						final double lMatrixEntryValue = lStateVariable.getPolarity() * lConstrainSumEquality.getWeight()
																							* (v % 2 == 0 ? 1 : -1);
						lValue += -(lMatrixEntryValue * lStateVariableValue);
					}
				}

				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}
			else if (lConstantConstrainAt instanceof ConstrainSumConstantEquality)
			{
				final ConstrainSumConstantEquality lConstrainSumConstantEquality = (ConstrainSumConstantEquality) lConstantConstrainAt;

				double lValue = lConstrainSumConstantEquality.getValue() * lConstrainSumConstantEquality.getWeight();

				if (lConstantConstrainAt.isAbsolute())
				{
					for (int v = 0; v < lConstrainSumConstantEquality.getStateVariableList()
																														.size(); v++)
					{
						final StateVariable lStateVariable = lConstrainSumConstantEquality.getVariable(v);
						final int lIndex = pFocusMatrixModel.getStateVariableIndex(lStateVariable);
						final double lStateVariableValue = pCurrentStateVector.getValueAt(lIndex);
						final double lMatrixEntryValue = lStateVariable.getPolarity() * lConstrainSumConstantEquality.getWeight();
						lValue += -(lMatrixEntryValue * lStateVariableValue);
					}
				}

				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}
			else if (lConstantConstrainAt instanceof ConstrainAverage)
			{
				final ConstrainAverage lConstrainAverage = (ConstrainAverage) lConstantConstrainAt;

				double lValue = 0;

				final double lNumberOfVariablesToTakeAverage = lConstrainAverage.getStateVariableList()
																																				.size() - 1;

				if (lConstantConstrainAt.isAbsolute())
				{
					final StateVariable lStateVariableAverage = lConstrainAverage.getVariable(0);
					final int lStateVariableAverageIndex = pFocusMatrixModel.getStateVariableIndex(lStateVariableAverage);
					final double lStateVariableAverageValue = pCurrentStateVector.getValueAt(lStateVariableAverageIndex);
					final double lAverageMatrixEntryValue = -lStateVariableAverage.getPolarity() * lConstrainAverage.getWeight();
					lValue += -(lAverageMatrixEntryValue * lStateVariableAverageValue);

					for (int v = 1; v < lConstrainAverage.getStateVariableList()
																								.size(); v++)
					{
						final StateVariable lStateVariable = lConstrainAverage.getVariable(v);
						final int lIndex = pFocusMatrixModel.getStateVariableIndex(lStateVariable);
						final double lStateVariableValue = pCurrentStateVector.getValueAt(lIndex);
						final double lMatrixEntryValue = lStateVariable.getPolarity() * lConstrainAverage.getWeight()
																							/ lNumberOfVariablesToTakeAverage;
						lValue += -(lMatrixEntryValue * lStateVariableValue);
					}
				}

				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}
			else if (lConstantConstrainAt instanceof ConstrainHarmonic)
			{
				final ConstrainHarmonic lConstrainHarmonic = (ConstrainHarmonic) lConstantConstrainAt;

				double lValue = 0;

				if (lConstantConstrainAt.isAbsolute())
				{
					final StateVariable lStateVariableHarmonic = lConstrainHarmonic.getVariable(0);
					final int lStateVariableHarmonicIndex = pFocusMatrixModel.getStateVariableIndex(lStateVariableHarmonic);
					final double lStateVariableHarmonicValue = pCurrentStateVector.getValueAt(lStateVariableHarmonicIndex);
					final double lHarmonicMatrixEntryValue = -lStateVariableHarmonic.getPolarity() * lConstrainHarmonic.getWeight();
					lValue += -(lHarmonicMatrixEntryValue * lStateVariableHarmonicValue);

					for (int v = 0; v < 4; v++)
					{
						final StateVariable lStateVariable = lConstrainHarmonic.getVariable(1 + v);
						final int lIndex = pFocusMatrixModel.getStateVariableIndex(lStateVariable);
						final double lStateVariableValue = pCurrentStateVector.getValueAt(lIndex);
						final double lMatrixEntryValue = ConstrainHarmonic.cFilterCoefs[v] * lStateVariable.getPolarity()
																							* lConstrainHarmonic.getWeight();
						lValue += -(lMatrixEntryValue * lStateVariableValue);
					}
				}

				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}

			lRowCounter++;
		}
	}

	private static int addObservableConstrainsToObservationVector(final ConstrainGraph pFocusMatrixModel,
																																final DenseMatrix64F pConstrainLessObservationVector,
																																int lRowCounter,
																																final SimpleMatrix lSimpleMatrix)
	{
		final int lNumberOfObservableConstrains = pFocusMatrixModel.getNumberOfObservableConstrains();
		for (int i = 0; i < lNumberOfObservableConstrains; i++)
		{
			final Constrain lObservableConstrainAt = pFocusMatrixModel.getObservableConstrainAt(i);
			// System.out.println(lObservableConstrainAt);
			if (lObservableConstrainAt instanceof ConstrainIdentity)
			{
				final double lValue = pConstrainLessObservationVector.get(lRowCounter,
																																	0) * lObservableConstrainAt.getWeight();

				lSimpleMatrix.set(lRowCounter, 0, lValue);
			}
			else if (lObservableConstrainAt instanceof ConstrainDifference)
			{
				final double lValue = pConstrainLessObservationVector.get(i,
																																	0) * lObservableConstrainAt.getWeight();

				lSimpleMatrix.set(lRowCounter, 0, lValue);
				// System.out.println(lRowCounter);
			}
			lRowCounter++;
		}
		// System.out.println(lSimpleMatrix);
		return lRowCounter;
	}

}
