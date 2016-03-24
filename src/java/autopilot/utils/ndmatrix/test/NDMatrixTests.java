package autopilot.utils.ndmatrix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import autopilot.utils.ndmatrix.NDMatrix;

public class NDMatrixTests
{

	@Test
	public void basictest()
	{
		final NDMatrix<Integer> matrix = new NDMatrix<Integer>(3, 2, 1);

		assertEquals(3 * 2 * 1, matrix.getSize(), 0);

		{
			int lIndex = matrix.getIndex(1, 0, 0);
			System.out.println(lIndex);
			assertEquals(lIndex, 1, 0);
		}

		{
			int lIndex = matrix.getIndex(0, 1, 0);
			System.out.println(lIndex);
			assertEquals(lIndex, 3, 0);
		}
		{
			int lIndex = matrix.getIndex(0, 0, 1);
			System.out.println(lIndex);
			assertEquals(lIndex, 6, 0);
		}

	}

	@Test
	public void readwritetest()
	{
		final NDMatrix<Integer> matrix = new NDMatrix<Integer>(2, 1, 4);

		assertEquals(2 * 1 * 4, matrix.getSize(), 0);

		for (int x = 0; x < 2; x++)
		{
			for (int y = 0; y < 1; y++)
			{
				for (int z = 0; z < 4; z++)
				{
					matrix.set(x + y * z, x, y, z);
				}
			}
		}

		for (int x = 0; x < 2; x++)
		{
			for (int y = 0; y < 1; y++)
			{
				for (int z = 0; z < 4; z++)
				{
					assertEquals(x + y * z, matrix.get(x, y, z), 0);
				}
			}
		}

		assertEquals(0, matrix.clipAndGet(-1, -1, -1), 0);
		assertEquals(1, matrix.clipAndGet(2, 1, 4), 0);

	}
}
