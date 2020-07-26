package de.FBEditor.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import org.apache.commons.codec.binary.Base64;

public class CalcChecksum {
	
	private static int type = 0;
	private static boolean file = false;
	private static CRC32 crc;
	private static String expected = "";
	private static String last;
	
	public CalcChecksum() {
		/* Initialize crc32 */
		crc = new CRC32();
	}

	private void calchk(String line) {
		/* start of new section? */
		if (type == 0) {
			// Debug.debug(Long.toHexString(crc.getValue()));
			
			String filename;
			if ((filename = Utils.pMatch("\\*\\*\\*\\* CFGFILE:(.*?)$", line, 1)) != null) {
				Debug.debug(line);
				file = true;
				type = 1;
			} else if ((filename = Utils.pMatch("\\*\\*\\*\\* BINFILE:(.*?)$", line, 1)) != null) {
				file = true;
				Debug.debug(line);
				type = 2;
			} else if ((filename = Utils.pMatch("\\*\\*\\*\\* CRYPTEDBINFILE:(.*?)$", line, 1)) != null) {
				file = true;
				Debug.debug(line);
				type = 4;
				// CRYPTEDBINFILE bin file 19.04.2015
			} else if ((filename = Utils.pMatch("\\*\\*\\*\\* B64FILE:(.*?)$", line, 1)) != null) {
				file = true;
				Debug.debug(line);
				type = 5;
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
			// parse filename
			if (file) {
				line = filename + '\0';
				updateCRC(line);
				file = false;
				return;
			}
		} else {
			if (type == 1) { // cfg file (stripcslashes, add '\n' at the end)
				if (line.indexOf("**** END OF FILE") == 0) {
					type = 0;
					if (last != null) {
						updateCRC(last);
					}
					last = null;
					return;
				}
				if (last != null) {
					last = last.replace("\\\\", "\\");
					last = last + '\n';
					updateCRC(last);
				}
				last = line;
				return;
			}
			if (type == 2) {  // bin file
				if (line.indexOf("**** END OF FILE") == 0) {
					type = 0;
					return;
				}
				String hex = line.trim().toLowerCase().replace("\n", "");
				String bin_line = "";
				for (int i = 0; i < hex.length(); i += 2) {
					//int b = Integer.parseInt(hex.substring(i, i + 2), 16);
					
					// FIXME: This is very slow!
					//updateCRC(b);
					bin_line += (char) Integer.parseInt(hex.substring(i, i + 2), 16);
				}
				updateCRC(bin_line);

				return;
			}
			if (type == 4) {  // CRYPTEDBINFILE bin file 19.04.2015
				if (line.indexOf("**** END OF FILE") == 0) {
					type = 0;
					return;
				}
				String hex = line.trim().toLowerCase().replace("\n", "");
				String bin_line = "";
				for (int i = 0; i < hex.length(); i += 2) {
					bin_line += (char) Integer.parseInt(hex.substring(i, i + 2), 16);
				}
				updateCRC(bin_line);

				return;
			}
			if (type == 5) {  // base64 file
				if (line.indexOf("**** END OF FILE") == 0) {
					type = 0;
					return;
				}
				String base64 = line.trim().replace("\n", "");
				byte[] dec = Base64.decodeBase64(base64.getBytes());
				crc.update(dec);

				return;
			}
			if (type == 3) { // variable (remove "=", add '\0' to the end
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
		text = text.replace("\r", ""); // Remove all CRs for calculation
		Pattern p = Pattern.compile("(.*?)\\n", 2);
		for (Matcher matcher = p.matcher(text); matcher.find(); calchk(matcher.group(0).replace("\n", ""))) {
			@SuppressWarnings("unused")
			String temp = matcher.group(0);
		}

		return crc.getValue();
	}

	public static boolean getchecksum() {
		String checksum = Long.toHexString(crc.getValue()).toUpperCase();
		if (checksum.length() < 8)
			checksum = '0' + checksum;
		if (!checksum.equals(expected)) {
			Debug.debug("CHECKSUM FIXED: " + checksum + "(old: " + expected + ")");
			return false;
		} else {
			Debug.debug("CHECKSUM OK: " + checksum);
			return true;
		}
	}

	private void updateCRC(String line) {
		try {
			crc.update(line.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*
	private void updateCRC(int b) {
		crc.update(b);
	}
*/
	
	/*
	 * Calculate new checksum and replace if different
 	 */
	public static String replaceChecksum(String text) {
		String newText = "";
		String checksum;
		if ((checksum = Utils.pMatch("\\*\\*\\*\\* END OF EXPORT (.*?) \\*\\*\\*\\*", text, 1)) != null) {
			CalcChecksum exportsumme = new CalcChecksum();
			String newChecksum = Long.toHexString(exportsumme.getChecksum(text));
			newChecksum = newChecksum.toUpperCase();
			if (!CalcChecksum.getchecksum()) { // 25.07.2020
				if (newChecksum.length() < 8)
					newChecksum = '0' + newChecksum;
				newText = text.replace(checksum, newChecksum);
			} else {
				newText = text;
			}
		} else {
			newText = text;
		}
		return newText;
	}
}
