package de.FBEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ActionListen implements ActionListener {

	public ActionListen(FBEdit fbedit) {
		this.fbedit = fbedit;
	}

	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		CutAndPastePopup cutAndPaste = fbedit.getCutAndPaste();
		if (action.equals("Beenden"))
			fbedit.beenden();
		else if (action.equals("Öffnen"))
			fbedit.loadFile();
		else if (action.equals("Neu"))
			fbedit.newFile();
		else if (action.equals("Speichern"))
			fbedit.saveFile();
		else if (action.equals("Reconnect"))
			FBEdit.makeNewConnection(false);
		else if (action.equals("Über"))
			fbedit.about();
		else if (action.equals("Konfiguration einlesen"))
			fbedit.getFile();
		else if (action.equalsIgnoreCase("konfiguration automatisch einlesen"))
			fbedit.changeRAS();
		else if (action.equalsIgnoreCase("nochecks"))
			fbedit.changeNoChecks();
		else if (action.equalsIgnoreCase("konfiguration zurückspielen"))
			fbedit.putFile();
		else if (action.equals("Host / IP"))
			fbedit.getHost(false);
		else if (action.equals("Passwort"))
			fbedit.getPassword(false);
		else if (action.equals("Boxinfo"))
			fbedit.showBoxInfo();
		else if (action.equals("Suchen"))
			fbedit.search();
		else if (action.equals("Ersetzen"))
			fbedit.replace();
		else if (action.equals("Ausschneiden"))
			cutAndPaste.cut();
		else if (action.equals("Kopieren"))
			cutAndPaste.copy();
		else if (action.equals("Einfügen"))
			cutAndPaste.paste();
		else if (action.equals("Löschen"))
			cutAndPaste.delete();
		else if (action.equals("Alles markieren"))
			cutAndPaste.markall();
		else if (action.startsWith("Rückgängig"))
			fbedit.undoredo(1);
		else if (action.startsWith("Wiederherstellen"))
			fbedit.undoredo(2);
	}

	private FBEdit fbedit;
	public static final int addundo = 1;
	public static final int addredo = 2;
}
