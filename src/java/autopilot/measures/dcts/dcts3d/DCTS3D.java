package autopilot.measures.dcts.dcts3d;

import edu.emory.mathcs.jtransforms.dct.DoubleDCT_3D;

/**
 * Class providing methods for computing the DCTS3D volumetric focus measure
 * 
 * @author royer
 */
public class DCTS3D
{

	/**
	 * Computes the DCTS3D focus measure.
	 * 
	 * @param pArray
	 *          array containing the data
	 * @param pWidth
	 *          width of the volume
	 * @param pHeight
	 *          height of the volume
	 * @param pDepth
	 *          depth of the volume
	 * @param pPSFSupportDiameterXY
	 *          PSF support diameter in XY
	 * @param pPSFSupportDiameterZ
	 *          PSF support diameter in Z
	 * @return DCTS3D value
	 */
	public static final double dcts3d(final double[] pArray,
																		final int pWidth,
																		final int pHeight,
																		final int pDepth,
																		final double pPSFSupportDiameterXY,
																		final double pPSFSupportDiameterZ)
	{
		final DoubleDCT_3D dct3d = new DoubleDCT_3D(pHeight,
																								pWidth,
																								pDepth);
		dct3d.forward(pArray, false);
		normalizeNormL2(pArray);

		// System.out.println("DCTS3D: Received data of width: " + pWidth);
		// System.out.println("DCTS3D: Received data of height: " + pHeight);
		// System.out.println("DCTS3D:  Received data of depth: " + pDepth);

		// System.out.println("DCTS3D: received array of length: " + pArray.length);

		// System.out.println("DCTS3D: pPSFSupportDiameterXY: " +
		// pPSFSupportDiameterXY);
		// System.out.println("DCTS3D: pPSFSupportDiameterZ: " +
		// pPSFSupportDiameterZ);

		final int lOTFSupportX = (int) (pWidth / pPSFSupportDiameterXY);
		final int lOTFSupportY = (int) (pHeight / pPSFSupportDiameterXY);
		final int lOTFSupportZ = (int) (pDepth / pPSFSupportDiameterZ);

		// System.out.println("DCTS3D: lOTFSupportX: " + lOTFSupportX);
		// System.out.println("DCTS3D: lOTFSupportY: " + lOTFSupportY);
		// System.out.println("DCTS3D: lOTFSupportZ: " + lOTFSupportZ);

		final double ldcts3d = entropyShannonSubTriangle3D(	pArray,
																												pWidth,
																												pHeight,
																												pDepth,
																												lOTFSupportX,
																												lOTFSupportY,
																												lOTFSupportZ);

		return ldcts3d;

	}

	private final static double entropyShannonSubTriangle3D(final double[] pArray,
																													final int pWidth,
																													final int pHeight,
																													final int pDepth,
																													final int xh,
																													final int yh,
																													final int zh)
	{

		final double[] marray = pArray;
		double entropy = 0;

		loop1: for (int z = 0; z < zh; z++)
		{
			final int zi = z * pWidth * pDepth;
			final int yend = yh - z * yh / zh;
			for (int y = 0; y < yend; y++)
			{
				final int yi = y * pWidth;
				final int xend = xh - y * xh / yh;
				for (int x = 0; x < xend; x++)
				{
					final int i = zi + yi + x;
					double value = 0;
					value = marray[i];

					if (value > 0)
					{
						entropy += value * Math.log(value);
					}
					else if (value < 0)
					{
						entropy += -value * Math.log(-value);
					}
				}
			}
		}
		entropy = -entropy;

		entropy = 6 * entropy / (xh * yh * zh);

		return entropy;
	}

	public static final double normalizeNormL2(final double[] pArray)
	{
		final double norm = normL2(pArray);
		if (norm != 0)
		{
			final double invnorm = 1 / norm;

			final int length = pArray.length;
			final double[] marray = pArray;
			for (int i = 0; i < length; i++)
			{
				final double value = marray[i];
				marray[i] = value * invnorm;
			}
		}
		return norm;
	}

	public static final double normL2(final double[] pArray)
	{
		final int length = pArray.length;
		final double[] marray = pArray;
		double norm = 0;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			norm += value * value;
		}
		norm = Math.sqrt(norm);
		return norm;
	}

}
