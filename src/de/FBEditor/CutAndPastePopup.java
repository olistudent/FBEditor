package de.FBEditor;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import java.awt.event.*;

/**
 * Rightclick menu
 *
 */
public class CutAndPastePopup extends JComponent implements ClipboardOwner {

	public CutAndPastePopup(ActionListen action) {
		
		popupMenu = new JPopupMenu();
		popupMenu.setLabel("Edit");
		
		// hier noch nicht implementiert
		// revert = new JMenuItem("Rückgängig");
		cut = new JMenuItem("Ausschneiden");
		copy = new JMenuItem("Kopieren");
		insert = new JMenuItem("Einfügen");
		delete = new JMenuItem("Löschen");
		markall = new JMenuItem("Alles markieren");
		
		clipbd  = getToolkit().getSystemClipboard();
		// undoManager = fbedit.getUndoManager();

		cut.setActionCommand("cut");
		copy.setActionCommand("copy");
		insert.setActionCommand("insert");
		delete.setActionCommand("delete");
		markall.setActionCommand("markall");
		
		cut.addActionListener(action);
		copy.addActionListener(action);
		insert.addActionListener(action);
		delete.addActionListener(action);
		markall.addActionListener(action);
		
		// revert.addActionListener(this);
		// revert.setEnabled(false);
		// popupMenu.add(revert);
		// popupMenu.addSeparator();
		
		popupMenu.add(cut);
		popupMenu.add(copy);
		popupMenu.add(insert);
		popupMenu.add(delete);
		popupMenu.addSeparator();
		popupMenu.add(markall);
	}

	public void updateMenu() {
		if (source.getSelectedText() != null) {
			copy.setEnabled(true);
			cut.setEnabled(true);
			delete.setEnabled(true);
		} else {
			copy.setEnabled(false);
			cut.setEnabled(false);
			delete.setEnabled(false);
		}
		if (clipbd.getContents(this) != null)
			insert.setEnabled(true);
		else
			insert.setEnabled(false);
		// revert.setEnabled(undoManager.canUndo());
	}

	public void lostOwnership(Clipboard c, Transferable t) {
		selection = null;
	}
	
	public void updateSource(JTextComponent source) {
		this.source = source;
	}

	public void cut() {
		selection = source.getSelectedText();
		StringSelection clipString = new StringSelection(selection);
		clipbd.setContents(clipString, clipString);
		source.replaceSelection("");
		/* updateMenu(); */
	}

	public void copy() {
		selection = source.getSelectedText();
		StringSelection clipString = new StringSelection(selection);
		clipbd.setContents(clipString, clipString);
		/* updateMenu(); */
	}

	public void markall() {
		source.setSelectionStart(0);
		source.setSelectionEnd(source.getDocument().getLength());
		/* updateMenu(); */
	}

	public void paste() {
		Transferable clipData = clipbd.getContents(this);
		try {
			String clipString = (String) clipData.getTransferData(DataFlavor.stringFlavor);
			source.replaceSelection(clipString);
			/* updateMenu(); */
		} catch (Exception ex) {
			System.out.println("not String flavor");
		}
	}

	public void delete() {
		selection = source.getSelectedText();
		source.replaceSelection("");
		/* updateMenu(); */
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	private static final long serialVersionUID = 1L;
	private static JPopupMenu popupMenu;
	private Clipboard clipbd;
	private String selection;
	JMenuItem revert;
	JMenuItem cut;
	JMenuItem copy;
	JMenuItem insert;
	JMenuItem delete;
	JMenuItem markall;
	JTextComponent source;
	// CompoundUndoManager undoManager;
}
