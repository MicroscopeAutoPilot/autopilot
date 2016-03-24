package autopilot.fmatrix.constraingraph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.constrains.ConstrainConstantEquality;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

public class FocusMatrixModelTests
{

	@Test
	public void test()
	{
		final ConstrainGraph lFocusMatrixModel = new ConstrainGraph();

		final StateVariable lD1 = lFocusMatrixModel.addStateVariable(	"D1",
																																	1);

		final StateVariable lD2 = lFocusMatrixModel.addStateVariable(	"D2",
																																	1);
		final StateVariable lD3 = lFocusMatrixModel.addStateVariable(	"D3",
																																	-1);
		final StateVariable lI1 = lFocusMatrixModel.addStateVariable(	"I1",
																																	1);
		final StateVariable lI2 = lFocusMatrixModel.addStateVariable(	"I2",
																																	-1);

		final ObservableVariable lF1 = lFocusMatrixModel.addObservableVariable("F1");

		final ObservableVariable lF2 = lFocusMatrixModel.addObservableVariable("F2");
		final ObservableVariable lF3 = lFocusMatrixModel.addObservableVariable("F3");
		final ObservableVariable lF4 = lFocusMatrixModel.addObservableVariable("F4");

		final Constrain lOC1 = lFocusMatrixModel.addDifferenceConstrain("DCF1",
																																		lF1,
																																		lD1,
																																		lI1);

		lFocusMatrixModel.addDifferenceConstrain("DCF2", lF2, lD1, lI2);
		lFocusMatrixModel.addDifferenceConstrain("DCF3", lF3, lD2, lI1);
		lFocusMatrixModel.addDifferenceConstrain("DCF4", lF4, lD2, lI2);

		final ConstrainConstantEquality lCC1 = lFocusMatrixModel.addEqualityConstrain("Anchor D1",
																																									lD1,
																																									3);

		final Constrain lCC2 = lFocusMatrixModel.addEqualityConstrain("Link D2 and D3",
																																	lD2,
																																	lD3);

		assertEquals(lD1.getName(), "D1");
		assertEquals(lD1.getPolarity(), 1.0, 0);

		assertSame(lFocusMatrixModel.getNumberOfStateVariables(), 5);

		assertEquals(lF1.getName(), "F1");
		assertSame(lF1.isMissing(), false);

		assertSame(lFocusMatrixModel.getNumberOfObservableVariables(), 4);

		assertEquals(lOC1.getName(), "DCF1");
		assertEquals(lOC1.getConstrainVariable(), lF1);
		assertEquals(lOC1.getVariable1(), lD1);
		assertEquals(lOC1.getVariable2(), lI1);

		assertSame(lFocusMatrixModel.getNumberOfObservableConstrains(), 4);

		assertEquals(lCC1.getName(), "Anchor D1");
		assertEquals(lCC1.getVariable1(), lD1);
		assertEquals(lCC1.getValue(), 3, 0);

		assertEquals(lCC2.getName(), "Link D2 and D3");
		assertEquals(lCC2.getVariable1(), lD2);
		assertEquals(lCC2.getVariable2(), lD3);

		assertSame(lFocusMatrixModel.getNumberOfConstantConstrains(), 2);
		assertSame(	lFocusMatrixModel.getTotalNumberOfLinearConstrains(),
								6);

	}
}
