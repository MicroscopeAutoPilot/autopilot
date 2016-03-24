package autopilot.measures;

import autopilot.finder.FocusFinderFactoryInterface;
import autopilot.finder.FocusFinderInterface;
import autopilot.finder.SimpleFocusFinder;
import autopilot.image.DoubleArrayImage;
import autopilot.measures.implementations.correlative.SymmetricVollathF4;
import autopilot.measures.implementations.correlative.VollathF4;
import autopilot.measures.implementations.correlative.VollathF5;
import autopilot.measures.implementations.differential.AbsoluteLaplacian;
import autopilot.measures.implementations.differential.BlockTotalVariation;
import autopilot.measures.implementations.differential.BrennerMeasure;
import autopilot.measures.implementations.differential.SquaredLaplacian;
import autopilot.measures.implementations.differential.Tenengrad;
import autopilot.measures.implementations.differential.TotalVariation;
import autopilot.measures.implementations.spectral.DCTHighLowFreqRatio;
import autopilot.measures.implementations.spectral.DCTLpSparsity;
import autopilot.measures.implementations.spectral.DFTHighLowFreqRatio;
import autopilot.measures.implementations.spectral.KristanBayesSpectralEntropy;
import autopilot.measures.implementations.spectral.LogMomentSpectralPower;
import autopilot.measures.implementations.spectral.NormDCTEntropyBayesian;
import autopilot.measures.implementations.spectral.NormDCTEntropyBayesianPower4;
import autopilot.measures.implementations.spectral.NormDCTEntropyBayesianPower6;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannonDownscaled;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannonMedianFiltered;
import autopilot.measures.implementations.spectral.NormDFTEntropyShannon;
import autopilot.measures.implementations.spectral.NormHaarEntropyShannon;
import autopilot.measures.implementations.statistic.DifferenceKurtosis;
import autopilot.measures.implementations.statistic.HistogramEntropy;
import autopilot.measures.implementations.statistic.Kurtosis;
import autopilot.measures.implementations.statistic.LpSparsity;
import autopilot.measures.implementations.statistic.Max;
import autopilot.measures.implementations.statistic.Mean;
import autopilot.measures.implementations.statistic.Variance;

/**
 * Class with static methods and convenience methods for managing all focus
 * measures
 * 
 * @author royer
 */
public class FocusMeasures
{

	/**
	 * Enum containing the main types of focus measures.
	 * 
	 * @author royer
	 */
	public static enum FocusMeasureType
	{
		/**
		 * Differential focus measures are based on local differential operators
		 * such as first n seocnd order finite diffrences, sobel operators, etc...
		 */
		Differential,
		/**
		 * Statistical focus measures are based on statistics such as average
		 * brightness, standard deviation, kurtosis, etc...
		 */
		Statistic,
		/**
		 * Correlative focus measures estimate sharpness by evaluation the spatial
		 * auto-correlation of images.
		 */
		Correlative,
		/**
		 * Spectral focus measures estimate image sharpness by analyzing the
		 * frequency content of images. they rely on transforms such as the discrete
		 * Fourrier transform, discrete cosine transform, etc...
		 */
		Spectral
	}

	/**
	 * Enum listing all main focus measures exposed by this library.
	 * 
	 * @author royer
	 */
	public static enum FocusMeasure
	{
		DifferentialBrennerMeasure,
		DifferentialSquaredLaplacian,
		DifferentialAbsoluteLaplacian,
		DifferentialTenengrad,
		DifferentialTotalVariation,
		DifferentialBlockTotalVariation,
		StatisticMax,
		StatisticMean,
		StatisticVariance,
		StatisticNormalizedVariance,
		StatisticKurtosis,
		StatisticDifferenceKurtosis,
		StatisticHistogramEntropy,
		StatisticLpSparsity,
		SpectralLogMomentSpectralPower,
		CorrelativeVollathF4,
		CorrelativeVollathF5,
		CorrelativeSymmetricVollathF4,
		SpectralNormDCTEntropyShannon,
		SpectralNormDCTEntropyBayesian,
		SpectralNormDCTEntropyShannonMedianFiltered,
		SpectralNormDCTEntropyBayesianE4,
		SpectralNormDCTEntropyBayesianE6,
		SpectralNormDCTEntropyShanonDownscaled,
		SpectralKristanBayesSpectralEntropy,
		SpectralDCTLpSparsity,
		SpectralNormHaarEntropyShannon,
		SpectralNormDFTEntropyShannon,
		SpectralDCTHighLowFreqRatio,
		SpectralDFTHighLowFreqRatio;

