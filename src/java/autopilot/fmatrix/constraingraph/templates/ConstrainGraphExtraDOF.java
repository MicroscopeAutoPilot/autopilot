package autopilot.fmatrix.constraingraph.templates;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.ConstrainAverage;
import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;
import autopilot.utils.ndmatrix.NDMatrix;

/**
 * This subclass of ConstrainGraph handles other generic DOFs (i.e. not D,I,Y,A
 * or B) that might be needed for some experiments. The constrain graphs built
 * handles missing information in the most logical manner.
 * 
 * @author royer
 *
 */
public class ConstrainGraphExtraDOF extends ConstrainGraph
{

	static public final double cEqualityConstrainEqualityWeight = 1000;
	private static final double cEpsilon = 0.0001;
	private static final double cMissingInfoConstrainWeight = 1;

	private final int mNumberOfWavelengths;
	private final int mNumberOfPlanes;
	private final boolean[] mMissingObservations;
	private final double[] mMaximalCorrections;

	private final NDMatrix<StateVariable> mStateVariableMatrix;
	private final NDMatrix<Boolean> mUnconstrainedVariableMatrix;
	private int mNumberOfDOFs;

	/**
	 * Constructs a constrain graph for a given number of colors, planes, and
	 * extra DOFs. A missing observation array as well as a maxmal corrections
	 * array must be provided.
	 * 
	 * @param pNumberOfWavelengths
	 * @param pNumberOfPlanes
	 * @param pNumberOfDOFs
	 * @param pMissingObservations
	 * @param pMaximalCorrections
	 */
	public ConstrainGraphExtraDOF(final int pNumberOfWavelengths,
																final int pNumberOfPlanes,
																final int pNumberOfDOFs,
																final boolean[] pMissingObservations,
																final double[] pMaximalCorrections)
	{
		super();
		mNumberOfWavelengths = pNumberOfWavelengths;
		mNumberOfPlanes = pNumberOfPlanes;
		mNumberOfDOFs = pNumberOfDOFs;
		mMissingObservations = pMissingObservations;
		mMaximalCorrections = pMaximalCorrections;

		mStateVariableMatrix = new NDMatrix<StateVariable>(	mNumberOfWavelengths,
																												mNumberOfPlanes,
																												mNumberOfDOFs);

		mUnconstrainedVariableMatrix = new NDMatrix<Boolean>(	mNumberOfWavelengths,
																													mNumberOfPlanes,
																													mNumberOfDOFs);

		addBaseConstrains();

		addMissingInfoConstrains();

		addMaximalCorrectionsInequalityConstrains();

		/*for (Constrain lConstrain : getConstantConstrains())
			System.out.println(lConstrain);
		for (Constrain lConstrain : getObservableConstrains())
			System.out.println(lConstrain);/**/

	}

	private void addBaseConstrains()
	{
		int lObservableVariableCounter = 0;
		for (int w = 0; w < mNumberOfWavelengths; w++)
		{

			final String sw = "w" + w;
			for (int p = 0; p < mNumberOfPlanes; p++)
			{
				final String sp = sw + "p" + p;

				for (int o = 0; o < mNumberOfDOFs; o++)
				{
					final String so = sp + "v" + o;

					final StateVariable lSV = addStateVariable("SV" + so, 1);

					/*System.out.format("i=%d, w=%d, p=%d, o=%d lSV=%s\n",
														mStateVariableMatrix.getIndex(w, p, o),
														w,
														p,
														o,
														lSV);/**/
					mStateVariableMatrix.set(lSV, w, p, o);
					// System.out.println(mStateVariableMatrix);
					final ObservableVariable lOV = addObservableVariable("OV" + so);

					boolean lMissingInfo = mMissingObservations[lObservableVariableCounter++];
					if (lMissingInfo)
						lOV.setMissing(true);

					addIdentityConstrain("EDC" + so, lOV, lSV);

				}
			}
		}

	}

	private void addMaximalCorrectionsInequalityConstrains()
	{
		if (mMaximalCorrections == null)
			return;

		int lNumberOfStateVariables = getNumberOfStateVariables();

		for (int i = 0; i < lNumberOfStateVariables; i++)
		{
			double lMax = mMaximalCorrections[i];
			if (lMax <= 0)
				lMax = cEpsilon;

			StateVariable lStateVariable = getStateVariableByIndex(i);

			String lConstrainName = "MaxCorrectionFor" + lStateVariable.getName();

			ConstantVariable lConstantVariable = new ConstantVariable(lConstrainName,
																																lMax);

			addAbsoluteDifferentialInequalityConstrain(	lConstrainName,
																									lConstantVariable,
																									lStateVariable);
		}
	}

