package autopilot.fmatrix.constraingraph.templates;

import java.util.ArrayList;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.constrains.ConstrainAverage;
import autopilot.fmatrix.constraingraph.constrains.ConstrainEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumConstantEquality;
import autopilot.fmatrix.constraingraph.constrains.ConstrainSumEquality;
import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;
import autopilot.utils.ndmatrix.NDMatrix;

/**
 * This subclass of ConstrainGraph is specially dedicated to build constrain
 * graphs for light-sheet microscopes with two detection and two illumination
 * paths.
 * 
 * @author royer
 *
 */
public class ConstrainGraph2D2I extends ConstrainGraph
{

	static public final double cEqualityConstrainEqualityWeight = 1000;
	private static final double cEpsilon = 0.0001;
	private static final boolean cInterColorDetectionEquality = false;

	private final boolean mExtraDOF;
	private final boolean mAnchorDetection;
	private final boolean mSymmetricAnchor;
	private final int mNumberOfWavelengths;
	private final int mNumberOfPlanes;
	private final boolean[] mSyncPlaneIndices;
	private final double mConstantConstrainWeight;
	private final boolean[] mMissingObservations;
	private final double[] mMaximalCorrections;

	private final NDMatrix<StateVariable> mStateVariableMatrix;
	private final NDMatrix<Boolean> mUnconstrainedVariableMatrix;

	/**
	 * Constructs a ConstrainGraph2D2I for the given number of colors, planes and
	 * sync plane index. The constant constrain weight can also be provided.
	 * 
	 * @param pNumberOfWavelengths
	 * @param pNumberOfPlanes
	 * @param pSyncPlaneIndex
	 * @param pConstantConstrainWeight
	 */
	public ConstrainGraph2D2I(final int pNumberOfWavelengths,
														final int pNumberOfPlanes,
														final int pSyncPlaneIndex,
														final double pConstantConstrainWeight)
	{
		this(	false,
					false,
					false,
					pNumberOfWavelengths,
					pNumberOfPlanes,
					pSyncPlaneIndex,
					pConstantConstrainWeight,
					null,
					null);
	}

	/**
	 * Constructs a ConstrainGraph2D2I with more details, including whether the
	 * Y,A, and B DOFs should be included, anchor symmetry, missing observation
	 * table, and maximal corrections for each DOF.
	 * 
	 * @param pAddExtraDOF
	 *          true if extra DOFs should be added,
	 * @param pAnchorDetection
	 *          true for anchoring of detection DOFs,
	 * @param pSymmetricAnchor
	 *          true for symmetric anchoring,
	 * @param pNumberOfWavelengths
	 *          number of wavelengths,
	 * @param pNumberOfPlanes
	 *          number of planes,
	 * @param pSyncPlaneIndex
	 *          sync plane index,
	 * @param pConstantConstrainWeight
	 *          constant constrain weight,
	 * @param pMissingObservations
	 *          missing observations array,
	 * @param pMaximalCorrections
	 *          maximal corrections array
	 */
	public ConstrainGraph2D2I(final boolean pAddExtraDOF,
														final boolean pAnchorDetection,
														final boolean pSymmetricAnchor,
														final int pNumberOfWavelengths,
														final int pNumberOfPlanes,
														final int pSyncPlaneIndex,
														final double pConstantConstrainWeight,
														final boolean[] pMissingObservations,
														final double[] pMaximalCorrections)
	{
		this(	pAddExtraDOF,
					pAnchorDetection,
					pSymmetricAnchor,
					pNumberOfWavelengths,
					pNumberOfPlanes,
					generateSyncPlanesIndicesFromSingleSyncPlane(	pNumberOfWavelengths,
																												pNumberOfPlanes,
																												pSyncPlaneIndex),
					pConstantConstrainWeight,
					pMissingObservations,
					pMaximalCorrections);
	}

	/**
	 * Generates an array of sync plane indices given a number of colors, planes
	 * and a single sync plane index.
	 * 
	 * @param pNumberOfWavelengths
	 *          numebr of colors,
	 * @param pNumberOfPlanes
	 *          number of planes,
	 * @param pSyncPlaneIndex
	 *          sync plane index,
	 * @return array of sync planes indices.
	 */
	public static boolean[] generateSyncPlanesIndicesFromSingleSyncPlane(	final int pNumberOfWavelengths,
																																				final int pNumberOfPlanes,
																																				final int pSyncPlaneIndex)
	{
		final boolean[] lSyncPlaneIndicesArray = new boolean[pNumberOfWavelengths * pNumberOfPlanes];
		for (int w = 0; w < pNumberOfWavelengths; w++)
		{
			for (int p = 0; p < pNumberOfPlanes; p++)
			{
				if (p == pSyncPlaneIndex)
				{
					lSyncPlaneIndicesArray[w * pNumberOfPlanes + p] = true;
				}
			}
		}

		return lSyncPlaneIndicesArray;
	}

