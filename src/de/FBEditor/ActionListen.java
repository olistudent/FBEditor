package de.FBEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ActionListen implements ActionListener {

	public ActionListen(FBEdit fbedit) {
		this.fbedit = fbedit;
	}

	public void actionPerformed(ActionEvent event) {
		CutAndPastePopup cutAndPaste = fbedit.getCutAndPaste();
		cutAndPaste.updateSource(fbedit.getEditor());
		
		switch (event.getActionCommand()) {
		case "exit":
			fbedit.exit();
			break;
		case "open":
			fbedit.loadFile();
			break;
		case "new":
			fbedit.newFile();
			break;
		case "save":
			fbedit.saveFile();
			break;
		case "reconnect":
			FBEdit.makeNewConnection(false);
			break;
		case "about":
			fbedit.about();
			break;
		case "config_read":
			fbedit.getFile();
			break;
		case "config_auto_read":
			fbedit.changeRAS();
			break;
		case "nochecks":
			fbedit.changeNoChecks();
			break;
		case "config_write":
			fbedit.putFile();
			break;
		case "host_ip":
			fbedit.getHost(false);
			break;
		case "password":
			fbedit.getPassword(false);
			break;
		case "username":
			fbedit.getUsername(false);
			break;
		case "boxinfo":
			fbedit.showBoxInfo();
			break;
		case "search":
			fbedit.search();
			break;
		case "replace":
			fbedit.replace();
			break;
		case "cut":
			cutAndPaste.cut();
			break;
		case "copy":
			cutAndPaste.copy();
			break;
		case "insert":
			cutAndPaste.paste();
			break;
		case "delete":
			cutAndPaste.delete();
			break;
		case "markall":
			cutAndPaste.markall();
			break;
		case "revert":
			fbedit.undoredo(1);
			break;
		case "restore":
			fbedit.undoredo(2);
			break;
		default:
			// TODO unknown action listener
			System.out.println("unknown action listener: " + event.getActionCommand());
			break;
		}
	}

	private FBEdit fbedit;
	public static final int addundo = 1;
	public static final int addredo = 2;
}
