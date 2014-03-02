package de.moonflower.jfritz.simpletests;

import de.moonflower.jfritz.struct.SIDLogin;
import de.moonflower.jfritz.utils.Debug;

public class NewChallengeTest {
	private static String sRetSID = "0000000000000000";

	public static void main(String[] args) {
		final String urlstr = "http://fritz.box:80/cgi-bin/webcm"; //$NON-NLS-1$, //$NON-NLS-2$
		final String box_user = "fritzboxuser";
		final String box_password = "badpassword";
		
		@SuppressWarnings("unused")
		SIDLogin sidLogin = new SIDLogin();
		sRetSID = "0000000000000001";
		
			SIDLogin.check("TestBox", urlstr, box_password, box_user, sRetSID);

			// hier hast du dann die Ergebnisse:
			if (SIDLogin.isSidLoginLua())
			{			
			    Debug.debug("SID-Login Lua erforderlich: " + SIDLogin.isSidLoginLua());
			    Debug.debug("SID-Login Lua Username: " + box_user);
			    Debug.debug("SID: " + SIDLogin.getSessionId());
			}
			else if (SIDLogin.isSidLogin())
			{
			    Debug.debug("SID-Login erforderlich: " + SIDLogin.isSidLogin());
				Debug.debug("SID: " + SIDLogin.getSessionId());
			}
			else if (SIDLogin.isLogin())
			{
				Debug.debug("OLD Login, SID nicht erforderlich ");
			}
			else
			{
				Debug.debug("Fehler: Login nicht erfolgreich ");	
			}
	}
}
