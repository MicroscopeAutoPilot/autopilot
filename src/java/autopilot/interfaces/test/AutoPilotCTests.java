package autopilot.interfaces.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.junit.Test;

import rtlib.core.units.Magnitude;
import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.interfaces.AutoPilotC;
import cern.colt.Arrays;

public class AutoPilotCTests
{

	// 1180 x 748 x 130 1180, 748, 130
	final static int nx = 1180;
	final static int ny = 748;
	final static int nz = 130;

	public static final void println(String pString)
	{
		System.out.println(pString);
	}

	@Test
	public void dcts2()
	{
		final ByteBuffer lImageByteBuffer = getImageByteBuffer();

		final double dcts2 = AutoPilotC.dcts16bit(lImageByteBuffer,
																							nx,
																							ny,
																							3);

		System.out.println(dcts2);
	}

	@Test
	public void dcts2stack() throws IOException
	{
		for (int i = 0; i < 7; i++)
		{
			final DoubleArrayImage lDoubleArrayImage = TiffReader.read(	AutoPilotCTests.class.getResourceAsStream("./data/TP=1169_Zi=4_LS=1.tif"),
																																	i,
																																	null);

			final ByteBuffer lImageByteBuffer = lDoubleArrayImage.getByteBufferOfUnsignedShorts();

			// System.out.println("lDoubleArrayImage.getWidth()=" +
			// lDoubleArrayImage.getWidth());
			// System.out.println("lDoubleArrayImage.getHeight()=" +
			// lDoubleArrayImage.getHeight());

			final double dcts2 = AutoPilotC.dcts16bit(lImageByteBuffer,
																								lDoubleArrayImage.getWidth(),
																								lDoubleArrayImage.getHeight(),
																								3);

			System.out.println("dcts=\t" + dcts2);
		}
	}

	@Test
	public void tenengrad()
	{
		final ByteBuffer lImageByteBuffer = getImageByteBuffer();

		final double dcts2 = AutoPilotC.tenengrad16bit(	lImageByteBuffer,
																										nx,
																										ny,
																										3);

		System.out.println(dcts2);
	}

