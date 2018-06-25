package de.FBEditor.utils;

import static de.FBEditor.FBEdit.fbConnection;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import de.FBEditor.FBEdit;
import de.FBEditor.FritzBoxFirmware;
import de.FBEditor.struct.MyProperties;
import de.FBEditor.struct.SIDLogin;

public class Utils {

	// Check if we can connect to the given host
	public static boolean checkhost(String host) throws IOException {
		String[] temp;
		int port;
		Boolean bool = false;

		Pattern p = Pattern.compile(":");
		temp = p.split(host);

		InetAddress addr = InetAddress.getByName(temp[0]);
		if (temp.length == 2)
			port = Integer.valueOf(temp[1]).intValue();
		else
			port = 80;

		SocketAddress sockaddr = new InetSocketAddress(addr, port);
		Socket test = new Socket();
		int timeoutMs = 1000; // 1 second
		test.connect(sockaddr, timeoutMs);
		test.close();
		bool = true;
		return bool;
	}

	public static String pMatch(String s, String s1, int i) {
		Pattern pattern = Pattern.compile(s, 2);
		Matcher matcher = pattern.matcher(s1);
		if (matcher.find())
			return matcher.group(i);
		else
			return null;
	}

	public static boolean exportData(FBEdit fbedit, String box_address,
			String data) {
		boolean result = false;

        // 14.12.2014
        FritzBoxFirmware firmware = null;
        firmware = fbConnection.getFirmware();
//*
		try {
			String url = (new StringBuilder("http://")).append(box_address)
					.append("/cgi-bin/firmwarecfg").toString();
			File uploadFile = createTempFile(data);
//*
			PostMethod mPost = new PostMethod(url);

		        // Kennwort der Sicherungsdatei
			String ConfigImExPwd = "";
/*
			String box_ConfigImExPwd = FBEdit.getInstance().getbox_ConfigImExPwd();
			System.out.println("box.ConfigImExPwd: " + box_ConfigImExPwd);

			if ( !"".equals(box_ConfigImExPwd) ) {
				// Hier kann man ein PopUp Dialog verwenden
				// mit der Frage, mit oder ohne Kennwort Speichern

				FBEdit.getInstance().getConfigImExPwd(false);

	            if ( FBEdit.isConfigImExPwdOk() == true ) {
			    	box_ConfigImExPwd = FBEdit.getInstance().getbox_ConfigImExPwd();
		    		// ConfigImExPwd = ""; // Abbrechen -> ohne Kennwort
	    			ConfigImExPwd = box_ConfigImExPwd; // OK -> mit Kennwort	
	            }
				System.out.println("ConfigImExPwd: " + ConfigImExPwd + " -> " + FBEdit.isConfigImExPwdOk());
			}
*/

			String sid = SIDLogin.getSessionId();

			Part[] parts = null;
			if (SIDLogin.isSidLogin()) {
				// with session id
				// 14.12.2014 ab Firmware xxx.06.xx with "apply"
				if (firmware.getMajorFirmwareVersion() >= 6) { // ab Firmware xxx.06.xx with "apply"
                	System.out.println("DEBUG: firmware.getMajorFirmwareVersion() >= 6: " + firmware.getMajorFirmwareVersion());
                	parts = new Part[4];
                	parts[0] = new StringPartNoTransferEncoding("sid", sid);
                	//parts[1] = new StringPartNoTransferEncoding(
                	//		"ImportExportPassword", "");
                	parts[1] = new StringPartNoTransferEncoding(
                			"ImportExportPassword", ConfigImExPwd);
                	parts[2] = new FilePart("ConfigImportFile",
                			uploadFile.getName(), uploadFile);
                	parts[3] = new StringPartNoTransferEncoding(
                			"apply", "");
				} else {
					System.out.println("DEBUG: firmware.getMajorFirmwareVersion(): " + firmware.getMajorFirmwareVersion());
					parts = new Part[3];
					parts[0] = new StringPartNoTransferEncoding("sid", sid);
					//parts[1] = new StringPartNoTransferEncoding(
					//		"ImportExportPassword", "");
					parts[1] = new StringPartNoTransferEncoding(
							"ImportExportPassword", ConfigImExPwd);
					parts[2] = new FilePart("ConfigImportFile",
							uploadFile.getName(), uploadFile);
				}
			} else {
				// old style, no session id
				parts = new Part[2];
				//parts[0] = new StringPartNoTransferEncoding(
				//		"ImportExportPassword", "");
				parts[0] = new StringPartNoTransferEncoding(
						"ImportExportPassword", ConfigImExPwd);
				parts[1] = new FilePart("ConfigImportFile",
						uploadFile.getName(), uploadFile);
			}

			mPost.setRequestEntity(new MultipartRequestEntity(parts, mPost
					.getParams()));

			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(8000);

			int statusCode1 = client.executeMethod(mPost);

			BufferedInputStream bis = new BufferedInputStream(
					mPost.getResponseBodyAsStream());
			byte buf[] = new byte[4096];
			StringBuffer sb = new StringBuffer();
			int len;
			while ((len = bis.read(buf)) > 0)
				sb.append(new String(buf, 0, len));
			data = HTMLUtil.stripEntities(sb.toString());
			data = HTMLUtil.stripNbsp(data);
			bis.close();

			if (statusCode1 == 200 && checkResponse(data))
				result = true;
			else
				JOptionPane.showMessageDialog(fbedit, FBEdit.getMessage(""),
						FBEdit.getMessage("error"), 0);

			mPost.releaseConnection();
//*/
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}
//*/
		return result;
	}

