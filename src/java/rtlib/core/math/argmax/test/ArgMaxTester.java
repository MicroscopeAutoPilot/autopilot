package rtlib.core.math.argmax.test;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import rtlib.core.math.argmax.ArgMaxFinder1DInterface;

import com.google.common.io.Resources;

public class ArgMaxTester
{

	public static TDoubleArrayList loadData(Class<?> pContextClass,
											String pRessource,
											int pColumn) throws IOException,
														URISyntaxException
	{
		final TDoubleArrayList lList = new TDoubleArrayList();

		try (BufferedReader lBufferedReader = Files.newBufferedReader(Paths.get(Resources.getResource(	pContextClass,
																										pRessource)
																							.toURI())))
		{
			String lLine;
			while ((lLine = lBufferedReader.readLine()) != null)
			{
				final String[] lSplittedLine = lLine.split("\t");
				if (pColumn < lSplittedLine.length)
				{
					final String lCell = lSplittedLine[pColumn];
					if (!lCell.trim().isEmpty())
					{
						final double lValue = Double.parseDouble(lCell);
						lList.add(lValue);
					}
				}
			}
		}
		return lList;
	}

	public static double test(	ArgMaxFinder1DInterface pArgMaxFinder1DInterface,
								int pNumberOfDatasets)	throws IOException,
														URISyntaxException
	{
		double lMaxError = 0;
		for (int i = 1; i <= pNumberOfDatasets; i++)
		{
			final TDoubleArrayList lY = loadData(	ArgMaxTester.class,
													"./benchmark/Benchmark.txt",
													i);
			final double LArgMaxReference = lY.get(0);
			lY.remove(0, 1);
			System.out.println(lY);

			final TDoubleArrayList lX = loadData(	ArgMaxTester.class,
													"./benchmark/Benchmark.txt",
													0);
			lX.remove(0, 1);
			if (lY.size() < lX.size())
				lX.remove(lY.size(), lX.size() - lY.size());
			System.out.println(lX);

			System.out.println("LArgMaxReference: " + LArgMaxReference);

			final Double lArgmax = pArgMaxFinder1DInterface.argmax(	lX.toArray(),
																	lY.toArray());

			System.out.println("class: " + pArgMaxFinder1DInterface
								+ "\n\t\targmax: "
								+ lArgmax);/**/

			double lError = 0;

			if (lArgmax == null)
				lError = Double.POSITIVE_INFINITY;
			else
				lError = abs(LArgMaxReference - lArgmax);

			if (LArgMaxReference == -1)
				lError = 0;

			lMaxError = max(lMaxError, lError);

		}
		return lMaxError;
	}
}
