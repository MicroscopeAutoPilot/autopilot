package autopilot.stackanalysis;

public class OrientationTransform
{

	// 0->id, 1-> cw90, 2->ccw90
	int mId;

	public static OrientationTransform getFromInt(int pOrientationId)
	{
		return new OrientationTransform(pOrientationId);
	}

	public OrientationTransform(int pId)
	{
		super();
		mId = pId;
	}

	public double transformX(	double pWidth,
														double pHeight,
														double pX,
														double pY)
	{
		switch (mId)
		{
		case 0:
			return pX;
		case 1:
			return pY;
		case 2:
			return pHeight - pY;
		}
		return 0;
	}

	public double transformY(	double pWidth,
														double pHeight,
														double pX,
														double pY)
	{
		switch (mId)
		{
		case 0:
			return pY;
		case 1:
			return pWidth - pX;
		case 2:
			return pX;
		}
		return 0;
	}

	public int transformWidth(int pImageWidth, int pImageHeight)
	{
		switch (mId)
		{
		case 0:
			return pImageWidth;
		case 1:
			return pImageHeight;
		case 2:
			return pImageHeight;
		}
		return 0;
	}

	public int transformHeight(int pImageWidth, int pImageHeight)
	{
		switch (mId)
		{
		case 0:
			return pImageHeight;
		case 1:
			return pImageWidth;
		case 2:
			return pImageWidth;
		}
		return 0;
	}

	@Override
	public String toString()
	{
		return String.format("OrientationTransform [mId=%s]", mId);
	}

}
