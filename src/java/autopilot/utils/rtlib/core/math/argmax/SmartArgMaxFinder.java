package autopilot.utils.rtlib.core.math.argmax;

import autopilot.utils.rtlib.core.math.argmax.fitprob.FitQualityEstimator;
import autopilot.utils.rtlib.core.math.argmax.methods.COMArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.ClampingArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.DenoisingArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.EnsembleArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.GaussianFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.LoessFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.MedianArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.ModeArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.NormalizingArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.ParabolaFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.QuarticFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.RandomSplineFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.SplineFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.SymetricParabolaFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.Top5ArgMaxFinder;

public class SmartArgMaxFinder	implements
								ArgMaxFinder1DInterface,
								Fitting1D,
								FitProbabilityInterface
{

	private static final double cDefaultFitProbabilityThreshold = 0.95;

	private FitQualityEstimator mFitQualityEstimator;

	private final ParabolaFitArgMaxFinder mParabolaFitArgMaxFinder;
	private final SymetricParabolaFitArgMaxFinder mSymetricParabolaFitArgMaxFinder;
	private final GaussianFitArgMaxFinder mGaussianFitArgMaxFinder;
	private final QuarticFitArgMaxFinder mQuarticFitArgMaxFinder;
	private final SplineFitArgMaxFinder mSplineFitArgMaxFinder;
	private final RandomSplineFitArgMaxFinder mRandomSplineFitArgMaxFinder;
	private final LoessFitArgMaxFinder mLoessFitArgMaxFinder;
	private final Top5ArgMaxFinder mTop5ParabolaArgMaxFinder;
	private final COMArgMaxFinder mCOMArgMaxFinder;
	private final ModeArgMaxFinder mModeArgMaxFinder;
	private final MedianArgMaxFinder mMedianArgMaxFinder;
	private final DenoisingArgMaxFinder mDenoisingArgMaxFinder;

	private boolean mDenoisingActive = true;

	private Double mFitProbability;
	private Double mRMSD;

	public SmartArgMaxFinder()
	{
		super();

		mSymetricParabolaFitArgMaxFinder = new SymetricParabolaFitArgMaxFinder();
		mParabolaFitArgMaxFinder = new ParabolaFitArgMaxFinder();
		mGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder(128);
		mQuarticFitArgMaxFinder = new QuarticFitArgMaxFinder();
		mSplineFitArgMaxFinder = new SplineFitArgMaxFinder();
		mRandomSplineFitArgMaxFinder = new RandomSplineFitArgMaxFinder();
		mLoessFitArgMaxFinder = new LoessFitArgMaxFinder();
		mTop5ParabolaArgMaxFinder = new Top5ArgMaxFinder(new ParabolaFitArgMaxFinder());
		mCOMArgMaxFinder = new COMArgMaxFinder();
		mModeArgMaxFinder = new ModeArgMaxFinder();
		mMedianArgMaxFinder = new MedianArgMaxFinder();
		mDenoisingArgMaxFinder = new DenoisingArgMaxFinder(new ModeArgMaxFinder());

	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		mFitQualityEstimator = new FitQualityEstimator();

		final int lLocalMaxima = countLocalMaxima(pY);
		final boolean lDenoiseBefore = lLocalMaxima > 1;

		mFitProbability = mFitQualityEstimator.probability(pX, pY);
		mRMSD = mFitQualityEstimator.getRMSD();

		if (mFitProbability == null)
			return null;

		// System.out.println("lFitProbability=" + lFitProbability);

		// if (mFitProbability < mFitProbabilityThreshold)
		// return null;

		final EnsembleArgMaxFinder lEnsembleArgMaxFinder = new EnsembleArgMaxFinder();
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mParabolaFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mSymetricParabolaFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mGaussianFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mQuarticFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mSplineFitArgMaxFinder));
		/*lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mRandomSplineFitArgMaxFinder));/**/
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mLoessFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mTop5ParabolaArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mCOMArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mModeArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
												mMedianArgMaxFinder));
		lEnsembleArgMaxFinder.add(mDenoisingArgMaxFinder);

		return lEnsembleArgMaxFinder.argmax(pX, pY);
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		if (mFitQualityEstimator == null)
			if (argmax(pX, pY) == null)
				return null;

		final FitQualityEstimator lFitQualityEstimator = mFitQualityEstimator;
		mFitQualityEstimator = null;
		return lFitQualityEstimator.getFit(pX, pY);
	}

	public Double getLastFitProbability()
	{
		return mFitProbability;
	}

	@Override
	public double getRMSD()
	{
		return mRMSD;
	}

	private ArgMaxFinder1DInterface denoiseBefore(	boolean pDenoiseBefore,
													ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		if (pDenoiseBefore && mDenoisingActive)
			return new DenoisingArgMaxFinder(normalize(clamp(pArgMaxFinder1DInterface)));
		else
			return normalize(clamp(pArgMaxFinder1DInterface));
	}

	private ArgMaxFinder1DInterface normalize(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		return new NormalizingArgMaxFinder(pArgMaxFinder1DInterface);
	}

	private ArgMaxFinder1DInterface clamp(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		return new ClampingArgMaxFinder(pArgMaxFinder1DInterface);
	}

	private int countLocalMaxima(double[] pY)
	{
		int lCount = 0;
		final int lLength = pY.length;

		if (lLength >= 2)
		{
			if (pY[0] > pY[1])
				lCount++;
			if (pY[pY.length - 1] > pY[pY.length - 2])
				lCount++;
		}

		for (int i = 1; i < lLength - 1; i++)
		{
			final double lY = pY[i];
			final double lYbefore = pY[i - 1];
			final double lYafter = pY[i + 1];
			if (lY > lYbefore && lY > lYafter)
				lCount++;
		}

		return lCount;
	}

	@Override
	public String toString()
	{
		return String.format(	"SmartArgMaxFinder [mParabolaFitArgMaxFinder=%s, mSymetricParabolaFitArgMaxFinder=%s, mGaussianFitArgMaxFinder=%s, mQuarticFitArgMaxFinder=%s, mSplineFitArgMaxFinder=%s, mRandomSplineFitArgMaxFinder=%s, mLoessFitArgMaxFinder=%s, mTop5ParabolaArgMaxFinder=%s, mCOMArgMaxFinder=%s, mModeArgMaxFinder=%s, mMedianArgMaxFinder=%s, mDenoisingArgMaxFinder=%s]",
								mParabolaFitArgMaxFinder,
								mSymetricParabolaFitArgMaxFinder,
								mGaussianFitArgMaxFinder,
								mQuarticFitArgMaxFinder,
								mSplineFitArgMaxFinder,
								mRandomSplineFitArgMaxFinder,
								mLoessFitArgMaxFinder,
								mTop5ParabolaArgMaxFinder,
								mCOMArgMaxFinder,
								mModeArgMaxFinder,
								mMedianArgMaxFinder,
								mDenoisingArgMaxFinder);
	}

	public boolean isDenoisingActive()
	{
		return mDenoisingActive;
	}

	public void setDenoisingActive(boolean pDenoisingActive)
	{
		mDenoisingActive = pDenoisingActive;
	}

}
