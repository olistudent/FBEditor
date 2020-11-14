package de.FBEditor;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager; // Sorry nur für den Mac
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;

import de.FBEditor.struct.CompoundUndoManager;
import de.FBEditor.struct.ExampleFileFilter;
import de.FBEditor.struct.JTextPane2;
import de.FBEditor.struct.MyProperties;
import de.FBEditor.struct.OverwriteCaret;
import de.FBEditor.struct.SIDLogin;
import de.FBEditor.utils.CalcChecksum;
import de.FBEditor.utils.Debug;
import de.FBEditor.utils.Encryption;
import de.FBEditor.utils.Listener;
import de.FBEditor.utils.Utils;
import de.FBEditor.utils.upnp.UPNPUtils;

public class FBEdit extends JFrame implements Runnable

{
//	private static final String version = "0.7.2.1"; // 14.12.2014
//	private static final String version = "0.7.2.1c"; // 15.04.2015 "0.7.2.2" // 27.04.2018 "0.7.2.3" // 05.05.2018 Bug Fix Java 9/10 "0.7.2.3"
//	private static final String version = "0.7.2.1d"; // 22.06.2018 "0.7.2.4" language Italien
//	private static final String version = "0.7.2.1e"; // 23.06.2018 "0.7.2.5" Fixed typo error language Italien
//	private static final String version = "0.7.2.1g"; // 25.06.2018 "0.7.2.7" language setting manuell
//	private static final String version = "0.7.2.1h"; // 25.06.2018 "0.7.2.8" Program Start Dialog
//	private static final String version = "0.7.2.1i"; // 23.07.2020 "0.7.2.9" Fix Checksumme Firmware xxx.07.20
//	private static final String version = "0.7.2.1j"; // 25.07.2020 "0.7.2.10" Fix checksum fuehrende Null Fehlte
	private static final String version = "0.7.2.1k"; // 28.07.2020 "0.7.2.11" Dialog Update IP/Passwort/User/Kennwort/About

	private static String ProgramCreator = "Erwin G. (Pikachu)";
	private static String ProgramCreatorLink = "Forum: https://www.ip-phone-forum.de/threads/fbeditor.79513"
											+ "\nCode: https://github.com/olistudent/FBEditor"
											+ "\nCode: https://github.com/mypikachu/FBEditor";

	//private static String ProgramCreatorInfo = "Diese Software Version wurde erstellt von:";

	private static final String PROPERTIES_FILE = "FBEditor.properties.xml";

	public static FritzBoxConnection fbConnection = null;

	private static Caret overwriteCaret;
	private static Caret insertCaret;

	private static final long serialVersionUID = 1L;
	private static FBEdit INSTANCE = null;
	private static FritzBoxFirmware firmware;
	private static JTextPane2 pane;
	private String jFile = "";
	private static String box_address = "";
	private static String box_password = "";
	private static String box_username = "";
//	private static String box_ConfigImExPwd = "";
//	private static boolean box_isConfigImExPwdOk = false;
	private static String box_login_lua = "false"; // 25.06.2018 true or false Box Login Lua ab Firmware Version xxx.05.50
	private static String readOnStartup = "false";
	private static String NoChecks = "false";
	private static String language = "false";
	private static String language_manuell = "no"; // 25.06.2018 no or yes Setting Language Manuell
	private static String ProgramStartDialog = "false"; // 25.06.2018 true or false Program Start Dialog

	private static MyProperties properties;
	private final CompoundUndoManager undoManager;
	private static String progName = "Fritz!Box Export Editor";
	private String fileName = "";
	private boolean stoprequested;
	private static CutAndPastePopup cutAndPaste;
	private ActionListen action;
	private MyMenu myMenu;
	private static boolean insertMode = true;

	private static DocumentListener docListen;
	private FindReplace findReplace;
	private static JPopupMenu popup;
	Thread thread = new Thread(this);

