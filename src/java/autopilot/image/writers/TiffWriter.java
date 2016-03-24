package autopilot.image.writers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import autopilot.image.DoubleArrayImage;

public class TiffWriter
{
	
	public static void write(	final List<DoubleArrayImage> pDoubleArrayImageList,
														final File pTiffFile)	throws DependencyException,
																									ServiceException,
																									FormatException,
																									IOException
	{
		final int pixelType = FormatTools.FLOAT;
		final int c = 1;

		// create metadata object with minimum required metadata
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
																		pDoubleArrayImageList.get(0).getWidth(),
																		pDoubleArrayImageList.get(0).getHeight(),
																		pDoubleArrayImageList.size(),
																		c,
																		1,
																		c);

		final String id = pTiffFile.getAbsolutePath();
		// write image plane to disk
		final IFormatWriter writer = new ImageWriter();
		writer.setMetadataRetrieve(meta);
		writer.setId(id);
		
		int i=0;
		for(DoubleArrayImage lImage : pDoubleArrayImageList)
		{
			final byte[] img = double2float2Byte(lImage.getArray());
			writer.saveBytes(i++, img);
		}
		
		writer.close();
	}
	
	

	public static void write(	final DoubleArrayImage pDoubleArrayImage,
														final File pTiffFile)	throws DependencyException,
																									ServiceException,
																									FormatException,
																									IOException
	{
		write(pDoubleArrayImage.getArray(),
					pDoubleArrayImage.getWidth(),
					pDoubleArrayImage.getHeight(),
					pTiffFile);
	}

	public static void write(	final double[] data,
														final int pWidth,
														final int pHeight,
														final File pTiffFile)	throws DependencyException,
																									ServiceException,
																									FormatException,
																									IOException
	{
		final int pixelType = FormatTools.FLOAT;
		final int c = 1;
		final byte[] img = double2float2Byte(data);

		// create metadata object with minimum required metadata
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
																		pWidth,
																		pHeight,
																		1,
																		c,
																		1,
																		c);

		final String id = pTiffFile.getAbsolutePath();
		// write image plane to disk
		final IFormatWriter writer = new ImageWriter();
		writer.setMetadataRetrieve(meta);
		writer.setId(id);
		writer.saveBytes(0, img);
		writer.close();
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

	public static void write16bit(final DoubleArrayImage pDoubleArrayImage,
																final File pTiffFile)	throws DependencyException,
																											ServiceException,
																											FormatException,
																											IOException
	{
		write16bit(	pDoubleArrayImage.getArray(),
								pDoubleArrayImage.getWidth(),
								pDoubleArrayImage.getHeight(),
								pTiffFile);
	}

	public static void write16bit(final double[] data,
																final int pWidth,
																final int pHeight,
																final File pTiffFile)	throws DependencyException,
																											ServiceException,
																											FormatException,
																											IOException
	{
		final int pixelType = FormatTools.UINT16;
		final int c = 1;
		final byte[] img = double2short2Byte(data);

		// create metadata object with minimum required metadata
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
																		pWidth,
																		pHeight,
																		1,
																		c,
																		1,
																		c);

		final String id = pTiffFile.getAbsolutePath();
		// write image plane to disk
		final IFormatWriter writer = new ImageWriter();
		writer.setMetadataRetrieve(meta);
		writer.setId(id);
		writer.saveBytes(0, img);
		writer.close();
	}

	// float2Byte method - writes floats to byte array
	private static final byte[] double2short2Byte(final double[] inData)
	{
		int j = 0;
		final int length = inData.length;
		final byte[] outData = new byte[length * 2];
		for (int i = 0; i < length; i++)
		{
			final int data = (int) inData[i];
			outData[j++] = (byte) (data >>> 8);
			outData[j++] = (byte) (data >>> 0);
		}
		return outData;
	}

}
