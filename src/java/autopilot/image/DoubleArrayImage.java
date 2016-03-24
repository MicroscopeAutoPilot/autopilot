package autopilot.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import autopilot.image.writers.PngWriter;
import autopilot.image.writers.TiffWriter;
import autopilot.utils.ArrayMatrix;
import edu.emory.mathcs.jtransforms.dct.DoubleDCT_2D;
import edu.emory.mathcs.jtransforms.dht.DoubleDHT_2D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

/**
 * Basic 2D image backed by a single double[] java array.
 * 
 * @author royer
 */
public class DoubleArrayImage
{

	protected final int mWidth;
	protected final int mHeight;
	protected final double[] array;

	/**
	 * Constructs an empty image o zero height and width.
	 */
	protected DoubleArrayImage()
	{
		array = new double[0];
		mWidth = 0;
		mHeight = 0;
	}

	/**
	 * Constructs an image of given width and height.
	 * 
	 * @param pWidth
	 *            width
	 * @param pHeight
	 *            height
	 */
	public DoubleArrayImage(final int pWidth, final int pHeight)
	{
		super();
		array = new double[pWidth * pHeight];
		mWidth = pWidth;
		mHeight = pHeight;
	}

	/**
	 * Wraps an existing {@code double[]} array an image of given width and
	 * height.
	 * 
	 * @param pWidth
	 *            width
	 * @param pHeight
	 *            height
	 * @param pArray
	 *            existing array
	 */
	public DoubleArrayImage(final int pWidth,
							final int pHeight,
							final double[] pArray)
	{
		super();
		array = pArray;
		mWidth = pWidth;
		mHeight = pHeight;
	}

	/**
	 * Constructs an image as copy of another existing image.
	 * 
	 * @param pDoubleArrayImage
	 *            image from which a copy is made
	 */
	public DoubleArrayImage(final DoubleArrayImage pDoubleArrayImage)
	{
		array = Arrays.copyOf(	pDoubleArrayImage.array,
								pDoubleArrayImage.array.length);
		mWidth = pDoubleArrayImage.mWidth;
		mHeight = pDoubleArrayImage.mHeight;
	}

	/**
	 * Returns a copy of this image (deep copy)
	 * 
	 * @return deep copy
	 */
	public final DoubleArrayImage copy()
	{
		return new DoubleArrayImage(this);
	}

	/**
	 * Returns a copy of the provided image.
	 * 
	 * @param pDoubleArrayImage
	 *            image
	 * @return copy of image
	 */
	public static final DoubleArrayImage copy(final DoubleArrayImage pDoubleArrayImage)
	{
		final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(pDoubleArrayImage);
		return lDoubleArrayImage;
	}

	/**
	 * Attempst to copy the contents of the provided into tis image if the width
	 * and height are compatible. returns false if it failed.
	 * 
	 * @param pDoubleArrayImage
	 *            image
	 * @return true if success, false if failed.
	 */
	public boolean copyFrom(final DoubleArrayImage pDoubleArrayImage)
	{
		if (mWidth == pDoubleArrayImage.mWidth && mHeight == pDoubleArrayImage.mHeight)
		{
			System.arraycopy(	pDoubleArrayImage.array,
								0,
								array,
								0,
								mWidth * mHeight);
			return true;
		}
		else
		{
			return false;
		}

	}

	public DoubleArrayImage(final InputStream pInputStream) throws IOException
	{
		super();
		final BufferedImage lBufferedRead = ImageIO.read(pInputStream);

		mWidth = lBufferedRead.getWidth();
		mHeight = lBufferedRead.getHeight();
		array = new double[mWidth * mHeight];

		int i = 0;
		for (int y = 0; y < mHeight; y++)
		{
			for (int x = 0; x < mWidth; x++)
			{
				final int argb = lBufferedRead.getRGB(x, y);
				final int r = argb & 0xFF;
				final double luminance = (double) r / 255;
				array[i++] = luminance;
			}
		}
	}

	public final void load8bitByteBuffer(final byte[] pByteBuffer)
	{
		final int lByteBufferLength = pByteBuffer.length;
		final double[] marray = array;
		for (int i = 0, j = 0; i < lByteBufferLength;)
		{
			final int a = pByteBuffer[i++] & 0xff;
			final int intvalue = a;
			final double doublevalue = intvalue;
			marray[j++] = doublevalue;
		}
	}

	public final void load16bitByteBuffer(final byte[] pByteBuffer)
	{
		final int lByteBufferLength = pByteBuffer.length;
		final double[] marray = array;
		for (int i = 0, j = 0; i < lByteBufferLength;)
		{
			final int a = pByteBuffer[i++] & 0xff;
			final int b = pByteBuffer[i++] & 0xff;
			final int intvalue = a << 8 | b;
			final double doublevalue = intvalue;
			marray[j++] = doublevalue;
		}
	}

	public final void load16bitByteBuffer(final ByteBuffer pByteBuffer)
	{
		pByteBuffer.rewind();
		final double[] marray = array;
		final int lUsableBufferLength = Math.min(	marray.length * 2,
													pByteBuffer.limit());

		for (int i = 0, j = 0; i < lUsableBufferLength; i += 2)
		{
			final int a = pByteBuffer.get() & 0xff;
			final int b = pByteBuffer.get() & 0xff;
			final int intvalue = b << 8 | a;
			final double doublevalue = intvalue;
			marray[j++] = doublevalue;
		}
	}

