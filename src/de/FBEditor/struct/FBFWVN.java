package de.FBEditor.struct;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Erwin
 */
public class FBFWVN {

 private boolean isOK = false;
 private boolean isFritzboxLanguageOK = false;
 // private static String sFBFWV10 = "";
 private static String sFBFWV81 = "";
 private static String sFBFWV82 = "";
 private static String sFBFWV83 = "";
 private static String sFBFWV90 = "";
 private static String sFBFWV100 = "";
 private static String sFBFWV110 = "";
 private static String sRetFBV = "";
 private static String sRetFBFW = "";
 private static String sRetFBFWV = "";

 /**
  * 
  * @param sFBFWV_IN
  */
 public FBFWVN(String sFBFWV_IN) {
 // sRetFBFWV = sFBFWV_IN;
  FBFWVN0(sFBFWV_IN);
 }

 /**
  * 
  * @param sFBFWV_IN
  */
 public final void FBFWVN0(String sFBFWV_IN)
 {

  if (sFBFWV_IN == null) {
   sFBFWV_IN = "";
  }

  String sFBV = "";
  String sFBFW = "";
  String sFBFWV = "";

  sFBFWV81 = "0";
  sFBFWV82 = "0";
  sFBFWV83 = "0";
  sFBFWV90 = "";
  sFBFWV100 = "";
  sFBFWV110 = "";

  isOK = false;
  isFritzboxLanguageOK = false;

// 15.04.2015
  int ii = 0;
  String ss = "";

  Pattern status1Pattern = Pattern.compile("<body>([^-<]*)-([^-<]*)-([^-<]*)-([^-<]*)-([^-<]*)-([^-<]*)-([^-<]*)-([^-<]*)[|-]?([^-<]*)[|-]?([^-<]*)[|-]?([^-<]*)[|-]?([^-<]*)[|-]?([^-<]*)[|-]?([^-<]*)[|-]?([^<]*)</body>", Pattern.CASE_INSENSITIVE);
  Matcher status1Matcher = status1Pattern.matcher(sFBFWV_IN);

  if (status1Matcher.find(0) == true) {

   String[] S10 = new String[status1Matcher.groupCount() + 1];

   if (status1Matcher.groupCount() >= 9) {

    for (int i = 0; i <= status1Matcher.groupCount(); i++) {
     // 15.04.2015
     ss = status1Matcher.group(i);
     S10[i - ii] = ss;
     //System.out.println(S10[i]);
    }

    if (S10[8].length() >= 5) {
     sFBFW = S10[1] + " " + S10[8].substring(0, S10[8].length() - 4) + "." + S10[8].substring(S10[8].length() - 4, S10[8].length() - 2) + "." + S10[8].substring(S10[8].length() - 2, S10[8].length());
     sFBV = S10[1];
     sFBFWV = S10[8].substring(0, S10[8].length() - 4) + "." + S10[8].substring(S10[8].length() - 4, S10[8].length() - 2) + "." + S10[8].substring(S10[8].length() - 2, S10[8].length());

     // sFBFWV10 = S10[1];
     sFBFWV81 = S10[8].substring(0, S10[8].length() - 4);
     sFBFWV82 = S10[8].substring(S10[8].length() - 4, S10[8].length() - 2);
     sFBFWV83 = S10[8].substring(S10[8].length() - 2, S10[8].length());
     // sFBFWV90 = S10[9];

     if (status1Matcher.groupCount() >= 9) {
      sFBFWV90 = S10[9];
     }
     if (status1Matcher.groupCount() >= 10) {
      sFBFWV100 = S10[10];
     }
     if (status1Matcher.groupCount() >= 11) {
      sFBFWV110 = S10[11];
      if (!"".equals(S10[11]) && S10[11].length() == 2) {
       isFritzboxLanguageOK = true;
      }
     }

     // 15.04.2015
     if (sFBFWV81.length() > 3) {
   	  System.out.println("isOK = false");
     } else {
   	  isOK = true;
     }

    }
   }
  }

  sRetFBV = sFBV;
  sRetFBFW = sFBFW;
  sRetFBFWV = sFBFWV;
 }

 /**
  * 
  * @return
  */
 public final boolean isOK() {
  return isOK;
 }

 /**
  * 
  * @param sFBFWV_IN
  * @return
  */
 public String getFBFWVN(String sFBFWV_IN) {
 // if (sFBFWV_IN == null) sFBFWV_IN = "";
  FBFWVN0(sFBFWV_IN);
  return sFBFWV81;
 }

 /**
  * 
  * @return
  */
 public String getBoxTypeFBFWVN() {
  return sFBFWV81;
 }

 /**
  * 
  * @return
  */
 public String getMajorFBFWVN() {
  return sFBFWV82;
 }

 /**
  * 
  * @return
  */
 public String getMinorFBFWVN() {
  return sFBFWV83;
 }

 /**
  * 
  * @return
  */
 public String getModFBFWVN() {
  return sFBFWV90;
 }

 /**
  * 
  * @return
  */
 public String getFritzbox() {
  return sRetFBV;
 }

 /**
  * 
  * @return
  */
 public String getFirmware() {
  return sRetFBFWV;
 }

 /**
  * 
  * @return
  */
 public String getFritzboxFirmware() {
  return sRetFBFW;
 }

 /**
  * 
  * @return
  */
 public String getFritzboxOEM() {
  return sFBFWV100;
 }
 
 /**
  * 
  * @return
  */
 public String getFritzboxLanguage() {
  return sFBFWV110;
 }
 
 /**
  * 
  * @return
  */
 public final boolean isFritzboxLanguage() {
  return isFritzboxLanguageOK;
 }

 /**
  * 
  * @return
  */
 public int getBoxType() {  // Muss Integer sein wegen neuen Firmware Typ > 127
// public byte getBoxType() {  
 // return Byte.parseByte(sFBFWV81);
  return Integer.parseInt(sFBFWV81);  // Muss Integer sein wegen neuen Firmware Typ > 127
 // return sFBFWV81;
 // return boxtype;
 }

 /**
  * 
  * @return
  */
 public final byte getMajorFirmwareVersion() {
  return Byte.parseByte(sFBFWV82);
 // return sFBFWV82;
 // return majorFirmwareVersion;
 }

 /**
  * 
  * @return
  */
 public final byte getMinorFirmwareVersion() {
  return Byte.parseByte(sFBFWV83);
//   return sFBFWV83;
  //return minorFirmwareVersion;
 }

 /**
  * 
  * @return
  */
 public final String getModFirmwareVersion() {
  //return modFirmwareVersion;
  return sFBFWV90;
 }
}