	/**
	 * Constructs a ConstrainGraph2D2I with more details, including whether the
	 * Y,A, and B DOFs should be included, anchor symmetry, missing observation
	 * table, and maximal corrections for each DOF. This constructor must be
	 * provided with an array of sync plane indices - this is more flexible and
	 * powerfull than providing just one sync plane index..
	 * 
	 * @param pAddExtraDOF
	 * @param pAnchorDetection
	 * @param pSymmetricAnchor
	 * @param pNumberOfWavelengths
	 * @param pNumberOfPlanes
	 * @param pSyncPlaneIndices
	 * @param pConstantConstrainWeight
	 * @param pMissingObservations
	 * @param pMaximalCorrections
	 */
	public ConstrainGraph2D2I(final boolean pAddExtraDOF,
														final boolean pAnchorDetection,
														final boolean pSymmetricAnchor,
														final int pNumberOfWavelengths,
														final int pNumberOfPlanes,
														final boolean[] pSyncPlaneIndices,
														final double pConstantConstrainWeight,
														final boolean[] pMissingObservations,
														final double[] pMaximalCorrections)
	{
		super();
		mAnchorDetection = pAnchorDetection;
		mSymmetricAnchor = pSymmetricAnchor;
		mNumberOfWavelengths = pNumberOfWavelengths;
		mNumberOfPlanes = pNumberOfPlanes;
		mExtraDOF = pAddExtraDOF;
		mSyncPlaneIndices = pSyncPlaneIndices;
		mConstantConstrainWeight = pConstantConstrainWeight;
		mMissingObservations = pMissingObservations;
		mMaximalCorrections = pMaximalCorrections;

		mStateVariableMatrix = new NDMatrix<StateVariable>(	mNumberOfWavelengths,
																												mNumberOfPlanes,
																												mExtraDOF	? 10
																																	: 4);
		mUnconstrainedVariableMatrix = new NDMatrix<Boolean>(	mNumberOfWavelengths,
																													mNumberOfPlanes,
																													mExtraDOF	? 10
																																		: 4);

		addBaseConstrains();

		addMissingInfoConstrains();

		addInterColorConstrains();

		addAnchorConstrains();

		addMaximalCorrectionsInequalityConstrains();

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

	private void addAnchorConstrains()
	{
		// Anchoring of detection arms:
		if (mAnchorDetection)
		{
			for (int w = 0; w < mNumberOfWavelengths; w++)
			{
				if (!hasSyncPlane(mSyncPlaneIndices, w))
				{
					continue;
				}

				if (mSymmetricAnchor)
				{
					for (int p = 0; p < mNumberOfPlanes; p++)
					{
						final ConstrainSumConstantEquality lAddSumConstantEqualityConstrainFirst = addSumConstantEqualityConstrain(	"AnchorCenterOfMassD1D2w" + w
																																																														+ "p"
																																																														+ p,
																																																												0,
																																																												mStateVariableMatrix.get(	w,
																																																																									p,
																																																																									0),
																																																												mStateVariableMatrix.get(	w,
																																																																									p,
																																																																									1));/**/
						lAddSumConstantEqualityConstrainFirst.setWeight(mConstantConstrainWeight);
					}

				}
				else
				{
					final ConstrainSumConstantEquality lAddSumConstantEqualityConstrain = addSumConstantEqualityConstrain("AnchorD1",
																																																								0,
																																																								mStateVariableMatrix.get(	w,
																																																																					0,
																																																																					0));/**/
					lAddSumConstantEqualityConstrain.setWeight(mConstantConstrainWeight);
				}
			}

		}
	}

	private void addInterColorConstrains()
	{
		// Adding constrains enforcing equality of center of masses of detection
		// variables:

		if (mNumberOfWavelengths > 1)
		{
			for (int p = 0; p < mNumberOfPlanes; p++)
			{
				StateVariable lCurrentD1 = mStateVariableMatrix.get(0, p, 0);
				StateVariable lCurrentD2 = mStateVariableMatrix.get(0, p, 1);

				for (int w = 1; w < mNumberOfWavelengths; w++)
				{
					final String swp = "w" + (w - 1) + "w" + w + "p" + p;

					final boolean lOneOfTheTwoHasNoSyncPlanes = !hasSyncPlane(mSyncPlaneIndices,
																																		w) || !hasSyncPlane(mSyncPlaneIndices,
																																												w - 1);

					final StateVariable lD1 = mStateVariableMatrix.get(w, p, 0);
					final StateVariable lD2 = mStateVariableMatrix.get(w, p, 1);
					final ConstrainSumEquality lSumEqualityConstrainFirst = addSumEqualityConstrain("InterColorCenterOfMasssEquality" + swp,
																																													lCurrentD1,
																																													lD1,
																																													lCurrentD2,
																																													lD2);
					lSumEqualityConstrainFirst.setWeight(mConstantConstrainWeight);
					lSumEqualityConstrainFirst.setRelative(false);
					if (cInterColorDetectionEquality || lOneOfTheTwoHasNoSyncPlanes)
					{
						addEqualityConstrain(	"InterColorEqualityD1" + swp,
																	lCurrentD1,
																	lD1);
						addEqualityConstrain(	"InterColorEqualityD2" + swp,
																	lCurrentD2,
																	lD2);
					}
				}

			}
		}
	}

	private void addMissingInfoConstrains()
	{
		// Missing Info Constrain
		for (int w = 0; w < mNumberOfWavelengths; w++)
		{
			final String sw = "w" + w;
			for (int p = 0; p < mNumberOfPlanes; p++)
			{
				final String swp = sw + "p" + p;
				for (int o = 2; o < 4; o++)
				{
					final String swpv = swp + "v" + o;
					if (mUnconstrainedVariableMatrix.get(w, p, o))
					{
						final ConstrainAverage lMissingInfoAverageConstrain = addAverageConstrain("AverageConstrain" + swpv,
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
						lMissingInfoAverageConstrain.setWeight(mConstantConstrainWeight);
					}

				}

				if (mExtraDOF)
					for (int o = 4; o < 10; o++)
					{
						final String swvp = swp + "v" + o;
						if (mUnconstrainedVariableMatrix.get(w, p, o))
						{

							final ConstrainAverage lMissingInfoConstrainAverage = addAverageConstrain("AverageConstrain" + swvp,
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

							lMissingInfoConstrainAverage.setRelative(false);
							lMissingInfoConstrainAverage.setWeight(mConstantConstrainWeight);

						}
					}
			}
		}

		/*
		for (int p = 0; p < mNumberOfPlanes; p++)
		{
			final String sp = "p" + p;
			for (int o = 2; o < (mExtraDOF ? 10 : 4); o++)
			{
				final String spv = sp + "v" + o;

				boolean lUnconstrainedForOneColor = false;
				for (int w = 0; w < mNumberOfWavelengths; w++)
					lUnconstrainedForOneColor |= mUnconstrainedVariableMatrix.get(w,
																																				p,
																																				o);

				if (lUnconstrainedForOneColor)
					for (int w = 0; w < mNumberOfWavelengths; w++)
						if (mUnconstrainedVariableMatrix.get(w, p, o))
						{
							final String swpv = "w" + w + spv;

							for (int u = 0; u < mNumberOfWavelengths; u++)
								if (!mUnconstrainedVariableMatrix.get(u, p, o))
								{
									final String swpvu = swpv + "u" + u;
									final ConstrainAverage lMissingInfoAverageConstrain = addAverageConstrain("AverageConstrain" + swpvu,
																																														mStateVariableMatrix.clipAndGet(w,
																																																														p,
																																																														o),
																																														mStateVariableMatrix.clipAndGet(u,
																																																														p,
																																																														o));

									lMissingInfoAverageConstrain.setRelative(false);
									lMissingInfoAverageConstrain.setWeight(mConstantConstrainWeight);
									break;
								}
						}

			}
		}/**/

	}

	private int addBaseConstrains()
	{
		int lObservableVariableCounter = 0;
		for (int w = 0; w < mNumberOfWavelengths; w++)
		{
			final ArrayList<StateVariable> lD1List = new ArrayList<StateVariable>();
			final ArrayList<StateVariable> lD2List = new ArrayList<StateVariable>();

			final String sw = "w" + w;
			for (int p = 0; p < mNumberOfPlanes; p++)
			{
				final String sp = sw + "p" + p;
				final StateVariable lD1 = addStateVariable("D1" + sp, 1);
				lD1List.add(lD1);

				mStateVariableMatrix.set(lD1, w, p, 0);

				final StateVariable lD2 = addStateVariable("D2" + sp, -1);
				lD2List.add(lD2);

				mStateVariableMatrix.set(lD2, w, p, 1);

				final StateVariable lI1 = addStateVariable("I1" + sp, 1);
				mStateVariableMatrix.set(lI1, w, p, 2);

				final StateVariable lI2 = addStateVariable("I2" + sp, -1);
				mStateVariableMatrix.set(lI2, w, p, 3);

				StateVariable lX1 = null, lX2 = null, lA1 = null, lA2 = null, lB1 = null, lB2 = null;
				if (mExtraDOF)
				{
					lX1 = addStateVariable("X1" + sp, 1);
					lX2 = addStateVariable("X2" + sp, 1);
					lA1 = addStateVariable("A1" + sp, 1);
					lA2 = addStateVariable("A2" + sp, 1);
					lB1 = addStateVariable("B1" + sp, 1);
					lB2 = addStateVariable("B2" + sp, 1);

					mStateVariableMatrix.set(lX1, w, p, 4);
					mStateVariableMatrix.set(lX2, w, p, 5);
					mStateVariableMatrix.set(lA1, w, p, 6);
					mStateVariableMatrix.set(lA2, w, p, 7);
					mStateVariableMatrix.set(lB1, w, p, 8);
					mStateVariableMatrix.set(lB2, w, p, 9);
				}

				final ObservableVariable lF11 = addObservableVariable("F11" + sp);
				final ObservableVariable lF12 = addObservableVariable("F12" + sp);
				final ObservableVariable lF21 = addObservableVariable("F21" + sp);
				final ObservableVariable lF22 = addObservableVariable("F22" + sp);

				ObservableVariable lOX1 = null, lOX2 = null, lOA1 = null, lOA2 = null, lOB1 = null, lOB2 = null;
				if (mExtraDOF)
				{
					lOX1 = addObservableVariable("OX1" + sp);
					lOX2 = addObservableVariable("OX2" + sp);
					lOA1 = addObservableVariable("OA1" + sp);
					lOA2 = addObservableVariable("OA2" + sp);
					lOB1 = addObservableVariable("OB1" + sp);
					lOB2 = addObservableVariable("OB2" + sp);
				}

				lObservableVariableCounter = handleMissingObservations(	lObservableVariableCounter,
																																w,
																																p,
																																lF11,
																																lF12,
																																lF21,
																																lF22);

				Constrain lDCF11 = null, lDCF12 = null, lDCF21 = null, lDCF22 = null;

				lDCF11 = addDifferenceConstrain("DCF11" + sp, lF11, lD1, lI1);
				lDCF12 = addDifferenceConstrain("DCF12" + sp, lF12, lD1, lI2);
				lDCF21 = addDifferenceConstrain("DCF21" + sp, lF21, lD2, lI1);
				lDCF22 = addDifferenceConstrain("DCF22" + sp, lF22, lD2, lI2);

				if (hasSyncPlane(mSyncPlaneIndices, w))
				{
					if (isBeforeSyncPlanes(mSyncPlaneIndices, w, p))
					{
						lDCF21.setWeight(0);
						lDCF22.setWeight(0);
					}
					if (isAfterSyncPlanes(mSyncPlaneIndices, w, p))
					{
						lDCF11.setWeight(0);
						lDCF12.setWeight(0);
					}
				}
				else
				{
					if (isBeforeSyncPlanes(mSyncPlaneIndices, p))
					{
						lDCF21.setWeight(0);
						lDCF22.setWeight(0);
					}
					if (isAfterSyncPlanes(mSyncPlaneIndices, p))
					{
						lDCF11.setWeight(0);
						lDCF12.setWeight(0);
					}
				}

				if (mExtraDOF)
				{
					lObservableVariableCounter = handleMissingObservationsExtraDOF(	lObservableVariableCounter,
																																					w,
																																					p,
																																					lOX1,
																																					lOX2,
																																					lOA1,
																																					lOA2,
																																					lOB1,
																																					lOB2);

					addIdentityConstrain("ECX1" + sp, lOX1, lX1);
					addIdentityConstrain("ECX2" + sp, lOX2, lX2);
					addIdentityConstrain("ECA1" + sp, lOA1, lA1);
					addIdentityConstrain("ECA2" + sp, lOA2, lA2);
					addIdentityConstrain("ECB1" + sp, lOB1, lB1);
					addIdentityConstrain("ECB2" + sp, lOB2, lB2);

				}

			}

			if (mNumberOfPlanes > 1)
			{

				for (int p = 0; p < mNumberOfPlanes - 1; p++)
				{
					final ConstrainEquality lEqualityConstrain = addEqualityConstrain("D1Equality" + sw,
																																						lD1List.get(p),
																																						lD1List.get(p + 1));
					lEqualityConstrain.setWeight(mConstantConstrainWeight);
					lEqualityConstrain.setRelative(false);

				}

				for (int p = 0; p < mNumberOfPlanes - 1; p++)
				{
					final ConstrainEquality lEqualityConstrain = addEqualityConstrain("D2Equality" + sw,
																																						lD2List.get(p),
																																						lD2List.get(p + 1));
					lEqualityConstrain.setWeight(mConstantConstrainWeight);
					lEqualityConstrain.setRelative(false);
				}

			}

		}
		return lObservableVariableCounter;
	}

	private int handleMissingObservations(int lObservableVariableCounter,
																				final int w,
																				final int p,
																				final ObservableVariable lF11,
																				final ObservableVariable lF12,
																				final ObservableVariable lF21,
																				final ObservableVariable lF22)
	{
		for (int o = 0; o < 4; o++)
		{
			mUnconstrainedVariableMatrix.set(false, w, p, o);
		}

		if (mMissingObservations != null)
		{
			final boolean lMissingObservationF11 = mMissingObservations[lObservableVariableCounter++];
			lF11.setMissing(lMissingObservationF11);

			final boolean lMissingObservationF12 = mMissingObservations[lObservableVariableCounter++];
			lF12.setMissing(lMissingObservationF12);

			final boolean lMissingObservationF21 = mMissingObservations[lObservableVariableCounter++];
			lF21.setMissing(lMissingObservationF21);

			final boolean lMissingObservationF22 = mMissingObservations[lObservableVariableCounter++];
			lF22.setMissing(lMissingObservationF22);

			if (isSyncPlane(mSyncPlaneIndices, w, p))
			{
				mUnconstrainedVariableMatrix.set(	lMissingObservationF11 && lMissingObservationF21,
																					w,
																					p,
																					2);
				mUnconstrainedVariableMatrix.set(	lMissingObservationF12 && lMissingObservationF22,
																					w,
																					p,
																					3);
			}
			else if (hasSyncPlane(mSyncPlaneIndices, w))
			{
				if (isBeforeSyncPlanes(mSyncPlaneIndices, p))
				{
					mUnconstrainedVariableMatrix.set(	lMissingObservationF11,
																						w,
																						p,
																						2);
					mUnconstrainedVariableMatrix.set(	lMissingObservationF12,
																						w,
																						p,
																						3);
				}
				if (isAfterSyncPlanes(mSyncPlaneIndices, p))
				{
					mUnconstrainedVariableMatrix.set(	lMissingObservationF21,
																						w,
																						p,
																						2);
					mUnconstrainedVariableMatrix.set(	lMissingObservationF22,
																						w,
																						p,
																						3);
				}
			}
			else if (isBeforeSyncPlanes(mSyncPlaneIndices, w, p))
			{
				mUnconstrainedVariableMatrix.set(	lMissingObservationF11,
																					w,
																					p,
																					2);
				mUnconstrainedVariableMatrix.set(	lMissingObservationF12,
																					w,
																					p,
																					3);
			}
			else
			{
				mUnconstrainedVariableMatrix.set(	lMissingObservationF21,
																					w,
																					p,
																					2);
				mUnconstrainedVariableMatrix.set(	lMissingObservationF22,
																					w,
																					p,
																					3);
			}

		}
		return lObservableVariableCounter;
	}

	private int handleMissingObservationsExtraDOF(int lObservableVariableCounter,
																								final int w,
																								final int p,
																								final ObservableVariable pOX1,
																								final ObservableVariable pOX2,
																								final ObservableVariable pOA1,
																								final ObservableVariable pOA2,
																								final ObservableVariable pOB1,
																								final ObservableVariable pOB2)
	{
		for (int o = 4; o < 10; o++)
		{
			mUnconstrainedVariableMatrix.set(false, w, p, o);
		}

		if (mMissingObservations != null)
		{
			final boolean lMissingObservationX1 = mMissingObservations[lObservableVariableCounter++];
			mUnconstrainedVariableMatrix.set(lMissingObservationX1, w, p, 4);
			pOX1.setMissing(lMissingObservationX1);

			final boolean lMissingObservationX2 = mMissingObservations[lObservableVariableCounter++];
			mUnconstrainedVariableMatrix.set(lMissingObservationX2, w, p, 5);
			pOX2.setMissing(lMissingObservationX2);

			final boolean lMissingObservationA1 = mMissingObservations[lObservableVariableCounter++];
			mUnconstrainedVariableMatrix.set(lMissingObservationA1, w, p, 6);
			pOA1.setMissing(lMissingObservationA1);

			final boolean lMissingObservationA2 = mMissingObservations[lObservableVariableCounter++];
			mUnconstrainedVariableMatrix.set(lMissingObservationA2, w, p, 7);
			pOA2.setMissing(lMissingObservationA2);

			final boolean lMissingObservationB1 = mMissingObservations[lObservableVariableCounter++];
			mUnconstrainedVariableMatrix.set(lMissingObservationB1, w, p, 8);
			pOB1.setMissing(lMissingObservationB1);

			final boolean lMissingObservationB2 = mMissingObservations[lObservableVariableCounter++];
			mUnconstrainedVariableMatrix.set(lMissingObservationB2, w, p, 9);
			pOB2.setMissing(lMissingObservationB2);
		}
		return lObservableVariableCounter;
	}

	private boolean isAfterSyncPlanes(final boolean[] pSyncPlaneIndices,
																		final int pP)
	{
		if (!hasSyncPlane(pSyncPlaneIndices))
		{
			return false;
		}

		for (int w = 0; w < mNumberOfWavelengths; w++)
		{
			for (int p = pP; p < mNumberOfPlanes; p++)
			{
				if (isSyncPlane(pSyncPlaneIndices, w, p))
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean isBeforeSyncPlanes(	final boolean[] pSyncPlaneIndices,
																			final int pP)
	{
		if (!hasSyncPlane(pSyncPlaneIndices))
		{
			return false;
		}

		for (int w = 0; w < mNumberOfWavelengths; w++)
		{
			for (int p = pP; p >= 0; p--)
			{
				if (isSyncPlane(pSyncPlaneIndices, w, p))
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean isAfterSyncPlanes(final boolean[] pSyncPlaneIndices,
																		final int pW,
																		final int pP)
	{
		if (!hasSyncPlane(pSyncPlaneIndices, pW))
		{
			return false;
		}

		for (int p = pP; p < mNumberOfPlanes; p++)
		{
			if (isSyncPlane(pSyncPlaneIndices, pW, p))
			{
				return false;
			}
		}
		return true;
	}

	private boolean isBeforeSyncPlanes(	final boolean[] pSyncPlaneIndices,
																			final int pW,
																			final int pP)
	{
		if (!hasSyncPlane(pSyncPlaneIndices, pW))
		{
			return false;
		}

		for (int p = pP; p >= 0; p--)
		{
			if (isSyncPlane(pSyncPlaneIndices, pW, p))
			{
				return false;
			}
		}
		return true;
	}

	private boolean hasSyncPlane(final boolean[] pSyncPlaneIndices)
	{
		for (int w = 0; w < mNumberOfWavelengths; w++)
		{
			for (int p = 0; p < mNumberOfPlanes; p++)
			{
				if (isSyncPlane(pSyncPlaneIndices, w, p))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasSyncPlane(	final boolean[] pSyncPlaneIndices,
																final int pW)
	{
		for (int p = 0; p < mNumberOfPlanes; p++)
		{
			if (isSyncPlane(pSyncPlaneIndices, pW, p))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isSyncPlane(final boolean[] pSyncPlaneIndices,
															final int pW,
															final int pP)
	{
		return pSyncPlaneIndices[pW * mNumberOfPlanes + pP];
	}

	private int getObservableVariableIndex(	final int pNumberOfPlanes,
																					final int pWavelengthIndex,
																					final int pPlaneIndex)
	{
		return pWavelengthIndex * pNumberOfPlanes * 4 + 4 * pPlaneIndex;
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
