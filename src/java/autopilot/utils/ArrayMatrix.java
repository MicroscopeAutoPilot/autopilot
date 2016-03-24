package autopilot.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayMatrix<O> extends ArrayList<List<O>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2464738472120882612L;

	public ArrayMatrix()
	{
		super();
	}

	public ArrayMatrix(final ArrayMatrix<O> pMatrix)
	{
		super();
		for (final List<O> lList : pMatrix)
		{
			add(new ArrayList<O>(lList));
		}
	}

	public ArrayMatrix(final Collection<? extends List<O>> pC)
	{
		super(pC);
	}

	public ArrayMatrix(final int pInitialCapacity)
	{
		super(pInitialCapacity);
	}

	public void add2Point(final O p1, final O p2)
	{
		final List<O> lList = new ArrayList<O>();
		lList.add(p1);
		lList.add(p2);
		add(lList);
	}

	public ArrayList<O> flatten()
	{
		final ArrayList<O> lList = new ArrayList<O>();

		for (final List<O> lRow : this)
		{
			for (final O lValue : lRow)
			{
				lList.add(lValue);
			}
		}

		return lList;
	}

	public O get(final int pX, final int pY)
	{
		return this.get(pX).get(pY);
	}

	public int matrixSize()
	{
		int lCount = 0;
		for (final List<O> lLine : this)
		{
			for (final O lEntry : lLine)
			{
				lCount++;
			}
		}
		return lCount;
	}

}
