package autopilot.utils.R.test;

import java.io.File;
import java.io.IOException;

import com.github.rcaller.rStuff.RCaller;
import com.github.rcaller.rStuff.RCode;
import org.junit.Test;


import autopilot.utils.R.R;

/**
 */
public class RCallerTests
{

	@Test
	public void testR() throws IOException
	{
		try
		{
			final R r = new R();

			final double[][] m = new double[][]
			{
			{ 1, 2 },
			{ 4, 3 },
			{ 3, 8 },
			{ 5, 1 },
			{ 6, 2 },
			{ 10, 2 } };

			r.addDoubleArray("m", m);
			r.startPlot();
			r.addCode("library(ggplot2)");
			r.addCode("d <- as.data.frame(m)");
			r.addCode("qplot(V1, V2, data = d, geom = \"line\")");
			r.endPlot();
			r.run();
			// r.showPlot();

			Thread.sleep(10000);
		}
		catch (final Exception e)
		{
			System.out.println(e.toString());
		}
	}

	@Test
	public void testRCaller() throws IOException
	{
		try
		{
			final RCaller caller = new RCaller();
			caller.setRscriptExecutable("/opt/local/bin/rscript");

			final RCode code = new RCode();
			code.clear();

			final double[] numbers = new double[]
			{ 1, 4, 3, 5, 6, 10 };

			code.addDoubleArray("x", numbers);
			final File file = code.startPlot();
			System.out.println("Plot will be saved to : " + file);
			code.addRCode("plot.ts(x)");
			code.endPlot();

			caller.setRCode(code);
			caller.runOnly();
			// code.showPlot(file);
			Thread.sleep(10000);
		}
		catch (final Exception e)
		{
			System.out.println(e.toString());
		}
	}

}
