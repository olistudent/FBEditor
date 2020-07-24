package de.FBEditor.utils.upnp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.FBEditor.FBEdit;
import de.FBEditor.utils.Debug;

/*
 * Created on 22.05.2005
 *
 */

/**
 * @author Arno Willig
 *
 * TODO: Bugfix: Exception if no UPNP enabled
 */
public class UPNPUtils {

	private static String URL_SERVICE_CREATEURLSID = ":49000/upnp/control/deviceconfig"; // 01.08.2015
	private static String URN_SERVICE_CREATEURLSID = "urn:dslforum-org:service:DeviceConfig:1#X_AVM-DE_CreateUrlSID"; // 01.08.2015

	private static String URL_SERVICE_2FA = ":49000/upnp/control/x_auth"; // 27.04.2018
//	private static String URL_SERVICE_2FA = ":49443/upnp/control/x_auth"; // 27.04.2018
	private static String URN_SERVICE_2FA = "urn:dslforum-org:service:X_AVM-DE_Auth:1#GetInfo"; // 27.04.2018

	private static String protocol = "http";
//	private static String protocol = "https";
	private static FBEdit fbe;
	
	public UPNPUtils() {
	}

	/**
	 * function calls the web service specified by the url with the soap
	 * envelope specified in xml
	 *
	 * @param url of the web service
	 * @param urn
	 * @param xml soap element to be trasmitted
	 * @return
	 */
	public static String getSOAPData(String url, String urn, String xml) {

		String data = "";
		BufferedReader d = null;
		DataOutputStream printout = null;
		try {
			URL u = new URL(url);

			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setRequestMethod("POST");

			// 5 Sekunden-Timeout für Verbindungsaufbau
			uc.setConnectTimeout(5000);

			uc.setReadTimeout(2000);

			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);

			byte[] bytes = xml.getBytes();

			uc.setRequestProperty("HOST", u.getAuthority()); // 14.05.2018
			uc.setRequestProperty("USER-AGENT", "AVM UPnP/1.0 Client 1.0");
			uc.setRequestProperty("Connection", "Keep-Alive");
			uc.setRequestProperty("CONTENT-LENGTH", String.valueOf(bytes.length));
			uc.setRequestProperty("CONTENT-TYPE", //$NON-NLS-1$
							"text/xml; charset=\"utf-8\""); //$NON-NLS-1$
			uc.setRequestProperty("SOAPACTION", urn); //$NON-NLS-1$
			uc.setRequestProperty("Connection", "close");

			printout = new DataOutputStream(uc.getOutputStream());
			printout.write(bytes);
			printout.close();

			if (uc.getResponseCode() == 200) {
				//InputStream in = uc.getInputStream();
				d = new BufferedReader(new InputStreamReader(uc.getInputStream()));

				String str;
				while (null != ((str = d.readLine())))
					data += str + "\n";
			}

		} catch (IOException e) {
			Debug.error(e.toString());
		} finally {

			try {
				if(d!=null)
					d.close();
			}catch(IOException ioe){
				Debug.error("Error closing Stream");
			}

			try {
				if(printout!=null)
					printout.close();
			}catch(IOException ioe){
				Debug.error("Error closing Stream");
			}
		}
		return data;
	}

	// 01.08.2015 pikachu // 27.04.2018 pikachu
	public static String getSOAPDataAuth(FBEdit fbe, String url, String urn, String xml) {

		String data = "";
		BufferedReader d = null;
		DataOutputStream printout = null;

		//String username = "@CompatMode"; String password = "0000"; // geht nicht
		//String username = "boxuser100"; String password = "0000"; // geht nicht
		//String username = "test"; String password = "0000"; // geht nur mit Benutzer Login
		String username = FBEdit.getInstance().getbox_username(); String password = FBEdit.getInstance().getbox_password(); // geht nur mit Benutzer Login
//		System.out.println("password 1: " + password);
//		System.out.println("username 1: " + username);
		
		try {
			Authenticator.setDefault(new MyAuthenticator(username, password));
			URL u = new URL(url);

			//Debug.info("Result of DeviceConfig getSOAPDataAuth 1: " + u);
			//Debug.info("Result of DeviceConfig getSOAPDataAuth u: " + username);
			//Debug.info("Result of DeviceConfig getSOAPDataAuth p: " + password);

			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			//uc.setRequestProperty("Authorization", "Basic " + getBasicAuthenticationEncoding(username, password));
			uc.setRequestMethod("POST");

			// 5 Sekunden-Timeout für Verbindungsaufbau
			uc.setConnectTimeout(5000);

			uc.setReadTimeout(2000);

			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);

			byte[] bytes = xml.getBytes();

			uc.setRequestProperty("HOST", u.getAuthority());
			uc.setRequestProperty("USER-AGENT", "AVM UPnP/1.0 Client 1.0");
			uc.setRequestProperty("Connection", "Keep-Alive");
			uc.setRequestProperty("CONTENT-LENGTH", String.valueOf(bytes.length)); // 14.05.2018
			uc.setRequestProperty("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
			uc.setRequestProperty("SOAPACTION", urn);
			uc.setRequestProperty("Connection", "close");

			printout = new DataOutputStream(uc.getOutputStream());
			printout.write(bytes);
			printout.close();

			//Debug.info("Result of DeviceConfig getHeaderFields: " + uc.getHeaderFields());

			if (uc.getResponseCode() == 200) {
				//InputStream in = uc.getInputStream();
				d = new BufferedReader(new InputStreamReader(uc.getInputStream()));

				String str;
				while (null != ((str = d.readLine())))
					data += str + "\n";
			}

		} catch (IOException e) {
			Debug.error(e.toString());
		}finally{
			try{
				if(d!=null)
					d.close();
			}catch(IOException ioe){
				Debug.error("Error closing Stream");
			}
			try{
				if(printout!=null)
					printout.close();
			}catch(IOException ioe){
				Debug.error("Error closing Stream");
			}
		}
		//Debug.info("Result of DeviceConfig data 1: " + data);
		return data;
	}

	// 01.08.2015 pikachu // 27.04.2018 pikachu
	public static String getSIDUPNP() { // 15.08.2015
		String sSID = "0000000000000000";
		String xml =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
		"<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"<s:Body><u:CreateUrlSID xmlns:u=\"urn:dslforum-org:service:DeviceConfig:1\">\n" +
		"</u:CreateUrlSID>\n" +
		"</s:Body>\n" +
		"</s:Envelope>";

		String result = UPNPUtils.getSOAPDataAuth(fbe, protocol + "://" + FBEdit.getInstance().getbox_address() +
			    URL_SERVICE_CREATEURLSID, URN_SERVICE_CREATEURLSID, xml);

		Pattern p = Pattern.compile("<NewX_AVM-DE_UrlSID>sid=([^<]*)</NewX_AVM-DE_UrlSID>");
		Matcher m = p.matcher(result);
		if(m.find())
			sSID = m.group(1);
		//else
		//	sSID = "0000000000000000";
		
		//sSID = result;
//		Debug.info("Result of DeviceConfig CreateUrlSID: " + result);
		return sSID;
	}

	// 27.04.2018 pikachu	
	public static String get2FAUPNP() { // 27.04.2018
		String s2FA = "";
		String xml =
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
		"<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"<s:Body><u:GetInfo xmlns:u=\"urn:dslforum-org:service:X_AVM-DE_Auth:1\">\n" +
		"</u:GetInfo>\n" +
		"</s:Body>\n" +
		"</s:Envelope>";

		String result = UPNPUtils.getSOAPDataAuth(fbe, protocol + "://" + FBEdit.getInstance().getbox_address() +
			    URL_SERVICE_2FA, URN_SERVICE_2FA, xml);

		Pattern p = Pattern.compile("<NewEnabled>([^<]*)</NewEnabled>");
		Matcher m = p.matcher(result);
		if(m.find())
			s2FA = m.group(1);
		else
			s2FA = "0";
		
		//s2FA = result;
//		Debug.info("Result of X_AVM-DE_Auth GetInfo NewEnabled: " + result);
		return s2FA;
	}

}
