package de.FBEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketTimeoutException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.CannotRedoException;

import de.FBEditor.struct.CompoundUndoManager;
import de.FBEditor.struct.ExampleFileFilter;
import de.FBEditor.struct.JTextPane2;
import de.FBEditor.struct.MyHighlightPainter;
import de.FBEditor.struct.MyProperties;
import de.FBEditor.struct.OverwriteCaret;
import de.FBEditor.utils.CalcChecksum;
import de.FBEditor.utils.Utils;
import de.FBEditor.utils.Listener;
import de.moonflower.jfritz.exceptions.InvalidFirmwareException;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;

public class FBEdit extends JFrame implements Runnable

{
	private static final String version = "0.5.3";
	private static final String PROPERTIES_FILE = "FBEditor.properties.xml";

	public static FritzBoxConnection fbConnection = null;

	private static Caret overwriteCaret;
	private static Caret insertCaret;

	private static final long serialVersionUID = 1L;
	private static FBEdit INSTANCE = null;
	private static FritzBoxFirmware firmware = null;
	private static JTextPane2 pane;
	private String jFile = "";
	private static String box_address = "";
	private static String box_password = "";
	private static String readOnStartup = "false";
	private static String NoChecks = "false";

	private static MyProperties properties;
	private final CompoundUndoManager undoManager;
	private final String progName = "Fritz!Box Export Editor";;
	private String fileName = "Bearbeiten";
	private boolean stoprequested = false;
	private static CutAndPastePopup cutAndPaste;
	private ActionListen action;
	private MyMenu myMenu;
	private static boolean insertMode = true;

	private static DocumentListener docListen;
	private FindReplace findReplace = null;
	private static JPopupMenu popup;


	public FBEdit() {

		setLocation(100, 100);
		setSize(600, 600);
		updateTitle();
		setIconImage(getImageFromJAR("/icon.gif"));

		pane = new JTextPane2();

		// Try to load and set properties
		properties = new MyProperties();
		boolean loadProp = Utils.loadProperties(properties, PROPERTIES_FILE);
		setProperties(properties);
		if (!(loadProp)) {
			getHost(true);
			getPassword(true);
		}


		undoManager = new CompoundUndoManager(pane);
		action = new ActionListen(this);
		cutAndPaste = new CutAndPastePopup(action);
		popup = cutAndPaste.getPopupMenu();

		// Create menu and status bar
		myMenu = new MyMenu(this);
		JMenuBar menubar = new JMenuBar();
		menubar.add(myMenu.createFileMenu());
		menubar.add(myMenu.createEditMenu());
		menubar.add(myMenu.createConfigMenu());
		menubar.add(myMenu.createHelpMenu());
		setJMenuBar(menubar);
		getContentPane().add(myMenu.createStatusBar(), "South");
		getContentPane().add(new JScrollPane(pane), "Center");

		pane.setEditable(true);
		pane.setAutoscrolls(true);
		pane.setLineTerm(System.getProperty("line.separator"));

		overwriteCaret = new OverwriteCaret();
		insertCaret = pane.getCaret();
		pane.setCaret(insertMode ? insertCaret : overwriteCaret);
		pane.setCaretPosition(0);

		(new Thread(this)).start();
	}

	private void setProperties(MyProperties properties) {
		box_address = properties.getProperty("box.address");
		box_password = Encryption.decrypt(properties.getProperty("box.password"));
		readOnStartup = properties.getProperty("readOnStartup");
		// NoChecks = properties.getProperty("NoChecks");
		NoChecks = "true";
	}

