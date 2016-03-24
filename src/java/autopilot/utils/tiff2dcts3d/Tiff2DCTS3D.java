package autopilot.utils.tiff2dcts3d;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.image.readers.TiffReader.TiffStackCallBack;
import autopilot.measures.dcts.dcts3d.DCTS3D;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;

public class Tiff2DCTS3D
{

	/**
	 * @param args
	 *          command line arguments
	 */
	public static void main(final String[] args)
	{
		try
		{

			final File lTiffFolderOrFile = new File(args[0]);
			if (lTiffFolderOrFile.isFile())
			{
				onFile(args, lTiffFolderOrFile);
			}
			else if (lTiffFolderOrFile.isDirectory())
			{
				onFolder(args, lTiffFolderOrFile);
			}

		}
		catch (final NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	private static void onFile(	final String[] pArgs,
															final File pTiffFolderOrFile) throws IOException
	{
		final File lResultFileFolder = new File(pArgs[1]);

		final double lPSFSupportDiameterXY = Double.parseDouble(pArgs[2]);

		final double[] lDcts2d = dcts2d(pTiffFolderOrFile,
																		lPSFSupportDiameterXY);

		final File lResultFile = new File(lResultFileFolder,
																			"./" + pTiffFolderOrFile.getName()
																					+ ".dcts2d.txt");
		lResultFile.delete();
		final Formatter lResultFileFormatter = new Formatter(lResultFile);

		int i = 0;
		for (final double lValue : lDcts2d)
		{
			lResultFileFormatter.format("%d\t%e\n", i++, lValue);
		}
		lResultFileFormatter.flush();
		lResultFileFormatter.close();

	}

	private static void onFolder(	final String[] args,
																final File lTiffFolderOrFile)	throws FileNotFoundException,
																															IOException
	{
		final File lResultFileFolder = new File(args[1]);
		final File lResultFile = new File(lResultFileFolder,
																			"./dcts3d.txt");
		lResultFile.delete();
		final Formatter lResultFileFormatter = new Formatter(lResultFile);

		final File[] lLlistOfFiles = lTiffFolderOrFile.listFiles();

		final double lPSFSupportDiameterXY = Double.parseDouble(args[2]);
		final double lPSFSupportDiameterZ = Double.parseDouble(args[3]);

		for (final File lFile : lLlistOfFiles)
		{
			final String lFileName = lFile.getName();
			System.out.print(lFileName);
			if (lFile.isFile() && lFileName.endsWith(".tif"))
			{
				System.out.println(" ..selected!");
				final String[] lSplittedFileName = lFileName.split("_|\\.");

				int lTimePoint = 0;
				int lChannel = 0;
				for (final String lString : lSplittedFileName)
				{
					if (lString.startsWith("TM"))
					{
						lTimePoint = Integer.parseInt(lString.substring(2));
					}
					else if (lString.startsWith("CHN"))
					{
						lChannel = Integer.parseInt(lString.substring(3));
					}
				}

				final double lDcts3d = dcts3d(lFile,
																			lPSFSupportDiameterXY,
																			lPSFSupportDiameterZ);

				lResultFileFormatter.format("%s\t%d\t%d\t%e\n",
																		lFileName,
																		lTimePoint,
																		lChannel,
																		lDcts3d);
				lResultFileFormatter.flush();

				System.out.format("%d\t%d\t%e\n",
													lTimePoint,
													lChannel,
													lDcts3d);

			}
			else
			{
				System.out.println("");
			}
		}

		lResultFileFormatter.close();
	}

	public static double[] dcts2d(final File pFile,
																final double pPSFSupportDiameterXY) throws IOException
	{

		final MultipleArrayStack lMultipleArrayStack = read3dStackIntoSeveralArrays(pFile);

		final double[] lDCTS2dArray = new double[lMultipleArrayStack.depth];
		int i = 0;
		for (final TDoubleArrayList lTDoubleArrayList : lMultipleArrayStack.arrays)
		{
			final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(lMultipleArrayStack.width,
																																			lMultipleArrayStack.height,
																																			lTDoubleArrayList.toArray());
			final double lDCTS2d = NormDCTEntropyShannon.compute(	lDoubleArrayImage,
																														pPSFSupportDiameterXY);
			lDCTS2dArray[i++] = lDCTS2d;
		}
		return lDCTS2dArray;
	}

	private static MultipleArrayStack read3dStackIntoSeveralArrays(final File pFile) throws IOException
	{
		final MultipleArrayStack lMultipleArrayStack = new MultipleArrayStack();

		final TiffStackCallBack lTiffStackCallBack = new TiffStackCallBack()
		{

			@Override
			public boolean image(	final int pImageIndex,
														final DoubleArrayImage pDoubleArrayImage)
			{

				lMultipleArrayStack.width = pDoubleArrayImage.getWidth();
				lMultipleArrayStack.height = pDoubleArrayImage.getHeight();
				final TDoubleArrayList lPixelValueList = new TDoubleArrayList(lMultipleArrayStack.width * lMultipleArrayStack.height);
				lMultipleArrayStack.arrays.add(lPixelValueList);

				final double[] lArray = pDoubleArrayImage.getArray();
				final int lLength = pDoubleArrayImage.getLength();
				for (int i = 0; i < lLength; i++)
				{
					lPixelValueList.add(lArray[i]);
				}

				lMultipleArrayStack.depth++;

				return true;
			}
		};

		TiffReader.readTiffStack(pFile, lTiffStackCallBack, null);
		return lMultipleArrayStack;
	}

	private static class MultipleArrayStack
	{
		public volatile ArrayList<TDoubleArrayList> arrays = new ArrayList<TDoubleArrayList>();
		public volatile int width, height, depth;
	}

	public static final double dcts3d(final File pFile,
																		final double pPSFSupportDiameterXY,
																		final double pPSFSupportDiameterZ) throws IOException
	{

		final SingleArrayStack lDoubleArrayImage3D = read3dStackIntoOneArray(pFile);

		final double lDCTS3d = DCTS3D.dcts3d(	lDoubleArrayImage3D.array.toArray(),
																					lDoubleArrayImage3D.width,
																					lDoubleArrayImage3D.height,
																					lDoubleArrayImage3D.depth,
																					pPSFSupportDiameterXY,
																					pPSFSupportDiameterZ);
		return lDCTS3d;
	}

	private static class SingleArrayStack
	{
		public volatile TDoubleArrayList array;
		public volatile int width, height, depth;
	}

	private static SingleArrayStack read3dStackIntoOneArray(final File pFile) throws IOException
	{
		final SingleArrayStack lDoubleArrayImage3D = new SingleArrayStack();

		final TiffStackCallBack lTiffStackCallBack = new TiffStackCallBack()
		{

			@Override
			public boolean image(	final int pImageIndex,
														final DoubleArrayImage pDoubleArrayImage)
			{
				if (lDoubleArrayImage3D.array == null)
				{
					lDoubleArrayImage3D.width = pDoubleArrayImage.getWidth();
					lDoubleArrayImage3D.height = pDoubleArrayImage.getHeight();
					lDoubleArrayImage3D.array = new TDoubleArrayList(100 * lDoubleArrayImage3D.width
																														* lDoubleArrayImage3D.height);
				}

				final double[] lArray = pDoubleArrayImage.getArray();
				final int lLength = pDoubleArrayImage.getLength();
				for (int i = 0; i < lLength; i++)
				{
					lDoubleArrayImage3D.array.add(lArray[i]);
				}

				lDoubleArrayImage3D.depth++;

				return true;
			}
		};

		TiffReader.readTiffStack(pFile, lTiffStackCallBack, null);
		return lDoubleArrayImage3D;
	}
}
