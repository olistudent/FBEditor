package de.FBEditor.utils;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import de.FBEditor.struct.JTextPane2;
import de.FBEditor.*;

public class Listener {

	public static void addKeyListener(final FBEdit fbedit) {
		final JTextPane2 pane2 = fbedit.getJTextPane();
		final FindReplace findReplace = fbedit.getFindReplace();
		
		pane2.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent keyevent) {
			}

			public void keyPressed(KeyEvent keyevent) {
			}

			public void keyReleased(KeyEvent arg0) {
				int key = arg0.getKeyCode();

				/* Insert Mode */
				if (key == 155) {
					fbedit.toggleInsertMode();
					fbedit.selectCaret(pane2);
				}

				/* F3 - Weitersuchen */
				if (key == KeyEvent.VK_F3) {
					if (findReplace != null)
						findReplace.searchon();
				}
			}
		});
	}

	public static void addWinListener(final FBEdit fbedit) {
		fbedit.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent windowevent) {
			}

			public void windowClosing(WindowEvent e) {
				fbedit.beenden();
			}

			public void windowClosed(WindowEvent windowevent) {
			}

			public void windowIconified(WindowEvent windowevent) {
			}

			public void windowDeiconified(WindowEvent windowevent) {
			}

			public void windowActivated(WindowEvent windowevent) {
			}

			public void windowDeactivated(WindowEvent windowevent) {
			}
		});
	}

	public static DocumentListener myDocumentListener(final FBEdit fbedit) {
		DocumentListener myDocumentListener = new DocumentListener() {

			public void insertUpdate(DocumentEvent aEvent) {
				fbedit.updateMenu(fbedit.getMenu());
			}

			public void removeUpdate(DocumentEvent documentevent) {
			}

			public void changedUpdate(DocumentEvent aEvent) {
				fbedit.updateMenu(fbedit.getMenu());
			}
		};

		return myDocumentListener;
	}
	
	public static void addMouseListener(final FBEdit fbedit, final CutAndPastePopup cutAndPastePopup, final JPopupMenu popup ) {
	
	fbedit.getJTextPane().addMouseListener(new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				cutAndPastePopup.updateMenu((JTextComponent) e.getSource());
				popup.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				cutAndPastePopup.updateMenu((JTextComponent) e.getSource());
				popup.show((Component) e.getSource(), e.getX(), e.getY());
			}
		}
	});
	}
}
