package de.FBEditor.utils.upnp;

import java.net.Authenticator;
//import java.net.InetAddress;
import java.net.PasswordAuthentication;
//import java.net.URL;

public class MyAuthenticator extends Authenticator {

	private String mUsername = "";
	private String mPassword = "";
	
	// 01.08.2015
	public MyAuthenticator(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
//		    String promptString = getRequestingPrompt();
//		    System.out.println("Authenticator.getRequestingPrompt(): " + promptString);
//		    String hostname = getRequestingHost();
//		    System.out.println("Authenticator.getRequestingHost(): " + hostname);
//		    InetAddress ipaddr = getRequestingSite();
//		    System.out.println("Authenticator.getRequestingSite(): " + ipaddr);
//		    int port = getRequestingPort();
//		    System.out.println("Authenticator.getRequestingPort(): " + port);
//		    String schemeString = getRequestingScheme();
//		    System.out.println("Authenticator.getRequestingScheme(): " + schemeString);
//		    URL URLString = getRequestingURL();
//		    System.out.println("Authenticator.getRequestingURL(): " + URLString);
//		    RequestorType typeString = getRequestorType();
//		    System.out.println("Authenticator.getRequestorType(): " + typeString);
//		    String protocolString = getRequestingProtocol();
//		    System.out.println("Authenticator.getRequestingProtocol(): " + protocolString);

//		    String username = "name";
//		    String password = "password";

		    return new PasswordAuthentication(mUsername, mPassword.toCharArray());
		  }
}
