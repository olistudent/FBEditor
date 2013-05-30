package de.FBEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ActionListen implements ActionListener {
	
	private enum AC {
		EXIT("exit"), OPEN("open"), NEW("new"), SAVE("save"),
		RECONNECT("RECONNECT"), ABOUT("about"), CONFIG_READ("config_read"), 
		CONFIG_AUTO_READ("config_auto_read"), NOCHECKS("nochecks"), 
		CONFIG_WRITE("config_write"), HOST_IP("host_ip"), PASSWORD("password"),
		BOXINFO("boxinfo"), SEARCH("search"), REPLACE("replace"), CUT("cut"), 
		COPY("copy"), INSERT("insert"), DELETE("delete"), MARKALL("markall"),
		REVERT("revert"), RESTORE("restore"), NONE("none");
		
		private String command;
		
		AC(String command) {
			this.command = command;
		}
		
		public static AC fromString(String text) {
		    if (text != null) {
		      for (AC b : AC.values()) {
		        if (text.equalsIgnoreCase(b.command)) {
		          return b;
		        }
		      }
		    }
		    return NONE;
		  }
	}

	public ActionListen(FBEdit fbedit) {
		this.fbedit = fbedit;
	}

	public void actionPerformed(ActionEvent event) {
		CutAndPastePopup cutAndPaste = fbedit.getCutAndPaste();
		cutAndPaste.updateSource(fbedit.getEditor());
		
		/* This is an ugly hack because java versions prior to 7
		 * don't support strings in switch statement.
		 */
		AC MyAction = AC.fromString(event.getActionCommand());
		
		switch (MyAction) {
		case EXIT:
			fbedit.exit();
			break;
		case OPEN:
			fbedit.loadFile();
			break;
		case NEW:
			fbedit.newFile();
			break;
		case SAVE:
			fbedit.saveFile();
			break;
		case RECONNECT:
			FBEdit.makeNewConnection(false);
			break;
		case ABOUT:
			fbedit.about();
			break;
		case CONFIG_READ:
			fbedit.getFile();
			break;
		case CONFIG_AUTO_READ:
			fbedit.changeRAS();
		case NOCHECKS:
			fbedit.changeNoChecks();
			break;
		case CONFIG_WRITE:
			fbedit.putFile();
			break;
		case HOST_IP:
			fbedit.getHost(false);
			break;
		case PASSWORD:
			fbedit.getPassword(false);
			break;
		case BOXINFO:
			fbedit.showBoxInfo();
			break;
		case SEARCH:
			fbedit.search();
			break;
		case REPLACE:
			fbedit.replace();
			break;
		case CUT:
			cutAndPaste.cut();
			break;
		case COPY:
			cutAndPaste.copy();
			break;
		case INSERT:
			cutAndPaste.paste();
			break;
		case DELETE:
			cutAndPaste.delete();
			break;
		case MARKALL:
			cutAndPaste.markall();
			break;
		case REVERT:
			fbedit.undoredo(1);
			break;
		case RESTORE:
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
