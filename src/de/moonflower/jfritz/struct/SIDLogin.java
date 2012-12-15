package de.moonflower.jfritz.struct;

//import java.io.IOException;
import de.FBEditor.struct.HttpPost;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;

//import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.utils.Debug;

//import de.moonflower.jfritz.utils.JFritzUtils;
public class SIDLogin {

 private static boolean sidLogin;
 private static boolean sidLoginLua; // 28.11.2012
 private static boolean isLoginOld;
 private static boolean isLogOut;
 private static String sessionId;
 private static String TsessionId;
 private static String sidResponse;
 private final static String LOGIN_SID_LUA = "/login_sid.lua"; // 28.11.2012
 private final static String POSTDATA_LOGIN_SID_LUA_RESPONSE = "page=/login_sid.lua&response="; // 28.11.2012
 private final static String POSTDATA_LOGIN_XML = "getpage=../html/login_sid.xml";
 private final static String POSTDATA_LOGIN_XML_RESPONSE = "getpage=../html/login_sid.xml&login:command/response="; // 20.10.2012
 private final static String PATTERN_WRITE_ACCESS = "<iswriteaccess>([^<]*)</iswriteaccess>";
 private final static String PATTERN_CHALLENGE = "<Challenge>([^<]*)</Challenge>";
 private final static String PATTERN_SID = "<SID>([^<]*)</SID>"; // 18.10.2012
 private static SIDLogin INSTANCE;
 private static String UrlLogoutFB = "";

 public SIDLogin() {
  sidLogin = false;
  sidLoginLua = false;
  isLoginOld = false;
  isLogOut = false;
  sessionId = "0000000000000000";
  TsessionId = "0000000000000000";
  sidResponse = "";
//  UrlLogoutFB = "";
  INSTANCE = null; // 06.11.2012
 }

