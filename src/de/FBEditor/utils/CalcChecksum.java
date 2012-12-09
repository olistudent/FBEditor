package de.FBEditor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import de.moonflower.jfritz.utils.Debug;

public class CalcChecksum {

	public CalcChecksum() {
		crc = new CRC32();
	}

	private void calchk(String line) {
		if (type == 0) {
			String filename;
			if ((filename = Utils.pMatch("\\*\\*\\*\\* CFGFILE:(.*?)$", line, 1)) != null) {
				Debug.debug(line);
				file = true;
				type = 1;
			} else if ((filename = Utils.pMatch("\\*\\*\\*\\* BINFILE:(.*?)$", line, 1)) != null) {
				file = true;
				Debug.debug(line);
				type = 2;
			} else {
				if (Utils.pMatch("\\*\\*\\*\\* (.+) CONFIGURATION EXPORT", line, 0) != null) {
					file = false;
					Debug.debug(line);
					type = 3;
					return;
				}
				if (Utils.pMatch("\\*\\*\\*\\* END OF EXPORT (.*?)", line, 0) != null) {
					expected = line.substring(19, 27);
					Debug.debug(line);
				}
			}
			if (file) {
				line = filename + '\0';
				updateCRC(line);
				file = false;
				return;
			}
		} else {
			if (type == 1) {
				if (line.indexOf("**** END OF FILE") == 0) {
					type = 0;
					if (last != null) {
						updateCRC(last);
					}
					last = null;
					return;
				}
				if (last != null) {
					last = last + '\n';
					updateCRC(last);
				}
				last = line;
				return;
			}
			if (type == 2) {
				if (line.indexOf("**** END OF FILE") == 0) {
					type = 0;
					return;
				}
				String hex = line.trim().toLowerCase();
				for (int i = 0; i < hex.length(); i += 2) {
					int b = Integer.parseInt(hex.substring(i, i + 2), 16);
					updateCRC(b);
				}

				return;
			}
			if (type == 3) {
				if (line.indexOf("****") != -1) {
					type = 0;
					calchk(line);
				}
				if (Utils.pMatch("(\\S*?)=(\\S*?)", line, 0) != null) {
					line = line.replaceFirst("=", "");
					line = line + '\0';
					updateCRC(line);
					return;
				} else {
					return;
				}
			}
		}
	}

	public long getChecksum(String text) {
		Pattern p = Pattern.compile("(.*?)\\n", 2);
		for (Matcher matcher = p.matcher(text); matcher.find(); calchk(matcher.group(0).replace("\n", ""))) {
			String temp = matcher.group(0);
		}

		return crc.getValue();
	}

	public static boolean getchecksum() {
		String checksum = Long.toHexString(crc.getValue()).toUpperCase();
		if (checksum.length() < 8)
			checksum = '0' + checksum;
		if (!checksum.equals(expected)) {
			Debug.debug("WRONG CHECKSUM " + checksum + " vs. " + expected);
			Debug.debug("CHECKSUM FIXED");
			return false;
		} else {
			Debug.debug("CHECKSUM OK");
			return true;
		}
	}

	private void updateCRC(String line) {
		crc.update(line.getBytes());
		Debug.debug(Long.toHexString(crc.getValue()));
	}

	private void updateCRC(int b) {
		crc.update(b);
		Debug.debug(Long.toHexString(crc.getValue()));
	}
	
	/*
	 * dummy function, checksum calculation doesn't work at the moment
	 */
	public static String replaceChecksum(String text) {
		return text;
	}
	/*
	 * Calculate new checksum and replace if different
 	 */
	/*
	public static String replaceChecksum(String text) {
		String newText = "";
		String checksum;
		if ((checksum = Utils.pMatch("\\*\\*\\*\\* END OF EXPORT (.*?) \\*\\*\\*\\*", text, 1)) != null) {
			CalcChecksum exportsumme = new CalcChecksum();
			String newChecksum = Long.toHexString(exportsumme.getChecksum(text));
			newChecksum = newChecksum.toUpperCase();
			if (!CalcChecksum.getchecksum())
				newText = text.replace(checksum, newChecksum);
			else
				newText = text;
		} else {
			newText = text;
		}
		return newText;
	}
*/

	private static int type = 0;
	private static boolean file = false;
	private static CRC32 crc;
	private static String expected = "";
	private static String last;

}
