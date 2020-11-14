package de.FBEditor;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.text.Element;

import de.FBEditor.struct.JTextPane2;

/**
 * Class implementing the menu items
 *
 */
public class MyMenu {

	public MyMenu(FBEdit fbedit) {
		this.fbedit = fbedit;
		this.action = fbedit.getActionListener();
		
	}

	JMenu createFileMenu() {
		JMenu ret = new JMenu(FBEdit.getMessage("menu.file"));
		ret.setMnemonic('D');
		JMenuItem mi = new JMenuItem(FBEdit.getMessage("menu.new"), 78);
		fbedit.setCtrlAccelerator(mi, 'N');
		mi.setActionCommand("new");
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem(FBEdit.getMessage("menu.open"), 102);
		fbedit.setCtrlAccelerator(mi, 'O');
		mi.setActionCommand("open");
		mi.addActionListener(action);
		ret.add(mi);
		exportcfg = new JMenuItem(FBEdit.getMessage("menu.config_read"), 115);
		fbedit.setCtrlAccelerator(exportcfg, 'E');
		exportcfg.setActionCommand("config_read");
		exportcfg.addActionListener(action);
		exportcfg.setEnabled(false);
		ret.add(exportcfg);
		ret.addSeparator();
		mi = new JMenuItem(FBEdit.getMessage("menu.config_save"), 112);
		fbedit.setCtrlAccelerator(mi, 'S');
		mi.setActionCommand("save");
		mi.addActionListener(action);
		ret.add(mi);
		importcfg = new JMenuItem(FBEdit.getMessage("menu.config_write"), 119);
		importcfg.setActionCommand("config_write");
		importcfg.addActionListener(action);
		importcfg.setEnabled(false);
		ret.add(importcfg);
		ret.addSeparator();
		reconnect = new JMenuItem(FBEdit.getMessage("menu.reconnect"));
		reconnect.setActionCommand("reconnect");
		reconnect.addActionListener(action);
		reconnect.setEnabled(false);
		ret.add(reconnect);
		ret.addSeparator();
		mi = new JMenuItem(FBEdit.getMessage("menu.exit"), 66);
		mi.setActionCommand("exit");
		mi.addActionListener(action);
		ret.add(mi);
		return ret;
	}

	JMenu createEditMenu() {
		JMenu ret = new JMenu(FBEdit.getMessage("menu.edit"));
		ret.setMnemonic('B');
		ret.addMenuListener(new EditMenuListener(fbedit));
		undo = new JMenuItem(FBEdit.getMessage("menu.revert"), 82);
		fbedit.setCtrlAccelerator(undo, 'Z');
		undo.setEnabled(false);
		undo.setActionCommand("revert");
		undo.addActionListener(action);
		ret.add(undo);
		redo = new JMenuItem(FBEdit.getMessage("menu.restore"), 87);
		fbedit.setCtrlAccelerator(redo, 'Y');
		redo.setActionCommand("restore");
		redo.addActionListener(action);
		redo.setEnabled(false);
		ret.add(redo);
		ret.addSeparator();
		cut = new JMenuItem(FBEdit.getMessage("menu.cut"), 117);
		fbedit.setCtrlAccelerator(cut, 'X');
		cut.setActionCommand("cut");
		cut.addActionListener(action);
		cut.setEnabled(false);
		ret.add(cut);
		copy = new JMenuItem(FBEdit.getMessage("menu.copy"), 75);
		fbedit.setCtrlAccelerator(copy, 'C');
		copy.setActionCommand("copy");
		copy.addActionListener(action);
		copy.setEnabled(false);
		ret.add(copy);
		insert = new JMenuItem(FBEdit.getMessage("menu.insert"), 105);
		fbedit.setCtrlAccelerator(insert, 'V');
		insert.setActionCommand("insert");
		insert.addActionListener(action);
		insert.setEnabled(false);
		ret.add(insert);
		delete = new JMenuItem(FBEdit.getMessage("menu.delete"), 76);
//		fbedit.setCtrlAccelerator(delete, 'L');
		delete.setActionCommand("delete");
		delete.addActionListener(action);
		delete.setEnabled(false);
		ret.add(delete);
		ret.addSeparator();
		JMenuItem mi = new JMenuItem(FBEdit.getMessage("menu.search"), 83);
		fbedit.setCtrlAccelerator(mi, 'F');
		mi.setActionCommand("search");
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem(FBEdit.getMessage("menu.replace"), 69);
		fbedit.setCtrlAccelerator(mi, 'R');
		mi.setActionCommand("replace");
		mi.addActionListener(action);
		ret.add(mi);
		return ret;
	}

