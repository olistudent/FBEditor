package de.FBEditor.struct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

public class JTextPane2 extends JTextPane implements KeyListener {

	private static final long serialVersionUID = 1L;

	public JTextPane2() {
		wrap = false;
		myLineTerm = "\n";
		origLineTerm = "\n";
		maxCharacters = 0;
		insertMode = true;
	}

	public JTextPane2(boolean wrap) {
		this.wrap = false;
		myLineTerm = "\n";
		origLineTerm = "\n";
		maxCharacters = 0;
		insertMode = true;
		this.wrap = wrap;
	}

	public JTextPane2(StyledDocument doc) {
		super(doc);
		wrap = false;
		myLineTerm = "\n";
		origLineTerm = "\n";
		maxCharacters = 0;
		insertMode = true;
	}

	public boolean getScrollableTracksViewportWidth() {
		if (wrap)
			return super.getScrollableTracksViewportWidth();
		else
			return false;
	}

	public void setSize(Dimension d) {
		if (!wrap && d.width < getParent().getSize().width)
			d.width = getParent().getSize().width;
		super.setSize(d);
	}

	void setLineWrap(boolean wrap) {
		setVisible(false);
		this.wrap = wrap;
		setVisible(true);
	}

	String getLineTermName() {
		if ("\r".equals(myLineTerm))
			return "Mac";
		if ("\n".equals(myLineTerm))
			return "UNIX";
		if ("\r\n".equals(myLineTerm))
			return "DOS";
		else
			return "UNIX";
	}

	void setLineTerm(int lineTermConst) {
		switch (lineTermConst) {
		case 2: // '\002'
			myLineTerm = "\n";
			break;

		case 1: // '\001'
			myLineTerm = "\r";
			break;

		case 0: // '\0'
			myLineTerm = "\r\n";
			break;
		}
	}

	public void setLineTerm(String newLineTerm) {
		myLineTerm = newLineTerm;
	}

	public String getLineTerm() {
		return myLineTerm;
	}

	public void keyTyped(KeyEvent keyevent) {
	}

	public void keyPressed(KeyEvent keyevent) {
	}

	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode() == 155)
			toggleInsertMode();
	}

	public boolean isInsertMode() {
		return insertMode;
	}

	public void toggleInsertMode() {
		insertMode = !insertMode;
	}

	public void setInsertMode(boolean insertMode) {
		this.insertMode = insertMode;
	}
	
	public static void findText(JTextPane2 pane, String s) {
		highlight(pane, s, 0);
	}

	public static void findAll(JTextPane2 pane, String s) {
		highlight(pane, s, 1);
	}

	public static void replaceText(JTextPane2 pane, String s) {
		replace(pane, s, 0);
	}

	public static void replaceAll(JTextPane2 pane, String s) {
		replace(pane, s, 1);
	}
	
	public static void highlight(JTextComponent textComp, String pattern, int findAll) {
		MyHighlightPainter myHighlightPainter = new MyHighlightPainter(Color.yellow);
		
		removeHighlights(textComp);
		if (pattern.length() > 0)
			try {
				Highlighter hilite = textComp.getHighlighter();
				Document doc = textComp.getDocument();
				String text = doc.getText(0, doc.getLength());
				text = text.toUpperCase();
				pattern = pattern.toUpperCase();
				int pos = textComp.getCaretPosition();
				if ((pos = text.indexOf(pattern, pos)) != -1)
					if (findAll == 0) {
						hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
						textComp.setCaretPosition(pos + pattern.length());
						textComp.setSelectionStart(pos);
						textComp.setSelectionEnd(pos + pattern.length());
					} else {
						for (; (pos = text.indexOf(pattern, pos)) >= 0; pos += pattern.length())
							hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);

					}
			} catch (BadLocationException badlocationexception) {
			}
	}

	private static void replace(JTextComponent textComp, String pattern, int replaceAll) {
		removeHighlights(textComp);
		Document doc = textComp.getDocument();
		String text = "";
		try {
			text = doc.getText(0, doc.getLength());
			int pos = textComp.getCaretPosition();
			if (textComp.getSelectedText() != null) {
				String replacement = pattern;
				pattern = textComp.getSelectedText();
				if (replaceAll == 0) {
					textComp.replaceSelection(replacement);
				} else {
					textComp.replaceSelection(replacement);
					text = doc.getText(0, doc.getLength());
					for (; (pos = text.indexOf(pattern, pos)) >= 0; pos += replacement.length()) {
						textComp.setSelectionStart(pos);
						textComp.setSelectionEnd(pos + pattern.length());
						textComp.replaceSelection(replacement);
					}

				}
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	private static void removeHighlights(JTextComponent textComp) {
		MyHighlightPainter myHighlightPainter = new MyHighlightPainter(Color.yellow);
		Highlighter hilite = textComp.getHighlighter();
		javax.swing.text.Highlighter.Highlight hilites[] = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++)
			if (hilites[i].getPainter() instanceof MyHighlightPainter)
				hilite.removeHighlight(hilites[i]);
	}

	boolean wrap;
	public static final int DOS_LINE_END = 0;
	public static final int MACOS_LINE_END = 1;
	public static final int UNIX_LINE_END = 2;
	private String myLineTerm;
	private String origLineTerm;
	int maxCharacters;
	private boolean insertMode;
}
