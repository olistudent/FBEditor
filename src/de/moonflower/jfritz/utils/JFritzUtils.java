/*
 * $Id: JFritzUtils.java,v 1.86 2009/03/23 20:39:03 robotniko Exp $
 * 
 * Created on 06.05.2005
 *
 */
package de.moonflower.jfritz.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.moonflower.jfritz.exceptions.WrongPasswordException;

//import de.FBEditor.WrongPasswordException;

/**
 * Static class for data retrieval from the fritz box
 * and for several global functions
 * 
 * @author akw
 * 
 */
public class JFritzUtils {
	private static final int READ_TIMEOUT = 30000;										//$NON-NLS-1$
    
	private final static String PATTERN_WAIT_FOR_X_SECONDS = "var loginBlocked = parseInt\\(\"([^\"]*)\",10\\);";

	/**
	 * fetches html data from url using POST requests in one single return String
	 * 
	 * @param affectedBox
	 * @param urlstr
	 * @param postdata
	 * @return html data
	 * @throws WrongPasswordException
	 * @throws IOException
	 */
	public static String fetchDataFromURLToString(String affectedBox, 
			String urlstr, String postdata,
			boolean retrieveData) throws WrongPasswordException, SocketTimeoutException, IOException {
		URL url = null;
		URLConnection urlConn;
		DataOutputStream printout;
		String data = ""; //$NON-NLS-1$
		boolean wrong_pass = false;
		Debug.debug("Urlstr: " + urlstr);
		Debug.debug("Postdata: " + postdata);

		try {
			url = new URL(urlstr);
		} catch (MalformedURLException e) {
			Debug.error("URL invalid: " + urlstr); //$NON-NLS-1$
			throw new MalformedURLException("URL invalid: " + urlstr); //$NON-NLS-1$
		}

		if (url != null) {
			urlConn = url.openConnection();
			// 5 Sekunden-Timeout für Verbindungsaufbau
			urlConn.setConnectTimeout(5000);
			urlConn.setReadTimeout(READ_TIMEOUT);

			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			// Sending postdata
			if (postdata != null) {
				urlConn.setRequestProperty("Content-Type", //$NON-NLS-1$
						"application/x-www-form-urlencoded"); //$NON-NLS-1$
				try {
					printout = new DataOutputStream(urlConn.getOutputStream());
					printout.writeBytes(postdata);
					printout.flush();
					printout.close();
				} catch (SocketTimeoutException ste)
				{
					Debug.error("Could not fetch data from url: "+ste.toString());
					throw ste;
				}
			}

			BufferedReader d;

			try {
				// Get response data
				d = new BufferedReader(new InputStreamReader(urlConn
						.getInputStream()));
				String str;
				while (null != ((str = HTMLUtil.stripEntities(d.readLine())))) {
					// Password seems to be wrong
					if ((str.indexOf("Das angegebene Kennwort ist ungültig") >= 0) //$NON-NLS-1$
						|| (str.indexOf("Password not valid") >= 0)
						|| (str.indexOf("<!--loginPage-->") >= 0)
						|| (str.indexOf("FRITZ!Box Anmeldung") >= 0))
					{
						Debug.debug("Wrong password detected: " + str);
						wrong_pass = true;
					}
					if (retrieveData)
					{
						data += str;
					}
				}
				d.close();
			} catch (IOException e1) {
				throw new IOException("Network unavailable"); //$NON-NLS-1$
			}

			if (wrong_pass)
			{
				int wait = 3; 
				Pattern waitSeconds = Pattern.compile(PATTERN_WAIT_FOR_X_SECONDS);
				Matcher m = waitSeconds.matcher(data);
				if (m.find())
				{
					try {
						wait = Integer.parseInt(m.group(1));
					}
					catch (NumberFormatException nfe)
					{
						wait = 3;
					}
				}

				throw new WrongPasswordException(affectedBox, "Password invalid", wait+2); //$NON-NLS-1$
			}
		}
		return data;
	}

	/**
	 * fetches html data from url using POST requests in one single return String
	 * 
	 * @param affectedBox
	 * @param urlstr
	 * @param postdata
	 * @return html data
	 * @throws WrongPasswordException
	 * @throws IOException
	 */
	public static Vector<String> fetchDataFromURLToVector(String affectedBox, 
			String urlstr, String postdata,
			boolean retrieveData) throws WrongPasswordException, SocketTimeoutException, IOException {
		URL url = null;
		URLConnection urlConn;
		DataOutputStream printout;
		Vector<String> data = new Vector<String>();
		boolean wrong_pass = false;
		Debug.debug("Urlstr: " + urlstr);
		Debug.debug("Postdata: " + postdata);

		try {
			url = new URL(urlstr);
		} catch (MalformedURLException e) {
			Debug.error("URL invalid: " + urlstr); //$NON-NLS-1$
			throw new MalformedURLException("URL invalid: " + urlstr); //$NON-NLS-1$
		}

		if (url != null) {
			urlConn = url.openConnection();
			// 5 Sekunden-Timeout für Verbindungsaufbau
			urlConn.setConnectTimeout(5000);
			urlConn.setReadTimeout(READ_TIMEOUT);
			
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			// Sending postdata
			if (postdata != null) {
				urlConn.setRequestProperty("Content-Type", //$NON-NLS-1$
						"application/x-www-form-urlencoded"); //$NON-NLS-1$
				try {
					printout = new DataOutputStream(urlConn.getOutputStream());
					printout.writeBytes(postdata);
					printout.flush();
					printout.close();
				} catch (SocketTimeoutException ste)
				{
					Debug.error("Could not fetch data from url: "+ste.toString());
					throw ste;
				} catch (NoRouteToHostException nrthe)
				{
					Debug.error("No route to host exception: " + nrthe.toString());
					throw nrthe;
				}
			}

			BufferedReader d;

			try {
				// Get response data
				d = new BufferedReader(new InputStreamReader(urlConn
						.getInputStream(), "UTF8"));
				String str;
				while (null != ((str = HTMLUtil.stripEntities(d.readLine())))) {
					// Password seems to be wrong
					if ((str.indexOf("Das angegebene Kennwort ist ungültig") >= 0) //$NON-NLS-1$
						|| (str.indexOf("Password not valid") >= 0)
						|| (str.indexOf("<!--loginPage-->") >= 0)
						|| (str.indexOf("FRITZ!Box Anmeldung") >= 0))
					{
						Debug.debug("Wrong password detected: " + str);
						wrong_pass = true;
					}
					if (retrieveData)
					{
						data.add(str);
					}
				}
				d.close();
			} catch (IOException e1) {
				throw new IOException("Network unavailable"); //$NON-NLS-1$
			}

			if (wrong_pass)
			{
				int wait = 3; 
				Pattern waitSeconds = Pattern.compile(PATTERN_WAIT_FOR_X_SECONDS);
				for (int i=0; i<data.size(); i++)
				{
					Matcher m = waitSeconds.matcher(data.get(i));
					if (m.find())
					{
						Debug.debug("Waiting string: " + data.get(i));
						try {
							wait = Integer.parseInt(m.group(1));
							break;
						}
						catch (Exception e)
						{
							Debug.error(e.toString());
							wait = 4;
						}
					}
				}
				
				throw new WrongPasswordException(affectedBox, "Password invalid", wait+2); //$NON-NLS-1$
			}
		}
		return data;
	}
}