		/**
		 * Returns the type of focus measure
		 * 
		 * @return focus measure type
		 */
		public final FocusMeasureType getType()
		{
			for (final FocusMeasureType lFocusMeasureType : FocusMeasureType.values())
			{
				if (this.name().startsWith(lFocusMeasureType.name()))
				{
					return lFocusMeasureType;
				}
			}
			return null;
		}

		/**
		 * Returns the long 'human readable' name of a focus measure.
		 * 
		 * @return long name
		 */
		public final String getLongName()
		{
			switch (this)
			{
			case CorrelativeSymmetricVollathF4:
				return "Symmetric Vollath F4";
			case CorrelativeVollathF4:
				return "Vollath F4";
			case CorrelativeVollathF5:
				return "Vollath F5";
			case DifferentialAbsoluteLaplacian:
				return "Absolute Laplacian";
			case DifferentialBlockTotalVariation:
				return "Block Total Variation";
			case DifferentialBrennerMeasure:
				return "Brenner Measure";
			case DifferentialSquaredLaplacian:
				return "Squared Laplacian";
			case DifferentialTenengrad:
				return "Tenengrad";
			case DifferentialTotalVariation:
				return "Total Variation";
			case SpectralDCTHighLowFreqRatio:
				return "High/low freq. DCT power Ratio";
			case SpectralDCTLpSparsity:
				return "Lp Sparsity of DCT";
			case SpectralDFTHighLowFreqRatio:
				return "High/low freq. DFT power Ratio";
			case SpectralKristanBayesSpectralEntropy:
				return "Kristan's 8x8 DCT Bayes-Spectral-Entropy";
			case SpectralLogMomentSpectralPower:
				return "Logarithmic Moment Spectral Power";
			case SpectralNormDCTEntropyBayesian:
				return "Normalized DCT Bayesian Entropy ";
			case SpectralNormDCTEntropyBayesianE4:
				return "Normalized DCT Generalized Bayesian Entropy (e=4)";
			case SpectralNormDCTEntropyBayesianE6:
				return "Normalized DCT Generalized Bayesian Entropy (e=6)";
			case SpectralNormDCTEntropyShannon:
				return "Normalized DCT Shannon Entropy";
			case SpectralNormDCTEntropyShannonMedianFiltered:
				return "Normalized DCT Shannon Entropy (median filtered)";
			case SpectralNormDCTEntropyShanonDownscaled:
				return "Normalized DCT Shannon Entropy (downscaled)";
			case SpectralNormDFTEntropyShannon:
				return "Normalized DFT Shannon Entropy";
			case SpectralNormHaarEntropyShannon:
				return "Normalized Haar Wavelet Transform Shannon Entropy";
			case StatisticDifferenceKurtosis:
				return "Kurtosis of Differences";
			case StatisticHistogramEntropy:
				return "Shannon Entropy of Histogram";
			case StatisticKurtosis:
				return "Kurtosis";
			case StatisticLpSparsity:
				return "Lp Sparsity";
			case StatisticMax:
				return "Maximum";
			case StatisticMean:
				return "Mean";
			case StatisticNormalizedVariance:
				return "NormalizedVariance";
			case StatisticVariance:
				return "Variance";
			default:
				return null;
			}
		}
	}

	/**
	 * Returns the list(array) of all focus measures
	 * 
	 * @return list of focus measures
	 */
	public static final FocusMeasure[] getFocusMeasuresArray()
	{
		return FocusMeasure.values();
	}

