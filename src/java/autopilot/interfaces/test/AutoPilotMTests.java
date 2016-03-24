package autopilot.interfaces.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import autopilot.interfaces.AutoPilotM;
import autopilot.measures.dcts.dcts3d.DCTS3D;

public class AutoPilotMTests
{
	// 1180 x 748 x 130 1180, 748, 130
	final static int nx = 1180;
	final static int ny = 748;
	final static int nz = 130;

	@Test
	public void dcts2()
	{
		final double[][] lImageArray = new double[ny][nx];

		for (int y = 0; y < ny; y++)
		{
			for (int x = 0; x < nx; x++)
			{
				lImageArray[y][x] = x + y;
			}
		}

		final double dcts2 = AutoPilotM.dcts2(lImageArray, 3);

		System.out.println(dcts2);
	}

	@Test
	public void dcts3()
	{
		final double[][][] lImageArray = new double[nz][ny][nx];
		final double[] lSingleImageArray = new double[nx * ny * nz];

		for (int z = 0; z < nz; z++)
		{
			for (int y = 0; y < ny; y++)
			{
				for (int x = 0; x < nx; x++)
				{
					final double lValue = x + y + z;
					final int lIndex = z * ny * nx + y * nx + x;
					lSingleImageArray[lIndex] = lValue;
					lImageArray[z][y][x] = lValue;
				}
			}
		}

		final double dcts3a = AutoPilotM.dcts3(lImageArray, 3, 3);
		final double dcts3b = DCTS3D.dcts3d(lSingleImageArray,
																				nx,
																				ny,
																				nz,
																				3,
																				3);
		System.out.println(dcts3a);
		System.out.println(dcts3b);
		assertEquals(dcts3a, dcts3b, 0.000001);

	}

	@Test
	public void dcts3Double()
	{
		final Double[][][] lImageArray = new Double[nz / 2][ny / 2][nx / 2];

		for (int z = 0; z < nz / 2; z++)
		{
			for (int y = 0; y < ny / 2; y++)
			{
				for (int x = 0; x < nx / 2; x++)
				{
					final double lValue = x + y + z;
					lImageArray[z][y][x] = lValue;
				}
			}
		}

		final double dcts3a = AutoPilotM.dcts3(lImageArray, 3, 3);
		System.out.println(dcts3a);

	}

	@Test
	public void tenengrad2()
	{
		final double[][] lImageArray = new double[ny][nx];

		for (int y = 0; y < ny; y++)
		{
			for (int x = 0; x < nx; x++)
			{
				lImageArray[y][x] = x + y;
			}
		}

		final double tenengrad2 = AutoPilotM.tenengrad2(lImageArray, 3);

		System.out.println(tenengrad2);
	}
}
