package autopilot.stackanalysis.visualization;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ScatterPlot3D {

    //TODO: there is currently a problem with the JOGL depencency. To prevent tests from failing the whole visualization code is deactivated.

    /*private IFrame mFrame;
    private final String mTitle;
    private final int mWidth;
    private final int mHeight;
    private final Chart mChart;

    private Scatter mScatter;/**/

    public ScatterPlot3D(String pTitle, int pWidth, int pHeight) {
        super();
        /*
        mTitle = pTitle;
		mWidth = pWidth;
		mHeight = pHeight;
		mChart = AWTChartComponentFactory.chart(Quality.Advanced,
												"awt");
		mChart.addKeyController();
		mChart.addMouseController();
		/**/
    }

    public synchronized void ensureOpened() {
        /*
		try
		{
			if (mFrame == null)
				mFrame = mChart.getFactory()
								.newFrame(	mChart,
											new Rectangle(	mWidth,
															mHeight),
											mTitle);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
		/**/
    }

    public synchronized void set(ScatterPlot3DData pScatterPlot3DData) {
		/*
		try
		{
			final Coord3d[] lPointsArray = pScatterPlot3DData.mPoints.toArray(new Coord3d[pScatterPlot3DData.mPoints.size()]);
			final Color[] lColorArray = pScatterPlot3DData.mColors.toArray(new Color[pScatterPlot3DData.mColors.size()]);

			if (mScatter != null && mChart != null
				&& mChart.getScene() != null)
				mChart.getScene().remove(mScatter);
			mScatter = new Scatter(lPointsArray, lColorArray, 5);
			if (mChart != null)
			{
				mChart.getScene().add(mScatter);
				mChart.render();
			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
		/**/

    }

}