 public static void check(String box_name, String urlstr, String box_password, String sRetSID) {
  try {
   //
   //  urlstr = "http://" + "192.168.178.1" + "/cgi-bin/webcm";
   //  box_password = "0000";
   URL Url2 = new URL(urlstr);
   UrlLogoutFB = Url2.getProtocol() + "://" + Url2.getHost() + "/cgi-bin/webcm";
   String sUrl = Url2.getProtocol() + "://" + Url2.getHost() + "";
   String sPostdata = "";

   HttpPost http = new HttpPost(); // 23.11.2012
   TsessionId = "1 --- " + urlstr;

   String login_xml = http.Post(urlstr, "sid=" + sRetSID + "&" + POSTDATA_LOGIN_XML); // 23.11.2012
   TsessionId = "1";
   Pattern writeAccessPattern = Pattern.compile(PATTERN_WRITE_ACCESS);
   Matcher matcher = writeAccessPattern.matcher(login_xml);
   if (matcher.find()) {

    int writeAccess = Integer.parseInt(matcher.group(1));

    if (writeAccess == 0) { // answer challenge
     //    try {
     String challenge = "";
     Pattern challengePattern = Pattern.compile(PATTERN_CHALLENGE);
     Matcher challengeMatcher = challengePattern.matcher(login_xml);
     if (challengeMatcher.find()) {
      challenge = challengeMatcher.group(1);
      TsessionId = "1 -> " + challenge;
      // replace all unicodecharacters greater than 255 with
      // the character '.'
      for (int i = 0; i < box_password.length(); i++) {
       int codePoint = box_password.codePointAt(i);
       if (codePoint > 255) {
        box_password = box_password.substring(0, i)
                + '.' + box_password.substring(i + 1);
       }
      }

      String pwd = challenge + "-" + box_password;

      MessageDigest m = null;
      try {
       m = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException ex) {
       Logger.getLogger(SIDLogin.class.getName()).log(Level.SEVERE, null, ex);
      }
      String md5Pass = "";
      //      byte passwordBytes[] = null;
      byte[] passwordBytes = new byte[0]; // 30.10.2012
      try {
       passwordBytes = pwd.getBytes("UTF-16LE");
       m.update(passwordBytes, 0, passwordBytes.length);
       md5Pass = new BigInteger(1, m.digest()).toString(16);
      } catch (UnsupportedEncodingException e) {
       Debug.error("UTF-16LE encoding not supported by your system. Can not communicate with FRITZ!Box!");
      }

      sidResponse = challenge + '-' + md5Pass;
      TsessionId = "1 -> " + sidResponse;
      //      Debug.debug("Challenge: " + challenge + " Response: " + sidResponse);

      login_xml = http.Post(urlstr, "sid=" + sRetSID + "&" + POSTDATA_LOGIN_XML_RESPONSE + sidResponse); // 22.11.2012

      //      Debug.debug("login_xml Response: " + login_xml);

      writeAccessPattern = Pattern.compile(PATTERN_WRITE_ACCESS);
      Matcher matcher1 = writeAccessPattern.matcher(login_xml);

      if (matcher1.find()) {

       writeAccess = Integer.parseInt(matcher1.group(1));

       if (writeAccess == 1) { // answer Response

        Pattern sidPattern = Pattern.compile(PATTERN_SID);
        Matcher sidMatcher = sidPattern.matcher(login_xml);

        if (sidMatcher.find()) {
         sidLogin = true;
         sessionId = sidMatcher.group(1);
         TsessionId = "1s -> " + sessionId;
         // sRetSID = sessionId; // 18.10.2012
         //         Debug.debug("login_xml sessionId Response: " + sessionId);
        }

       } else {
        sidLogin = false;
       }

      }

      //      Debug.debug("Challenge: " + challenge + " Response: " + sidResponse);
     } else {
      //      Debug.error("Could not determine challenge in login_sid.xml");
     }
     //    } catch (NoSuchAlgorithmException e) {
     //     Debug.netMsg("MD5 Algorithm not present in this JVM!");
     //     Debug.error(e.toString());
     //     e.printStackTrace();
     //    }
    } else if (writeAccess == 1) { // no challenge, use SID directly
     Pattern sidPattern = Pattern.compile(PATTERN_SID);
     Matcher sidMatcher = sidPattern.matcher(login_xml);
     if (sidMatcher.find()) {
      sidLogin = true;
      sessionId = sidMatcher.group(1);
      TsessionId = "2s -> " + sessionId;
     }
    } else {
     //    Debug.error("Could not determine writeAccess in login_sid.xml");
    }
    // Debug.errDlg(Integer.toString(writeAccess) + " " + sessionId);
   } else {
    sidLogin = false;
    sidLoginLua = false;

    login_xml = http.Post(sUrl + LOGIN_SID_LUA, "sid=" + sRetSID); // 23.11.2012
//    System.out.println(login_xml);
    TsessionId = "3";
    Pattern SIDwriteAccessPattern = Pattern.compile(PATTERN_SID);
    Matcher matcherSID = SIDwriteAccessPattern.matcher(login_xml);

    if (matcherSID.find()) {

     String SIDwriteAccess = matcherSID.group(1);

     if ("0000000000000000".equals(SIDwriteAccess)) { // answer challenge 
      String challenge = "";
      Pattern challengePattern = Pattern.compile(PATTERN_CHALLENGE);
      Matcher challengeMatcher = challengePattern.matcher(login_xml);
      if (challengeMatcher.find()) {
       challenge = challengeMatcher.group(1);
       TsessionId = "3 -> " + challenge;
       // replace all unicodecharacters greater than 255 with
       // the character '.'
       for (int i = 0; i < box_password.length(); i++) {
        int codePoint = box_password.codePointAt(i);
        if (codePoint > 255) {
         box_password = box_password.substring(0, i)
                 + '.' + box_password.substring(i + 1);
        }
       }

       String pwd = challenge + "-" + box_password;

       MessageDigest m = null;
       try {
        m = MessageDigest.getInstance("MD5");
       } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(SIDLogin.class.getName()).log(Level.SEVERE, null, ex);
       }
       String md5Pass = "";
       byte[] passwordBytes = new byte[0]; // 30.10.2012
       try {
        passwordBytes = pwd.getBytes("UTF-16LE");
        m.update(passwordBytes, 0, passwordBytes.length);
        md5Pass = new BigInteger(1, m.digest()).toString(16);
       } catch (UnsupportedEncodingException ex) {
        Logger.getLogger(SIDLogin.class.getName()).log(Level.SEVERE, null, ex);
       }

       sidResponse = challenge + '-' + md5Pass;
       TsessionId = "3 -> " + sidResponse;
       Debug.debug("Challenge: " + challenge + " Response: " + sidResponse);

       sPostdata = POSTDATA_LOGIN_SID_LUA_RESPONSE + sidResponse;
       login_xml = http.Post(sUrl + LOGIN_SID_LUA, sPostdata);

//       System.out.println("Login: " + "   " + login_xml);
       Debug.debug("login_xml Response: " + login_xml);

       SIDwriteAccessPattern = Pattern.compile(PATTERN_SID);
       Matcher matcher1 = SIDwriteAccessPattern.matcher(login_xml);

       if (matcher1.find()) {

        SIDwriteAccess = matcher1.group(1);

        if (!"0000000000000000".equals(SIDwriteAccess)) { // answer Response

         Pattern sidPattern = Pattern.compile(PATTERN_SID);
         Matcher sidMatcher = sidPattern.matcher(login_xml);

         if (sidMatcher.find()) {
          sidLogin = true;
          sidLoginLua = true;
          sessionId = sidMatcher.group(1);
          TsessionId = "3s -> " + sessionId;
          Debug.debug("login_xml sessionId Response: " + sessionId);
         }

        } else {
         sidLogin = false;
         sidLoginLua = false;
        }
       }

//         Debug.debug("Challenge: " + challenge + " Response: " + sidResponse);
      } else {
//         Debug.error("Could not determine challenge in login_sid.xml");
      }

     } else if (!"0000000000000000".equals(SIDwriteAccess)) { // no challenge, use SID directly
      Pattern sidPattern = Pattern.compile(PATTERN_SID);
      Matcher sidMatcher = sidPattern.matcher(login_xml);
      if (sidMatcher.find()) {
       sidLogin = true;
       sidLoginLua = true;
       sessionId = sidMatcher.group(1);
       TsessionId = "4s -> " + sessionId;
      }
     } else {
      Debug.error("Could not determine SIDwriteAccess in login_sid.lua");
     }
//     Debug.errDlg(SIDwriteAccess + " " + sessionId);

    } else {
     sidLogin = false;
     sidLoginLua = false;
     TsessionId = "1 -> 1";

     sPostdata = "getpage=../html/de/menus/menu2.html&login:command/password=" + box_password;
//    login_xml = http.Post( urlstr, sPostdata); // 23.11.2012
     login_xml = http.Post(sUrl + "/cgi-bin/webcm", sPostdata); // 23.11.2012

     if ("".equals(login_xml)) {
      isLoginOld = true; //sidLogin = true;
     }
     //   System.out.println("Login: " + isLoginOld + "   " + login_xml);
     TsessionId = "1 -> 2";
    }

   }
  } catch (MalformedURLException ex) {
   Logger.getLogger(SIDLogin.class.getName()).log(Level.SEVERE, null, ex);
  }

 }

 public static String getTsessionId() {
  return TsessionId;
 }

 public static boolean isLogin() {
  return isLoginOld;
 }

 public static boolean isSidLogin() {
  return sidLogin;
 }

 public static boolean isSidLoginLua() {
  return sidLoginLua;
 }

 public static String getResponse() {
  return sidResponse;
 }

 public static String getSessionId() {
  return sessionId;
 }

 public static boolean Logout() {
  // "Von der Benutzeroberfläche abmelden"

  URL Url2;
  String sUrl = "";
  try {
   Url2 = new URL(UrlLogoutFB);
//   UrlLogoutFB = Url2.getProtocol() + "://" + Url2.getHost() + "/cgi-bin/webcm";
   sUrl = Url2.getProtocol() + "://" + Url2.getHost() + "";
  } catch (MalformedURLException ex) {
   Logger.getLogger(SIDLogin.class.getName()).log(Level.SEVERE, null, ex);
  }

  HttpPost http = new HttpPost(); // 23.11.2012
  String login_xml = "";
  isLogOut = false;

  if (sidLogin == true) {

   login_xml = http.Post(UrlLogoutFB, "sid=" + sessionId + "&security:command/logout=&getpage=../html/confirm_logout.html"); // 23.11.2012  

   if ("".equals(login_xml)) {
    sessionId = "0000000000000000";
    sidLogin = false;
    sidLoginLua = false;
    isLoginOld = false;
    isLogOut = true;
   }

//   System.out.println("Logout: " + sessionId + "   " + login_xml);

  } else if (sidLoginLua == true) {

   login_xml = http.Post(sUrl + "/logout.lua", "sid=" + sessionId); // 23.11.2012  

   if ("".equals(login_xml)) {
    sessionId = "0000000000000000";
    sidLogin = false;
    sidLoginLua = false;
    isLoginOld = false;
    isLogOut = true;
   }

  } else if (isLoginOld == true) {

   login_xml = http.Post(UrlLogoutFB, "getpage=../html/de/menus/menu2.html&security:command/logout=" + "1"); // 23.11.2012  

   if ("".equals(login_xml)) {
    sessionId = "0000000000000000";
    sidLogin = false;
    sidLoginLua = false;
    isLoginOld = false;
    isLogOut = true;
   }
  }
  return sidLogin;
 }

 public static boolean isLogout() {
  return isLogOut;
 }

 public SIDLogin getInstance() {
  if (INSTANCE == null) {
   INSTANCE = new SIDLogin();
  }
  return INSTANCE;
 }
}