	JMenu createHelpMenu() {
		JMenu ret = new JMenu(FBEdit.getMessage("menu.help"));
		ret.setMnemonic('H');
		hardmenu = new JMenuItem(FBEdit.getMessage("menu.boxinfo"), 72);
		hardmenu.setEnabled(false);
		ret.add(hardmenu);
		hardmenu.setActionCommand("boxinfo");
		hardmenu.addActionListener(action);
		JMenuItem mi = new JMenuItem(FBEdit.getMessage("menu.about"), 98);
		ret.add(mi);
		mi.setActionCommand("about");
		mi.addActionListener(action);
		return ret;
	}

	JMenu createConfigMenu() {
		JMenu ret = new JMenu(FBEdit.getMessage("menu.settings"));
		ret.setMnemonic('B');
		JMenuItem mi = new JMenuItem(FBEdit.getMessage("menu.host_ip"), 73);
		fbedit.setCtrlAccelerator(mi, 'I');
		mi.setActionCommand("host_ip");
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem(FBEdit.getMessage("menu.password"), 80);
		fbedit.setCtrlAccelerator(mi, 'P');
		mi.setActionCommand("password");
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem(FBEdit.getMessage("menu.username"), 85);
		fbedit.setCtrlAccelerator(mi, 'U');
		mi.setActionCommand("username");
		mi.addActionListener(action);
		ret.add(mi);
/*
		mi = new JMenuItem(FBEdit.getMessage("menu.configimexpwd"), 67);
//		fbedit.setCtrlAccelerator(mi, 'C');
		mi.setActionCommand("configimexpwd");
		mi.addActionListener(action);
		ret.add(mi);
*/
		// 25.06.2018
		box_login_lua = new JCheckBoxMenuItem(FBEdit.getMessage("box.login_lua"), Boolean.parseBoolean(fbedit.getBoxLoginLuaState()));
		box_login_lua.setActionCommand("box_login_lua");
		box_login_lua.addActionListener(action);
		ret.add(box_login_lua);
		readOnStartup = new JCheckBoxMenuItem(FBEdit.getMessage("menu.config_auto_read"), Boolean.parseBoolean(fbedit.getRASstate()));
		readOnStartup.setActionCommand("config_auto_read");
		readOnStartup.addActionListener(action);
		ret.add(readOnStartup);
		NoChecks = new JCheckBoxMenuItem(FBEdit.getMessage("menu.nochecks"), Boolean.parseBoolean(fbedit.getNoChecksState()));
		NoChecks.setActionCommand("nochecks");
		NoChecks.addActionListener(action);
		// Disable NoChecks because it doesn't work at the moment
//		NoChecks.setEnabled(false); // 17.02.2014
		ret.add(NoChecks);
		// 25.06.2018
		ProgramStartDialog = new JCheckBoxMenuItem(FBEdit.getMessage("menu.program_start_dialog"), Boolean.parseBoolean(fbedit.getProgramStartDialogState()));
		ProgramStartDialog.setActionCommand("program_start_dialog");
		ProgramStartDialog.addActionListener(action);
		ret.add(ProgramStartDialog);
		return ret;
	}

	JPanel createStatusBar() {
		statusMsg = new JLabel(" ");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(2));
		panel.add(statusMsg);
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}

	void setstatusMsg(JTextPane2 pane2) {
		int off = pane2.getCaretPosition();
		Element map = pane2.getDocument().getDefaultRootElement();
		int currLine = map.getElementIndex(off);
		int numLines = map.getElementCount();
		statusMsg.setText(' ' + FBEdit.getMessage("menu.line") + " " + (currLine + 1) + " / " + numLines);
	}

	JMenuItem undo;
	JMenuItem redo;
	JMenuItem exportcfg;
	JMenuItem importcfg;
	JMenuItem copy;
	JMenuItem cut;
	JMenuItem insert;
	JMenuItem delete;
	JMenuItem hardmenu;
	JMenuItem reconnect;
	JCheckBoxMenuItem box_login_lua; // 25.06.2018
	JCheckBoxMenuItem readOnStartup;
	JCheckBoxMenuItem NoChecks;
	JCheckBoxMenuItem ProgramStartDialog; // 25.06.2018
	JLabel statusMsg;
	private ActionListen action;
	private FBEdit fbedit;
}
