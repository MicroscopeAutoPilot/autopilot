package autopilot.fmatrix.compiler.test;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import autopilot.fmatrix.compiler.CompileMatrix;
import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

public class FMatrixCompilerTests
{

	@Test
	public void test()
	{
		final ConstrainGraph lFocusMatrixModel = new ConstrainGraph();

		final StateVariable lD1 = lFocusMatrixModel.addStateVariable(	"D1",
																																	1);

		final StateVariable lD2 = lFocusMatrixModel.addStateVariable(	"D2",
																																	-1);

		final StateVariable lI1 = lFocusMatrixModel.addStateVariable(	"I1",
																																	1);
		final StateVariable lI2 = lFocusMatrixModel.addStateVariable(	"I2",
																																	-1);

		final ObservableVariable lF1 = lFocusMatrixModel.addObservableVariable("F1");

		final ObservableVariable lF2 = lFocusMatrixModel.addObservableVariable("F2");
		final ObservableVariable lF3 = lFocusMatrixModel.addObservableVariable("F3");
		final ObservableVariable lF4 = lFocusMatrixModel.addObservableVariable("F4");

		lFocusMatrixModel.addDifferenceConstrain("DCF1", lF1, lD1, lI1);

		lFocusMatrixModel.addDifferenceConstrain("DCF2", lF2, lD1, lI2);
		lFocusMatrixModel.addDifferenceConstrain("DCF3", lF3, lD2, lI1);
		lFocusMatrixModel.addDifferenceConstrain("DCF4", lF4, lD2, lI2);

		lFocusMatrixModel.addEqualityConstrain("Anchor D1", lD1, 3);

		final SimpleMatrix lCompiledMatrix = CompileMatrix.compile(lFocusMatrixModel);

		System.out.println(lCompiledMatrix);
	}
}
