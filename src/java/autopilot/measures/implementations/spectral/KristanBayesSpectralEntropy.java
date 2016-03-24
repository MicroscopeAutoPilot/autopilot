package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.utils.ArrayMatrix;

/**
 * Kristan's Bayes Spectral entropy focus measure (original 8x8 tiled version)
 * 
 * @author royer
 */
public class KristanBayesSpectralEntropy implements
																				FocusMeasureInterface
{

	private static final int cBlockSize = 8;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage);
	}

	/**
	 * Computes Kristan's Bayes Spectral entropy focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @return focus measure
	 */
	public static final double compute(final DoubleArrayImage pDoubleArrayImage)
	{

		final int lWidth = pDoubleArrayImage.getWidth();
		final int lHeight = pDoubleArrayImage.getHeight();
		final int lNumberOfTilesAlongX = (int) Math.floor(lWidth / cBlockSize);
		final int lNumberOfTilesAlongY = (int) Math.floor(lHeight / cBlockSize);
		final ArrayMatrix<DoubleArrayImage> lTiles = pDoubleArrayImage.extractTiles(lNumberOfTilesAlongX,
																																								lNumberOfTilesAlongY);

		final DoubleArrayImage lTileFocusMeasureMatrix = new DoubleArrayImage(lNumberOfTilesAlongX,
																																					lNumberOfTilesAlongY);

		for (int x = 0; x < lNumberOfTilesAlongX; x++)
		{
			for (int y = 0; y < lNumberOfTilesAlongY; y++)
			{
				final DoubleArrayImage lTile = lTiles.get(x, y);
				final double lTileFocusMeasure = computeFocusMeasureForTileWithOriginalKristanAlgorithm(lTile);
				lTileFocusMeasureMatrix.setInt(x, y, lTileFocusMeasure);
			}

		}

		return -lTileFocusMeasureMatrix.average();
	}

	private static double computeFocusMeasureForTileWithOriginalKristanAlgorithm(final DoubleArrayImage pTile)
	{
		pTile.getArray();
		pTile.getLength();
		pTile.getWidth();
		pTile.getHeight();

		final double lEntropy = pTile.entropyBayesSubTriangle(0,
																													0,
																													6,
																													6,
																													false);
		return lEntropy;
	}

}