	private void addMissingInfoConstrains()
	{
		int lObservableVariableCounter = 0;

		// Adds missing info constrains for a whole color if there is no available
		// info for that color
		for (int w = 0; w < mNumberOfWavelengths; w++)
		{

			for (int p = 0; p < mNumberOfPlanes; p++)
				for (int o = 0; o < mNumberOfDOFs; o++)
				{
					boolean lMissingInfo = mMissingObservations[lObservableVariableCounter++];
					mUnconstrainedVariableMatrix.set(lMissingInfo, w, p, o);
				}

			for (int o = 0; o < mNumberOfDOFs; o++)
				if (isInfoMissingForAllPlanes(w, o))
					addMissingInfoForWholeColor(w, o);/**/

			// Missing Info Constrain within a color:

			final String sw = "w" + w;
			for (int p = 0; p < mNumberOfPlanes; p++)
			{
				final String swp = sw + "p" + p;
				for (int o = 0; o < mNumberOfDOFs; o++)
					if (!isInfoMissingForAllPlanes(w, o))
					{
						final String swpv = swp + "v" + o;
						if (mUnconstrainedVariableMatrix.get(w, p, o))
						{
							final ConstrainAverage lMissingInfoAverageConstrain = addAverageConstrain("SinglePlaneAverageConstrain" + swpv,
																																												mStateVariableMatrix.clipAndGet(w,
																																																												p,
																																																												o),
																																												mStateVariableMatrix.clipAndGet(w,
																																																												p - 1,
																																																												o),
																																												mStateVariableMatrix.clipAndGet(w,
																																																												p,
																																																												o),
																																												mStateVariableMatrix.clipAndGet(w,
																																																												p + 1,
																																																												o));

							lMissingInfoAverageConstrain.setRelative(false);
							lMissingInfoAverageConstrain.setWeight(cMissingInfoConstrainWeight);
						}

					}

			}
		}

	}

	private void addMissingInfoForWholeColor(int w, int o)
	{
		final int wnm = findColorWithMostInformation(o);

		if (wnm == -1)
			return;

		for (int p = 0; p < mNumberOfPlanes; p++)
		{
			final String swp = "w" + w + "p" + p;
			final String swpv = swp + "v" + o;

			final ConstrainAverage lMissingInfoAverageConstrain = addAverageConstrain("WholeColorAverageConstrain" + swpv,
																																								mStateVariableMatrix.clipAndGet(w,
																																																								p,
																																																								o),
																																								mStateVariableMatrix.clipAndGet(wnm,
																																																								p,
																																																								o),
																																								mStateVariableMatrix.clipAndGet(w,
																																																								p,
																																																								o));

			lMissingInfoAverageConstrain.setRelative(false);
			lMissingInfoAverageConstrain.setWeight(cMissingInfoConstrainWeight);

		}
	}

	private boolean isInfoMissingForAllPlanes(int w, int o)
	{
		boolean lIsInfoMissingForAllPlanes = true;
		for (int p = 0; p < mNumberOfPlanes; p++)
		{
			final int i = w * mNumberOfPlanes
										* mNumberOfDOFs
										+ p
										* mNumberOfDOFs
										+ o;
			lIsInfoMissingForAllPlanes &= mMissingObservations[i];
		}
		return lIsInfoMissingForAllPlanes;
	}

	private int findColorWithMostInformation(int po)
	{
		int[] lNumberOfPlanesWithInfo = new int[mNumberOfWavelengths];

		int lObservableVariableCounter = 0;
		for (int w = 0; w < mNumberOfWavelengths; w++)
			for (int p = 0; p < mNumberOfPlanes; p++)
				for (int o = 0; o < mNumberOfDOFs; o++)
				{
					boolean lMissingInfo = mMissingObservations[lObservableVariableCounter++];
					if (o == po && !lMissingInfo)
					{
						lNumberOfPlanesWithInfo[w]++;
					}
				}

		int lColorWithMostInfo = -1;
		int lMostNumberOfPlanesWithInfo = 0;

		for (int w = 0; w < mNumberOfWavelengths; w++)
		{
			if (lNumberOfPlanesWithInfo[w] > lMostNumberOfPlanesWithInfo)
			{
				lMostNumberOfPlanesWithInfo = lNumberOfPlanesWithInfo[w];
				lColorWithMostInfo = w;
			}
		}

		return lColorWithMostInfo;
	}

	public int getNumberOfWavelengths()
	{
		return mNumberOfWavelengths;
	}

	public int getNumberOfPlanes()
	{
		return mNumberOfPlanes;
	}

}
