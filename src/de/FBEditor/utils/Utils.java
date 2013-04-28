package de.FBEditor.utils;


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
import de.FBEditor.struct.MyProperties;

import de.moonflower.jfritz.struct.SIDLogin;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.utils.HTMLUtil;

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
		try {
			String url = (new StringBuilder("http://")).append(box_address)
					.append("/cgi-bin/firmwarecfg").toString();
			File uploadFile = createTempFile(data);
			PostMethod mPost = new PostMethod(url);

			String sid = SIDLogin.getSessionId();

			Part[] parts = null;
			if (SIDLogin.isSidLogin()) {
				// with session id
				parts = new Part[3];
				parts[0] = new StringPartNoTransferEncoding("sid", sid);
				parts[1] = new StringPartNoTransferEncoding(
						"ImportExportPassword", "");
				parts[2] = new FilePart("ConfigImportFile",
						uploadFile.getName(), uploadFile);
			} else {
				// old style, no session id
				parts = new Part[2];
				parts[0] = new StringPartNoTransferEncoding(
						"ImportExportPassword", "");
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
		} catch (IOException ex) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
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
			properties.setProperty("readOnStartup", "no");
			properties.setProperty("NoChecks", "true");
			properties.setProperty("language", "de_DE");
		} catch (Exception exception) {
		}
		return false;
	}

	public static void saveProperties(String PROPERTIES_FILE, FBEdit fbedit) {
		MyProperties properties = new MyProperties();
		properties.setProperty("position.left",
				Integer.toString(fbedit.getLocation().x));
		properties.setProperty("position.top",
				Integer.toString(fbedit.getLocation().y));
		properties.setProperty("position.width",
				Integer.toString(fbedit.getSize().width));
		properties.setProperty("position.height",
				Integer.toString(fbedit.getSize().height));
		properties.setProperty("box.password",
				Encryption.encrypt(fbedit.getbox_password()));
		properties.setProperty("box.address", fbedit.getbox_address());
		properties.setProperty("readOnStartup", fbedit.getRASstate());
		properties.setProperty("NoChecks", fbedit.getNoChecksState());
		properties.setProperty("language", fbedit.getLanguage());
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
		return (data.indexOf("fehlgeschlagen") == -1);
	}

}