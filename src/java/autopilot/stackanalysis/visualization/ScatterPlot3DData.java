package autopilot.stackanalysis.visualization;

import java.util.ArrayList;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

public class ScatterPlot3DData
{

	protected ArrayList<Coord3d> mPoints = new ArrayList<>();
	protected ArrayList<Color> mColors = new ArrayList<>();

	public void addPoint(	double pX,
												double pY,
												double pZ,
												double r,
												double g,
												double b,
												double a)
	{
		mPoints.add(new Coord3d(pX, pY, pZ));
		mColors.add(new Color((float) r, (float) g, (float) b, (float) a));
	}

	public void clear()
	{
		mPoints.clear();
		mColors.clear();
	}

}
