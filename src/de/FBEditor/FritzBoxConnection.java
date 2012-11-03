package de.FBEditor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.FBEditor.utils.Utils;
import de.moonflower.jfritz.exceptions.InvalidFirmwareException;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.struct.SIDLogin;
import de.moonflower.jfritz.utils.JFritzUtils;

/**
 * Class holding connection information
 *
 */
public class FritzBoxConnection {

	private boolean connected = false;
	private static FritzBoxFirmware firmware;
	private SIDLogin sidLogin;
	private String postdata;
	private String urlstr;
	private String box_password;

	private final static String[] POSTDATA_ACCESS_METHOD = { "getpage=../html/de/menus/menu2.html", "getpage=../html/en/menus/menu2.html", "getpage=../html/menus/menu2.html" };

	private final static String[] POSTDATA_DETECT_FIRMWARE = { "&var%3Alang=de&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2F%LOGINMODE%=", "&var%3Alang=en&var%3Amenu=home&var%3Apagename=home&login%3Acommand%2F%LOGINMODE%=" };

	private final static String PATTERN_DETECT_FIRMWARE = "[Firmware|Labor][-| ][V|v]ersion[^\\d]*(\\d\\d\\d*).(\\d\\d).(\\d\\d\\d*)([^<]*)";
	private final static String PATTERN_DETECT_FIRMWARE_SPEEDPORT = "Firmware Version:</td> <td>(\\d\\d\\d*).(\\d\\d).(\\d\\d\\d*)([^<]*)";
	private final static String PATTERN_DETECT_LANGUAGE_DE = "Weitere";
	private final static String PATTERN_DETECT_LANGUAGE_EN = "More";

	public FritzBoxConnection(String box_address, String box_password) throws WrongPasswordException, IOException, InvalidFirmwareException {
		updateURLstr(box_address);
		this.box_password = box_password;
		if (Utils.checkhost(box_address))
			getAccessMethod();
	}

	private void updateURLstr(String boxAddress) {
		urlstr = "http://" + boxAddress + "/cgi-bin/webcm";
	}

	private String updatePostData() throws UnsupportedEncodingException {
		String postdata;
		if (sidLogin.isSidLogin()) {
			postdata = this.postdata.replace("%LOGINMODE%", "response");
			postdata = postdata + URLEncoder.encode(sidLogin.getResponse(), "ISO-8859-1");
		} else {
			postdata = this.postdata.replace("%LOGINMODE%", "password");
			postdata = postdata + URLEncoder.encode(box_password, "ISO-8859-1");
		}
		return postdata;
	}

	void getAccessMethod() throws WrongPasswordException, IOException, InvalidFirmwareException {
		String data = "";
		String language = "de";
		Boolean speedport = false;
		boolean detected = false;

		sidLogin = new SIDLogin();
		sidLogin.check("", urlstr, box_password);

		// test for access method
		for (int i = 0; i < (POSTDATA_ACCESS_METHOD).length; i++) {
			for (int j = 0; j < (POSTDATA_DETECT_FIRMWARE).length; j++) {

				postdata = POSTDATA_ACCESS_METHOD[i] + POSTDATA_DETECT_FIRMWARE[j];

				data = JFritzUtils.fetchDataFromURLToString("", urlstr, updatePostData(), true);

				Pattern p = Pattern.compile(PATTERN_DETECT_LANGUAGE_DE);
				Matcher m = p.matcher(data);
				if (m.find()) {
					language = "de";
					detected = true;
					break;
				}

				if (!detected) {
					p = Pattern.compile(PATTERN_DETECT_LANGUAGE_EN);
					m = p.matcher(data);
					if (m.find()) {
						language = "en";
						detected = true;
						break;
					}
				}
				if (!detected) {
					p = Pattern.compile(PATTERN_DETECT_FIRMWARE_SPEEDPORT);
					m = p.matcher(data);
					if (m.find()) {
						speedport = true;
						detected = true;
						break;
					}
				}
			}
			if (detected) {
				sidLogin.getSidFromResponse(data);
				connected = true;
				break;
			}
		}

		if (!detected)
			throw new InvalidFirmwareException();

		Pattern normalFirmware;
		if (speedport) {
			normalFirmware = Pattern.compile(PATTERN_DETECT_FIRMWARE_SPEEDPORT);
		} else {
			normalFirmware = Pattern.compile(PATTERN_DETECT_FIRMWARE);
		}

		Matcher m = normalFirmware.matcher(data);
		String boxtypeString = "", majorFirmwareVersion = "", minorFirmwareVersion = "", modFirmwareVersion = "";
		if (m.find()) {
			boxtypeString = m.group(1);
			majorFirmwareVersion = m.group(2);
			minorFirmwareVersion = m.group(3);
			modFirmwareVersion = m.group(4).trim();
		}
		firmware = new FritzBoxFirmware(boxtypeString, majorFirmwareVersion, minorFirmwareVersion, modFirmwareVersion, language);

	}

	public boolean reconnect(String box_address, String boxPassword) throws SocketTimeoutException, WrongPasswordException, IOException {
		boolean result = false;
		connected = false;
		updateURLstr(box_address);
		this.box_password = boxPassword;
		if (Utils.checkhost(box_address)) {
			sidLogin.check("", urlstr, box_password);
			String data = JFritzUtils.fetchDataFromURLToString("", urlstr, updatePostData(), true);
			sidLogin.getSidFromResponse(data);
			result = true;
			connected = true;
		}
		return result;
	}

	public boolean isConnected() {
		return connected;
	}

	public FritzBoxFirmware getFirmware() {
		return firmware;
	}
}