package autopilot.utils.rtlib.core.gui;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DocumentFilter;

public class LineLimitedDocumentFilter extends DocumentFilter
{

	private static final int cCheckPeriod = 1_000_000_000;
	private final JTextArea area;
	private final int max;

	private volatile long mLastCheck = Long.MIN_VALUE;
	private final DefaultCaret mDefaultCaret;

	public LineLimitedDocumentFilter(	JTextArea pTextArea,
										int pMaxNumberOfLines)
	{
		this.area = pTextArea;
		this.max = pMaxNumberOfLines;

		mDefaultCaret = (DefaultCaret) pTextArea.getCaret();

	}

	@Override
	public void replace(FilterBypass fb,
						int offset,
						int length,
						String text,
						AttributeSet attrs) throws BadLocationException
	{
		super.replace(fb, offset, length, text, attrs);
		controlSize(fb);
	}

	@Override
	public void insertString(	FilterBypass pFb,
								int pOffset,
								String pString,
								AttributeSet pAttr) throws BadLocationException
	{
		super.insertString(pFb, pOffset, pString, pAttr);
		controlSize(pFb);
	}

	private void controlSize(FilterBypass fb) throws BadLocationException
	{
		final long lTimeNow = System.nanoTime();
		if (lTimeNow > mLastCheck + cCheckPeriod)
		{
			final int lines = area.getLineCount();
			if (lines > max)
			{
				final int lUpdatePolicy = mDefaultCaret.getUpdatePolicy();
				mDefaultCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
				final int linesToRemove = lines - max - 1;
				final int lengthToRemove = area.getLineStartOffset(linesToRemove);
				remove(fb, 0, lengthToRemove);
				mDefaultCaret.setUpdatePolicy(lUpdatePolicy);
				// System.out.println("triming...");
			}
			mLastCheck = lTimeNow;
		}

	}
}
