package de.moonflower.jfritz.simpletests;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Vector;

import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.struct.SIDLogin;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.JFritzUtils;

public class ChallengeTest {
	private static String sRetSID = "0000000000000000";

	private final static String[] POSTDATA_ACCESS_METHOD = {
		"getpage=../html/de/menus/menu2.html", //$NON-NLS-1$
		"getpage=../html/en/menus/menu2.html", //$NON-NLS-1$
		"getpage=../html/menus/menu2.html" }; //$NON-NLS-1$

	private final static String[] POSTDATA_DETECT_FIRMWARE = {
		"&var%3Alang=de&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2F%LOGINMODE%=", //$NON-NLS-1$
		"&var%3Alang=en&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2F%LOGINMODE%="}; //$NON-NLS-1$

	public static void main(String[] args) {
		final String urlstr = "http://fritzbox:80/cgi-bin/webcm"; //$NON-NLS-1$, //$NON-NLS-2$
		final String box_user = "fritzboxuser";
		final String box_password = "badpassword";
		String postdata = POSTDATA_ACCESS_METHOD[0] + POSTDATA_DETECT_FIRMWARE[0];
		
		SIDLogin sidLogin = new SIDLogin();
		sRetSID = "0000000000000001";
		
		try {
			sidLogin.check("TestBox", urlstr, box_password, box_user, sRetSID);
			if (sidLogin.isSidLogin())
			{
				postdata = postdata.replace("%LOGINMODE%", "response");
				postdata = postdata + URLEncoder.encode(sidLogin.getResponse(), "ISO-8859-1");
			}
			else
			{
				postdata = postdata.replace("%LOGINMODE%", "password");
				postdata = postdata + URLEncoder.encode(box_password, "ISO-8859-1"); 
			}

			Vector<String> data = JFritzUtils.fetchDataFromURLToVector(
					"TestBox", urlstr, postdata, true);

			//sidLogin.getSidFromResponse(data);

			// hier hast du dann die Ergebnisse:
			Debug.debug("SID-Login erforderlich: " + sidLogin.isSidLogin());
			Debug.debug("SID: " + sidLogin.getSessionId());
			
		} catch (WrongPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
