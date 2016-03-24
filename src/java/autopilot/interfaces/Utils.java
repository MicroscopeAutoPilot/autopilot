package autopilot.interfaces;

import java.nio.ByteBuffer;

import autopilot.image.DoubleArrayImage;

public class Utils
{

	static ThreadLocal<DoubleArrayImage> sPreAllocatedDoubleArrayImageThreadLocal = new ThreadLocal<DoubleArrayImage>();

	public static DoubleArrayImage getThreadLocalDoubleArrayImage(final ByteBuffer p16BitImageByteBuffer,
																																final int pWidth,
																																final int pHeight)
	{
		DoubleArrayImage lDoubleArrayImage = sPreAllocatedDoubleArrayImageThreadLocal.get();
		if (lDoubleArrayImage == null || lDoubleArrayImage.getWidth() != pWidth
				|| lDoubleArrayImage.getHeight() != pHeight)
		{
			lDoubleArrayImage = new DoubleArrayImage(pWidth, pHeight);
			sPreAllocatedDoubleArrayImageThreadLocal.set(lDoubleArrayImage);
		}

		lDoubleArrayImage.load16bitByteBuffer(p16BitImageByteBuffer);
		return lDoubleArrayImage;
	}

	public static DoubleArrayImage getDoubleArrayImage(	final ByteBuffer p16BitImageByteBuffer,
																											final int pWidth,
																											final int pHeight)
	{
		final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(pWidth,
																																		pHeight);
		lDoubleArrayImage.load16bitByteBuffer(p16BitImageByteBuffer);
		return lDoubleArrayImage;
	}

	public static DoubleArrayImage getDoubleArrayImage(final double[][] pP2dImage)
	{
		final int lWidth = pP2dImage[0].length;
		final int lHeight = pP2dImage.length;

		final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(lWidth,
																																		lHeight);

		for (int y = 0; y < lHeight; y++)
		{
			for (int x = 0; x < lWidth; x++)
			{
				final double lValue = pP2dImage[y][x];
				lDoubleArrayImage.setIntQuick(x, y, lValue);
			}
		}

		return lDoubleArrayImage;
	}

	public static DoubleArrayImage getDoubleArrayImage(final Double[][] pP2dImage)
	{
		final int lWidth = pP2dImage[0].length;
		final int lHeight = pP2dImage.length;

		final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(lWidth,
																																		lHeight);

		for (int y = 0; y < lHeight; y++)
		{
			for (int x = 0; x < lWidth; x++)
			{
				final double lValue = pP2dImage[y][x];
				lDoubleArrayImage.setIntQuick(x, y, lValue);
			}
		}

		return lDoubleArrayImage;
	}

	public static double[] getDoubleArrayImage(final double[][][] p2dImage)
	{
		final int lWidth = p2dImage[0][0].length;
		final int lHeight = p2dImage[0].length;
		final int lDepth = p2dImage.length;

		final int length = lWidth * lHeight * lDepth;
		// System.out.println("Received data of width: " + lWidth);
		// System.out.println("Received data of height: " + lHeight);
		// System.out.println("Received data of depth: " + lDepth);
		// System.out.println("Received data of length: " + length);
		final double[] lSingleArrayImage = new double[length];

		for (int z = 0; z < lDepth; z++)
		{
			for (int y = 0; y < lHeight; y++)
			{
				for (int x = 0; x < lWidth; x++)
				{
					final double lValue = p2dImage[z][y][x];
					final int lIndex = z * lWidth * lHeight + y * lWidth + x;
					lSingleArrayImage[lIndex] = lValue;
				}
			}
		}

		return lSingleArrayImage;
	}

	public static double[] getDoubleArrayImage(final Double[][][] pP2dImage)
	{
		final int lWidth = pP2dImage[0][0].length;
		final int lHeight = pP2dImage[0].length;
		final int lDepth = pP2dImage.length;

		final int length = lWidth * lHeight * lDepth;
		// System.out.println("Received data of width: " + lWidth);
		// System.out.println("Received data of height: " + lHeight);
		// System.out.println("Received data of depth: " + lDepth);
		// System.out.println("Received data of length: " + length);

		final double[] lSingleArrayImage = new double[length];

		for (int z = 0; z < lDepth; z++)
		{
			for (int y = 0; y < lHeight; y++)
			{
				for (int x = 0; x < lWidth; x++)
				{
					final double lValue = pP2dImage[z][y][x];
					final int lIndex = z * lWidth * lHeight + y * lWidth + x;
					lSingleArrayImage[lIndex] = lValue;
				}
			}
		}

		return lSingleArrayImage;
	}

	static double[] convertDouble2double(final Double[] pArray)
	{
		final double[] lNewArray = new double[pArray.length];
		for (int i = 0; i < pArray.length; i++)
		{
			lNewArray[i] = pArray[i].doubleValue();
		}
		return lNewArray;
	}

	public static boolean[] convertBoolean2boolean(final Boolean[] pArray)
	{
		final boolean[] lNewArray = new boolean[pArray.length];
		for (int i = 0; i < pArray.length; i++)
		{
			lNewArray[i] = pArray[i].booleanValue();
		}
		return lNewArray;
	}

}
