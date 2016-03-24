package autopilot.measures.benchmark.run;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import autopilot.measures.benchmark.BenchmarkMeasures;

public class BenchmarkMeasuresRun
{

	@Test
	public void testMeasures() throws IOException
	{
		try
		{
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			final File lUserHome = new File(System.getProperty("user.home"));
			final File lFolder = new File(lUserHome,
																		"Projects/AutoPilot/datasets/bock");
			// "Projects/AutoPilot/datasets/focus/stacks");
			final File lResultFolder = new File(lUserHome,
																					"Projects/AutoPilot/datasets/bock/result");
			// "Projects/AutoPilot/datasets/focus/results/default");
			lResultFolder.mkdirs();
			BenchmarkMeasures lBenchmarkMeasures;

			lBenchmarkMeasures = new BenchmarkMeasures(	lFolder,
																									"aligned.tif",
																									"",
																									lResultFolder,
																									false);
			lBenchmarkMeasures.setPSFSupportDiameter(1);
			lBenchmarkMeasures.run();
			/**/

			/**/
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testMeasuresDifferentPSFRadii() throws IOException
	{
		try
		{
			final File lUserHome = new File(System.getProperty("user.home"));
			final File lFolder = new File(lUserHome,
																		"Projects/AutoPilot/datasets/focus/stacks");

			BenchmarkMeasures lBenchmarkMeasures;

			for (int r = 1; r <= 16; r++)
			{
				final File lResultFolder = new File(lUserHome,
																						"Projects/AutoPilot/datasets/focus/results/results(r=" + r
																								+ ")");
				/*if (lResultFolder.exists())
					continue;/**/

				lResultFolder.mkdirs();

				lBenchmarkMeasures = new BenchmarkMeasures(	lFolder,
																										"",
																										"SpectralNormDCT",
																										lResultFolder,
																										false);
				lBenchmarkMeasures.setPSFSupportDiameter(r);
				lBenchmarkMeasures.run();
			}

			/**/
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testMeasuresSyntheticStacks() throws IOException
	{
		try
		{
			final File lUserHome = new File(System.getProperty("user.home"));
			final File lFolder = new File(lUserHome,
																		"Projects/AutoPilot/datasets/focus/syntheticstacks");

			BenchmarkMeasures lBenchmarkMeasures;

			final File lResultFolder = new File(lUserHome,
																					"Projects/AutoPilot/datasets/focus/syntheticresults/default");
			lResultFolder.mkdirs();

			lBenchmarkMeasures = new BenchmarkMeasures(	lFolder,
																									"",
																									"",
																									lResultFolder,
																									true);
			lBenchmarkMeasures.setPSFSupportDiameter(6);

			lBenchmarkMeasures.run();

			/**/
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testMeasuresSyntheticStacksDifferentPSFradii() throws IOException
	{
		try
		{
			final File lUserHome = new File(System.getProperty("user.home"));
			final File lFolder = new File(lUserHome,
																		"Projects/AutoPilot/datasets/focus/syntheticstacks");

			BenchmarkMeasures lBenchmarkMeasures;

			for (int r = 1; r <= 16; r++)
			{
				final File lResultFolder = new File(lUserHome,
																						"Projects/AutoPilot/datasets/focus/syntheticresults/syntheticresults(r=" + r
																								+ ")");
				lResultFolder.mkdirs();

				lBenchmarkMeasures = new BenchmarkMeasures(	lFolder,
																										"",
																										"SpectralNormDCTEntropyShannon",
																										lResultFolder,
																										false);
				lBenchmarkMeasures.setPSFSupportDiameter(r);
				lBenchmarkMeasures.run();
			}

			/**/
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}
}
