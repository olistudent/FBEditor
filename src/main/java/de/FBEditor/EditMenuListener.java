package de.FBEditor;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

class EditMenuListener implements MenuListener {

	public EditMenuListener(FBEdit fbedit) {
		this.fbedit = fbedit;
	}

	public void menuSelected(MenuEvent e) {
		fbedit.updateMenu(fbedit.getMenu());
	}

	public void menuCanceled(MenuEvent menuevent) {
	}

	public void menuDeselected(MenuEvent menuevent) {
	}

	private FBEdit fbedit;
}