	@Test
	public void qpsolverTestReturnCode()
	{
		try
		{
			final boolean pAnchorDetection = true;
			final boolean pSymmetricAnchor = true;
			final int pNumberOfWavelengths = 1;
			final int pNumberOfPlanes = 3;
			final boolean[] pSyncPlanesIndices = new boolean[]
			{ false, true, false };
			final double[] pCurrentStateVector = new double[30];
			final double[] pObservationsVector = new double[30];
			final boolean[] pMissingObservations = new boolean[30];
			final double[] pMaximalCorrections = new double[30];
			final double[] pNewStateVector = new double[30];
			final int lReturnCode = AutoPilotC.qpsolve(	pAnchorDetection,
																									pSymmetricAnchor,
																									pNumberOfWavelengths,
																									pNumberOfPlanes,
																									pSyncPlanesIndices,
																									pCurrentStateVector,
																									pObservationsVector,
																									pMissingObservations,
																									pMaximalCorrections,
																									pNewStateVector);
			// System.out.println(lReturnCode);
			assertTrue(lReturnCode == 0);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void qpsolverTestExtraDOFs()
	{
		try
		{
			final int pNumberOfWavelengths = 3;
			final int pNumberOfPlanes = 2;
			final int pNumberOfDOfs = 1;
			final int lNumberOfVariables = pNumberOfWavelengths * pNumberOfPlanes
																			* pNumberOfDOfs;

			final double[] pCurrentStateVector = new double[lNumberOfVariables];
			final double[] pObservationsVector = new double[lNumberOfVariables];
			final boolean[] pMissingObservations = new boolean[lNumberOfVariables];
			final double[] pMaximalCorrections = new double[lNumberOfVariables];

			pObservationsVector[0] = +1.1;
			pObservationsVector[1] = +2.5;
			pObservationsVector[pNumberOfPlanes] = +0;
			pObservationsVector[2 * pNumberOfPlanes] = 0.5;
			for (int i = 0; i < pNumberOfPlanes; i++)
				pMissingObservations[pNumberOfPlanes + i] = true;/**/
			pMissingObservations[lNumberOfVariables - 1] = true;
			for (int i = 0; i < lNumberOfVariables; i++)
				pMaximalCorrections[i] = 2;

			final double[] pNewStateVector = new double[lNumberOfVariables];

			final int lReturnCode = AutoPilotC.extrasolve(pNumberOfWavelengths,
																										pNumberOfPlanes,
																										pNumberOfDOfs,
																										pCurrentStateVector,
																										pObservationsVector,
																										pMissingObservations,
																										pMaximalCorrections,
																										pNewStateVector);

			/*println(Arrays.toString(pCurrentStateVector));
			println(Arrays.toString(pObservationsVector));
			println(Arrays.toString(pMissingObservations));
			println(Arrays.toString(pMaximalCorrections));
			println(Arrays.toString(pNewStateVector));/**/

			assertEquals(1.1, pNewStateVector[0], 0.01);
			assertEquals(2, pNewStateVector[1], 0.01);
			assertEquals(1.1, pNewStateVector[2], 0.01);
			assertEquals(2, pNewStateVector[3], 0.01);
			assertEquals(0.5, pNewStateVector[4], 0.01);
			assertEquals(0.5, pNewStateVector[5], 0.01);

			assertTrue(lReturnCode == 0);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void argmax()
	{
		try
		{
			final double[] lX = new double[]
			{ -3, -2, -1, 0, 1, 2, 3 };

			{
				final double[] lResult = new double[2];
				final double[] lY = new double[]
				{ 0.21, 0.31, 0.41, 0.43, 0.39, 0.29, 0.19 };

				final double[] lFittedY = new double[lY.length];

				final int lRepeats = 3;
				final long lStart = System.nanoTime();
				int lReturnCode = -1;
				for (int i = 0; i < lRepeats; i++)
					lReturnCode = AutoPilotC.argmax(lX, lY, lFittedY, lResult);
				final long lStop = System.nanoTime();
				final double lTimeInMsPerCall = Magnitude.nano2milli((1.0 * lStop - lStart) / lRepeats);
				System.out.format("argmax time: %g ms \n", lTimeInMsPerCall);
				final double lArgMax = lResult[0];
				final double lFitprobability = lResult[1];

				System.out.println("lReturnCode=" + lReturnCode);
				System.out.println("lArgMax=" + lArgMax);
				System.out.println("lFitprobability=" + lFitprobability);
				System.out.println("lX=" + Arrays.toString(lX));
				System.out.println("lY=" + Arrays.toString(lY));
				System.out.println("lFittedY=" + Arrays.toString(lFittedY));

				assertTrue(lReturnCode == 0);
			}

			{
				final double[] lResult = new double[2];
				final double[] lY = new double[]
				{ 0.1, 0.4, 0.45, 0.3, 0.1, 0.41, 0.31 };

				final double[] lFittedY = new double[lY.length];

				final int lReturnCode = AutoPilotC.argmax(lX,
																									lY,
																									lFittedY,
																									lResult);
				final double lArgMax = lResult[0];
				final double lFitprobability = lResult[1];

				System.out.println("lReturnCode=" + lReturnCode);
				System.out.println("lArgMax=" + lArgMax);
				System.out.println("lFitprobability=" + lFitprobability);
				System.out.println("lX=" + Arrays.toString(lX));
				System.out.println("lY=" + Arrays.toString(lY));
				System.out.println("lFittedY=" + Arrays.toString(lFittedY));

				assertTrue(lReturnCode == -2);
			}

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			fail();
		}
	}

	ByteBuffer getImageByteBuffer()
	{
		final double[] lImageArray = new double[ny * nx];

		for (int y = 0; y < ny; y++)
		{
			for (int x = 0; x < nx; x++)
			{
				lImageArray[x + nx * y] = x + y;
			}
		}

		final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(nx * ny
																															* 8);
		final DoubleBuffer lDoubleBuffer = lByteBuffer.asDoubleBuffer();
		lDoubleBuffer.clear();
		lDoubleBuffer.put(lImageArray);
		return lByteBuffer;
	}

	public static void main(String[] args)
	{
		new AutoPilotCTests().dcts2();
	}

}