	private static Vector<Locale> supported_languages;
	private static ResourceBundle messages;
	private static ResourceBundle en_messages;
	
	private boolean macos; // 27.04.2018

	public FBEdit() {
		
		String jvm_version = System.getProperty("java.version");

// 27.04.2018
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")){
		    //Betriebssystem ist Windows-basiert
			System.out.println("OS: " + os);
		}
		else if (os.contains("osx")){
		    //Betriebssystem ist Apple OSX
			System.out.println("OS: " + os);
			macos = true;
		}      
		else if (os.contains("nix") || os.contains("aix") || os.contains("nux")){
		    //Betriebssystem ist Linux/Unix basiert
			System.out.println("OS: " + os);
		}

// 27.04.2018
		if (macos){
			// Sorry nur für den Mac
			// Try to set a more native look and feel on supported platforms
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "AgentX");
			} catch (final Exception e) {
				//TODO catch exception
				e.printStackTrace();
			}
		}

		// Try to load and set properties
		properties = new MyProperties();
		Utils.createDefaultProperties(properties); // Set Properties Default Values // 22.02.2014
		boolean loadProp = Utils.loadProperties(properties, PROPERTIES_FILE);

		if (loadProp) {

			String position_top = properties.getProperty("position.top", "60");
			String position_left = properties.getProperty("position.left", "60");
			String position_height = properties.getProperty("position.height", "480");
			String position_width = properties.getProperty("position.width", "680");

			System.out.println("position_top: " + position_top);
			System.out.println("position_left: " + position_left);
			System.out.println("position_height: " + position_height);
			System.out.println("position_width: " + position_width);

//			box_ConfigImExPwd = properties.getProperty("box.ConfigImExPwd", "");
//			System.out.println("box.ConfigImExPwd: " + box_ConfigImExPwd);
/*
			System.out.println("box.address: " + box_address);
			System.out.println("box.password: " + box_password);
			System.out.println("box.username: " + box_username);
			System.out.println("readOnStartup: " + readOnStartup);
			System.out.println("NoChecks: " + NoChecks);
			System.out.println("language: " + language);
*/
			setLocation(Integer.parseInt(position_left.trim()),
					Integer.parseInt(position_top.trim()));
			setSize(Integer.parseInt(position_width.trim()),
					Integer.parseInt(position_height.trim()));

		} else {
			setLocation(60, 60);
			setSize(680, 480);
		}

		updateTitle();
		setIconImage(getImageFromJAR("/icon.gif"));

		setProperties(properties);

		// load supported languages
		loadLanguages();

		Debug.always("OS Language: " + System.getProperty("user.language"));
		Debug.always("OS Country: " + System.getProperty("user.country"));