	public static boolean createDefaultProperties(MyProperties properties) {
		// properties = new MyProperties();
		// Set Properties Default Values // 22.02.2014
		// Muss immer vor Load and Save File aufgerufen werden

		System.out.println("Properties " + "Set" + " using default values");

		properties.setProperty("position.top", "60");
		properties.setProperty("position.left", "60");
		properties.setProperty("position.height", "480");
		properties.setProperty("position.width", "680");

		properties.setProperty("box.address", "fritz.box");
		properties.setProperty("box.password", "");
		properties.setProperty("box.username", "");
//		properties.setProperty("box.ConfigImExPwd", "");
		properties.setProperty("readOnStartup", "no");
//		properties.setProperty("NoChecks", "true"); // 17.02.2014
		properties.setProperty("NoChecks", "false"); // 17.02.2014
		properties.setProperty("language", "de_DE");
		properties.setProperty("language.setting.manuell", "no"); // 25.06.2018

		return true;
	}

	public static boolean loadProperties(MyProperties properties,
			String PROPERTIES_FILE) {
		// properties = new MyProperties();
		try {
			FileInputStream fis = new FileInputStream(PROPERTIES_FILE);
			properties.loadFromXML(fis);
			fis.close();
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("File " + PROPERTIES_FILE
					+ " not found, using default values");
			properties.setProperty("box.address", "fritz.box");
			properties.setProperty("box.password", "");
			properties.setProperty("box.username", "");
//			properties.setProperty("box.ConfigImExPwd", "");
			properties.setProperty("readOnStartup", "no");
//			properties.setProperty("NoChecks", "true"); // 17.02.2014
			properties.setProperty("NoChecks", "false"); // 17.02.2014
			properties.setProperty("language", "de_DE");
			properties.setProperty("language.setting.manuell", "no"); // 25.06.2018
		} catch (Exception exception) {
		}
		return false;
	}

	public static void saveProperties(String PROPERTIES_FILE, FBEdit fbedit) {
		MyProperties properties = new MyProperties();
		properties.setProperty("position.top",
				Integer.toString(fbedit.getLocation().y));
		properties.setProperty("position.left",
				Integer.toString(fbedit.getLocation().x));
		properties.setProperty("position.height",
				Integer.toString(fbedit.getSize().height));
		properties.setProperty("position.width",
				Integer.toString(fbedit.getSize().width));
		properties.setProperty("box.address", fbedit.getbox_address());
		properties.setProperty("box.password",
				Encryption.encrypt(fbedit.getbox_password()));
		properties.setProperty("box.username", fbedit.getbox_username());
//		properties.setProperty("box.ConfigImExPwd", fbedit.getbox_ConfigImExPwd());
		properties.setProperty("readOnStartup", fbedit.getRASstate());
		properties.setProperty("NoChecks", fbedit.getNoChecksState());
		properties.setProperty("language", fbedit.getLanguage());
		properties.setProperty("language.setting.manuell", fbedit.getLanguageManuell()); // 25.06.2018
		try {
			FileOutputStream fos = new FileOutputStream(PROPERTIES_FILE);
			properties.storeToXML(fos, "Properties for FBEditor");
			fos.close();
		} catch (FileNotFoundException filenotfoundexception) {
		} catch (IOException ioexception) {
		}
	}

	private static File createTempFile(String data) throws IOException {
		// Create temp file.
		File temp = File.createTempFile("FRITZ.BOX", ".export");

		// Delete temp file when program exits.
		temp.deleteOnExit();

		// Write to temp file
		BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		out.write(data);
		out.close();
		return temp;
	}

	private static boolean checkResponse(String data) {
		return (!data.contains("fehlgeschlagen"));
	}

}
