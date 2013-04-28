package de.FBEditor;

import de.FBEditor.struct.HttpPost;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.FBEditor.utils.Utils;
import de.FBEditor.struct.FBFWVN;
import de.moonflower.jfritz.struct.SIDLogin;
import javax.swing.JOptionPane;

/**
 * Class holding connection information
 */
public class FritzBoxConnection {

	private boolean connected = false;
	private static FritzBoxFirmware firmware;
	private SIDLogin sidLogin;
	private String postdata;
	private String urlstr;
	private String urlstr1;
	private String box_password;
	// private final static String[] POSTDATA_ACCESS_METHOD = {
	// "getpage=../html/de/menus/menu2.html",
	// "getpage=../html/en/menus/menu2.html", "getpage=../html/menus/menu2.html"
	// };
	// private final static String[] POSTDATA_DETECT_FIRMWARE = {
	// "&var%3Alang=de&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2F%LOGINMODE%=",
	// "&var%3Alang=en&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2F%LOGINMODE%="
	// };
	private static String sRetSID = "0000000000000000";

	// private final static String PATTERN_DETECT_FIRMWARE =
	// "[Firmware|Labor][-| ][V|v]ersion[^\\d]*(\\d\\d\\d*).(\\d\\d).(\\d\\d\\d*)([^<]*)";
	// private final static String PATTERN_DETECT_FIRMWARE_SPEEDPORT =
	// "Firmware Version:</td> <td>(\\d\\d\\d*).(\\d\\d).(\\d\\d\\d*)([^<]*)";
	// private final static String PATTERN_DETECT_LANGUAGE_DE = "Weitere";
	// private final static String PATTERN_DETECT_LANGUAGE_EN = "More";

	public FritzBoxConnection(String box_address, String box_password) {
		updateURLstr(box_address);
		try {
			this.box_password = box_password;

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

	private String updatePostData() {
		postdata = "";
		if (!sidLogin.isSidLogin()) {
			try {
				postdata = this.postdata.replace("%LOGINMODE%", "password");
				postdata = postdata
						+ URLEncoder.encode(box_password, "ISO-8859-1");
			} catch (UnsupportedEncodingException ex) {
				Logger.getLogger(FritzBoxConnection.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
		return postdata;
	}

	void getAccessMethod() {
		String data = "";
		String language = "de";
		Boolean speedport = false;
		boolean detected = false;

		sidLogin.check("", urlstr1, box_password, sRetSID);
		sRetSID = sidLogin.getSessionId();

		FBFWVN fbfwvn = new FBFWVN(getFirmwareStatus());

		if (sidLogin.isSidLogin()) {
			if (fbfwvn.isOK()) {
				detected = true;
			}

		} else if (sidLogin.isLogin()) {
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

			Pattern normalFirmware;
			normalFirmware = Pattern.compile("([0-9]*).([0-9]*).([0-9]*)");

			Matcher m = normalFirmware.matcher(data);
			if (m.find()) {
				boxtypeString = m.group(1);
				majorFirmwareVersion = m.group(2);
				minorFirmwareVersion = m.group(3);
				modFirmwareVersion = m.group(4).trim();
			}

		}
		firmware = new FritzBoxFirmware(FritzboxName, boxtypeString,
				majorFirmwareVersion, minorFirmwareVersion, modFirmwareVersion,
				language);

	}

	public boolean reconnect(String box_address, String boxPassword) {
		boolean result = false;
		connected = false;
		try {
			updateURLstr(box_address);
			this.box_password = boxPassword;
			if (Utils.checkhost(box_address)) {
				sRetSID = sidLogin.getSessionId();
				sidLogin.check("", urlstr1, box_address, sRetSID);
				sRetSID = sidLogin.getSessionId();
				if (sidLogin.isSidLogin()) {
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