package autopilot.image;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ViewImage
{

	public static JLabel view(final Image pImage) throws IOException
	{
		final Icon icon = new ImageIcon(pImage);
		final JLabel label = new JLabel(icon);

		final JFrame f = new JFrame("Image Viewer");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(label);
		f.pack();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});
		return label;
	}

	public static JLabel update(final JLabel pJLabel, final Image pImage) throws IOException
	{
		if (pJLabel == null)
		{
			return view(pImage);
		}
		final Icon icon = new ImageIcon(pImage);
		pJLabel.setIcon(icon);
		return pJLabel;
	}

	public static void view(final File pFile) throws IOException
	{
		view(ImageIO.read(pFile));
	}

	public static void view(final DoubleArrayImage pDoubleArrayImage) throws IOException
	{
		final File lTempPngFile = File.createTempFile(	"ViewImage",
																											".png");
		System.out.println(lTempPngFile);
		lTempPngFile.deleteOnExit();
		
		pDoubleArrayImage.writePng(lTempPngFile);

		view(lTempPngFile);
	}
}
