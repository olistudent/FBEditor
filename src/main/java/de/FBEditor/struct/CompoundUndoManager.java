package de.FBEditor.struct;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class CompoundUndoManager extends UndoManager implements UndoableEditListener, DocumentListener {
	class MyCompoundEdit extends CompoundEdit {

		private static final long serialVersionUID = 1L;

		public boolean isInProgress() {
			return false;
		}

		public void undo() throws CannotUndoException {
			if (compoundEdit != null)
				compoundEdit.end();
			super.undo();
			compoundEdit = null;
		}
	}

	private boolean startCombine = false;

	public CompoundUndoManager(JTextComponent editor) {
		this.editor = editor;
		editor.getDocument().addUndoableEditListener(this);
	}

	public void undo() {
		editor.getDocument().addDocumentListener(this);
		super.undo();
		editor.getDocument().removeDocumentListener(this);
	}

	public void redo() {
		editor.getDocument().addDocumentListener(this);
		super.redo();
		editor.getDocument().removeDocumentListener(this);
	}

	public void undoableEditHappened(UndoableEditEvent e) {

		if (compoundEdit == null) {
			compoundEdit = startCompoundEdit(e.getEdit());
			lastLength = editor.getDocument().getLength();
			startCombine = false;
			return;
		}

		if (e.getEdit() instanceof DefaultDocumentEvent) { // Java 9 / 10 05.05.2018
		
			 // Java 6 to 8
			DefaultDocumentEvent event = (DefaultDocumentEvent) e.getEdit();

			if (event.getType().equals(EventType.CHANGE)) {
				compoundEdit.addEdit(e.getEdit());
				return;
			}

			int offsetChange = editor.getCaretPosition() - lastOffset;
			int lengthChange = editor.getDocument().getLength() - lastLength;

			if (Math.abs(offsetChange) == 1 && Math.abs(lengthChange) == 1) {
				compoundEdit.addEdit(e.getEdit());
				lastOffset = editor.getCaretPosition();
				lastLength = editor.getDocument().getLength();
				return;
			} else {
				compoundEdit.end();
				compoundEdit = startCompoundEdit(e.getEdit());
				return;
			}

		} else { // Java 9 to 10 // 05.05.2018

			if (startCombine && !e.getEdit().isSignificant()) {
		        compoundEdit.addEdit(e.getEdit());
		        startCombine = false;
				lastOffset = editor.getCaretPosition();
				lastLength = editor.getDocument().getLength();
		        return;
			} else {
				compoundEdit.end();
				compoundEdit = startCompoundEdit(e.getEdit());
				return;
			}

		}

	}

	private CompoundEdit startCompoundEdit(UndoableEdit anEdit) {
		lastOffset = editor.getCaretPosition();
		lastLength = editor.getDocument().getLength();
		compoundEdit = new MyCompoundEdit();
		compoundEdit.addEdit(anEdit);
		addEdit(compoundEdit);
		return compoundEdit;
	}

	public void insertUpdate(final DocumentEvent e) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				int offset = e.getOffset() + e.getLength();
				offset = Math.min(offset, editor.getDocument().getLength());
				editor.setCaretPosition(offset);
			}
		});
	}

	public void removeUpdate(DocumentEvent e) {
		editor.setCaretPosition(e.getOffset());
	}

	public void pause() {
		editor.getDocument().removeUndoableEditListener(this);
	}

	public void resume() {
		editor.getDocument().addUndoableEditListener(this);
	}

	public void changedUpdate(DocumentEvent documentevent) {
	}
	
	public JTextComponent getEditor() {
		return this.editor;
	}

	/**
	 * Start to combine the next operations together.  Only the next operation is combined.
	 * The flag is then automatically reset.
	 */
	public void startCombine() {
		startCombine = true;
	}

	private static final long serialVersionUID = 1L;
	public CompoundEdit compoundEdit;
	private JTextComponent editor;
	private int lastOffset;
	private int lastLength;

}
