package autopilot.utils.svg;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;

import autopilot.image.DoubleArrayImage;

/**
 * Basic SVG generator
 * 
 * @author royer
 */
public class SimpleSVGGenerator implements AutoCloseable
{
	static final String cSVGHeader = "<?xml version=\"1.0\" standalone=\"no\"?>\r\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \r\n  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"> \n";
	static final String cSVGBegin = "<svg width=\"%dpx\" height=\"%dpx\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\"> \n";
	static final String cSVGImage = "<image xlink:href=\"%s\" x=\"%dpx\" y=\"%dpx\" width=\"%dpx\" height=\"%dpx\" /> \n";
	static final String cSVGRectangle = "<rect x=\"%dpx\" y=\"%dpx\" rx=\"%dpx\" ry=\"%dpx\" width=\"%dpx\" height=\"%dpx\" stroke=\"%s\" fill=\"%s\" stroke-width=\"%d\" opacity=\"%g\"/> \n";
	static final String cSVGText = "<text x = \"%d\" y = \"%d\" fill = \"%s\" font-size = \"%d\">%s</text> \n";
	static final String cSVGColor = "fill=\"rgba(%d,%d,%d,%g)\"";
	static final String cSVGEnd = "</svg> \n";

	private final Formatter mFormatter;
	private final StringBuilder[] mStringBuilders = new StringBuilder[10];
	private volatile int mCurrentLayer = 0;
	private final File mSvgFile;

	/**
	 * Constructs a basic SVG file.
	 * 
	 * @param pSvgFile
	 *          svg file
	 * @param pWidth
	 *          image width
	 * @param pHeight
	 *          image height
	 * @throws FileNotFoundException
	 *           if file cannot be created.
	 */
	public SimpleSVGGenerator(final File pSvgFile,
														final int pWidth,
														final int pHeight) throws FileNotFoundException
	{
		super();
		mSvgFile = pSvgFile;
		mFormatter = new Formatter(pSvgFile);
		mFormatter.format(cSVGHeader);
		mFormatter.format(cSVGBegin, pWidth, pHeight);

		for (int i = 0; i < 10; i++)
			mStringBuilders[i] = new StringBuilder();
	}

	/**
	 * Adds a PNG image into the SVG file.
	 * 
	 * @param pFileName
	 *          PNG file namen
	 * @param pDoubleArrayImage
	 *          source image
	 * @param x
	 *          position along X-axis
	 * @param y
	 *          position along Y-axis
	 * @param width
	 *          image width
	 * @param height
	 *          image height
	 * @throws IOException
	 *           exception
	 */
	public void addPngImage(final String pFileName,
													final DoubleArrayImage pDoubleArrayImage,
													final int x,
													final int y,
													final int width,
													final int height) throws IOException
	{
		final File lParentFolder = mSvgFile.getParentFile();
		final File lImagesFolder = new File(lParentFolder,
																				mSvgFile.getName() + ".images");
		lImagesFolder.mkdirs();
		final File lImageFile = new File(	lImagesFolder,
																			pFileName + ".png");
		pDoubleArrayImage.writePng(lImageFile);
		mStringBuilders[mCurrentLayer].append(String.format(cSVGImage,
																												lImageFile.getPath(),
																												x,
																												y,
																												width,
																												height));
	}

	/**
	 * Adds a TIFF image to the SVG file.
	 * 
	 * @param pFileName
	 *          TIFF file name
	 * @param pDoubleArrayImage
	 *          source image
	 * @param x
	 *          position along X-axis
	 * @param y
	 *          position along Y-axis
	 * @param width
	 *          image width
	 * @param height
	 *          image height
	 * @throws IOException
	 *           exception
	 */
	public void addTiffImage(	final String pFileName,
														final DoubleArrayImage pDoubleArrayImage,
														final int x,
														final int y,
														final int width,
														final int height) throws IOException
	{
		final File lParentFolder = mSvgFile.getParentFile();
		final File lImagesFolder = new File(lParentFolder,
																				mSvgFile.getName() + ".images");
		lImagesFolder.mkdirs();
		final File lImageFile = new File(	lImagesFolder,
																			pFileName + ".tiff");
		pDoubleArrayImage.writeTiff(lImageFile);
		mStringBuilders[mCurrentLayer].append(String.format(cSVGImage,
																												lImageFile.getPath(),
																												x,
																												y,
																												width,
																												height));
	}

	/**
	 * Adds a rectangle to the SVG file.
	 * 
	 * @param x
	 *          position along X-axis
	 * @param y
	 *          position along Y-axis
	 * @param width
	 *          rectangle width
	 * @param height
	 *          rectangle height
	 * @param pStrokeColor
	 *          rectangle stroke color
	 * @param pFillColor
	 *          rectangle fill color
	 * @param pStrokeWidth
	 *          rectangle stroke width
	 * @param pOpacity
	 *          rectangle opacity
	 */
	public void addRectangle(	final int x,
														final int y,
														final int width,
														final int height,
														final String pStrokeColor,
														final String pFillColor,
														final int pStrokeWidth,
														final double pOpacity)
	{
		mStringBuilders[mCurrentLayer].append(String.format(cSVGRectangle,
																												x,
																												y,
																												0,
																												0,
																												width,
																												height,
																												pStrokeColor,
																												pFillColor,
																												pStrokeWidth,
																												pOpacity));

	}

	/**
	 * Adds text to the SVG file.
	 * 
	 * @param pTextString
	 *          text string
	 * @param x
	 *          position along X-axis
	 * @param y
	 *          position along Y-axis
	 * @param pTextColor
	 *          text color
	 * @param pFontSize
	 *          font size
	 */
	public void addText(final String pTextString,
											final int x,
											final int y,
											final String pTextColor,
											final int pFontSize)
	{
		mStringBuilders[mCurrentLayer].append(String.format(cSVGText,
																												x,
																												y,
																												pTextColor,
																												pFontSize,
																												pTextString));

	}

	/**
	 * Returns color string.
	 * 
	 * @param red
	 *          red component (0-255)
	 * @param green
	 *          green component (0-255)
	 * @param blue
	 *          blue component (0-255)
	 * @return color string
	 */
	public final static String getColorString(final int red,
																						final int green,
																						final int blue)
	{
		return String.format("#%02x%02x%02x", red, green, blue);
	}

	/**
	 * Closes the SVG file
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException
	{
		for (int i = 0; i < 10; i++)
			if (mStringBuilders[i].length() > 0)
				mFormatter.format(mStringBuilders[i].toString());

		mFormatter.format(cSVGEnd);
		mFormatter.close();
	}

	/**
	 * Opens the SVG file on the desktop (default program).
	 * 
	 * @throws IOException
	 *           exception
	 */
	public void openOnDesktop() throws IOException
	{
		mFormatter.flush();
		final Desktop lDesktop = Desktop.getDesktop();
		lDesktop.open(mSvgFile);
	}

	public int getCurrentLayer()
	{
		return mCurrentLayer;
	}

	public void setCurrentLayer(int pCurrentLayer)
	{
		mCurrentLayer = pCurrentLayer;
	}

}
