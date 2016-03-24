package autopilot.image.writers;

import java.io.File;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import autopilot.image.DoubleArrayImage;

public class TiffStackWriter
{

	private final File mTiffFile;
	private IFormatWriter mIFormatWriter = null;
	private int mWidth, mHeight;
	private final int mDepth;
	private int mImageIndex;
	private final int mDuration;
	private boolean mOMEMode = true;

	public TiffStackWriter(final File pTiffFile, final int pDepth)
	{
		this(pTiffFile, pDepth, 1);
	}

	public TiffStackWriter(	final File pTiffFile,
													final int pDepth,
													final int pDuration)
	{
		super();
		mTiffFile = pTiffFile;
		mDepth = pDepth;
		mDuration = pDuration;
		mImageIndex = 0;

	}

	public boolean isOMEMode()
	{
		return mOMEMode;
	}

	public void setOMEMode(final boolean oMEMode)
	{
		mOMEMode = oMEMode;
	}

	public void addImage(final DoubleArrayImage pDoubleArrayImage) throws java.lang.Exception
	{
		try
		{
			addImage(	pDoubleArrayImage.getArray(),
								pDoubleArrayImage.getWidth(),
								pDoubleArrayImage.getHeight(),
								mImageIndex++);
		}
		catch (final Throwable e)
		{
			throw new java.lang.Exception(e);
		}
	}

	private void addImage(final double[] data,
												final int pWidth,
												final int pHeight,
												final int pZIndex) throws FormatException,
																					IOException,
																					DependencyException,
																					ServiceException
	{
		ensureInitialized(pWidth, pHeight);
		final byte[] img = double2float2Byte(data);
		mIFormatWriter.saveBytes(pZIndex, img);
	}

	public void ensureInitialized(final int pWidth, final int pHeight) throws DependencyException,
																																		ServiceException,
																																		FormatException,
																																		IOException
	{
		if (mIFormatWriter == null)
		{
			mWidth = pWidth;
			mHeight = pHeight;
			final int pixelType = FormatTools.FLOAT;
			final int c = 1;

			// create metadata object with minimum required
			// metadata
			// fields
			final ServiceFactory factory = new ServiceFactory();
			final OMEXMLService service = factory.getInstance(OMEXMLService.class);
			final IMetadata meta = service.createOMEXMLMetadata();

			MetadataTools.populateMetadata(	meta,
																			0,
																			null,
																			false,
																			"XYZCT",
																			FormatTools.getPixelTypeString(pixelType),
																			mWidth,
																			mHeight,
																			mDepth,
																			c,
																			mDuration,
																			c);

			final String id = mTiffFile.getAbsolutePath();

			if (mOMEMode)
			{
				final OMETiffWriter lOMETiffWriter = new OMETiffWriter();
				lOMETiffWriter.setBigTiff(true);
				mIFormatWriter = lOMETiffWriter;
			}
			else
			{
				mIFormatWriter = new ImageWriter();
			}
			mIFormatWriter.setMetadataRetrieve(meta);
			mIFormatWriter.setId(id);
		}
	}

	public final void close() throws java.lang.Exception
	{
		try
		{
			if (mIFormatWriter != null)
			{
				mIFormatWriter.close();
			}
		}
		catch (final Throwable e)
		{
			throw new java.lang.Exception(e);
		}
	}

	// float2Byte method - writes floats to byte array
	private static final byte[] double2float2Byte(final double[] inData)
	{
		int j = 0;
		final int length = inData.length;
		final byte[] outData = new byte[length * 4];
		for (int i = 0; i < length; i++)
		{
			final int data = Float.floatToIntBits((float) inData[i]);
			outData[j++] = (byte) (data >>> 24);
			outData[j++] = (byte) (data >>> 16);
			outData[j++] = (byte) (data >>> 8);
			outData[j++] = (byte) (data >>> 0);
		}
		return outData;
	}

}