	/*
	 The following constants ae the default values for focus measure parameters
	**/
	private static final int cNumberOfBins = 512;
	public static final int cNumberOfAngleBins = 8;
	public static double cPSFSupportDiameter = 3;
	public static double cOTFFilterRatio = 1 / cPSFSupportDiameter;
	public static double cDCFilterRatio = 0.01;
	public static double cLowHighFreqRatio = 0.5;
	public static int cBlockSize = 7;
	public static int cNumberOfTiles = 8;
	public static int cExponent = 4;

	/**
	 * Computes all focus measures for a given image.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @return array of focus measure values (double)
	 */
	public static final double[] computAllFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] lFocusMeasuresArray = new double[FocusMeasure.values().length];
		int i = 0;
		for (final FocusMeasure lFocusMeasure : FocusMeasure.values())
		{
			final double lFocusMeasureValue = computeFocusMeasure(lFocusMeasure,
																														pDoubleArrayImage);
			lFocusMeasuresArray[i++] = lFocusMeasureValue;
		}

		return lFocusMeasuresArray;
	}

	/**
	 * Returns a focus measure for a given image. the image content might be
	 * modified during the focus measure computation.
	 * 
	 * @param pMeasure
	 *          focus measure to use
	 * @param pDoubleArrayImage
	 *          image from which to compute the focus measure.
	 * @return focus measure value
	 */
	public static final double computeFocusMeasure(	final FocusMeasure pMeasure,
																									final DoubleArrayImage pDoubleArrayImage)
	{
		return computeFocusMeasure(pMeasure, pDoubleArrayImage, null);
	}

	/**
	 * Returns a focus measure for a given image. A working image is provided to
	 * protect the original image from modification. If the working image is null
	 * or of incompatible dimensions then the method will make an internal working
	 * copy.
	 * 
	 * @param pMeasure
	 *          focus measure
	 * @param pDoubleArrayImage
	 *          original - non modified - image
	 * @param pWorkingDoubleArrayImage
	 *          working image
	 * @return focus measure value
	 */
	public static final double computeFocusMeasure(	final FocusMeasure pMeasure,
																									final DoubleArrayImage pDoubleArrayImage,
																									DoubleArrayImage pWorkingDoubleArrayImage)
	{
		if (pWorkingDoubleArrayImage == null || pDoubleArrayImage.getWidth() != pWorkingDoubleArrayImage.getWidth()
				|| pDoubleArrayImage.getHeight() != pWorkingDoubleArrayImage.getHeight())
		{
			pWorkingDoubleArrayImage = new DoubleArrayImage(pDoubleArrayImage);
		}
		else
		{
			pWorkingDoubleArrayImage.copyFrom(pDoubleArrayImage);
		}

		switch (pMeasure)
		{

		case DifferentialBrennerMeasure:
			return BrennerMeasure.compute(pWorkingDoubleArrayImage,
																		cPSFSupportDiameter);

		case DifferentialTotalVariation:
			return TotalVariation.compute(pWorkingDoubleArrayImage,
																		cPSFSupportDiameter);

		case DifferentialBlockTotalVariation:
			return BlockTotalVariation.compute(	pWorkingDoubleArrayImage,
																					cBlockSize,
																					cPSFSupportDiameter);

		case DifferentialSquaredLaplacian:
			return SquaredLaplacian.compute(pWorkingDoubleArrayImage,
																			cPSFSupportDiameter);

		case DifferentialAbsoluteLaplacian:
			return AbsoluteLaplacian.compute(	pWorkingDoubleArrayImage,
																				cPSFSupportDiameter);

		case DifferentialTenengrad:
			return Tenengrad.compute(	pWorkingDoubleArrayImage,
																cPSFSupportDiameter);

		case StatisticMax:
			return Max.compute(	pWorkingDoubleArrayImage,
													cPSFSupportDiameter);

		case StatisticMean:
			return Mean.compute(pWorkingDoubleArrayImage,
													cPSFSupportDiameter);

		case StatisticVariance:
			return Variance.compute(pWorkingDoubleArrayImage,
															cPSFSupportDiameter);

		case StatisticNormalizedVariance:
			return Variance.compute(pWorkingDoubleArrayImage,
															cPSFSupportDiameter);

		case StatisticHistogramEntropy:
			return HistogramEntropy.compute(pWorkingDoubleArrayImage,
																			cPSFSupportDiameter,
																			cNumberOfBins);

		case StatisticKurtosis:
			return Kurtosis.compute(pWorkingDoubleArrayImage,
															cPSFSupportDiameter);

		case StatisticDifferenceKurtosis:
			return DifferenceKurtosis.compute(pWorkingDoubleArrayImage,
																				cPSFSupportDiameter);

		case StatisticLpSparsity:
			return LpSparsity.compute(pWorkingDoubleArrayImage,
																cPSFSupportDiameter);

		case CorrelativeVollathF4:
			return VollathF4.compute(	pWorkingDoubleArrayImage,
																cPSFSupportDiameter);

		case CorrelativeSymmetricVollathF4:
			return SymmetricVollathF4.compute(pWorkingDoubleArrayImage,
																				cPSFSupportDiameter);

		case CorrelativeVollathF5:
			return VollathF5.compute(	pWorkingDoubleArrayImage,
																cPSFSupportDiameter);

		case SpectralDCTHighLowFreqRatio:
			return DCTHighLowFreqRatio.compute(	pWorkingDoubleArrayImage,
																					cOTFFilterRatio,
																					cDCFilterRatio,
																					cLowHighFreqRatio);

		case SpectralDFTHighLowFreqRatio:
			return DFTHighLowFreqRatio.compute(	pWorkingDoubleArrayImage,
																					cOTFFilterRatio,
																					cDCFilterRatio,
																					cLowHighFreqRatio);

		case SpectralDCTLpSparsity:
			return DCTLpSparsity.compute(	pWorkingDoubleArrayImage,
																		cPSFSupportDiameter);

		case SpectralKristanBayesSpectralEntropy:
			return KristanBayesSpectralEntropy.compute(pDoubleArrayImage);/**/

		case SpectralLogMomentSpectralPower:
			return LogMomentSpectralPower.compute(pWorkingDoubleArrayImage,
																						cPSFSupportDiameter);

		case SpectralNormDCTEntropyBayesian:
			return NormDCTEntropyBayesian.compute(pWorkingDoubleArrayImage,
																						cPSFSupportDiameter);

		case SpectralNormDCTEntropyBayesianE4:
			return NormDCTEntropyBayesianPower4.compute(pWorkingDoubleArrayImage,
																									cPSFSupportDiameter);

		case SpectralNormDCTEntropyBayesianE6:
			return NormDCTEntropyBayesianPower6.compute(pWorkingDoubleArrayImage,
																									cPSFSupportDiameter);

		case SpectralNormDCTEntropyShannon:
			return NormDCTEntropyShannon.compute(	pWorkingDoubleArrayImage,
																						cPSFSupportDiameter);

		case SpectralNormDCTEntropyShannonMedianFiltered:
			return NormDCTEntropyShannonMedianFiltered.compute(	pWorkingDoubleArrayImage,
																													cPSFSupportDiameter);

		case SpectralNormDCTEntropyShanonDownscaled:
			return NormDCTEntropyShannonDownscaled.compute(	pWorkingDoubleArrayImage,
																											cPSFSupportDiameter);

		case SpectralNormDFTEntropyShannon:
			return NormDFTEntropyShannon.compute(	pWorkingDoubleArrayImage,
																						cPSFSupportDiameter);

		case SpectralNormHaarEntropyShannon:
			return NormHaarEntropyShannon.compute(pWorkingDoubleArrayImage,
																						cPSFSupportDiameter);

		default:
			break;

		}
		throw new UnsupportedOperationException(String.format("Focus measure %s unknown!",
																													pMeasure.toString()));
	}

	/**
	 * Returns a factory for simple focus finder for a given focus measure.
	 * 
	 * @param pFocusMeasure
	 *          focus measure
	 * @return simple finder factory
	 */
	public static FocusFinderFactoryInterface<Double> getFactoryForSimpleFocusFinder(final FocusMeasure pFocusMeasure)
	{
		return new FocusFinderFactoryInterface<Double>()
		{

			@Override
			public FocusFinderInterface<Double> instantiate()
			{
				return new SimpleFocusFinder<Double>(pFocusMeasure);
			}
		};
	}

}
