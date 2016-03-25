package autopilot.utils.rtlib.core.log.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;
import autopilot.utils.rtlib.core.gui.LineLimitedDocumentFilter;

class LogPanel extends JPanel
{
	static final int cMaxNumberOfLines = 500_000;

	private static final long serialVersionUID = 1L;
	private JTextArea mTextArea = null;
	private JScrollPane mPane = null;

	public LogPanel()
	{
		super();
		mTextArea = new JTextArea();

		((AbstractDocument) mTextArea.getDocument()).setDocumentFilter(new LineLimitedDocumentFilter(	mTextArea,
																										cMaxNumberOfLines));

		final DefaultCaret lDefaultCaret = (DefaultCaret) mTextArea.getCaret();
		lDefaultCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setLayout(new MigLayout("insets 0",
								"[4px,grow,fill]",
								"[20px,grow,fill]"));

		mPane = new JScrollPane(mTextArea);
		add(mPane, "cell 0 0,alignx left,aligny top");
		setVisible(true);
	}

	public void append(String data)
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{

				@Override
				public void run()
				{
					mTextArea.append(data);
					LogPanel.this.validate();
				}
			});
		}
		catch (InvocationTargetException | InterruptedException e)
		{
			e.printStackTrace();
		}

	}
}
