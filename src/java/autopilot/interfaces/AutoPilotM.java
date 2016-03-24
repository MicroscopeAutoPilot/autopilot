package autopilot.interfaces;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.dcts.dcts3d.DCTS3D;
import autopilot.measures.implementations.differential.Tenengrad;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;

public class AutoPilotM
{

	public AutoPilotM()
	{
		super();
	}

	public static double dcts2(	final double[][] p2DImage,
															final double pPSFSupportDiameter)
	{
		final DoubleArrayImage lDoubleArrayImage = Utils.getDoubleArrayImage(p2DImage);

		final double dcts = NormDCTEntropyShannon.compute(lDoubleArrayImage,
																											pPSFSupportDiameter);
		return dcts;
	}

	public static double dcts2(	final Double[][] p2DImage,
															final double pPSFSupportDiameter)
	{
		final DoubleArrayImage lDoubleArrayImage = Utils.getDoubleArrayImage(p2DImage);

		final double dcts = NormDCTEntropyShannon.compute(lDoubleArrayImage,
																											pPSFSupportDiameter);
		return dcts;
	}

	public static double dcts3(	final double[][][] p2DImage,
															final double pPSFSupportDiameterXY,
															final double pPSFSupportDiameterZ)
	{
		final double[] lSingleArrayImage = Utils.getDoubleArrayImage(p2DImage);

		final int lWidth = p2DImage[0][0].length;
		final int lHeight = p2DImage[0].length;
		final int lDepth = p2DImage.length;

		final double dcts = DCTS3D.dcts3d(lSingleArrayImage,
																			lWidth,
																			lHeight,
																			lDepth,
																			pPSFSupportDiameterXY,
																			pPSFSupportDiameterZ);

		return dcts;
	}

	public static double dcts3(	final Double[][][] p2DImage,
															final double pPSFSupportDiameterXY,
															final double pPSFSupportDiameterZ)
	{
		final double[] lSingleArrayImage = Utils.getDoubleArrayImage(p2DImage);

		final int lWidth = p2DImage[0][0].length;
		final int lHeight = p2DImage[0].length;
		final int lDepth = p2DImage.length;

		final double dcts = DCTS3D.dcts3d(lSingleArrayImage,
																			lWidth,
																			lHeight,
																			lDepth,
																			pPSFSupportDiameterXY,
																			pPSFSupportDiameterZ);

		return dcts;
	}

	public static double tenengrad2(final double[][] p2DImage,
																	final double pPSFSupportDiameter)
	{
		final DoubleArrayImage lDoubleArrayImage = Utils.getDoubleArrayImage(p2DImage);

		final double tenengrad = Tenengrad.compute(	lDoubleArrayImage,
																								pPSFSupportDiameter);

		return tenengrad;
	}

	public static double tenengrad2(final Double[][] p2DImage,
																	final double pPSFSupportDiameter)
	{
		final DoubleArrayImage lDoubleArrayImage = Utils.getDoubleArrayImage(p2DImage);

		final double tenengrad = Tenengrad.compute(	lDoubleArrayImage,
																								pPSFSupportDiameter);

		return tenengrad;
	}

	private static final double cEqualityConstrainWeight = 1;

	public static final int l2solve(final boolean pAnchorDetection,
																	final boolean pSymmetricAnchor,
																	final int pNumberOfWavelengths,
																	final int pNumberOfPlanes,
																	final int pSyncPlaneIndex,
																	final Double[] pCurrentStateVector,
																	final Double[] pObservationsVector,
																	final Boolean[] pMissingObservations,
																	final Double[] pNewStateVector)
	{

		return AutoPilotC.l2solve(pAnchorDetection,
															pSymmetricAnchor,
															pNumberOfWavelengths,
															pNumberOfPlanes,
															pSyncPlaneIndex,
															Utils.convertDouble2double(pCurrentStateVector),
															Utils.convertDouble2double(pObservationsVector),
															Utils.convertBoolean2boolean(pMissingObservations),
															Utils.convertDouble2double(pNewStateVector));

	}

}
