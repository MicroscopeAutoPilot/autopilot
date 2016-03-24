package autopilot.measures.maps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.image.readers.TiffReader.TiffStackCallBack;
import autopilot.measures.FocusMeasures;
import autopilot.measures.FocusMeasures.FocusMeasure;
import autopilot.measures.implementations.differential.Tenengrad;
import autopilot.utils.ArrayMatrix;
import autopilot.utils.svg.SimpleSVGGenerator;

/**
 * Computes focus maps for stacks in a folder.
 * 
 * @author royer
 */
public class FocusMapBuilder
{
	private final File mFolder;
	private final File mDestinationFolder;
	private final int mNumberOfTiles;

	/**
	 * Constructs a focus map builder.
	 * 
	 * @param pFolder
	 *          folder in which tif stacks are located
	 * @param pNumberOfTiles
	 *          number of tiles
	 * @param pDestinationFolder
	 *          destination folder for the focus maps
	 */
	public FocusMapBuilder(	final File pFolder,
													final int pNumberOfTiles,
													final File pDestinationFolder)
	{
		super();
		mFolder = pFolder;
		mNumberOfTiles = pNumberOfTiles;
		mDestinationFolder = pDestinationFolder;
	}

	/**
	 * Launches the building of the maps.
	 * 
	 * @throws IOException
	 *           exception
	 */
	public void run() throws IOException
	{
		final File[] lListFiles = mFolder.listFiles();

		final ArrayList<File> lSelectedFiles = new ArrayList<File>();

		for (final File lFile : lListFiles)
		{
			final String lName = lFile.getName();
			final boolean isTiff = lName.endsWith(".tif");

			if (isTiff)
			{
				System.out.format("selected file: %s \n", lName);
				lSelectedFiles.add(lFile);
			}
		}

		for (final File lSelectedFile : lSelectedFiles)
		{
			System.out.format("Processing File: %s \n", lSelectedFile);
			generateMap(lSelectedFile, mDestinationFolder);
		}

	}

	/**
	 * Generates a map for a given Tiff stack file
	 * 
	 * @param pTiffFile
	 *          tiff stack file
	 * @param pDestinationFolder
	 *          destination folder
	 * @throws IOException
	 *           exception
	 */
	public void generateMap(final File pTiffFile,
													final File pDestinationFolder) throws IOException
	{
		final DoubleArrayImage lDoubleArrayImage = null;

		final File lMapFolder = new File(	pDestinationFolder,
																			pTiffFile.getName() + ".FOCUSMAP");
		lMapFolder.mkdirs();

		TiffReader.readTiffStack(pTiffFile, new TiffStackCallBack()
		{
			@Override
			public boolean image(	final int pImageIndex,
														final DoubleArrayImage pDoubleArrayImage)
			{
				if (pImageIndex % 10 != 0)
				{
					return true;
				}
				System.out.format("ImageIndex=%d \n", pImageIndex);

				final ArrayMatrix<DoubleArrayImage> lTiles = pDoubleArrayImage.extractTiles(mNumberOfTiles,
																																										mNumberOfTiles);

				final File lSVGFile = new File(	lMapFolder,
																				pTiffFile.getName() + "."
																						+ pImageIndex
																						+ ".svg");
				final int lWidth = pDoubleArrayImage.getWidth();
				final int lHeight = pDoubleArrayImage.getHeight();

				try
				{
					final SimpleSVGGenerator lSimpleSVGGenerator = new SimpleSVGGenerator(lSVGFile,
																																								lWidth,
																																								lHeight);

					lSimpleSVGGenerator.addPngImage("image" + pImageIndex,
																					pDoubleArrayImage,
																					0,
																					0,
																					pDoubleArrayImage.getWidth(),
																					pDoubleArrayImage.getHeight());

					final ArrayMatrix<Double> lFocusMeasurePerTile = new ArrayMatrix<Double>();
					final ArrayMatrix<Double> lAveragePerTile = new ArrayMatrix<Double>();

					for (int x = 0; x < mNumberOfTiles; x++)
					{
						final ArrayList<Double> lFocusMeasureColumn = new ArrayList<Double>();
						final ArrayList<Double> lAverageColumn = new ArrayList<Double>();

						for (int y = 0; y < mNumberOfTiles; y++)
						{
							final DoubleArrayImage lTile = lTiles.get(x, y);
							final double lFocusMeasure = FocusMeasures.computeFocusMeasure(	FocusMeasure.SpectralNormDCTEntropyShannon,
																																							lTile);
							lFocusMeasureColumn.add(lFocusMeasure);
							final double lAverage = Tenengrad.compute(lTile); // lTile.autocorr(2,
																																// 0); //
																																// VollathF5.compute(lTile);
							lAverageColumn.add(lAverage);
						}
						lFocusMeasurePerTile.add(lFocusMeasureColumn);
						lAveragePerTile.add(lAverageColumn);
					}

					final ArrayList<Double> lFocusMeasures = lFocusMeasurePerTile.flatten();
					final double lMaxFocusMeasure = Collections.max(lFocusMeasures);
					final double lMinFocusMeasure = Collections.min(lFocusMeasures);

					final ArrayList<Double> lAverages = lAveragePerTile.flatten();
					final double lMaxAverage = Collections.max(lAverages);
					final double lMinAverage = Collections.min(lAverages);

					for (int x = 0; x < mNumberOfTiles; x++)
					{
						for (int y = 0; y < mNumberOfTiles; y++)
						{
							final DoubleArrayImage lTile = lTiles.get(x, y);
							final int lTileWidth = lTile.getWidth();
							final int lTileHeight = lTile.getHeight();

							/*lSimpleSVGGenerator.addPngImage(String.format("imagetile(%d,%d)",
																													x,
																													y),
																						lTile,
																						x*lTileWidth,
																						y*lTileHeight,
																						lTileWidth,
																						lTileHeight);/**/

							final double lFocusMeasure = lFocusMeasurePerTile.get(x,
																																		y);
							final double lNormalizedFocusMeasure = (lFocusMeasure - lMinFocusMeasure) / (lMaxFocusMeasure - lMinFocusMeasure);

							final double lAverage = lAveragePerTile.get(x, y);
							final double lNormalizedAverage = (lAverage - lMinAverage) / (lMaxAverage - lMinAverage);

							// String.format("%s.%d.%d.png", lSVGFile.getName(), x, y);

							final int lBlue = (int) (255 * lNormalizedFocusMeasure);
							final int lRed = (int) (255 * lNormalizedAverage);

							lSimpleSVGGenerator.addRectangle(	x * lTileWidth,
																								y * lTileHeight,
																								lTileWidth,
																								lTileHeight,
																								"transparent",
																								SimpleSVGGenerator.getColorString(lRed,
																																									0,
																																									lBlue),
																								0,
																								0.3);

							lSimpleSVGGenerator.addText(String.format("an=%1.4f",
																												lNormalizedAverage),
																					x * lTileWidth,
																					y * lTileHeight + 30,
																					"orange",
																					8);
							lSimpleSVGGenerator.addText(String.format("fn=%1.4f",
																												lNormalizedFocusMeasure),
																					x * lTileWidth,
																					y * lTileHeight + 20,
																					"orange",
																					8);
							lSimpleSVGGenerator.addText(String.format("f=%1.6f",
																												lFocusMeasure),
																					x * lTileWidth,
																					y * lTileHeight + 10,
																					"orange",
																					8);
						}
					}

					lSimpleSVGGenerator.close();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}

				return true;
			}
		},
															lDoubleArrayImage);

	}
}
