package autopilot.image.writers;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import autopilot.image.DoubleArrayImage;

public class PngWriter
{

	public static final void writePng(final DoubleArrayImage pDoubleArrayImage,
																		final File pFile) throws IOException
	{
		final Image lImage = getImage(pDoubleArrayImage);
		final BufferedImage lBufferedImage = GraphicsEnvironment.getLocalGraphicsEnvironment()
																														.getDefaultScreenDevice()
																														.getDefaultConfiguration()
																														.createCompatibleImage(	lImage.getWidth(null),
																																										lImage.getHeight(null));
		final Graphics lGraphics = lBufferedImage.getGraphics();

		lGraphics.drawImage(lImage,
												0,
												0,
												pDoubleArrayImage.getWidth(),
												pDoubleArrayImage.getHeight(),
												null);
		lGraphics.dispose();

		ImageIO.write(lBufferedImage, "png", pFile);
	}

	public final static Image getImage(final DoubleArrayImage pDoubleArrayImage)
	{
		final int[] rgbintarray = new int[pDoubleArrayImage.getWidth() * pDoubleArrayImage.getHeight()];

		Image mMemoryImageSourceImage;
		MemoryImageSource mMemoryImageSource;

		mMemoryImageSource = new MemoryImageSource(	pDoubleArrayImage.getWidth(),
																								pDoubleArrayImage.getHeight(),
																								rgbintarray,
																								0,
																								pDoubleArrayImage.getWidth());
		mMemoryImageSource.setAnimated(true);
		mMemoryImageSource.setFullBufferUpdates(true);

		mMemoryImageSourceImage = Toolkit.getDefaultToolkit()
																			.createImage(mMemoryImageSource);

		toRGBMapped(pDoubleArrayImage.getArray(), rgbintarray);

		mMemoryImageSource.newPixels(	rgbintarray,
																	ColorModel.getRGBdefault(),
																	0,
																	pDoubleArrayImage.getWidth());

		return mMemoryImageSourceImage;
	}

	private static final void toRGBMapped(final double[] doublearray,
																				final int[] rgbarray)
	{
		final int length = doublearray.length;

		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < length; i++)
		{
			final double value = doublearray[i];
			min = Math.min(min, value);
			max = Math.max(max, value);
		}
		for (int i = 0; i < length; i++)
		{
			if (max - min <= 0)
				continue;
			final double value = (doublearray[i] - min) / (max - min);
			final int intvalue = (int) (value * 255) & 0xFF;
			rgbarray[i] = (255 << 24) + (intvalue << 16)
										+ (intvalue << 8)
										+ intvalue;
		}
	}

}