	public final ByteBuffer getByteBufferOfDoubles()
	{
		final int length = array.length;
		final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(8 * length);
		lByteBuffer.order(ByteOrder.nativeOrder());

		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			lByteBuffer.putDouble(value);
		}

		return lByteBuffer;
	}

	public final ByteBuffer getByteBufferOfUnsignedShorts()
	{
		return getByteBufferOfUnsignedShorts(null);
	}

	public final ByteBuffer getByteBufferOfUnsignedShorts(ByteBuffer pByteBuffer)
	{
		final int length = array.length;
		if (pByteBuffer == null)
			pByteBuffer = ByteBuffer.allocateDirect(2 * length);
		pByteBuffer.order(ByteOrder.nativeOrder());
		pByteBuffer.rewind();

		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			final char charvalue = (char) value;
			pByteBuffer.putChar(charvalue);
		}

		return pByteBuffer;
	}

	public final double[] getArray()
	{
		return array;
	}

	public final int getWidth()
	{
		return mWidth;
	}

	public final int getHeight()
	{
		return mHeight;
	}

	public double getAspectRatio()
	{
		return (double) mWidth / mHeight;
	}

	public final int getLength()
	{
		return mWidth * mHeight;
	}

	public final int getSize()
	{
		return getLength();
	}

	private boolean isSameDimensions(final DoubleArrayImage pDoubleArrayImage)
	{
		return mWidth == pDoubleArrayImage.mWidth && mHeight == pDoubleArrayImage.mHeight;
	}

	public final double getInt(int pX, int pY)
	{
		pX = pX < 0 ? 0 : pX >= mWidth ? mWidth - 1 : pX;
		pY = pY < 0 ? 0 : pY >= mHeight ? mHeight - 1 : pY;

		return array[pX + mWidth * pY];
	}

	public final double getIntQuick(final int pX, final int pY)
	{
		return array[pX + mWidth * pY];
	}

	public final void setInt(int pX, int pY, final double pV)
	{
		pX = pX < 0 ? 0 : pX >= mWidth ? mWidth - 1 : pX;
		pY = pY < 0 ? 0 : pY >= mHeight ? mHeight - 1 : pY;

		array[pX + mWidth * pY] = pV;
	}

	public final void setIntQuick(	final int pX,
									final int pY,
									final double pV)
	{
		array[pX + mWidth * pY] = pV;
	}

	public final void addInt(	final int pX,
								final int pY,
								final double pV)
	{
		final int lX = pX < 0 ? 0 : pX >= mWidth ? mWidth - 1 : pX;
		final int lY = pY < 0 ? 0 : pY >= mHeight ? mHeight - 1 : pY;

		array[lX + mWidth * lY] += pV;
	}

	public final void addIntQuick(	final int pX,
									final int pY,
									final double pV)
	{
		array[pX + mWidth * pY] += pV;
	}

	public final void set(final double pValue)
	{
		Arrays.fill(array, pValue);
	}

	public final int replace(	final double pValue,
								final double pNewValue)
	{
		final int length = array.length;
		final double[] marray = array;
		int count = 0;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			if (value == pValue)
			{
				marray[i] = pNewValue;
				count++;
			}
		}
		return count;
	}

	public void setRectangle(	int x,
								int y,
								final int w,
								final int h,
								final int v)
	{
		final int xe = x + w;
		final int ye = y + h;
		final int width = mWidth;
		final double[] marray = array;

		for (; y < ye; y++)
		{
			final int yi = width * y;
			for (; x < xe; x++)
			{
				final int i = yi + x;
				marray[i] = v;
			}
		}
	}

	public final boolean hasNaN()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			if (value != value)
			{
				return true;
			}
		}
		return false;
	}

	public ByteBuffer getMonochromeByteBufferAuto()
	{
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;

		for (final double lValue : array)
		{
			min = min > lValue ? lValue : min;
			max = max < lValue ? lValue : max;
		}

		return getMonochromeByteBuffer(min, max);
	}

	public ByteBuffer getMonochromeByteBuffer(	final double pMin,
												final double pMax)
	{
		final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(array.length);
		lByteBuffer.order(ByteOrder.nativeOrder());

		for (final double lValue : array)
		{
			final double lScaledValue = (lValue - pMin) / (pMax - pMin);
			final byte l256value = (byte) (lScaledValue * 255);
			lByteBuffer.put(l256value);
		}
		lByteBuffer.flip();

		return lByteBuffer;
	}

	public ByteBuffer getRGBByteBufferAuto()
	{
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;

		for (final double lValue : array)
		{
			min = min > lValue ? lValue : min;
			max = max < lValue ? lValue : max;
		}

		return getRGBByteBuffer(min, max);
	}

	public ByteBuffer getRGBByteBuffer(	final double pMin,
										final double pMax)
	{
		final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(4 * array.length);
		lByteBuffer.order(ByteOrder.nativeOrder());

		for (final double lValue : array)
		{
			final double lScaledValue = (lValue - pMin) / (pMax - pMin);
			final double lClampedScaledValue = lScaledValue > 1	? 1
																: lScaledValue < 0	? 0
																					: lScaledValue;
			final int l256value = (int) (lClampedScaledValue * 255);
			final int argb = (0 & 0xFF << 24) + ((l256value & 0xFF) << 16)
								+ ((l256value & 0xFF) << 8)
								+ (l256value & 0xFF);
			lByteBuffer.putInt(argb);

		}
		lByteBuffer.flip();

		return lByteBuffer;
	}

	public DoubleArrayImage subImage(	final int pX,
										final int pY,
										final int pWidth,
										final int pHeight,
										DoubleArrayImage pDestinationImage)
	{
		if (pDestinationImage == null)
		{
			pDestinationImage = new DoubleArrayImage(pWidth, pHeight);
		}

		final double[] orgarray = array;
		final double[] destarray = pDestinationImage.array;

		if (pWidth < 10)
		{
			for (int y = 0; y < pHeight; y++)
			{
				int destoffset = y * pWidth;
				int orgoffset = (pY + y) * mWidth + pX;
				for (int x = 0; x < pWidth; x++)
				{
					destarray[destoffset++] = orgarray[orgoffset++];
				}
			}
		}
		else
		{
			for (int y = 0; y < pHeight; y++)
			{
				final int destoffset = y * pWidth;
				final int orgoffset = (pY + y) * mWidth + pX;
				System.arraycopy(	orgarray,
									orgoffset,
									destarray,
									destoffset,
									pWidth);
			}
		}

		return pDestinationImage;
	}

	public DoubleArrayImage subImageVerticallyCollapsed(final int pX,
														final int pY,
														final int pWidth,
														final int pHeight,
														DoubleArrayImage pOutputImage)
	{

		if (pOutputImage == null || pOutputImage.getWidth() != pWidth
			|| pOutputImage.getHeight() != 1)
		{
			pOutputImage = new DoubleArrayImage(pWidth, 1);
		}
		pOutputImage.set(0);

		final double[] orgarray = array;
		final double[] destarray = pOutputImage.array;

		final int lWidth = mWidth;
		int orgoffset = lWidth * pY + pX;
		final int endoffset = orgoffset + pHeight * lWidth;
		for (; orgoffset < endoffset; orgoffset += lWidth)
		{
			for (int x = 0; x < pWidth; x++)
			{
				destarray[x] += orgarray[orgoffset++];
			}
			orgoffset -= pWidth;
		}

		return pOutputImage;
	}

	public DoubleArrayImage subImageHorizontallyCollapsed(	final int pX,
															final int pY,
															final int pWidth,
															final int pHeight,
															DoubleArrayImage pOutputImage)
	{

		if (pOutputImage == null || pOutputImage.getWidth() != pHeight
			|| pOutputImage.getHeight() != 1)
		{
			pOutputImage = new DoubleArrayImage(pHeight, 1);
		}
		final double[] orgarray = array;
		final double[] destarray = pOutputImage.array;

		final int lWidth = mWidth;
		int orgoffset = lWidth * pY + pX;
		for (int y = 0; y < pHeight; y++)
		{
			double sum = destarray[y];
			for (int x = 0; x < pWidth; x++)
			{
				sum += orgarray[orgoffset++];
			}

			destarray[y] = sum;
			orgoffset += lWidth - pWidth;
		}

		return pOutputImage;
	}

	public void downscale(final DoubleArrayImage pDownscaleImage)
	{
		pDownscaleImage.set(0);
		final double[] oarray = pDownscaleImage.getArray();
		final int dswidth = pDownscaleImage.getWidth();
		final int dsheight = pDownscaleImage.getHeight();

		final double[] marray = array;
		int width = mWidth;
		int height = mHeight;

		final int dsx = width / dswidth;
		final int dsy = height / dsheight;

		// Make sure that width and height are multiples of the downsampled
		// image:
		width = dsx * (width / dsx);
		height = dsy * (height / dsy);

		for (int y = 0; y < height; y++)
		{
			final int yi = y * width;
			final int yip = y / dsy * dswidth;
			for (int x = 0; x < width; x++)
			{
				final int i = yi + x;
				final int xp = x / dsx;
				final int ip = yip + xp;

				oarray[ip] += marray[i];
			}
		}
	}

	public final void writePng(final File pFile) throws IOException
	{
		PngWriter.writePng(this, pFile);
	}

	public final boolean writeTiff(final File pTiffFile)
	{
		try
		{
			TiffWriter.write(this, pTiffFile);
			return true;
		}
		catch (final Throwable e)
		{
			throw new RuntimeException(e);
		}
	}

	public final boolean writeTiff16bit(final File pTiffFile)
	{
		try
		{
			TiffWriter.write16bit(this, pTiffFile);
			return true;
		}
		catch (final Throwable e)
		{
			throw new RuntimeException(e);
		}
	}

	public final double average()
	{
		final int length = array.length;
		final double[] marray = array;
		double average = 0;
		for (int i = 0; i < length; i++)
		{
			average += marray[i];
		}
		average = average / length;
		return average;
	}

	public final double variance(final double lAverage)
	{
		final int length = array.length;
		final double[] marray = array;
		double variance = 0;
		for (int i = 0; i < length; i++)
		{
			final double diff = marray[i] - lAverage;
			variance += diff * diff;
		}
		variance = variance / (length - 1);
		return variance;
	}

	public final double variance()
	{
		final double lAverage = average();
		final double lVariance = variance(lAverage);
		return lVariance;
	}

	public final double normalizedVariance()
	{
		final double lAverage = average();
		final double lVariance = variance(lAverage);
		final double lNormalizedVariance = lVariance / lAverage;
		return lNormalizedVariance;
	}

	public final double stddev(final double lAverage)
	{
		final double lVariance = variance(lAverage);
		final double lStdDev = Math.sqrt(lVariance);
		return lStdDev;
	}

	public final double kurthosis()
	{
		final int length = array.length;
		final double[] marray = array;
		double mean = 0, M2 = 0, M3 = 0, M4 = 0;

		for (int n = 0; n < length;)
		{
			final double x = marray[n];
			final double n1 = n;
			n = n + 1;
			final double delta = x - mean;
			final double delta_n = delta / n;
			final double delta_n2 = delta_n * delta_n;
			final double term1 = delta * delta_n * n1;
			mean = mean + delta_n;
			M4 = M4	+ term1
					* delta_n2
					* (n * n - 3 * n + 3)
					+ 6
					* delta_n2
					* M2
					- 4
					* delta_n
					* M3;
			M3 = M3 + term1 * delta_n * (n - 2) - 3 * delta_n * M2;
			M2 = M2 + term1;
		}
		final double kurtosis = length * M4 / (M2 * M2) - 3;
		return kurtosis;
	}

	public final double[] minmax()
	{
		final int length = array.length;
		final double[] marray = array;
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			min = Math.min(value, min);
			max = Math.max(value, max);
		}
		return new double[]
		{ min, max };
	}

	public final double min()
	{
		final int length = array.length;
		final double[] marray = array;
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			min = Math.min(value, min);
		}
		return min;
	}

	public final double max()
	{
		final int length = array.length;
		final double[] marray = array;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			max = Math.max(value, max);
		}
		return max;
	}

	public void max(DoubleArrayImage pDoubleArrayImage)
	{
		final int length = array.length;
		final double[] marray = array;
		final double[] oarray = pDoubleArrayImage.array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			final double ovalue = oarray[i];
			marray[i] = Math.max(value, ovalue);
		}
	}

	public final double maxslope(final int pRadius)
	{
		final int length = array.length;
		final double[] marray = array;

		final int radiush = pRadius;
		final int radiusv = pRadius * mWidth;

		final int start = radiush + radiusv;
		final int stop = length - radiush - radiusv;

		double max = Double.NEGATIVE_INFINITY;
		for (int i = start; i < stop; i++)
		{
			final double valueh = marray[i - radiush] - marray[i + radiush];
			max = Math.max(valueh, max);
			final double valuev = marray[i - radiusv] - marray[i + radiusv];
			max = Math.max(valuev, max);
		}
		return max;
	}

	public final double sum()
	{
		final int length = array.length;
		final double[] marray = array;
		double sum = 0;
		for (int i = 0; i < length; i++)
		{
			sum += marray[i];
		}
		return sum;
	}

	public final double normalizeStandardDeviationNoDC()
	{
		final double lAverage = average();
		final double lStdDev = stddev(lAverage);
		final double lInverseStdDev = 1 / lStdDev;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] = lInverseStdDev * (marray[i] - lAverage);
		}
		return lAverage;
	}

	public double normalizeDC(final double pNewDC)
	{
		final double lAverage = average();
		final double lCorrection = pNewDC - lAverage;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] += lCorrection;
		}
		return lAverage;
	}

	public final double normalizeSum()
	{
		final double sum = sum();
		if (sum != 0)
		{
			final double invsum = 1 / sum;
			final int length = array.length;
			final double[] marray = array;
			for (int i = 0; i < length; i++)
			{
				marray[i] *= invsum;
			}
		}
		return sum;
	}

	public final double normalizeNormL1()
	{
		final double normL1 = normL1();
		if (normL1 != 0)
		{
			final double invabssum = 1 / normL1;
			final int length = array.length;
			final double[] marray = array;
			for (int i = 0; i < length; i++)
			{
				final double value = marray[i];
				marray[i] = value * invabssum;
			}
		}
		return normL1;
	}

	public final double normalizeNormL2()
	{
		final double norm = normL2();
		if (norm != 0)
		{
			final double invnorm = 1 / norm;

			final int length = array.length;
			final double[] marray = array;
			for (int i = 0; i < length; i++)
			{
				final double value = marray[i];
				marray[i] = value * invnorm;
			}
		}
		return norm;
	}

	public final double normalizePowerNorm(final double pPower)
	{
		final double norm = normLp(pPower);
		if (norm != 0)
		{
			final double invnorm = 1 / norm;

			final int length = array.length;
			final double[] marray = array;
			for (int i = 0; i < length; i++)
			{
				final double value = marray[i];
				marray[i] = value * invnorm;
			}
		}
		return norm;
	}

	public final double normL1()
	{
		final int length = array.length;
		final double[] marray = array;
		double sum = 0;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			sum += Math.abs(value);
		}
		return sum;
	}

	public final double normL2()
	{
		final int length = array.length;
		final double[] marray = array;
		double norm = 0;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			norm += value * value;
		}
		norm = Math.sqrt(norm);
		return norm;
	}

	public final double normLp(final double pPower)
	{
		final int length = array.length;
		final double[] marray = array;
		double norm = 0;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			final double absvalue = Math.abs(value);
			norm += Math.pow(absvalue, pPower);
		}
		norm = Math.pow(norm, 1 / pPower);
		return norm;
	}

	private static final double median3(final double a,
										final double b,
										final double c)
	{
		if (a < b)
		{
			if (b < c)
			{
				return b;
			}
			else if (b == c)
			{
				return b;
			}
			else if (a < c)
			{
				return c;
			}
			else if (a == c)
			{
				return a;
			}
			else
			{
				return a;
			}

		}
		else if (a == b)
		{
			return a;
		}
		else
		{
			if (a < c)
			{
				return a;
			}
			else if (a == c)
			{
				return a;
			}
			else if (b < c)
			{
				return c;
			}
			else if (b == c)
			{
				return b;
			}
			else
			{
				return b;
			}
		}
	}

	public final void median3x3(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] oarray = pDoubleArrayImage.array;
		final int width = mWidth;
		final int length = array.length;
		final double[] marray = array;
		final int begin = width + 1;
		final int end = length - width - 1;
		for (int i = begin; i < end; i++)
		{
			final double a = oarray[i - width - 1];
			final double b = oarray[i - width];
			final double c = oarray[i - width + 1];
			final double d = oarray[i - 1];
			final double e = oarray[i];
			final double f = oarray[i + 1];
			final double g = oarray[i + width - 1];
			final double h = oarray[i + width];
			final double k = oarray[i + width + 1];

			final double row1med = median3(a, e, k);
			final double row2med = median3(b, f, g);
			final double row3med = median3(c, d, h);

			final double rowsmed = median3(row1med, row2med, row3med);

			final double col1med = median3(c, e, g);
			final double col2med = median3(b, d, k);
			final double col3med = median3(a, f, h);

			final double colsmed = median3(col1med, col2med, col3med);

			marray[i] = 0.5 * (rowsmed + colsmed);
		}
	}

	public final void fastInPlaceMedian()
	{
		final int width = mWidth;
		final int length = array.length;
		final double[] marray = array;
		final int begin = width + 1;
		final int end = length - width - 1;
		for (int i = begin; i < end; i++)
		{
			final double d = marray[i - 1];
			final double e = marray[i];
			final double f = marray[i + 1];
			final double row2med = median3(d, e, f);
			marray[i] = row2med;
		}
		for (int i = begin; i < end; i++)
		{
			final double b = marray[i - width];
			final double e = marray[i];
			final double h = marray[i + width];
			final double col2med = median3(b, e, h);
			marray[i] = col2med;
		}
	}

	public final void median3x3spokes(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] oarray = pDoubleArrayImage.array;
		final int width = mWidth;
		final int length = array.length;
		final double[] marray = array;
		final int begin = width + 1;
		final int end = length - width - 1;
		for (int i = begin; i < end; i++)
		{
			final double a = oarray[i - width - 1];
			final double b = oarray[i - width];
			final double c = oarray[i - width + 1];
			final double d = oarray[i - 1];
			final double e = oarray[i];
			final double f = oarray[i + 1];
			final double g = oarray[i + width - 1];
			final double h = oarray[i + width];
			final double k = oarray[i + width + 1];

			final double u = median3(a, e, k);
			final double v = median3(a, f, h);
			final double w = median3(d, b, k);

			final double alpha = median3(u, v, w);

			final double up = median3(g, e, c);
			final double vp = median3(c, d, h);
			final double wp = median3(g, b, f);

			final double beta = median3(up, vp, wp);

			marray[i] = 0.5 * (alpha + beta);
		}
	}

	public final void despeckle(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] oarray = pDoubleArrayImage.array;
		final int width = mWidth;
		final int length = array.length;
		final double[] marray = array;
		final int begin = width + 1;
		final int end = length - width - 1;
		for (int i = begin; i < end; i++)
		{
			final double a = oarray[i - width - 1];
			final double b = oarray[i - width];
			final double c = oarray[i - width + 1];
			final double d = oarray[i - 1];
			final double e = oarray[i];
			final double f = oarray[i + 1];
			final double g = oarray[i + width - 1];
			final double h = oarray[i + width];
			final double k = oarray[i + width + 1];

			final double u = median3(a, b, c);
			final double v = median3(d, e, f);
			final double w = median3(g, h, k);

			final boolean isposridgeuvw = v > Math.max(u, w);
			final boolean isnegridgeuvw = v < Math.min(u, w);
			final boolean isposridgeuew = e > Math.max(u, w);
			final boolean isnegridgeuew = e < Math.min(u, w);

			double horizproposal = e;

			if (2 * e < 3 * w - u || 2 * e > 3 * u - w
				|| isposridgeuvw
				^ isposridgeuew
				|| isnegridgeuvw
				^ isnegridgeuew)
			{
				horizproposal = median3(u, v, w);
			}

			final double pu = median3(a, d, g);
			final double pv = median3(b, e, h);
			final double pw = median3(c, f, k);

			final boolean isposridgepupvpw = pv > Math.max(pu, pw);
			final boolean isnegridgepupvpw = pv < Math.min(pu, pw);
			final boolean isposridgepuepw = e > Math.max(pu, pw);
			final boolean isnegridgepuepw = e < Math.min(pu, pw);

			double vertproposal = e;
			if (2 * e < 3 * pw - pu || 2 * e > 3 * pu - pw
				|| isposridgepupvpw
				^ isposridgepuepw
				|| isnegridgepupvpw
				^ isnegridgepuepw)
			{
				vertproposal = median3(u, v, w);
			}

			marray[i] = 0.5 * (horizproposal + vertproposal);
		}
	}

	public final boolean substract(final DoubleArrayImage pDoubleArrayImage)
	{
		if (isSameDimensions(pDoubleArrayImage))
		{
			final int length = array.length;
			final double[] oarray = pDoubleArrayImage.array;
			final double[] larray = array;
			for (int i = 0; i < length; i++)
			{
				larray[i] -= oarray[i];
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public final boolean add(final DoubleArrayImage pDoubleArrayImage)
	{
		if (isSameDimensions(pDoubleArrayImage))
		{
			final int length = array.length;
			final double[] oarray = pDoubleArrayImage.array;
			final double[] larray = array;
			for (int i = 0; i < length; i++)
			{
				larray[i] += oarray[i];
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public final void absdiff(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] otherarray = pDoubleArrayImage.array;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] = Math.abs(array[i] - otherarray[i]);
		}
	}

	public final void absdiff(	final DoubleArrayImage pDoubleArrayImage1,
								final DoubleArrayImage pDoubleArrayImage2)
	{
		final double[] oarray1 = pDoubleArrayImage1.array;
		final double[] oarray2 = pDoubleArrayImage2.array;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] = Math.abs(oarray1[i] - oarray2[i]);
		}
	}

	public final void addabsdiff(	final DoubleArrayImage pDoubleArrayImage1,
									final DoubleArrayImage pDoubleArrayImage2)
	{
		final double[] oarray1 = pDoubleArrayImage1.array;
		final double[] oarray2 = pDoubleArrayImage2.array;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] += Math.abs(oarray1[i] - oarray2[i]);
		}
	}

	public final void negdiff(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] otherarray = pDoubleArrayImage.array;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			final double othervalue = otherarray[i];
			final double diff = value - othervalue;
			marray[i] = diff < 0 ? -diff : 0;
		}
	}

	public final void posdiff(final DoubleArrayImage pDoubleArrayImage)
	{
		final double[] otherarray = pDoubleArrayImage.array;
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			final double othervalue = otherarray[i];
			final double diff = value - othervalue;
			marray[i] = diff > 0 ? diff : 0;
		}
	}

	public final void add(final double pValue)
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] += pValue;
		}
	}

	public final void mult(final double pValue)
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] *= pValue;
		}
	}

	public void affine(double pA, double pB)
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			marray[i] = pA * marray[i] + pB;
		}
	}

	public final void power(final double pExponent)
	{
		if (pExponent == 1)
		{
			return;
		}
		else if (pExponent == -1)
		{
			inverse();
		}
		else if (pExponent == 2)
		{
			square();
		}
		else if (pExponent == 3)
		{
			cube();
		}
		else
		{
			final int length = array.length;
			final double[] marray = array;
			for (int i = 0; i < length; i++)
			{
				final double value = marray[i];
				marray[i] = Math.pow(value, pExponent);
			}
		}
	}

	public void inverse()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = 1.0 / value;
		}
	}

	public void square()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = value * value;
		}
	}

	public void cube()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = value * value * value;
		}
	}

	public void sqrt()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = Math.sqrt(value);
		}
	}

	public final void log()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = Math.log(value);
		}
	}

	public final void log1p()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = Math.log1p(value);
		}
	}

	public void sympower(final double pPower)
	{
		final int length = array.length;
		final double[] larray = array;
		for (int i = 0; i < length; i++)
		{
			final double x = 2 * (larray[i] - 0.5);
			final double s = x > 0 ? 1 : -1;
			final double a = x > 0 ? x : -x;
			larray[i] = 0.5 + 0.5 * s * (1 - Math.pow(1 - a, pPower));
		}
	}

	public final void abs()
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = value > 0 ? value : -value;
		}
	}

	public final void addAndClamp(	final double v,
									final double min,
									final double max)
	{
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i] + v;
			marray[i] = value < 0 ? 0 : value > 1 ? 1 : value;
		}
	}

	public final void map(	final double pMin0,
							final double pMax0,
							final double pMin1,
							final double pMax1)
	{
		final int length = array.length;
		final double[] marray = array;
		final double l0 = pMax0 - pMin0;
		final double l1 = pMax1 - pMin1;
		final double rl1l0 = l1 / l0;
		for (int i = 0; i < length; i++)
		{
			final double value = (marray[i] - pMin0) * rl1l0 + pMin1;
			marray[i] = value < pMin1	? pMin1
										: (value > pMax1 ? pMax1
														: value);
		}
	}

	public final DoubleArrayImage histogram(final DoubleArrayImage pHistogram)
	{
		final double[] lMinMax = minmax();
		final double lMin = lMinMax[0];
		final double lMax = lMinMax[1];
		return histogram(pHistogram, lMin, lMax);

	}

	public void round(int pPlaces)
	{
		final int length = array.length;
		final double[] marray = array;
		
		for (int i = 0; i < length; i++)
		{
			final double value = round(marray[i],pPlaces);
			marray[i] = value;
		}
	}

	private static double round(double value, int places)
	{
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public final DoubleArrayImage histogram(final DoubleArrayImage pHistogram,
											final double pMin,
											final double pMax)
	{
		final int lNumberOfBins = pHistogram.getLength();
		final double[] hist = pHistogram.getArray();

		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = (marray[i] - pMin) / (pMax - pMin);
			int index = (int) (value * (lNumberOfBins - 1));
			if (index >= hist.length)
			{
				index = hist.length - 1;
			}
			if (index < 0)
			{
				index = 0;
			}

			hist[index]++;
		}

		for (int i = 0; i < hist.length; i++)
		{
			hist[i] /= length;
		}

		return pHistogram;
	}

	public final DoubleArrayImage extractSubImage(	final int pX,
													final int pY,
													final int pWidth,
													final int pHeight,
													final DoubleArrayImage pDestination)
	{
		if (pDestination.getWidth() != pWidth || pDestination.getHeight() != pHeight)
		{
			return null;
		}

		final double[] darray = pDestination.array;
		final double[] marray = array;
		final int width = mWidth;
		final int pYi = pY * width;

		for (int dy = 0; dy < pHeight; dy++)
		{
			final int yi = dy * pWidth;
			final int syi = width * dy;
			for (int dx = 0; dx < pWidth; dx++)
			{
				darray[yi + dx] = marray[pYi + pX + syi + dx];
			}
		}

		return pDestination;
	}

	public final double dot(final DoubleArrayImage pDoubleArrayImage)
	{
		final int length = array.length;
		final double[] marray = array;
		final double[] oarray = pDoubleArrayImage.array;
		double dotprod = 0;
		for (int i = 0; i < length; i++)
		{
			dotprod += marray[i] * oarray[i];
		}
		return dotprod;
	}

	public double autocorr(final int dx, final int dy)
	{
		final double[] marray = array;
		final int width = mWidth;
		final int height = mHeight;
		final int dytwidthpdx = dy * width + dx;

		double sum = 0;
		for (int y = Math.max(0, -dy); y < Math.min(height,
													height - dy); y++)
		{
			final int yi = y * width;
			for (int x = Math.max(0, -dx); x < Math.min(width,
														width - dx); x++)
			{
				final int i = yi + x;
				sum += marray[i] * marray[i + dytwidthpdx];
			}
		}

		return sum;
	}

	public final void dhtforward()
	{
		final DoubleDHT_2D dht = new DoubleDHT_2D(mHeight, mWidth);
		dht.forward(array);
	}

	public final void dhtinverse()
	{
		final DoubleDHT_2D dht = new DoubleDHT_2D(mHeight, mWidth);
		dht.inverse(array, true);
	}

	public final void dctforward()
	{
		final DoubleDCT_2D dct = new DoubleDCT_2D(mHeight, mWidth);
		dct.forward(array, false);
	}

	public final void dctinverse()
	{
		final DoubleDCT_2D dct = new DoubleDCT_2D(mHeight, mWidth);
		dct.inverse(array, false);
	}

	public final void dctlogpower()
	{
		final DoubleDCT_2D dct = new DoubleDCT_2D(mHeight, mWidth);
		dct.forward(array, false);
		final int length = array.length;
		final double[] marray = array;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			marray[i] = Math.log(1 + value * value);
		}
	}

	public final void fftLogPower()
	{
		final double[] larray = array;
		final int lWidth = mWidth;
		final int lHeight = mHeight;

		final DoubleFFT_2D fft = new DoubleFFT_2D(lHeight, lWidth);
		final double[] lTempArray = new double[2 * lWidth * lHeight];

		for (int r = 0; r < lHeight; r++)
		{
			for (int c = 0; c < lWidth; c++)
			{
				lTempArray[2 * r * lWidth + 2 * c] = larray[r * lWidth
															+ c];
			}
		}

		fft.complexForward(lTempArray);

		for (int r = 0; r < lHeight; r++)
		{
			for (int c = 0; c < lWidth; c++)
			{
				final double real = lTempArray[2 * r * lWidth + 2 * c];
				final double imag = lTempArray[2 * r
												* lWidth
												+ 2
												* c
												+ 1];

				final int rt = (r + lHeight / 2) % lHeight;
				final int ct = (c + lWidth / 2) % lWidth;

				larray[rt * lWidth + ct] = Math.log(1 + real
													* real
													+ imag
													* imag);
			}
			// unpacker.unpack(r, c, lTempArray, mHeight*mWidth/2+mWidth/2);
		}

	}

	public final void fftPower()
	{
		final double[] larray = array;
		final int lWidth = mWidth;
		final int lHeight = mHeight;

		final DoubleFFT_2D fft = new DoubleFFT_2D(lHeight, lWidth);
		final double[] lTempArray = new double[2 * lWidth * lHeight];

		for (int r = 0; r < lHeight; r++)
		{
			for (int c = 0; c < lWidth; c++)
			{
				lTempArray[2 * r * lWidth + 2 * c] = larray[r * lWidth
															+ c];
			}
		}

		fft.complexForward(lTempArray);

		for (int r = 0; r < lHeight; r++)
		{
			for (int c = 0; c < lWidth; c++)
			{
				final double real = lTempArray[2 * r * lWidth + 2 * c];
				final double imag = lTempArray[2 * r
												* lWidth
												+ 2
												* c
												+ 1];

				final int rt = (r + lHeight / 2) % lHeight;
				final int ct = (c + lWidth / 2) % lWidth;

				larray[rt * lWidth + ct] = (real * real + imag * imag);
			}
			// unpacker.unpack(r, c, lTempArray, mHeight*mWidth/2+mWidth/2);
		}

	}

	public final void fftAbsSum()
	{
		final double[] larray = array;
		final int lWidth = mWidth;
		final int lHeight = mHeight;

		final DoubleFFT_2D fft = new DoubleFFT_2D(lHeight, lWidth);
		final double[] lTempArray = new double[2 * lWidth * lHeight];

		for (int r = 0; r < lHeight; r++)
		{
			for (int c = 0; c < lWidth; c++)
			{
				lTempArray[2 * r * lWidth + 2 * c] = larray[r * lWidth
															+ c];
			}
		}

		fft.realForwardFull(lTempArray);

		for (int r = 0; r < lHeight; r++)
		{
			for (int c = 0; c < lWidth; c++)
			{
				final double real = lTempArray[2 * r * lWidth + 2 * c];
				final double imag = lTempArray[2 * r
												* lWidth
												+ 2
												* c
												+ 1];

				final int rt = (r + lHeight / 2) % lHeight;
				final int ct = (c + lWidth / 2) % lWidth;

				larray[rt * lWidth + ct] = Math.sqrt(real * real
														+ imag
														* imag);
			}
			// unpacker.unpack(r, c, lTempArray, mHeight*mWidth/2+mWidth/2);
		}

	}

	public final double entropyShannon(final boolean pPerPixel)
	{
		final int length = array.length;
		final double[] marray = array;
		double entropy = 0;
		for (int i = 0; i < length; i++)
		{
			final double value = marray[i];
			if (value > 0)
			{
				entropy += value * Math.log(value);
			}
			else if (value < 0)
			{
				entropy += -value * Math.log(-value);
			}
		}
		entropy = -entropy;

		if (pPerPixel)
		{
			entropy = entropy / length;
		}

		return entropy;
	}

	public final double entropyShannonSubRectangle(	final int xl,
													final int yl,
													final int xh,
													final int yh,
													final boolean pPerPixel)
	{
		final double[] marray = array;
		double entropy = 0;
		for (int y = yl; y < yh; y++)
		{
			final int yi = y * mWidth;

			for (int x = xl; x < xh; x++)
			{
				final int i = yi + x;
				final double value = marray[i];
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
		entropy = -entropy;

		if (pPerPixel)
		{
			entropy = entropy / ((xh - xl) * (yh - yl));
		}

		return entropy;
	}

	public final double entropyShannonSubTriangle(	final int xl,
													final int yl,
													final int xh,
													final int yh,
													final boolean pPerPixel)
	{
		final int width = mWidth;
		final double[] marray = array;
		double entropy = 0;
		for (int y = yl; y < yh; y++)
		{
			final int yi = y * width;

			final int xend = xh - y * xh / yh;
			for (int x = xl; x < xend; x++)
			{
				final int i = yi + x;
				final double value = marray[i];
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
		entropy = -entropy;

		if (pPerPixel)
		{
			entropy = 2 * entropy / ((xh - xl) * (yh - yl));
		}

		return entropy;
	}

	public final double entropyBayesSubTriangle(final int xl,
												final int yl,
												final int xh,
												final int yh,
												final boolean pPerPixel)
	{
		final int width = mWidth;
		final double[] marray = array;
		double a = 0, b = 0;
		for (int y = yl; y < yh; y++)
		{
			final int xend = xh - y * xh / yh;
			for (int x = xl; x < xend; x++)
			{
				final int i = x + width * y;
				final double v = marray[i];
				a += v * v;
				b += Math.abs(v);
			}
		}
		final double c = pPerPixel	? (double) ((xh - xl) * (yh - yl)) / 2
									: 1;
		final double entropy = 1 - a * c / (b * b);

		return entropy;
	}

	public final double entropyBayesSubRectangle(	final int xl,
													final int yl,
													final int xh,
													final int yh,
													final boolean pPerPixel)
	{
		final int width = mWidth;
		final double[] marray = array;
		double a = 0, b = 0;
		for (int y = yl; y < yh; y++)
		{
			for (int x = xl; x < xh; x++)
			{
				final int i = x + width * y;
				final double v = marray[i];
				a += v * v;
				b += Math.abs(v);
			}
		}
		final double c = pPerPixel	? (double) ((xh - xl) * (yh - yl))
									: 1;
		final double entropy = 1 - a * c / (b * b);

		return entropy;
	}

	public final double entropyBayesSubTriangle(final int xl,
												final int yl,
												final int xh,
												final int yh,
												final boolean pPerPixel,
												final double pExponent)
	{
		final int width = mWidth;
		final double[] marray = array;
		double a = 0, b = 0;
		for (int y = yl; y < yh; y++)
		{
			final int xend = xh - y * xh / yh;
			for (int x = xl; x < xend; x++)
			{
				final int i = x + width * y;
				final double v = marray[i];
				a += Math.pow(Math.abs(v), pExponent);
				b += Math.abs(v);
			}
		}
		final double c = pPerPixel	? Math.pow(	(double) ((xh - xl) * (yh - yl)) / 2,
												pExponent - 1)
									: 1;
		// System.out.println(lRatio);
		final double entropy = 1 - Math.pow(a		* c
													/ Math.pow(	b,
																pExponent),
											1 / pExponent);
		;

		return entropy;
	}

	public ArrayMatrix<DoubleArrayImage> extractTiles(	final int pMatrixWidth,
														final int pMatrixHeight)
	{
		final ArrayMatrix<DoubleArrayImage> lMatrix = new ArrayMatrix<DoubleArrayImage>();

		final double lTileWithDouble = (double) mWidth / pMatrixWidth;
		final int lTileWidth = (int) lTileWithDouble;

		final double lTileHeightDouble = (double) mHeight / pMatrixHeight;
		final int lTileHeight = (int) lTileHeightDouble;

		for (int x = 0; x < pMatrixWidth; x++)
		{
			final ArrayList<DoubleArrayImage> lRow = new ArrayList<DoubleArrayImage>();

			for (int y = 0; y < pMatrixHeight; y++)
			{
				final int tx = x * lTileWidth;
				final int ty = y * lTileHeight;

				final DoubleArrayImage lSubImage = subImage(tx,
															ty,
															lTileWidth,
															lTileHeight,
															null);
				lRow.add(lSubImage);
			}
			lMatrix.add(lRow);
		}

		return lMatrix;
	}

	@Override
	public String toString()
	{
		return "DoubleArrayImage [mWidth=" + mWidth
				+ ", mHeight="
				+ mHeight
				+ "]";
	}

}