//		language_manuell = "yes"; // 25.06.2018 Test
//		language = "es_ES"; // 25.06.2018 Test
//		language = "it_IT"; // 25.06.2018 Test

		if (language_manuell == null || language.length() != 5 || !language.contains("_")) language_manuell = "no"; // 25.06.2018

		if (language == null || (!language.equals(System.getProperty("user.language") + "_" + System.getProperty("user.country")) && language_manuell.equals("no"))) { // 22.06.2018
			Debug.info("No language set yet ... Setting language to OS language");
			// Check if language is supported. If not switch to English
			if (supported_languages.contains(new Locale(System
					.getProperty("user.language"), System
					.getProperty("user.country")))) {
				language = System.getProperty("user.language") + "_"
						+ System.getProperty("user.country");
			} else {
				Debug.warning("Your language ist not yet supported.");
				language = "en_US";
			}
		} else if (language_manuell.equals("yes")) { // 25.06.2018
			Debug.info("No language set yet ... Setting language to manuell yes");
			// Check if language is supported. If not switch to English
			Debug.always("language manuell: " + language.substring(0, 2) + " " + language.substring(3, 5));
			if (supported_languages.contains(new Locale(language.substring(0, 2), language.substring(3, 5)))) {
			} else {
				Debug.warning("Your language ist not yet supported.");
				language = "en_US";
			}
		}

		Debug.always("Selected language: " + language);
		Debug.always("Selected language setting manuell: " + language_manuell); // 25.06.2018

		loadMessages(new Locale(
				language.substring(0, language.indexOf("_")),
				language.substring(language.indexOf("_") + 1, language.length())));

		fileName = FBEdit.getMessage("main.unknown_file");
		
		Debug.always("Java version: " + jvm_version);

		Font font = null;
		try {
			font = Font.createFont( Font.TRUETYPE_FONT, getClass().getResourceAsStream( "/de/FBEditor/font/Consola.ttf") );
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont( font );
			System.out.println("Font: " + font.getName());
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!loadProp) {
			getHost(true);
			getPassword(true);
			getUsername(true); // 25.06.2018
		} else if (loadProp) { // 25.06.2018
			programStartDialog(loadProp);
		}

		pane = new JTextPane2();
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

	}

	private void setProperties(MyProperties properties) {
		box_address = properties.getProperty("box.address");
		box_password = Encryption.decrypt(properties.getProperty("box.password"));
		box_username = properties.getProperty("box.username");
		readOnStartup = properties.getProperty("readOnStartup");
		NoChecks = properties.getProperty("NoChecks");
		language = properties.getProperty("language");
		language_manuell = properties.getProperty("language.setting.manuell"); // 25.06.2018
		ProgramStartDialog = properties.getProperty("program.start.dialog"); // 25.06.2018
		box_login_lua = properties.getProperty("box.login.lua"); // 25.06.2018
	}

	// Dateiname im Titel und Cursor Position setzen
	public void run() {
		while (!stoprequested) {
			updateTitle();
			myMenu.setstatusMsg(pane);
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException ex) {
				Logger.getLogger(FBEdit.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
	}

	public void updateMenu(MyMenu myMenu) {
		myMenu.undo.setEnabled(undoManager.canUndo());
		myMenu.redo.setEnabled(undoManager.canRedo());
		if (pane.getSelectedText() != null) {
			myMenu.copy.setEnabled(true);
			myMenu.cut.setEnabled(true);
			myMenu.delete.setEnabled(true);
		} else {
			myMenu.copy.setEnabled(false);
			myMenu.cut.setEnabled(false);
			myMenu.delete.setEnabled(false);
		}
		Clipboard clipbd = getToolkit().getSystemClipboard();
		if (clipbd.getContents(this) != null)
			myMenu.insert.setEnabled(true);
		else
			myMenu.insert.setEnabled(false);
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
		//return;
	}

	public JTextComponent getEditor() {
		return this.undoManager.getEditor();
	}

	void newFile() {
		pane.setText("");
		undoManager.discardAllEdits();
	}

	public void exit() {
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
// Consolas Font Pack for Microsoft Visual Studio 2005 or 2008
// Download: http://www.microsoft.com/en-us/download/details.aspx?id=17879
		pane2.setFont(new Font("Consolas", 0, 16));
		/* Speedup */
		removeDocumentListener(pane2, docListen2);
		pane2.setText(FBEdit.getMessage("box.get_config"));
		EventQueue.invokeLater(new ImportData());
		fileName = "fritzbox.export";
		jFile = null;
	}

	// Editor mit Inhalt füllen
	public void setData(String data) {
		JTextPane2 pane2 = this.getJTextPane();
//		pane2.setText(""); // Absturz in Java 9/10 / 05.05.2018
// Consolas Font Pack for Microsoft Visual Studio 2005 or 2008
// Download: http://www.microsoft.com/en-us/download/details.aspx?id=17879
		pane2.setFont(new Font("Consolas", 0, 12));
		pane2.setEditable(false);
		undoManager.pause();
		pane2.setText(""); // 05.05.2018
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
		int response = JOptionPane.showConfirmDialog(this,
				FBEdit.getMessage("box.affirmation"),
				FBEdit.getMessage("settings.backup"), 0, 0);
		if (response == 0) {
			String text = CalcChecksum.replaceChecksum(this.getJTextPane()
					.getText());
			if (text.startsWith("**** ") && text.endsWith(" ****\n")) {
				/* NoChecks=yes einfügen */
				if (NoChecks.equals("true")) {
					int index = text.indexOf("**** CFGFILE:ar7.cfg");
					text = text.substring(0, index) + "NoChecks=yes" + '\n'
							+ text.substring(index);
					text = CalcChecksum.replaceChecksum(text); // Neue Checksumme mit NoChecks
				}
				boolean result = false;
				result = Utils.exportData(getframe(), getbox_address(), text);
				if (result) setData(new String(text)); // 27.04.2018
				if (result)
					JOptionPane.showMessageDialog(this,
							FBEdit.getMessage("box.restart"),
							FBEdit.getMessage("settings.backup"), 1);

				enableMenu(false);
			} else {
				JOptionPane.showMessageDialog(this,
						FBEdit.getMessage("box.settings_error"),
						FBEdit.getMessage("settings.backup"), 0);
			}
		}
	}

	void loadFile() {
		JTextPane2 pane2 = this.getJTextPane();
		JFileChooser chooser = new JFileChooser(".");
		ExampleFileFilter filter = new ExampleFileFilter("export");
		filter.setDescription(FBEdit.getMessage("export.file"));
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == 0) {
			fileName = chooser.getSelectedFile().getName();
			jFile = chooser.getSelectedFile().getAbsolutePath();
			try {
				FileInputStream fis = new FileInputStream(jFile);
				byte[] donnees = new byte[fis.available()];
				fis.read(donnees);
				setData(new String(donnees));
				fis.close();
			} catch (IOException e) {
				pane2.setText(FBEdit.getMessage("export.load.error"));
			}
			undoManager.discardAllEdits();
		}
	}

	void saveFile() {
		JTextPane2 pane2 = this.getJTextPane();
		JFileChooser chooser = new JFileChooser(".");
		ExampleFileFilter filter = new ExampleFileFilter("export");
		filter.setDescription(FBEdit.getMessage("export.file"));
		chooser.setFileFilter(filter);

		chooser.setSelectedFile(new File(fileName));
		int returnVal = chooser.showSaveDialog(this);

		// int javax.swing.JFileChooser.CANCEL_OPTION = 1 [0x1]
		// CANCEL_OPTION Return value if cancel is chosen.
		// int javax.swing.JFileChooser.APPROVE_OPTION = 0 [0x0]
		// APPROVE_OPTION Return value if approve (yes, ok) is chosen. 
		if (returnVal == JFileChooser.APPROVE_OPTION) {

        	fileName = chooser.getSelectedFile().getName();
    		jFile = chooser.getSelectedFile().getAbsolutePath();

	    	try {
		    	FileOutputStream fos = new FileOutputStream(jFile);
    			PrintStream pfos = new PrintStream(fos);
	    		String text = CalcChecksum.replaceChecksum(pane2.getText());
    			pfos.print(text);
	    		fos.close();
	    		setData(new String(text)); // 27.04.2018
    		} catch (IOException e) {
	    		JOptionPane.showMessageDialog(this.getframe(),
		    			FBEdit.getMessage("export.save.error"),
			    		FBEdit.getMessage("main.error"), 0);
    		}
	    	undoManager.discardAllEdits();

		}
	}

	void about() {
		JOptionPane.showMessageDialog(this,
				(new StringBuilder("Fritz!Box Export Editor ")).append(version).append("\n")
						.append("by Oliver Metz\n\n").append(FBEdit.getMessage("main.programcreatorinfo"))
						.append("\n").append(ProgramCreator).append("\n").append(ProgramCreatorLink)
						.append("\n\n").append(FBEdit.getMessage("main.thanks")).toString(),
				FBEdit.getMessage("menu.about"), 0, new ImageIcon(getImageFromJAR("/icon.gif")));
	}

	void showBoxInfo() {
		@SuppressWarnings("unused")
		BoxInfo hardware = new BoxInfo(firmware.getBoxName(),
				firmware.getFirmwareVersion(), firmware.getModFirmwareVersion());
	}

	void getHost(boolean first) {
		JTextField field = new JTextField(box_address);
		int retValue = JOptionPane.showConfirmDialog(this, field, FBEdit.getMessage("settings.host_ip"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		field.requestFocus();
		String new_box_address = new String(field.getText()).toString();
		if (retValue == JOptionPane.OK_OPTION && !new_box_address.equals(box_address)) {
			box_address = new_box_address;
		}

		if (!first)	makeNewConnection(first);
	}

	void getPassword(boolean first) {
		JPasswordField field = new JPasswordField(box_password);
		int retValue = JOptionPane.showConfirmDialog(this, field, FBEdit.getMessage("settings.password"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
//				JOptionPane.INFORMATION_MESSAGE);
		field.requestFocus();
		String newPass = new String(field.getPassword()).toString();
		if (retValue == JOptionPane.OK_OPTION && !newPass.equals(box_password)) {
			box_password = newPass;
		}

		if (!first)	makeNewConnection(first);
	}

	void getUsername(boolean first) {
		JTextField field = new JTextField(box_username);
		int retValue = JOptionPane.showConfirmDialog(this, field, FBEdit.getMessage("settings.username"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		field.requestFocus();
		String new_box_username = new String(field.getText()).toString();
		if (retValue == JOptionPane.OK_OPTION && !new_box_username.equals(box_username)) {
			box_username = new_box_username;
		}

		if (!first)	makeNewConnection(first);
	}
/*
	public void getConfigImExPwd(boolean first) {
		JTextField field = new JTextField(box_ConfigImExPwd);
		int retValue = JOptionPane.showConfirmDialog(this, field, FBEdit.getMessage("settings.ConfigImExPwd"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		field.requestFocus();
		String new_box_ConfigImExPwd = new String(field.getText()).toString();
		if (retValue == JOptionPane.OK_OPTION && !new_box_ConfigImExPwd.equals(box_ConfigImExPwd)) {
			box_ConfigImExPwd = new_box_ConfigImExPwd;
//			System.out.println("box_ConfigImExPwd: " + box_ConfigImExPwd + " -> " + retValue);
		} else if (retValue == JOptionPane.OK_OPTION) {	

		} else {	
			new_box_ConfigImExPwd = "";
		}
/*
		if (retValue == JOptionPane.OK_OPTION) {
			System.out.println("retValue: OK " + " -> " + retValue); // 0
		} else if (retValue == JOptionPane.CLOSED_OPTION) {
			System.out.println("retValue: CLOSED " + " -> " + retValue); // -1
		} else if (retValue == JOptionPane.CANCEL_OPTION) {
			System.out.println("retValue: Chancel " + " -> " + retValue); // 2
		}
		if (retValue == JOptionPane.OK_CANCEL_OPTION) {
			System.out.println("retValue: OK Chancel " + " -> " + retValue); // 2
		}
*/
/*		if (!"".equals(new_box_ConfigImExPwd)) {
			// box_isConfigImExPwdOk = true;
			setConfigImExPwdOk(true);
			System.out.println("new_box_ConfigImExPwd true: " + isConfigImExPwdOk());
		} else if ("".equals(new_box_ConfigImExPwd)) {
			// box_isConfigImExPwdOk = false;
			setConfigImExPwdOk(false);
			System.out.println("new_box_ConfigImExPwd false: " + isConfigImExPwdOk());
		}

		System.out.println("new_box_ConfigImExPwd: " + new_box_ConfigImExPwd + " -> " + isConfigImExPwdOk());

		if (!first) setbox_ConfigImExPwd(box_ConfigImExPwd);
	}
*/
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

	public void enableMenu2FA(boolean bool) { // 27.04.2018 2FA Active
		// After settings restore only reconnect is allowed
		myMenu.hardmenu.setEnabled(bool);
		myMenu.importcfg.setEnabled(false);
		myMenu.exportcfg.setEnabled(bool);
		myMenu.reconnect.setEnabled(true);
	}

	public static FBEdit getInstance() {
		if (INSTANCE == null)
			INSTANCE = new FBEdit();
		return INSTANCE;
	}

    public static void main(String[] s) throws InterruptedException {
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

		fbedit.thread.start(); // Korrektur Statuszeile geht sonst nicht

		//Debug.always("sleep: 0");
		TimeUnit.SECONDS.sleep(2);
		//Debug.always("sleep: 1");

		makeNewConnection(true);

	}

	public static void makeNewConnection(Boolean firstStart) {
		FBEdit.getInstance().disableMenu();

		fbConnection = new FritzBoxConnection(box_address, box_password, box_username);

		if (fbConnection.isConnected()) {
			firmware = fbConnection.getFirmware();
			if (firmware.getMajorFirmwareVersion() == 4
					|| firmware.getMajorFirmwareVersion() >= 5) { // ab Firmware xxx.05.xx / xxx.06.xx
				FBEdit.getInstance().enableMenu(true);
				// FBEdit.getInstance().setData(new String(FBEdit.getInstance().getupnp2FAsid())); // 27.04.2018
				// Bei setData hier haengt sich der Dialog auf
				FBEdit.getInstance().getupnp2FAsid(); // 22.06.2018
			} else {
				FBEdit.getInstance().enableMenu(false);
			}
			if (firstStart && Boolean.parseBoolean(readOnStartup))
				FBEdit.getInstance().getFile();
		} else {
			JOptionPane.showMessageDialog(INSTANCE,
					FBEdit.getMessage("box.not_found"),
					FBEdit.getMessage("main.error"), 0);
		}
	}

	public String getbox_address() {
		return box_address;
	}

	public String getbox_password() {
		return box_password;
	}

	public String getbox_username() {
		return box_username;
	}
/*
	public void setbox_ConfigImExPwd(String boxConfigImExPwd) {
	    box_ConfigImExPwd = boxConfigImExPwd;
	    properties.setProperty("box.ConfigImExPwd", box_ConfigImExPwd);
	    System.out.println("Set box.ConfigImExPwd: " + box_ConfigImExPwd);
	}

	public String getbox_ConfigImExPwd() {
//		box_ConfigImExPwd = properties.getProperty("box.ConfigImExPwd", "");
		return box_ConfigImExPwd;
	}
	public static boolean isConfigImExPwdOk() {
		return box_isConfigImExPwdOk;
	}

	public static void setConfigImExPwdOk(boolean ConfigImExPwdOk) {
		box_isConfigImExPwdOk = ConfigImExPwdOk;
	}
*/
	public MyMenu getMenu() {
		return myMenu;
	}

	public FBEdit getframe() {
		return this;
	}

	void updateTitle() {
		setTitle((new StringBuilder(String.valueOf(progName))).append(" ").append(version).append(" - ").append(fileName).toString());
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

	private static void addDocumentListener(JTextPane2 pane2,
			DocumentListener doclisten) {
		pane2.getDocument().addDocumentListener(doclisten);
	}

	private void removeDocumentListener(JTextPane2 pane2,
			DocumentListener docListen) {
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
		return Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(fileName));
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

	public String getLanguage() {
		return language;
	}

	// 25.06.2018 no or yes Setting Language Manuell
	public static void setLanguageManuell(String languageM) {
		language_manuell = languageM;
	}

	public String getLanguageManuellState() { // 25.06.2018
		return language_manuell;
	}

	// 25.06.2018 true or false Program Start Dialog
	public static void setProgramStartDialog(String PSD) {
		ProgramStartDialog = PSD;
	}

	public String getProgramStartDialogState() { // 25.06.2018
		return ProgramStartDialog;
	}

	void programStartDialog(boolean first) { // 25.06.2018
		if (ProgramStartDialog.equalsIgnoreCase("true")) { 
			if (first) {
				getHost(true);
				getPassword(true);
				getUsername(true);
//				getConfigImExPwd(true);
			}
		}
	}

	/* Program Start Dialog setzen */
	public void changeProgramStartDialog() { // 25.06.2018
		if (ProgramStartDialog.equalsIgnoreCase("true"))
			ProgramStartDialog = "false";
		else
			ProgramStartDialog = "true";

		myMenu.ProgramStartDialog.setState(Boolean.parseBoolean(ProgramStartDialog));
	}

	public String getBoxLoginLuaState() { // 25.06.2018
		return box_login_lua;
	}

	/* Box Login Lua setzen */
	public void changeBoxLoginLua() { // 25.06.2018
		if (box_login_lua.equalsIgnoreCase("true"))
			box_login_lua = "false";
		else
			box_login_lua = "true";

		myMenu.box_login_lua.setState(Boolean.parseBoolean(box_login_lua));
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

	private static void loadLanguages() {
		supported_languages = new Vector<Locale>();
		supported_languages.add(new Locale("de", "DE"));
		supported_languages.add(new Locale("en", "US"));
		supported_languages.add(new Locale("es", "ES"));
		supported_languages.add(new Locale("it", "IT")); // 22.06.2018

		/*
		 * supported_languages.add(new Locale("nl","NL"));
		 * supported_languages.add(new Locale("pl","PL"));
		 * supported_languages.add(new Locale("ru","RU"));
		 */
	}

	/**
	 * Loads resource messages
	 * 
	 * @param locale
	 */
	public static void loadMessages(Locale locale) {
		try {
			Debug.info("Loading locale: " + locale);
			en_messages = ResourceBundle.getBundle(
					"fbeditor", new Locale("en", "US"));//$NON-NLS-1$
			messages = ResourceBundle.getBundle("fbeditor", locale);//$NON-NLS-1$
		} catch (MissingResourceException e) {
			Debug.error("Can't find i18n resource! (\"fbeditor_" + locale + ".properties\")");//$NON-NLS-1$
			JOptionPane.showMessageDialog(null, progName
					+ " v"//$NON-NLS-1$
					+ version + "\n\nCannot find the language file \"fbeditor_"
					+ locale + ".properties\"!" + "\nProgram will exit!");//$NON-NLS-1$
		}
	}

	/**
	 * @param msg
	 * @return Returns an internationalized message.
	 */
	public static String getMessage(String msg) {
		String i18n = ""; //$NON-NLS-1$
		try {
			if (!messages.getString(msg).equals("")) {
				i18n = messages.getString(msg);
			} else {
				i18n = msg;
			}
		} catch (MissingResourceException e) {
			Debug.error("Can't find resource string for " + msg); //$NON-NLS-1$
			i18n = en_messages.getString(msg);
		}
		return i18n;
	}
	
	public String getupnp2FAsid() {

		String s2FA = "";

		if (SIDLogin.isSidLoginLua()) {

			s2FA = UPNPUtils.get2FAUPNP();
		
			if (s2FA.equals("1")) {
				FBEdit.getInstance().enableMenu2FA(true);
//				return  "Box Login " + FBEdit.getMessage("main.error") + "!" + " -> " + "2FA is Active" + "\n"; // Aktiv / Active / Activo / Attivo
				return  "Box Login " + FBEdit.getMessage("main.error") + "!" + " -> " + FBEdit.getMessage("main.2fa_is_active") + "\n" + "\n" + FBEdit.getMessage("main.2fa_info") + "\n"; // 25.06.2018
			}
		
		}
//		return "sid=" + UPNPUtils.getSIDUPNP() + "\n" + "2FA -> " + UPNPUtils.get2FAUPNP() + "\n";
		return "";
	}
}
