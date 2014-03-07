package de.FBEditor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import de.FBEditor.struct.FBFWVN;
import de.FBEditor.struct.HttpPost;
import de.FBEditor.struct.SIDLogin; // 01.03.2014
import de.FBEditor.utils.Utils;

/**
 * Class holding connection information
 */
public class FritzBoxConnection {

	private boolean connected = false;
	private static FritzBoxFirmware firmware;
	@SuppressWarnings("unused")
	private SIDLogin sidLogin;
	private String postdata;
	private String urlstr;
	private String urlstr1;
	private String box_password;
	private String box_username;
	private static String sRetSID = "0000000000000000";

	public FritzBoxConnection(String box_address, String box_password,
			String box_username) {
		updateURLstr(box_address);
		try {
			this.box_password = box_password;
			this.box_username = box_username;

			sidLogin = new SIDLogin();

			if (Utils.checkhost(box_address)) {
				getAccessMethod();
			}
		} catch (IOException ex) {
			Logger.getLogger(FritzBoxConnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	private void updateURLstr(String boxAddress) {
		urlstr = "http://" + boxAddress + "/cgi-bin/webcm";
		urlstr1 = urlstr;
	}

	void getAccessMethod() {
		String data = "";
		String language = "de";
		@SuppressWarnings("unused")
		Boolean speedport = false;
		boolean detected = false;

		SIDLogin.check("", urlstr1, box_password, box_username, sRetSID);
		sRetSID = SIDLogin.getSessionId();

		FBFWVN fbfwvn = new FBFWVN(getFirmwareStatus());
		// FBFWVN fbfwvn = new
		// FBFWVN("<html><body>FRITZ!Box Fon WLAN 7362 SL-B-101100-000008-630046-320710-787902-1310601-12345-avm-de</body></html>");

		if (SIDLogin.isSidLogin()) {
			if (fbfwvn.isOK()) {
				detected = true;
			}

		} else if (SIDLogin.isLogin()) {
			detected = true;
		}

		if (detected) {
			connected = true;
		}

		if (!detected) {
			JOptionPane.showMessageDialog(FBEdit.getInstance().getframe(),
					FBEdit.getMessage("utils.read_error"),
					FBEdit.getMessage("main.error"), 0);
			return;
		}

		String FritzboxName = "", boxtypeString = "", majorFirmwareVersion = "", minorFirmwareVersion = "", modFirmwareVersion = "";

		if (fbfwvn.isOK()) {

			FritzboxName = fbfwvn.getFritzbox();
			boxtypeString = fbfwvn.getBoxTypeFBFWVN();
			majorFirmwareVersion = fbfwvn.getMajorFBFWVN();
			minorFirmwareVersion = fbfwvn.getMinorFBFWVN();
			modFirmwareVersion = fbfwvn.getModFBFWVN();

			// language != "de" 01.03.2014
			if (fbfwvn.isFritzboxLanguage()) {
				language = fbfwvn.getFritzboxLanguage().toLowerCase();
			}

		} else {

			HttpPost http = new HttpPost();
			boolean isQueryOld = false;
			String url = urlstr;
			String sRetQueryOld = "";
			String sRetQueryNew = "";
			postdata = "sid=" + sRetSID + "&"
					+ "getpage=../html/query.txt&var:cnt=1" + "&var:n" + "0"
					+ "=" + "logic:status/nspver";
			sRetQueryOld = http.Post2(url, postdata);
			postdata = "sid=" + sRetSID + "&"
					+ "getpage=../html/query.txt&var:cnt=1" + "&var:n[" + "0"
					+ "]=" + "logic:status/nspver";
			sRetQueryNew = http.Post2(url, postdata);

			if (sRetQueryOld.length() > sRetQueryNew.length()) {
				isQueryOld = true;
				data = sRetQueryOld;
			} else {
				data = sRetQueryNew;
			}
			System.out.println(url);
			System.out.println("QueryOld: " + isQueryOld + "     "
					+ sRetQueryOld + "     " + sRetQueryNew);
			// data = "131.06.55-12345"; // Test 01.03.2014
			//data = "33.04.57-12345"; // Test 01.03.2014
			Pattern normalFirmware;
			// normalFirmware = Pattern.compile("([0-9]*).([0-9]*).([0-9]*)");
			normalFirmware = Pattern
					.compile("([0-9]*).([0-9]*).([0-9]*)[|-]?([^[0-9]<]*)"); // 01.03.2014 mit mod

			Matcher m = normalFirmware.matcher(data);
			if (m.find()) {
				boxtypeString = m.group(1);
				majorFirmwareVersion = m.group(2);
				minorFirmwareVersion = m.group(3);

				// modFirmwareVersion = m.group(4).trim(); // Fehler nicht
				// vorhanden
				modFirmwareVersion = ""; // erkennt sonst die Box 701/900 nicht

				try { // Test 01.03.2014
					modFirmwareVersion = m.group(4);
					System.out.println("modFirmwareVersion: " + m.group(4));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("error modFirmwareVersion: "
							+ m.group(4));

				}
			}

		}
		firmware = new FritzBoxFirmware(FritzboxName, boxtypeString,
				majorFirmwareVersion, minorFirmwareVersion, modFirmwareVersion,
				language);

		System.out.println("Debug boxtype: " + boxtypeString);

	}

	public boolean reconnect(String box_address, String box_password,
			String box_username) {
		boolean result = false;
		connected = false;
		try {
			updateURLstr(box_address);
			this.box_password = box_password;
			this.box_username = box_username;
			if (Utils.checkhost(box_address)) {
				sRetSID = SIDLogin.getSessionId();
				SIDLogin.check("", urlstr1, box_password, box_username, sRetSID);
				sRetSID = SIDLogin.getSessionId();
				if (SIDLogin.isSidLogin()) {
					result = true;
					connected = true;
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(FritzBoxConnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return result;
	}

	public boolean isConnected() {
		return connected;
	}

	public FritzBoxFirmware getFirmware() {
		return firmware;
	}

	public String getFirmwareStatus() {
		HttpPost http = new HttpPost();
		String url = (new StringBuilder("http://"))
				.append(FBEdit.getInstance().getbox_address())
				.append("/cgi-bin/system_status").toString();
		String sFBFW_Status = http.Post(url, "");
		return sFBFW_Status;
	}
}
