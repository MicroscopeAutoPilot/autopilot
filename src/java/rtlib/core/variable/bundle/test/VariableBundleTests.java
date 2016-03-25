package rtlib.core.variable.bundle.test;

import org.junit.Test;

import rtlib.core.variable.VariableListener;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.types.doublev.DoubleVariable;

public class VariableBundleTests
{

	@Test
	public void test()
	{
		final VariableBundle lVariableBundle = new VariableBundle("Name");
		lVariableBundle.addListener(new VariableListener<VariableBundle>()
		{

			@Override
			public void setEvent(	VariableBundle pCurrentValue,
									VariableBundle pNewValue)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void getEvent(VariableBundle pCurrentValue)
			{
				// TODO Auto-generated method stub

			}
		});

		final DoubleVariable lTestVariable = new DoubleVariable("var1");
		lVariableBundle.addVariable(lTestVariable);

	}

}