	// Dateiname im Titel und Cursor Position setzen
	public void run() {
		while (!stoprequested) {
			updateTitle();
			myMenu.setstatusMsg(pane);
			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateMenu(MyMenu myMenu) {
		myMenu.undo.setEnabled(undoManager.canUndo());
		myMenu.redo.setEnabled(undoManager.canRedo());
		if (pane.getSelectedText() != null) {
			myMenu.kopieren.setEnabled(true);
			myMenu.ausschneiden.setEnabled(true);
			myMenu.löschen.setEnabled(true);
		} else {
			myMenu.kopieren.setEnabled(false);
			myMenu.ausschneiden.setEnabled(false);
			myMenu.löschen.setEnabled(false);
		}
		Clipboard clipbd = getToolkit().getSystemClipboard();
		if (clipbd.getContents(this) != null)
			myMenu.einfügen.setEnabled(true);
		else
			myMenu.einfügen.setEnabled(false);
	}

	void undoredo(int addaction) {
		try {
			if (addaction == 1)
				undoManager.undo();
			else
				undoManager.redo();
			pane.requestFocus();
		} catch (CannotRedoException cre) {
			cre.printStackTrace();
		}
		updateMenu(myMenu);
		return;
	}

	void newFile() {
		pane.setText("");
		undoManager.discardAllEdits();
	}

	public void beenden() {
		stoprequested = true;
		Utils.saveProperties(PROPERTIES_FILE, this);
		dispose();
		System.exit(0);
	}

	void setCtrlAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, 2);
		mi.setAccelerator(ks);
	}

	// Konfiguration von Box holen
	void getFile() {
		JTextPane2 pane2 = this.getJTextPane();
		DocumentListener docListen2 = this.getDocListener();
		pane2.setFont(new Font("Courrier", 0, 16));
		/* Speedup */
		removeDocumentListener(pane2, docListen2);
		pane2.setText("Hole Konfiguration von Fritz!Box.\nBitte warten...");
		new ImportData();
		fileName = "fritzbox.export";
		jFile = null;
	}

	// Editor mit Inhalt füllen
	public void setData(String data) {
		JTextPane2 pane2 = this.getJTextPane();

		pane2.setFont(new Font("Courrier", 0, 12));
		pane2.setEditable(false);
		undoManager.pause();
		pane2.setText(data);
		pane2.setCaretPosition(0);
		undoManager.resume();
		undoManager.discardAllEdits();
		addDocumentListener(pane2, this.getDocListener());
		pane2.setEditable(true);
	}

	// Export auf die Box zurückspielen
	void putFile() {
		// Sicherheitsabfrage
		int response = JOptionPane.showConfirmDialog(this, "Sind sie sicher, dass sie die Konfiguration auf die Fritz!Box zur\374ckspielen wollen?\nDer Autor dieses Programms \374bernimmt keine Haftung f\374r defekte Boxen!!!", "Einstellungen wiederherstellen", 0, 0);
		if (response == 0) {
			String text = CalcChecksum.replaceChecksum(this.getJTextPane().getText());
			/* NoChecks=yes einfügen */
			if (NoChecks.equals("true")) {
				int index = text.indexOf("**** CFGFILE:ar7.cfg");
				text = text.substring(0, index) + "NoChecks=yes" + '\n' + text.substring(index);
			}
			if (text.startsWith("**** ") && text.endsWith(" ****\n")) {
				boolean result = false;
				try {
					result = Utils.exportData(getframe(), getbox_address(), text);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WrongPasswordException e) {
					e.printStackTrace();
				}
				if (result)
					JOptionPane.showMessageDialog(this, "Die Einstellungen wurden erfolgreich wiederhergestellt.\nDie Anlage startet jetzt neu.\n", "Einstellungen wiederherstellen", 1);

				enableMenu(false);
			} else {
				JOptionPane.showMessageDialog(this, "Die Konfiguration ist fehlerhaft und konnte nicht zur\374ckgespielt werden!", "Einstellungen wiederherstellen", 0);
			}
		}
	}

