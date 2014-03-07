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
import javax.swing.KeyStroke;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;

import de.FBEditor.struct.CompoundUndoManager;
import de.FBEditor.struct.ExampleFileFilter;
import de.FBEditor.struct.JTextPane2;
import de.FBEditor.struct.MyProperties;
import de.FBEditor.struct.OverwriteCaret;
import de.FBEditor.utils.CalcChecksum;
import de.FBEditor.utils.Debug;
import de.FBEditor.utils.Encryption;
import de.FBEditor.utils.Listener;
import de.FBEditor.utils.Utils;

public class FBEdit extends JFrame implements Runnable

{
	private static final String version = "0.7";
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
	private static String box_username = "";
	private static String readOnStartup = "false";
	private static String NoChecks = "false";
	private static String language = "false";

	private static MyProperties properties;
	private final CompoundUndoManager undoManager;
	private static String progName = "Fritz!Box Export Editor";
	private String fileName = "";
	private boolean stoprequested = false;
	private static CutAndPastePopup cutAndPaste;
	private ActionListen action;
	private MyMenu myMenu;
	private static boolean insertMode = true;

	private static DocumentListener docListen;
	private FindReplace findReplace = null;
	private static JPopupMenu popup;
	Thread thread = new Thread(this);

	private static Vector<Locale> supported_languages;
	private static ResourceBundle messages;
	private static ResourceBundle en_messages;

	public FBEdit() {
		
		String jvm_version = System.getProperty("java.version");
		
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
			
			System.out.println("box.address: " + box_address);
			System.out.println("box.password: " + box_password);
			System.out.println("box.username: " + box_username);
			System.out.println("readOnStartup: " + readOnStartup);
			System.out.println("NoChecks: " + NoChecks);
			System.out.println("language: " + language);

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

		pane = new JTextPane2();

		setProperties(properties);

		// load supported languages
		loadLanguages();

		Debug.always("OS Language: " + System.getProperty("user.language"));
		Debug.always("OS Country: " + System.getProperty("user.country"));
		if (language == null || language.equals(false)) {
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
		}
		Debug.always("Selected language: " + language);

		loadMessages(new Locale(
				language.substring(0, language.indexOf("_")),
				language.substring(language.indexOf("_") + 1, language.length())));

		fileName = FBEdit.getMessage("main.unknown_file");
		
		Debug.always("Java version: " + jvm_version);

		Font font = null;
		try {
			font = Font.createFont( Font.TRUETYPE_FONT, getClass().getResourceAsStream( "/de/FBEditor/font/Consola.ttf") );
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont( font );
			System.out.println("Font: : " + font.getName());
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	}

	private void setProperties(MyProperties properties) {
		box_address = properties.getProperty("box.address");
		box_password = Encryption.decrypt(properties
				.getProperty("box.password"));
		box_username = properties.getProperty("box.username");
		readOnStartup = properties.getProperty("readOnStartup");
		NoChecks = properties.getProperty("NoChecks");
		language = properties.getProperty("language");
	}

	// Dateiname im Titel und Cursor Position setzen
	public void run() {
		while (!stoprequested) {
			updateTitle();
			myMenu.setstatusMsg(pane);
			try {
				Thread.sleep(200L);
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
		return;
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

		pane2.setText("");
		pane2.setFont(new Font("Consolas", 0, 12));
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
				}
				boolean result = false;
				result = Utils.exportData(getframe(), getbox_address(), text);
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
    		} catch (IOException e) {
	    		JOptionPane.showMessageDialog(this.getframe(),
		    			FBEdit.getMessage("export.save.error"),
			    		FBEdit.getMessage("main.error"), 0);
    		}
	    	undoManager.discardAllEdits();

		}
	}

	void about() {
		JOptionPane.showMessageDialog(
				this,
				(new StringBuilder("Fritz!Box Export Editor ")).append(version)
						.append("\n").append("by Oliver Metz\n\n")
						.append(FBEdit.getMessage("main.thanks")).toString(),
				FBEdit.getMessage("menu.about"), 0, new ImageIcon(
						getImageFromJAR("/icon.gif")));
	}

	void showBoxInfo() {
		@SuppressWarnings("unused")
		BoxInfo hardware = new BoxInfo(firmware.getBoxName(),
				firmware.getFirmwareVersion(), firmware.getModFirmwareVersion());
	}

	void getHost(boolean first) {
		String new_box_address = JOptionPane.showInputDialog(this,
				FBEdit.getMessage("settings.host_ip"), box_address);
		if (new_box_address != null && !new_box_address.equals(box_address)) {
			box_address = new_box_address;
		}
		
		if (!first)
			makeNewConnection(first);
	}

	void getPassword(boolean first) {
		JPasswordField field = new JPasswordField(box_password);
		JOptionPane.showConfirmDialog(this, field,
				FBEdit.getMessage("settings.password"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		field.requestFocus();
		String newPass = new String(field.getPassword());
		if (newPass != null && !newPass.equals(box_password)) {
			box_password = newPass;

			if (!first)
				makeNewConnection(first);
		}
	}
	
	void getUsername(boolean first) {
		String new_box_username = JOptionPane.showInputDialog(this,
				FBEdit.getMessage("settings.username"), box_username);
		if (new_box_username != null && !new_box_username.equals(box_username)) {
			box_username = new_box_username;
		}
		
		if (!first)
			makeNewConnection(first);
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

	public static void main(String[] s) {
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

		makeNewConnection(true);

	}

	public static void makeNewConnection(Boolean firstStart) {
		FBEdit.getInstance().disableMenu();

		fbConnection = new FritzBoxConnection(box_address, box_password, box_username);

		if (fbConnection.isConnected()) {
			firmware = fbConnection.getFirmware();
			if (firmware.getMajorFirmwareVersion() == 4
					|| firmware.getMajorFirmwareVersion() >= 5) // ab Firmware xxx.05.xx / xxx.06.xx
				FBEdit.getInstance().enableMenu(true);
			else
				FBEdit.getInstance().enableMenu(false);
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

	public MyMenu getMenu() {
		return myMenu;
	}

	public FBEdit getframe() {
		return this;
	}

	void updateTitle() {
		setTitle((new StringBuilder(String.valueOf(progName))).append(" - ")
				.append(fileName).toString());
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

		/*
		 * supported_languages.add(new Locale("it","IT"));
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
}
