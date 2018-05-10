/*
 * Created on 20.05.2005
 *
 */
package de.FBEditor.utils;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Write debug messages to STDOUT or FILE. Show Error-Dialog with a special
 * message
 * 
 * 14.05.06 Added support for redirecting System.out and System.err Now all
 * exceptions are also included in the debug file Brian Jensen
 * 
 * @author Robert Palmer
 * 
 */
public class Debug {
	public static final LogSeverity LS_ALWAYS = new LogSeverity(0, "LS_ALWAYS", "");
	public static final LogSeverity LS_ERROR = new LogSeverity(1, "LS_ERROR", ": ERROR");
	public static final LogSeverity LS_WARNING = new LogSeverity(2, "LS_WARNING", ": WARNING");
	public static final LogSeverity LS_NETWORK = new LogSeverity(3, "LS_NETWORK", ": NETWORK");
	public static final LogSeverity LS_INFO = new LogSeverity(4, "LS_INFO", ": INFO");
	public static final LogSeverity LS_DEBUG = new LogSeverity(5, "LS_DEBUG", ": DEBUG");
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 9211082107025215527L;

	private static LogSeverity debugLevel;
	private static String debugLogFile;

	private static boolean verboseMode = false;

	private static PrintStream fileRedirecter, originalOut;
	
	private static JPanel main_panel = null;

	private static JTextArea log_area; 
	
	private static JButton close_button;
	private static JButton save_button;
	private static JButton refresh_button;
	private static JComboBox<?> log_severity_box;

	/**
	 * Turns debug-mode on
	 * 
	 */
	public static void on() {
		verboseMode = false;
		Debug.debugLevel = LS_DEBUG;
		//logToFile("debug.log");
	}

	/**
	 * This function works by redirecting System.out and System.err to fname The
	 * original console stream is saved as originalout 15.05.06 Brian Jensen
	 * 
	 * Turn on logging mode to file
	 * 
	 * @param fName
	 *            Filename to log into
	 */
	public static void logToFile(final String fName) {
		debugLogFile = fName;
		
		// Save the original outputstream so we can write to the console too!
		originalOut = System.out;
				
		try {
			// setup the redirection of Sysem.out and System.err
			FileOutputStream tmpOutputStream = new FileOutputStream(
					debugLogFile);
			fileRedirecter = new PrintStream(tmpOutputStream);
			System.setOut(fileRedirecter);
			System.setErr(fileRedirecter);
		}

		catch (Exception e) {
			System.err.println("EXCEPTION when writing to LOGFILE"); //$NON-NLS-1$ 
		}
	}

	/**
	 * 
	 * @return current Time HH:mm:ss
	 */
	private static String getCurrentTime() {
		Date now = new java.util.Date();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss"); //$NON-NLS-1$
		return df.format(now);
	}
	
	/**
	 * Print message with prioriry level
	 * 
	 * @param level
	 * @param msg
	 */
	private static void msg(LogSeverity level, final String msg) {
		if ( (debugLevel == null)
				|| (debugLevel != null) && (debugLevel.getId() >= level.getId())) {
			String message = msg;
			message = "(" + getCurrentTime() + ")"+ level.getPrefix() + ": "+ message; //$NON-NLS-1$,  //$NON-NLS-2$
			System.out.println(message); 
			
			// if both verbose mode and logging enabled, make sure output
			// still lands on the console as well!
			if (verboseMode) {
				originalOut.println(message);
			}
		}
	}

	/**
	 * This is a modified message function, used by the network subsystem
	 * so the debug output is more readable
	 * 
	 * @param msg
	 */
	public static void netMsg(final String msg){
		msg(LS_NETWORK, msg);		
	}

	public static void always(String msg) {
		msg(LS_ALWAYS, msg);
	}
	
	public static void error(String msg) {
		msg(LS_ERROR, msg);
	}

	public static void warning(String msg) {
		msg(LS_WARNING, msg);
	}

	public static void info(String msg) {
		msg(LS_INFO, msg);
	}
	
	public static void debug(String msg) {
		msg(LS_DEBUG, msg);
	}
		
	private static void loadDebugFile(){
		try {
			int selectedLogSeverityIndex = log_severity_box.getSelectedIndex();
			LogSeverity selectedLogSeverity = (LogSeverity) log_severity_box.getItemAt(selectedLogSeverityIndex);
			log_area.setText("");
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(new FileReader(debugLogFile));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if (selectedLogSeverity.getId() >= returnLineSeverity(zeile).getId())
				{
					log_area.append(zeile + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static LogSeverity returnLineSeverity(String line)
	{
		LogSeverity ls = LS_DEBUG;
		if (line.substring(19).startsWith(LS_DEBUG.getPrefix())) {
			ls = LS_DEBUG;			
		} else if (line.substring(19).startsWith(LS_INFO.getPrefix())) {
			ls = LS_INFO;
		} else if (line.substring(19).startsWith(LS_NETWORK.getPrefix())) {
			ls = LS_NETWORK;
		} else if (line.substring(19).startsWith(LS_WARNING.getPrefix())) {
			ls = LS_WARNING;
		} else if (line.substring(19).startsWith(LS_ERROR.getPrefix())) {
			ls = LS_ERROR;
		} else {
			ls = LS_ALWAYS;
		}
		
		return ls;
	}
	
	public static JPanel getPanel()
	{
		loadDebugFile();
		return main_panel;
	}
	
	public static void setCloseButtonText(String text)
	{
		close_button.setText(text);
	}

	public static void setSaveButtonText(String text)
	{
		save_button.setText(text);
	}

	public static void setRefreshButtonText(String text)
	{
		refresh_button.setText(text);
	}
		
	public static void setVerbose(boolean verbose)
	{
		verboseMode = verbose;
	}
	
	public static void setDebugLevel(LogSeverity level)
	{
		debugLevel = level;
		info("Set debug level to " + level.getName());
	}
}
