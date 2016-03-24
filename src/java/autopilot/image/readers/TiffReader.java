package autopilot.image.readers;

import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import autopilot.image.DoubleArrayImage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class TiffReader
{
	public static DoubleArrayImage read(final File pTiffFile,
																			final double[] pArray) throws IOException
	{
		return read(new FileInputStream(pTiffFile), 0, pArray);
	}

	public static DoubleArrayImage read(final File pTiffFile,
																			final int pImageIndex,
																			final double[] pArray) throws IOException
	{
		return read(new FileInputStream(pTiffFile), pImageIndex, pArray);
	}

	public static DoubleArrayImage read(final byte[] data,
																			final int offset,
																			final int length,
																			final int pImageIndex,
																			final double[] pArray) throws IOException
	{
		final ByteArrayInputStream lByteArrayInputStream = new ByteArrayInputStream(data,
																																								offset,
																																								length);
		lByteArrayInputStream.reset();

		return read(lByteArrayInputStream, pImageIndex, pArray);
	}

	public static DoubleArrayImage read(final InputStream pResourceAsStream,
																			final double[] pArray) throws IOException
	{
		return read(pResourceAsStream, 0, pArray);
	}

	public static int nbpages(final File pFile) throws IOException
	{
		try (final ImageReader lImageReader = new ImageReader();)
		{

			final String lAbsoluteFilePath = pFile.getAbsolutePath();

			lImageReader.setId(lAbsoluteFilePath);

			final int lNumberOfPages = lImageReader.getImageCount();

			return lNumberOfPages;
		}
		catch (FormatException e)
		{
			throw new IOException(e);
		}
	}

	public static int nbpages(final InputStream pResourceAsStream) throws IOException
	{
		try (final SeekableStream lSeekableStream = SeekableStream.wrapInputStream(	pResourceAsStream,
																																								true))
		{

			final ImageDecoder lImageDecoder = ImageCodec.createImageDecoder(	"tiff",
																																				lSeekableStream,
																																				null);
			return lImageDecoder.getNumPages();
		}

	}

	public static DoubleArrayImage read(final InputStream pResourceAsStream,
																			final int pImageIndex,
																			final double[] pArray) throws IOException
	{
		final TIFFDecodeParam lTIFFDecodeParam = null;

		final SeekableStream lSeekableStream = SeekableStream.wrapInputStream(pResourceAsStream,
																																					true);

		final ImageDecoder lImageDecoder = ImageCodec.createImageDecoder(	"tiff",
																																			lSeekableStream,
																																			lTIFFDecodeParam);

		final Raster lRaster = lImageDecoder.decodeAsRaster(pImageIndex);
		final int lWidth = lRaster.getWidth();
		final int lHeight = lRaster.getHeight();

		double[] lArray = pArray;
		if (lArray == null)
		{
			lArray = new double[lWidth * lHeight];
		}

		lRaster.getPixels(0, 0, lWidth, lHeight, lArray);

		final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(lWidth,
																																		lHeight,
																																		lArray);

		lSeekableStream.close();

		return lDoubleArrayImage;
	}

	public interface TiffStackCallBack
	{
		boolean image(int pImageIndex, DoubleArrayImage pDoubleArrayImage);
	}

	public static boolean readTiffStack(final File pFile,
																			final TiffStackCallBack pCallBack,
																			final DoubleArrayImage pDoubleArrayImage) throws IOException
	{
		try
		{

			final ImageReader lImageReader = new ImageReader();
			final String lAbsoluteFilePath = pFile.getAbsolutePath();

			lImageReader.setId(lAbsoluteFilePath);

			final int lBitsPerPixel = lImageReader.getBitsPerPixel();
			final int lNumberOfPages = lImageReader.getImageCount();
			final int lWidth = lImageReader.getSizeX();
			final int lHeight = lImageReader.getSizeY();

			DoubleArrayImage lDoubleArrayImage = pDoubleArrayImage;
			if (lDoubleArrayImage == null || lDoubleArrayImage.getWidth() != lWidth
					|| lDoubleArrayImage.getHeight() != lHeight)
			{
				lDoubleArrayImage = new DoubleArrayImage(lWidth, lHeight);
			}

			final IFormatReader lReader = lImageReader.getReader();

			final int lByteBufferSize = lWidth * lHeight
																	* (lBitsPerPixel / 8);
			final byte[] lByteBuffer = new byte[lByteBufferSize];

			for (int lPageIndex = 0; lPageIndex < lNumberOfPages; lPageIndex++)
			{
				lReader.openBytes(lPageIndex, lByteBuffer);
				if (lBitsPerPixel == 16)
				{
					lDoubleArrayImage.load16bitByteBuffer(lByteBuffer);
				}
				else if (lBitsPerPixel == 8)
				{
					lDoubleArrayImage.load8bitByteBuffer(lByteBuffer);
				}

				if (!pCallBack.image(lPageIndex, lDoubleArrayImage))
				{
					break;
				}
			}

			lImageReader.close();

		}
		catch (final FormatException e)
		{
			e.printStackTrace();
			return false;
		}

		return true;

	}
	/*
	public static VirtualStackZ readTiffVirtualStack(	final File pFile,
																										final DoubleArrayImage pDoubleArrayImage) throws IOException
	{
		try
		{

			final ImageReader lImageReader = new ImageReader();
			final String lAbsoluteFilePath = pFile.getAbsolutePath();

			lImageReader.setId(lAbsoluteFilePath);

			final int lBitsPerPixel = lImageReader.getBitsPerPixel();
			lImageReader.getImageCount();
			final int lWidth = lImageReader.getSizeX();
			final int lHeight = lImageReader.getSizeY();

			final boolean isNotAppropriate = pDoubleArrayImage == null || pDoubleArrayImage.getWidth() != lWidth
																				|| pDoubleArrayImage.getHeight() != lHeight;
			final DoubleArrayImage lDoubleArrayImage = isNotAppropriate	? new DoubleArrayImage(	lWidth,
																																													lHeight)
																																	: pDoubleArrayImage;

			final IFormatReader lReader = lImageReader.getReader();

			final int lByteBufferSize = lWidth * lHeight
																	* (lBitsPerPixel / 8);
			final byte[] lByteBuffer = new byte[lByteBufferSize];

			final VirtualStackZ lVirtualStack = new VirtualStackZ()
			{

				@Override
				public int getWidth()
				{
					return lReader.getSizeX();
				}

				@Override
				public int getHeight()
				{
					return lReader.getSizeY();
				}

				@Override
				public int getDepth()
				{
					return lReader.getSizeZ();
				}

				@Override
				public DoubleArrayImage getZSlice(final int pIndex)
				{
					try
					{
						lReader.openBytes(pIndex, lByteBuffer);
						lDoubleArrayImage.load16bitByteBuffer(lByteBuffer);
						return lDoubleArrayImage;
					}
					catch (final FormatException e)
					{
						e.printStackTrace();
						return null;
					}
					catch (final IOException e)
					{
						e.printStackTrace();
						return null;
					}
				}

				@Override
				public void close() throws WebServiceException
				{
					try
					{
						lReader.close();
						lImageReader.close();
					}
					catch (final IOException e)
					{
						e.printStackTrace();
					}
				}

			};

			return lVirtualStack;

		}
		catch (final FormatException e)
		{
			e.printStackTrace();
		}

		return null;

	}

	public static void readTiffStack(	final InputStream pResourceAsStream,
																		final TiffStackCallBack pCallBack,
																		final DoubleArrayImage pDoubleArrayImage) throws IOException
	{

		DoubleArrayImage lDoubleArrayImage = pDoubleArrayImage;
		final TIFFDecodeParam lTIFFDecodeParam = null;

		final SeekableStream lSeekableStream = SeekableStream.wrapInputStream(pResourceAsStream,
																																					true);

		final ImageDecoder lImageDecoder = ImageCodec.createImageDecoder(	"tiff",
																																			lSeekableStream,
																																			lTIFFDecodeParam);
		final int lNumberOfPages = lImageDecoder.getNumPages();

		for (int lPageIndex = 0; lPageIndex < lNumberOfPages; lPageIndex++)
		{

			final Raster lRaster = lImageDecoder.decodeAsRaster(lPageIndex);
			final int lWidth = lRaster.getWidth();
			final int lHeight = lRaster.getHeight();

			if (lDoubleArrayImage == null || lDoubleArrayImage.getWidth() != lWidth
					|| lDoubleArrayImage.getHeight() != lHeight)
			{
				lDoubleArrayImage = new DoubleArrayImage(lWidth, lHeight);
			}

			final double[] lArray = lDoubleArrayImage.getArray();
			lRaster.getPixels(0, 0, lWidth, lHeight, lArray);
			if (!pCallBack.image(lPageIndex, lDoubleArrayImage))
			{
				break;
			}

		}

		lSeekableStream.close();

	}/**/
}
