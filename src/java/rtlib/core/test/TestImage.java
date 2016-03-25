package rtlib.core.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class TestImage
{

	public static int width = 549;
	public static int height = 1080;

	public static ByteBuffer loadRawImage() throws FileNotFoundException
	{
		try
		{
			final String lFileName = "dm.549x1080.raw";
			final URL resourceLocation = TestImage.class.getResource(lFileName);
			if (resourceLocation == null)
			{
				throw new FileNotFoundException(lFileName);
			}
			final File myFile = new File(resourceLocation.toURI());
			final FileInputStream lFileInputStream = new FileInputStream(myFile);
			final FileChannel lChannel = lFileInputStream.getChannel();
			final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect((int) lChannel.size())
														.order(ByteOrder.nativeOrder());
			lChannel.read(lByteBuffer);
			lFileInputStream.close();
			lByteBuffer.rewind();
			return lByteBuffer;
		}
		catch (final URISyntaxException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
