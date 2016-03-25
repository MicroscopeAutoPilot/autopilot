package rtlib.core.math.argmax.fitprob;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

public class FitQualityEstimator
{
	private static final int cMaxNumberOfRandomizedDatasets = 100000;

	private static final ConcurrentHashMap<Integer, NormalDistribution> sNullHypothesisDistribution = new ConcurrentHashMap<>();

	private static final int cBufferLength = 128 * 1024;

	private final ExecutorService mExecutorService;

	private UnivariateDifferentiableFunction mUnivariateDifferentiableFunction;

	private Double mRealDataRMSD;

	public FitQualityEstimator()
	{
		this(Executors.newFixedThreadPool(Runtime.getRuntime()
													.availableProcessors()));
	}

	public FitQualityEstimator(ExecutorService pExecutorService)
	{
		mExecutorService = pExecutorService;
	}

	public NormalDistribution getNullHypothesisDistribution(int lLength)
	{
		// System.out.println("getNullHypothesisDistribution...");
		NormalDistribution lNormalDistribution = sNullHypothesisDistribution.get(lLength);
		if (lNormalDistribution == null)
		{
			final File lCacheFile = getFile(lLength);
			if (!lCacheFile.exists())
			{
				lNormalDistribution = computeNullHypothesisDistribution(lLength);
				sNullHypothesisDistribution.put(lLength,
												lNormalDistribution);
				try
				{
					writeToFile(lNormalDistribution, lCacheFile);
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					lNormalDistribution = readFromFile(lCacheFile);
				}
				catch (final ClassNotFoundException e)
				{
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}

			if (lNormalDistribution == null)
			{
				lNormalDistribution = computeNullHypothesisDistribution(lLength);
				sNullHypothesisDistribution.put(lLength,
												lNormalDistribution);
				try
				{
					writeToFile(lNormalDistribution, lCacheFile);
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}

		}
		// System.out.println("getNullHypothesisDistribution=" +
		// lNormalDistribution);
		return lNormalDistribution;
	}

	private File getFile(int pLength)
	{
		final File lUserHome = new File(System.getProperty("user.home"));
		final File lStatsFolder = new File(lUserHome, ".stats");
		if (!lStatsFolder.exists())
			lStatsFolder.mkdirs();
		final File lFile = new File(lStatsFolder,
									String.format(	"%s_l=%d.obj",
													this.getClass()
														.getSimpleName(),
													pLength));

		return lFile;
	}

	public void writeToFile(NormalDistribution pNormalDistribution,
							File pFile) throws IOException
	{
		final FileOutputStream lFileOutputStream = new FileOutputStream(pFile);
		final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream,
																					cBufferLength);
		final ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);
		lObjectOutputStream.writeObject(pNormalDistribution);
		lObjectOutputStream.close();
		lFileOutputStream.close();
	}

	public NormalDistribution readFromFile(File pFile)	throws IOException,
														ClassNotFoundException
	{
		final FileInputStream lFileInputStream = new FileInputStream(pFile);
		final BufferedInputStream lBufferedInputStream = new BufferedInputStream(	lFileInputStream,
																					cBufferLength);
		final ObjectInputStream lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
		final NormalDistribution lNormalDistribution = (NormalDistribution) lObjectInputStream.readObject();
		lObjectInputStream.close();
		lFileInputStream.close();

		return lNormalDistribution;
	}

	public NormalDistribution computeNullHypothesisDistribution(int lLength)
	{
		// System.out.println("computeNullHypothesisDistribution...");
		final int lNumberOfRandomizedDatasets = cMaxNumberOfRandomizedDatasets;

		final ArrayList<FutureTask<Double>> lTaskList = new ArrayList<FutureTask<Double>>(lNumberOfRandomizedDatasets);

		final double[] lX = new double[lLength];
		for (int i = 0; i < lLength; i++)
			lX[i] = i;

		for (int i = 0; i < lNumberOfRandomizedDatasets; i++)
		{
			final RandomizedDataGaussianFitter lRandomizedDataGaussianFitter = new RandomizedDataGaussianFitter();
			final Callable<Double> lCallable = () -> {
				Thread.currentThread()
						.setPriority(Thread.MIN_PRIORITY);
				return lRandomizedDataGaussianFitter.computeRMSDForRandomData(lX);
			};
			final FutureTask<Double> lFutureTask = new FutureTask<Double>(lCallable);
			mExecutorService.execute(lFutureTask);
			lTaskList.add(lFutureTask);
		}

		final TDoubleArrayList lIRMSDList = new TDoubleArrayList();
		for (final FutureTask<Double> lFutureTask : lTaskList)
		{
			try
			{
				final Double lRMSD = lFutureTask.get(	200,
														TimeUnit.MILLISECONDS);
				if (lRMSD != null)
					lIRMSDList.add(lRMSD);
			}
			catch (final Throwable e)
			{
			}
		}

		final double[] lIRMSDArray = lIRMSDList.toArray();
		final Mean lMean = new Mean();
		final Variance lVariance = new Variance();
		final double lVarianceValue = lVariance.evaluate(lIRMSDArray);
		final double lCenterValue = lMean.evaluate(lIRMSDArray);
		final double lStandardDeviation = sqrt(lVarianceValue);

		/*System.out.format("n= %d, mu=%g, sigma=%g \n",
											lLength,
											lCenterValue,
											lStandardDeviation);/**/

		// lCenterValue = 0.25;// lMean.evaluate(lIRMSDArray);
		// lStandardDeviation = 0.0625; // sqrt(lVarianceValue);

		final NormalDistribution lNormalDistribution = new NormalDistribution(	lCenterValue,
																				lStandardDeviation);
		return lNormalDistribution;

	}

	public Double probability(double[] pX, double[] pY)
	{
		final double[] lNormY = RandomizedDataGaussianFitter.normalizeCopy(pY);

		final RandomizedDataGaussianFitter lDataGaussianFitter = new RandomizedDataGaussianFitter(	pX,
																									lNormY);
		try
		{
			mRealDataRMSD = lDataGaussianFitter.computeRMSD();

			if (mRealDataRMSD == null)
				return 0.0;

			mUnivariateDifferentiableFunction = lDataGaussianFitter.getFunction();

			final NormalDistribution lNormalDistribution = getNullHypothesisDistribution(pX.length);

			final double lProbabilityThatRandomDataHasWorseFit = lNormalDistribution.cumulativeProbability(mRealDataRMSD);

			final double lFitProbability = 1 - lProbabilityThatRandomDataHasWorseFit;

			return lFitProbability;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return 0.0;
		}
	}

	public Double getRMSD()
	{
		return mRealDataRMSD;
	}

	public double[] getFit(double[] pX, double[] pY)
	{
		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			lMin = min(lMin, pY[i]);/**/
			lMax = max(lMax, pY[i]);/**/
		}

		final double[] lFittedY = new double[pX.length];
		for (int i = 0; i < pX.length; i++)
		{
			lFittedY[i] = lMin + (lMax - lMin)
							* (mUnivariateDifferentiableFunction == null ? 0
																		: mUnivariateDifferentiableFunction.value(pX[i]));
		}
		return lFittedY;
	}

}