	void loadFile() {
		JTextPane2 pane2 = this.getJTextPane();
		JFileChooser chooser = new JFileChooser(".");
		ExampleFileFilter filter = new ExampleFileFilter("export");
		filter.setDescription("Export-Datei");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == 0) {
			fileName = chooser.getSelectedFile().getName();
			jFile = chooser.getSelectedFile().getAbsolutePath();
			try {
				FileInputStream fis = new FileInputStream(jFile);
				byte donnees[] = new byte[fis.available()];
				fis.read(donnees);
				setData(new String(donnees));
				fis.close();
			} catch (IOException e) {
				pane2.setText("Fehler: Datei konnte nicht geladen werden.");
			}
			undoManager.discardAllEdits();
		}
	}

	void saveFile() {
		JTextPane2 pane2 = this.getJTextPane();
		JFileChooser chooser = new JFileChooser(".");
		ExampleFileFilter filter = new ExampleFileFilter("export");
		filter.setDescription("Export-Datei");
		chooser.setFileFilter(filter);

		chooser.setSelectedFile(new File(fileName));
		chooser.showSaveDialog(this);

		fileName = chooser.getSelectedFile().getName();
		jFile = chooser.getSelectedFile().getAbsolutePath();

		try {
			FileOutputStream fos = new FileOutputStream(jFile);
			PrintStream pfos = new PrintStream(fos);
			String text = CalcChecksum.replaceChecksum(pane2.getText());
			pfos.print(text);
			fos.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this.getframe(), "Datei konnte nicht gespeichert werden.", "Fehler", 0);
		}
		undoManager.discardAllEdits();
	}

	void about() {
		JOptionPane.showMessageDialog(this, (new StringBuilder("Fritz!Box Export Editor ")).append(version).append("\n").append("by Oliver Metz\n\nFür ihren Beitrag Danke ich Enrik Berkhan, Andreas Bühmann \nund dem JFritz-Team\n").toString(), "\334ber",
				0, new ImageIcon(getImageFromJAR("/icon.gif")));
	}

	void showBoxInfo() {
		BoxInfo hardware = new BoxInfo(firmware.getBoxName(), firmware.getFirmwareVersion(), firmware.getModFirmwareVersion());
	}

	void getHost(boolean first) {
		String new_box_address = JOptionPane.showInputDialog(this, "Host / IP:", box_address);
		if (new_box_address != null && !new_box_address.equals(box_address)) {
			box_address = new_box_address;

			if (!first)
				makeNewConnection(first);
		}
	}

	void getPassword(boolean first) {
		JPasswordField field = new JPasswordField(box_password);
		JOptionPane.showConfirmDialog(this, field, "Passwort:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		field.requestFocus();
		String newPass = new String(field.getPassword());
		if (newPass != null && !newPass.equals(box_password)) {
			box_password = newPass;

			if (!first)
				makeNewConnection(first);
		}
	}

	public void enableMenu(boolean bool) {
		// After settings restore only reconnect is allowed
		myMenu.hardmenu.setEnabled(bool);
		myMenu.importcfg.setEnabled(bool);
		myMenu.exportcfg.setEnabled(bool);
		myMenu.reconnect.setEnabled(true);
	}

	public void disableMenu() {
		myMenu.hardmenu.setEnabled(false);
		myMenu.importcfg.setEnabled(false);
		myMenu.exportcfg.setEnabled(false);
		myMenu.reconnect.setEnabled(false);
	}

	public static FBEdit getInstance() {
		if (INSTANCE == null)
			INSTANCE = new FBEdit();
		return INSTANCE;
	}

	public static void main(String s[]) {
		FBEdit fbedit = new FBEdit();

		// Add document, window and key listener
		docListen = Listener.myDocumentListener(fbedit);
		addDocumentListener(pane, docListen);
		Listener.addWinListener(fbedit);
		Listener.addKeyListener(fbedit);
		Listener.addMouseListener(fbedit, cutAndPaste, popup);

		fbedit.setVisible(true);

		INSTANCE = fbedit;

		// TODO
		// Debug.on();

		makeNewConnection(true);

	}

	public static void makeNewConnection(Boolean firstStart) {
		try {
			FBEdit.getInstance().disableMenu();

			fbConnection = new FritzBoxConnection(box_address, box_password);

			if (fbConnection.isConnected()) {
				firmware = fbConnection.getFirmware();
				if (firmware.getMajorFirmwareVersion() == 4 || firmware.getMajorFirmwareVersion() == 5)
					FBEdit.getInstance().enableMenu(true);
				else
					FBEdit.getInstance().enableMenu(false);
				if (firstStart && Boolean.parseBoolean(readOnStartup))
					FBEdit.getInstance().getFile();
			}
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(INSTANCE, "Fritz!Box wurde nicht gefunden.\nBitte die Einstellungen prüfen.", "Fehler", 0);
		} catch (InvalidFirmwareException e) {
			e.printStackTrace();
		}
	}

	public String getbox_address() {
		return box_address;
	}

	public String getbox_password() {
		return box_password;
	}

	public MyMenu getMenu() {
		return myMenu;
	}

	public FBEdit getframe() {
		return this;
	}

	void updateTitle() {
		setTitle((new StringBuilder(String.valueOf(progName))).append(" - ").append(fileName).toString());
	}

	void search() {
		findReplace = new FindReplace(this, 1);
	}

	void replace() {
		findReplace = new FindReplace(this, 2);
	}

	public JTextPane2 getJTextPane() {
		return pane;
	}

	CompoundUndoManager getUndoManager() {
		return undoManager;
	}

	private static void addDocumentListener(JTextPane2 pane2, DocumentListener doclisten) {
		pane2.getDocument().addDocumentListener(doclisten);
	}

	private void removeDocumentListener(JTextPane2 pane2, DocumentListener docListen) {
		pane2.getDocument().removeDocumentListener(docListen);
	}

	public static boolean isInsertMode() {
		return insertMode;
	}

	public void toggleInsertMode() {
		insertMode = !insertMode;
	}

	public static void setInsertMode(boolean insertM) {
		insertMode = insertM;
	}

	public void selectCaret(JTextPane2 pane2) {
		Caret newCaret = insertMode ? insertCaret : overwriteCaret;
		if (newCaret != pane2.getCaret()) {
			Caret caret = pane2.getCaret();
			int mark = caret.getMark();
			int dot = caret.getDot();
			caret.setVisible(false);
			pane2.setCaret(newCaret);
			newCaret.setDot(mark);
			newCaret.moveDot(dot);
			newCaret.setVisible(true);
		}
	}

	public void setCaret(Caret caret) {
		insertCaret = caret;
	}

	public void setOverwriteCaret(Caret caret) {
		overwriteCaret = caret;
	}

	public Caret getOverwriteCaret() {
		return overwriteCaret;
	}

	public Image getImageFromJAR(String fileName) {
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource(fileName));
	}

	/* readOnStartup lesen */
	public String getRASstate() {
		return readOnStartup;
	}


	/* readOnStartup setzen */
	public void changeRAS() {
		if (readOnStartup.equalsIgnoreCase("true"))
			readOnStartup = "false";
		else
			readOnStartup = "true";
		myMenu.readOnStartup.setState(Boolean.parseBoolean(readOnStartup));
	}

	public String getNoChecksState() {
		return NoChecks;
	}


	/* NoChecks setzen */
	public void changeNoChecks() {
		if (NoChecks.equalsIgnoreCase("true"))
			NoChecks = "false";
		else
			NoChecks = "true";

		myMenu.NoChecks.setState(Boolean.parseBoolean(NoChecks));
	}

	public FindReplace getFindReplace() {
		return findReplace;
	}

	private DocumentListener getDocListener() {
		return docListen;
	}

	public ActionListen getActionListener() {
		return action;
	}

	public CutAndPastePopup getCutAndPaste() {
		return cutAndPaste;
	}
}
