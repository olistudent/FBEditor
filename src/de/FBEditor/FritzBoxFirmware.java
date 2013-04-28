package de.FBEditor;

/**
 * Class modelling firmware and box information
 * 
 */
public class FritzBoxFirmware {

	private String FritzboxName;
	private byte boxtype;
	private byte majorFirmwareVersion;
	private byte minorFirmwareVersion;
	private String modFirmwareVersion;
	private String language;

	/**
	 * Firmware Constructor using Strings
	 * 
	 * @param boxtype
	 * @param majorFirmwareVersion
	 * @param minorFirmwareVersion
	 * @param modFirmwareVersion
	 * @param language
	 */
	public FritzBoxFirmware(String FritzboxName, String boxtype,
			String majorFirmwareVersion, String minorFirmwareVersion,
			String modFirmwareVersion, String language) {
		this.FritzboxName = FritzboxName;
		this.boxtype = Byte.parseByte(boxtype);
		this.majorFirmwareVersion = Byte.parseByte(majorFirmwareVersion);
		this.minorFirmwareVersion = Byte.parseByte(minorFirmwareVersion);
		this.modFirmwareVersion = modFirmwareVersion;
		this.language = language;
	}

	public String getFritzboxName() {
		return FritzboxName;
	}

	/**
	 * @return Returns the boxtype.
	 */
	public final byte getBoxType() {
		return boxtype;
	}

	/**
	 * @return Returns the majorFirmwareVersion.
	 */
	public final byte getMajorFirmwareVersion() {
		return majorFirmwareVersion;
	}

	/**
	 * @return Returns the minorFirmwareVersion.
	 */
	public final byte getMinorFirmwareVersion() {
		return minorFirmwareVersion;
	}

	/**
	 * @return Returns the minorFirmwareVersion.
	 */
	public final String getModFirmwareVersion() {
		return modFirmwareVersion;
	}

	/**
	 * @return Returns the majorFirmwareVersion.
	 */
	public final String getFirmwareVersion() {
		String boxtypeStr = Byte.toString(boxtype);
		String majorStr = Byte.toString(majorFirmwareVersion);
		String minorStr = Byte.toString(minorFirmwareVersion);
		if (boxtypeStr.length() == 1) {
			boxtypeStr = "0" + boxtypeStr;
		}
		if (majorStr.length() == 1) {
			majorStr = "0" + majorStr;
		}
		if (minorStr.length() == 1) {
			minorStr = "0" + minorStr;
		}
		return boxtypeStr + "." + majorStr + "." + minorStr; // +
																// modFirmwareVersion;
	}

	public String getBoxName() {
		switch (boxtype) {
		case 6:
			return "FRITZ!Box Fon";
		case 8:
			return "FRITZ!Box Fon WLAN";
		case 9:
			return "Fritz!Box SL WLAN";
		case 10:
			return "Fritz!Box SL";
		case 11:
			return "FRITZ!Box ata";
		case 12:
			return "FRITZ!Box 5050";
		case 14:
			return "FRITZ!Box 7050";
		case 15:
			return "Eumex 300 IP";
		case 16:
			return "FRITZ!Box 3050";
		case 17:
			return "FRITZ!Box 2030";
		case 19:
			return "FRITZ!Box 3070";
		case 20:
			return "FRITZ!Box 2070";
		case 21:
			return "FRITZ!Box 3030";
		case 23:
			return "FRITZ!Box 5010";
		case 25:
			return "FRITZ!Box 5012";
		case 28:
			return "Speedport W501V";
		case 29:
			return "FRITZ!Box 7170";
		case 30:
			return "FRITZ!Box 7140";
		case 38:
			return "FRITZ!Box 7150";
		case 40:
			return "FRITZ!Box 7141";
		case 43:
			return "FRITZ!Box 5140";
		case 44:
			return "FRITZ!Box 3130";
		case 49:
			return "FRITZ!Box 3170";
		case 50:
			return "FRITZ!Box 3131";
		case 51:
			return "FRITZ!Box 2170";
		case 54:
			return "FRITZ!Box 7270";
		case 67:
			return "FRITZ!Box 3270";
		case 73:
			return "FRITZ!Box 7240";
		case 74:
			return "FRITZ!Box 7270v3";
		case 84:
			return "FRITZ!Box 7390";
		case 85:
			return "FRITZ!Box 6360 Cable";
		case 87:
			return "FRITZ!Box 7112";
		case 93:
			return "Speedport W501V";
		case 96:
			return "FRITZ!Box 3270";
		case 101:
			return "Speedport W701V";
		case 102:
			return "Speedport W900V";
		default:
			return "unknown";
		}
	}
}