package de.FBEditor.struct;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class OverwriteCaret extends DefaultCaret {

	public OverwriteCaret() {
	}

	protected synchronized void damage(Rectangle r) {
		if (r != null)
			try {
				JTextComponent comp = getComponent();
				TextUI mapper = comp.getUI();
				Rectangle r2 = mapper.modelToView(comp, getDot() + 1);
				int width = r2.x - r.x;
				if (width == 0)
					width = 8;
				comp.repaint(r.x, r.y, width, r.height);
				x = r.x;
				y = r.y;
				this.width = width;
				height = r.height;
			} catch (BadLocationException badlocationexception) {
			}
	}

	public void paint(Graphics g) {
		if (isVisible())
			try {
				JTextComponent comp = getComponent();
				TextUI mapper = comp.getUI();
				Rectangle r1 = mapper.modelToView(comp, getDot());
				Rectangle r2 = mapper.modelToView(comp, getDot() + 1);
				g = g.create();
				g.setColor(comp.getForeground());
				g.setXORMode(comp.getBackground());
				int width = r2.x - r1.x;
				if (width == 0)
					width = 8;
				g.fillRect(r1.x, r1.y, width, r1.height);
				g.dispose();
			} catch (BadLocationException badlocationexception) {
			}
	}

	private static final long serialVersionUID = 1L;
	protected static final int MIN_WIDTH = 8;
}
