package de.FBEditor;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.text.Element;

import de.FBEditor.struct.*;

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
		JMenu ret = new JMenu("Datei");
		ret.setMnemonic('D');
		JMenuItem mi = new JMenuItem("Neu", 78);
		fbedit.setCtrlAccelerator(mi, 'N');
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem("\326ffnen", 102);
		fbedit.setCtrlAccelerator(mi, 'O');
		mi.addActionListener(action);
		ret.add(mi);
		exportcfg = new JMenuItem("Konfiguration einlesen", 115);
		fbedit.setCtrlAccelerator(exportcfg, 'E');
		exportcfg.addActionListener(action);
		exportcfg.setEnabled(false);
		ret.add(exportcfg);
		ret.addSeparator();
		mi = new JMenuItem("Speichern", 112);
		fbedit.setCtrlAccelerator(mi, 'S');
		mi.addActionListener(action);
		ret.add(mi);
		importcfg = new JMenuItem("Konfiguration zurückspielen", 119);
		importcfg.addActionListener(action);
		importcfg.setEnabled(false);
		ret.add(importcfg);
		ret.addSeparator();
		reconnect = new JMenuItem("Reconnect");
		reconnect.addActionListener(action);
		reconnect.setEnabled(false);
		ret.add(reconnect);
		ret.addSeparator();
		mi = new JMenuItem("Beenden", 66);
		mi.addActionListener(action);
		ret.add(mi);
		return ret;
	}

	JMenu createEditMenu() {
		JMenu ret = new JMenu("Bearbeiten");
		ret.setMnemonic('B');
		ret.addMenuListener(new EditMenuListener(fbedit));
		undo = new JMenuItem("Rückgängig", 82);
		fbedit.setCtrlAccelerator(undo, 'Z');
		undo.setEnabled(false);
		undo.addActionListener(action);
		ret.add(undo);
		redo = new JMenuItem("Wiederherstellen", 87);
		fbedit.setCtrlAccelerator(redo, 'Y');
		redo.addActionListener(action);
		redo.setEnabled(false);
		ret.add(redo);
		ret.addSeparator();
		ausschneiden = new JMenuItem("Ausschneiden", 117);
		fbedit.setCtrlAccelerator(ausschneiden, 'X');
		ausschneiden.addActionListener(action);
		ausschneiden.setEnabled(false);
		ret.add(ausschneiden);
		kopieren = new JMenuItem("Kopieren", 75);
		fbedit.setCtrlAccelerator(kopieren, 'C');
		kopieren.addActionListener(action);
		kopieren.setEnabled(false);
		ret.add(kopieren);
		einfügen = new JMenuItem("Einf\374gen", 105);
		fbedit.setCtrlAccelerator(einfügen, 'V');
		einfügen.addActionListener(action);
		einfügen.setEnabled(false);
		ret.add(einfügen);
		löschen = new JMenuItem("L\366schen", 105);
		fbedit.setCtrlAccelerator(löschen, 'V');
		löschen.addActionListener(action);
		löschen.setEnabled(false);
		ret.add(löschen);
		ret.addSeparator();
		JMenuItem mi = new JMenuItem("Suchen", 83);
		fbedit.setCtrlAccelerator(mi, 'F');
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem("Ersetzen", 69);
		fbedit.setCtrlAccelerator(mi, 'R');
		mi.addActionListener(action);
		ret.add(mi);
		return ret;
	}

	JMenu createHelpMenu() {
		JMenu ret = new JMenu("Hilfe");
		ret.setMnemonic('H');
		hardmenu = new JMenuItem("Boxinfo", 72);
		hardmenu.setEnabled(false);
		ret.add(hardmenu);
		hardmenu.addActionListener(action);
		JMenuItem mi = new JMenuItem("\334ber", 98);
		ret.add(mi);
		mi.addActionListener(action);
		return ret;
	}

	JMenu createConfigMenu() {
		JMenu ret = new JMenu("Einstellungen");
		ret.setMnemonic('B');
		JMenuItem mi = new JMenuItem("Host / IP", 72);
		fbedit.setCtrlAccelerator(mi, 'H');
		mi.addActionListener(action);
		ret.add(mi);
		mi = new JMenuItem("Passwort", 80);
		fbedit.setCtrlAccelerator(mi, 'P');
		mi.addActionListener(action);
		ret.add(mi);
		readOnStartup = new JCheckBoxMenuItem("Konfiguration automatisch einlesen", Boolean.parseBoolean(fbedit.getRASstate()));
		readOnStartup.addActionListener(action);
		ret.add(readOnStartup);
		NoChecks = new JCheckBoxMenuItem("NoChecks", Boolean.parseBoolean(fbedit.getNoChecksState()));
		NoChecks.addActionListener(action);
		// Disable NoChecks because it doesn't work at the moment
		NoChecks.setEnabled(false);
		ret.add(NoChecks);
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
		statusMsg.setText(' ' + "Zeile " + (currLine + 1) + " / " + numLines);
	}

	JMenuItem undo;
	JMenuItem redo;
	JMenuItem exportcfg;
	JMenuItem importcfg;
	JMenuItem kopieren;
	JMenuItem ausschneiden;
	JMenuItem einfügen;
	JMenuItem löschen;
	JMenuItem hardmenu;
	JMenuItem reconnect;
	JCheckBoxMenuItem readOnStartup;
	JCheckBoxMenuItem NoChecks;
	JLabel statusMsg;
	private ActionListen action;
	private FBEdit fbedit;
}
