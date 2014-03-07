/*
 * Created on 08.06.2005
 *  
 */
package de.FBEditor.utils;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/**
 * Encrypting / decrypting of Strings 
 *
 */
public class Encryption {
	private static final String KEY_STRING = "193-155-248-97-234-56-100-241"; //$NON-NLS-1$

	public static String encrypt(String source) {
		try {
			// Get our secret key
			Key key = getKey();

			// Create the cipher
			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //$NON-NLS-1$

			// Initialize the cipher for encryption
			desCipher.init(Cipher.ENCRYPT_MODE, key);

			// Our cleartext as bytes
			byte[] cleartext = source.getBytes();

			// Encrypt the cleartext
			byte[] ciphertext = desCipher.doFinal(cleartext);

			// Return a String representation of the cipher text
			return getString(ciphertext);
		} catch (Exception e) {
            Debug.error(e.toString());
		}
		return null;
	}

	public static String generateKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("DES"); //$NON-NLS-1$
			SecretKey desKey = keygen.generateKey();
			byte[] bytes = desKey.getEncoded();
			return getString(bytes);
		} catch (Exception e) {
            Debug.error(e.toString());
			return null;
		}
	}

	public static String decrypt(final String source) {
		try {
			String sourceStr = source;
			if (sourceStr == null)
				sourceStr = "";
			// Get our secret key
			Key key = getKey();

			// Create the cipher
			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //$NON-NLS-1$

			// Encrypt the cleartext
			byte[] ciphertext = getBytes(sourceStr);

			// Initialize the same cipher for decryption
			desCipher.init(Cipher.DECRYPT_MODE, key);

			// Decrypt the ciphertext
			byte[] cleartext = desCipher.doFinal(ciphertext);

			// Return the clear text
			return new String(cleartext);
		} catch (Exception e) {
            Debug.error(e.toString());
		}
		return null;
	}

	private static Key getKey() {
		try {
			byte[] bytes = getBytes(KEY_STRING);
			DESKeySpec pass = new DESKeySpec(bytes);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES"); //$NON-NLS-1$
			SecretKey s = skf.generateSecret(pass);
			return s;
		} catch (Exception e) {
            Debug.error(e.toString());
		}
		return null;
	}

	/**
	 * Returns true if the specified text is encrypted, false otherwise
	 */
	public static boolean isEncrypted(String text) {
		// If the string does not have any separators then it is not
		// encrypted
		if (text.indexOf('-') == -1) {
			///System.out.println( "text is not encrypted: no dashes" );
			return false;
		}

		StringTokenizer st = new StringTokenizer(text, "-", false); //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.length() > 3) {
				// System.out.println( "text is not encrypted: length of token
				// greater than 3: " + token );
				return false;
			}
			for (int i = 0; i < token.length(); i++) {
				if (!Character.isDigit(token.charAt(i))) {
					// System.out.println( "text is not encrypted: token is not
					// a digit" );
					return false;
				}
			}
		}
		// System.out.println( "text is encrypted" );
		return true;
	}

	private static String getString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			sb.append((int) (0x00FF & b));
			if (i + 1 < bytes.length) {
				sb.append("-"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	private static byte[] getBytes(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StringTokenizer st = new StringTokenizer(str, "-", false); //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			int i = Integer.parseInt(st.nextToken());
			bos.write((byte) i);
		}
		return bos.toByteArray();
	}

}
