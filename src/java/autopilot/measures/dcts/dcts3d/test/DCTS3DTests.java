package autopilot.measures.dcts.dcts3d.test;

import org.junit.Test;

import autopilot.measures.dcts.dcts3d.DCTS3D;

public class DCTS3DTests
{

	@Test
	public void test()
	{
		final int lWidth = 1000;
		final int lHeight = 1000;
		final int lDepth = 100;

		final double[] lImageArray = new double[lWidth * lHeight * lDepth];

		for (int i = 0; i < lDepth; i++)
		{
			lImageArray[i] = Math.random();
		}

		final double lDcts3d = DCTS3D.dcts3d(	lImageArray,
																					lWidth,
																					lHeight,
																					lDepth,
																					3,
																					3);

		System.out.println("lDcts3d=" + lDcts3d);

	}

}
