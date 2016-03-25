package rtlib.core.math.kdtree.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rtlib.core.math.kdtree.KdTree;
import rtlib.core.math.kdtree.NearestNeighborIterator;
import rtlib.core.math.kdtree.SquareEuclideanDistanceFunction;

public class KDTreeTests
{

	@Test
	public void BasicTest()
	{
		KdTree<String> lKDTree = new KdTree<String>(3, 10);

		lKDTree.addPoint(new double[]
		{ 1, 2, 3 }, "A");

		lKDTree.addPoint(new double[]
		{ 2, 3, 4 }, "B");

		lKDTree.addPoint(new double[]
		{ 3, 4, 5 }, "C");

		NearestNeighborIterator<String> lNearestNeighborIterator = lKDTree.getNearestNeighborIterator(	new double[]
																										{	3,
																											4,
																											5 },
																										2,
																										new SquareEuclideanDistanceFunction());

		int i = 0;
		for (String lString : lNearestNeighborIterator)
		{
			System.out.println(lString);
			if (i == 0)
				assertEquals(lString, "C");
			else if (i == 1)
				assertEquals(lString, "B");
			i++;
		}

	}

}
