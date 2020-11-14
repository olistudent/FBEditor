package de.FBEditor;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import de.FBEditor.struct.SIDLogin; // 01.03.2014
import de.FBEditor.utils.StringPartNoTransferEncoding;

/**
 * Import configuration from FBox
 * 
 */
public class ImportData implements Runnable {

	public ImportData() {
	}

	public void run() {
		String data = "";
		int statusCode = 0;
		String url = (new StringBuilder("http://"))
				.append(FBEdit.getInstance().getbox_address())
				.append("/cgi-bin/firmwarecfg").toString();
		PostMethod mPost = new PostMethod(url);

        // Kennwort der Sicherungsdatei
		String ConfigImExPwd = "";
/*
		String box_ConfigImExPwd = FBEdit.getInstance().getbox_ConfigImExPwd();
		System.out.println("box.ConfigImExPwd: " + box_ConfigImExPwd);

		if ( !"".equals(box_ConfigImExPwd) ) {
            // Hier kann man ein PopUp Dialog verwenden
			// mit der Frage, mit oder ohne Kennwort Lesen

            FBEdit.getInstance().getConfigImExPwd(false);

            if ( FBEdit.isConfigImExPwdOk() == true ) {
		    	box_ConfigImExPwd = FBEdit.getInstance().getbox_ConfigImExPwd();
	    		// ConfigImExPwd = ""; // Abbrechen -> ohne Kennwort
    			ConfigImExPwd = box_ConfigImExPwd; // OK -> mit Kennwort	
            }
			System.out.println("ConfigImExPwd: " + ConfigImExPwd + " -> " + FBEdit.isConfigImExPwdOk());
		}
*/
		try {
			String sid = SIDLogin.getSessionId();

			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(8000);

			Part[] parts = null;
			if (SIDLogin.isSidLogin()) {
				// with session id
				parts = new Part[3];
				parts[0] = new StringPartNoTransferEncoding("sid", sid);
				//parts[1] = new StringPartNoTransferEncoding(
				//		"ImportExportPassword", "");
				parts[1] = new StringPartNoTransferEncoding(
						"ImportExportPassword", ConfigImExPwd);
				parts[2] = new StringPartNoTransferEncoding("ConfigExport", "");
			} else {
				// old style, no session id
				parts = new Part[2];
				//parts[0] = new StringPartNoTransferEncoding(
				//		"ImportExportPassword", "");
				parts[0] = new StringPartNoTransferEncoding(
						"ImportExportPassword", ConfigImExPwd);
				parts[1] = new StringPartNoTransferEncoding("ConfigExport", "");
			}

			mPost.setRequestEntity(new MultipartRequestEntity(parts, mPost
					.getParams()));

			statusCode = client.executeMethod(mPost);
			BufferedInputStream bis = new BufferedInputStream(
					mPost.getResponseBodyAsStream());
			byte buf[] = new byte[4096];
			StringBuffer sb = new StringBuffer();
			int len;
			while ((len = bis.read(buf)) > 0)
				sb.append(new String(buf, 0, len));
			data = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mPost.releaseConnection();
		}
		if (!(statusCode == 200 && checkResponse(data))) {

			JOptionPane.showMessageDialog(FBEdit.getInstance().getframe(),
					FBEdit.getMessage("utils.read_error") + "\n" + upnp(),
					FBEdit.getMessage("main.error"), 0);

//			data = FBEdit.getMessage("main.error") + "!";
			data = upnp(); // 27.04.2018

			// try to reconnect
			FBEdit.makeNewConnection(false);
		}
		// Put Export into Textpane
		FBEdit.getInstance().setData(data);
	}

	private boolean checkResponse(String data) {
		return data.startsWith("****");
	}
	
	private String upnp() { // 27.04.2018

		String s2FA = "";

		s2FA = FBEdit.getInstance().getupnp2FAsid();
		
		if (!s2FA.equals("")) {
			return s2FA;	
		}
		
//		return "sid=" + UPNPUtils.getSIDUPNP() + "\n" + "2FA -> " + UPNPUtils.get2FAUPNP() + "\n";
		return FBEdit.getMessage("main.error") + "!";
	}
}
