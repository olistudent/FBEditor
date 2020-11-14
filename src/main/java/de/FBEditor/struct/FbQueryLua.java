package de.FBEditor.struct;

public class FbQueryLua
{

  private static String sRetv5value = "";

  public FbQueryLua()
  {
	  
  }
  
  private static String sLeft(String sStr, int nPos)
  {
   String returnString = "";
   
   if (sStr == null) sStr = "";
//System.out.println(sStr);  
   if (nPos > sStr.length()) {
    nPos = sStr.length();
   }
   
   returnString = sStr.substring(0, nPos);
//System.out.println(sStr);
//System.out.println(returnString);
   return returnString;
  }

  private static String sRight(String sStr, int nPos)
  {
   String returnString = "";
   
   if (sStr == null) sStr = "";

   if (nPos > sStr.length()) {
    nPos = sStr.length();
   }
   
   returnString = sStr.substring(sStr.length() - nPos, sStr.length());
//System.out.println(sStr); 
   return returnString;
  }
  
  private static String sMid(String sStr, int nPos, int nAnz)
  {
   String returnString = "";
//System.out.println(sStr);
   if (sStr == null) sStr = "";

   if ( (nPos > 0) && (nPos <= sStr.length()) )
   {
    if (nPos > 0) nPos = nPos - 1;
    if (nPos >= sStr.length()) {
     nPos = sStr.length() - 1;
//     nAnz = 1;
    }
   
//    if ((nPos + nAnz) > sStr.length()) nAnz = 0;
    if ((nPos + nAnz) > sStr.length()) nAnz = sStr.length() - nPos;

    returnString = sStr.substring(nPos, nPos + nAnz);
   }
//System.out.println(sStr);
   return returnString;
  }

  private static String sRtrimLF(String sStr)
  {
   String returnString = "";
   
   if (sStr == null) sStr = "";
   
   String sTmp = sStr;

   if (sStr.length() > 0) {

    for ( int i = 0; i <= sStr.length(); i++ ) {
     if ( sRight(sTmp, 1).equals("\n") )
     {
      sTmp = sLeft(sTmp, sTmp.length() -1);
//      System.out.println(sTmp.length() + " D LF " + i);
     } else {
//      System.out.println(sTmp.length() + " B LF " + i);
      break;
     }
//      System.out.println(sTmp.length() + " D LF " + i);
    }

    returnString = sTmp;
   }
//System.out.println(sStr);
   return returnString;
  }

/*
  public static String sRetIn;
  public static String v5string;
  public static String v5name;
  public static String v5value;
  public static int v5index;


  public FbQueryLua(String sRetIn, String v5string, String v5name, String v5value, int v5index)
  {
   this.sRetIn = sRetIn; 
      this.v5string = v5string;
         this.v5name = v5name;
            this.v5value = v5value;
               this.v5index = v5index;
  }
*/

  //@SuppressWarnings({"SuspiciousIndentAfterControlStatement", "ConvertToStringSwitch"})
  public static int sQueryLuaAll(String sRetIn, String v5string, String v5name, String v5value, int v5index)
  {
   String sStrIn = ""; String sTmpFirst = ""; String sTmpLast = "";
   String sTmp1 = ""; String sTmp2 = ""; String sTmp3 = ""; String sTmp4 = "";

   int nTmp = 0; int nTmp1 = 0; int nTmp2 = 0;
   int nTmp4 = 0;
   int nRet = 0; int nDQ = 0; int nLF = 0; int nAMP = 0; int nKomma = 0;
   int nDP; int nSP; // 04.07.2018

   String sQueryStringIn = ""; String sQueryName = ""; String sQueryRetValue = ""; String sQueryNameIn = "";
   String[] sQueryNameArray = new String[10];

   int nQueryIndex = 0;
   int nIsSID = 0;

   v5value = "";
   sStrIn = sRetIn;
//   v5string = " Test "; 
   sQueryStringIn = v5string.trim();
//   System.out.println(sQueryStringIn + " sQueryStringIn");
   sQueryName = v5name.trim(); sQueryNameIn = v5name.trim();
   nQueryIndex = v5index;

   if ( sLeft(sQueryStringIn, 1).equals("?") )
   {
    sQueryStringIn = sRight(sQueryStringIn, sQueryStringIn.length() -1); 
//    System.out.println(sQueryStringIn);
   }

//   System.out.println(sLeft(sQueryStringIn, 1));
//   System.out.println(FbQueryLua.sLeft(sQueryStringIn, 1));
//   sTmp1 = sLeft(sQueryStringIn, 1);
//   System.out.println(sTmp1);

   if ( sLeft(sQueryStringIn, 1).equals("&") )
   {
//    System.out.println(FbQueryLua.sLeft(sQueryStringIn, 1));
    sQueryStringIn = sRight(sQueryStringIn, sQueryStringIn.length() -1);
//    System.out.println(sQueryStringIn);
   }

   sStrIn = sRtrimLF(sStrIn);
//   System.out.println(sStrIn.length() + " sStrIn");
//   System.out.println("sStrIn: " + sStrIn);
   
   if ( (sLeft(sStrIn, 1).equals("{")) && (sRight(sStrIn, 1).equals("}")) ) { // 04.07.2018
    if ( (sLeft(sStrIn, 2).equals("{\n") ) && (sRight(sStrIn, 1).equals("}") ) ) {
     sStrIn = sLeft(sStrIn, sStrIn.length() -1); sStrIn = sRight(sStrIn, sStrIn.length() -2);
    } else if ( (sLeft(sStrIn, 1).equals("{")) && (sRight(sStrIn, 1).equals("}")) ) { // 04.07.2018
     sStrIn = sLeft(sStrIn, sStrIn.length() -1); sStrIn = sRight(sStrIn, sStrIn.length() -1); // 04.07.2018
    }

//    System.out.println(sStrIn);
//    System.out.println(sStrIn.length());

    for ( int i = 1; i <= sStrIn.length(); i++ ) {
     if ( sMid(sStrIn, i, 1).equals("\n") ) nLF = nLF + 1;
     //     System.out.println(i + ": " + nLF);
    }
    if ( nLF >= 0 ) nLF = nLF + 1;
//    System.out.println(nLF);

    for ( int i = 1; i <= sQueryStringIn.length(); i++ ) {
     if ( sMid(sQueryStringIn, i, 1).equals("&") ) nAMP = nAMP + 1;
//     System.out.println(i + ": " + nAMP);
    }
    if ( nAMP >= 0 ) nAMP = nAMP + 1;
//    System.out.println(nAMP);

    if ( nLF >= nAMP )
    {
     sQueryNameArray = new String[nLF + 1];
    } else {
     sQueryNameArray = new String[nAMP + 1];
    }

//    System.out.println(sQueryNameArray.length);
//    char chr0 = 0;
//    sQueryNameArray[sQueryNameArray.length -1] = chr0;
//    sQueryNameArray[sQueryNameArray.length -1] = "";

    for ( nTmp = 1; nTmp <= nAMP; nTmp++ ) {

     sTmp1 = "";

     for ( nTmp2 = 1; nTmp2 <= sQueryStringIn.length(); nTmp2++ ) {
      if ( sMid(sQueryStringIn, nTmp2, 1).equals("&") )
      {
       sQueryStringIn = sRight(sQueryStringIn, sQueryStringIn.length() - nTmp2);
       break;
      } else {
       sTmp1 = sTmp1 + sMid(sQueryStringIn, nTmp2, 1);
      }
     }

     sTmp3 = sTmp1; sTmp4 = "";

     for ( nTmp4 = 1; nTmp4 <= sTmp3.length(); nTmp4++ ) {
      if ( sMid(sTmp3, nTmp4, 1).equals("=") )
      {
       sTmp3 = sRight(sTmp3, sTmp3.length() - nTmp4);
       break;
      } else {
       sTmp4 = sTmp4 + sMid(sTmp3, nTmp4, 1);
      }
     }

     if ( nIsSID == 1 )
     {
      sQueryNameArray[nTmp -1] = sTmp4.trim();
     } else {
      if ( nIsSID == 0 )
      {
       if ( sTmp4.toLowerCase().trim().equals("sid") )
       {
        nIsSID = 1;
       } else {
        sQueryNameArray[nTmp] = sTmp4.trim();
       }
      }
     }

//     System.out.println(sQueryStringIn);
//     System.out.println(nTmp2);
//     System.out.println(nTmp);
//     System.out.println(sTmp1 + "\n" + sTmp1.length());
//     System.out.println(sTmp4 + "\n" + sTmp4.length());
//     System.out.println(sTmp3 + "\n" + sTmp3.length());

    }

    if ( nIsSID == 1 ) nAMP = nAMP - 1;
    
    if ( nLF >= nAMP )
    {

     for ( nTmp = 1; nTmp <= nLF; nTmp++ )
     {

      sTmp1 = "";

      if ( nAMP <= nLF )
      {
       sQueryName = sQueryNameArray[nTmp];
       if ( "".equals(sQueryName) || sQueryName == null )
       {
        char chr = 0;
//        sQueryName = chr;
        sQueryName = String.valueOf(chr);
//        sQueryName = "0000";
//        sQueryName = "\0";
       }
      } else {
       char chr = 0;
//       sQueryName = chr;
       sQueryName = String.valueOf(chr);
//       sQueryName = "0001";
//       sQueryName = "\0";
      }
//      char chr = 0;
//      System.out.println(sQueryName + "---" + "\u0000" + "--" + "");
      if ( sQueryName.length() >= 1 )
//      System.out.println("---: " + (int) sQueryName.charAt(0));

      for ( nTmp2 = 1; nTmp2 <= sStrIn.length(); nTmp2++ )
      {
       if ( sMid(sStrIn, nTmp2, 1).equals("\n") )
       {
        sStrIn = sRight(sStrIn, sStrIn.length() - nTmp2);
        break;
       } else {
        sTmp1 = sTmp1 + sMid(sStrIn, nTmp2, 1);
       }
      }

//      if ( sTmp1.trim() == "" ) System.out.println( "--> " + sTmp1 + "" + "\r\n" + nTmp);

      sTmp2 = "";
      nDQ = 0;
      sTmpFirst = "";
      sTmpLast = "";
      nDP = 0; nSP = 0; // 04.07.2018

      for ( nTmp1 = 1; nTmp1 <= sTmp1.length(); nTmp1++ )
      {

       if ( sMid(sTmp1, nTmp1, 1).equals("\"") )
       {
        nDQ = nDQ + 1; nDP = 0; // 04.07.2018
       } else if ( sMid(sTmp1, nTmp1, 1).equals(":") ) {
        nDP = 1; // 04.07.2018
        if ( nDQ == 1 )
        {
         sTmp2 = sTmp2 + sMid(sTmp1, nTmp1, 1);
        } else {
         sTmpFirst = sTmp2;
         sTmp2 = "";
         if ( nDQ == 2 )
         {
          nDQ = 0;
         }
        }

       } else if ( sMid(sTmp1, nTmp1, 1).equals(",") ) {

        if ( nDQ == 2 || nDP == 2 ) { // 04.07.2018
         nKomma = 1; nDP = 0; nSP = 0; sTmp2 = "";
        }

       } else {

    	if ( nDQ == 2 ) nDQ = 0; // 04.07.2018

    	if ( nDP == 2 || nDQ == 1 ) { // 04.07.2018
    	 sTmp2 = sTmp2 + sMid(sTmp1, nTmp1, 1);
    	} else if ( (nDP == 1) && (nDQ == 0) ) {
    	 if ( (nDP == 1) && (nSP == 0) && (sMid(sTmp1, nTmp1, 1).equals(" ")) ) {
    	  nSP = 1; nDP = 2;
    	 } else if ( (nDP == 1) && (nSP == 0) && (!sMid(sTmp1, nTmp1, 1).equals(" ")) ) {
    	  sTmp2 = sTmp2 + sMid(sTmp1, nTmp1, 1);
    	  nSP = 0; nDP = 2;
    	 }
    	}
       }
      
      }

      sTmpLast = sTmp2;

//      System.out.println(sTmpFirst);
//      System.out.println(sTmpLast);
//      System.out.println(sQueryName);

      if ( sTmpFirst.toLowerCase().equals(sQueryName.toLowerCase()) )
      {
       v5value = sTmpLast.trim(); nRet = 1;
       sQueryRetValue = sQueryRetValue + sTmpLast.trim() + "\n";
      } else {
       sQueryRetValue = sQueryRetValue + "\n";
      }

      if ( nKomma == 1 ) nKomma = 0;
//      System.out.println(sQueryRetValue);
      if ( (sQueryNameIn.toLowerCase().equals(sQueryName.toLowerCase()) ) && (nQueryIndex == nTmp) )
      {
       sQueryRetValue = sTmpLast.trim() + "\n\n";
       break;
      }

     }
    }

    if ( nRet == 1 ) v5value = sQueryRetValue; else v5value = "";
//    System.out.println(sQueryRetValue);
   }

   if ( nRet == 1 ) sRetv5value = sQueryRetValue; else sRetv5value = "";
//   System.out.println(sQueryRetValue);
   return nRet;
//   return new FbQueryLua(sRetIn, v5string, v5name, v5value, v5index);  
//   return new sQueryLuaAll(sRetIn, v5string, v5name, v5value, v5index);
//   return this;
  }

  public static int sQueryLua(String sRetIn, String v5name, String v5value)
  {
  // 13.07.2013 // 04.07.2018
   String sStrIn; String sTmpFirst; String sTmpLast;
   String sTmp1; String sTmp2;
   int nRet;
   int nTmp; int nTmp1; int nTmp2;
   int nKomma; int nDQ; int nLF;

   int nDP; int nSP; // 04.07.2018

   sStrIn = sRetIn; nRet = 0; nLF = 0; v5value = ""; nKomma = 0;

   sStrIn = sRtrimLF(sStrIn);

   if ( (sLeft(sStrIn, 1).equals("{")) && (sRight(sStrIn, 1).equals("}")) ) { // 04.07.2018
    if ( (sLeft(sStrIn, 2).equals("{\n") ) && (sRight(sStrIn, 1).equals("}") ) ) {
     sStrIn = sLeft(sStrIn, sStrIn.length() -1); sStrIn = sRight(sStrIn, sStrIn.length() -2);
    } else if ( (sLeft(sStrIn, 1).equals("{")) && (sRight(sStrIn, 1).equals("}")) ) { // 04.07.2018
     sStrIn = sLeft(sStrIn, sStrIn.length() -1); sStrIn = sRight(sStrIn, sStrIn.length() -1); // 04.07.2018
    }

    for ( int i = 1; i <= sStrIn.length(); i++ ) {
     if ( sMid(sStrIn, i, 1).equals("\n") ) nLF = nLF + 1;
     // System.out.println(i + ": " + nLF);
    }
    if ( nLF >= 0 ) nLF = nLF + 1;
    // System.out.println(nLF);

    for ( nTmp = 1; nTmp <= nLF; nTmp++ ) {

     sTmp1 = "";

     for ( nTmp2 = 1; nTmp2 <= sStrIn.length(); nTmp2++ )
     {
      if ( sMid(sStrIn, nTmp2, 1).equals("\n") )
      {
       sStrIn = sRight(sStrIn, sStrIn.length() - nTmp2);
       break;
      } else {
       sTmp1 = sTmp1 + sMid(sStrIn, nTmp2, 1);
      }
     }

     sTmp2 = ""; nDQ = 0; sTmpFirst = ""; sTmpLast = "";
     nDP = 0; nSP = 0; // 04.07.2018

     for ( nTmp1 = 1; nTmp1 <= sTmp1.length(); nTmp1++ )
     {
      switch (sMid(sTmp1, nTmp1, 1)) {
       case "\"" : nDQ = nDQ + 1; nDP = 0; break; // 04.07.2018
       case ":":
    	nDP = 1; // 04.07.2018
    	if ( nDQ == 1 ) {
         sTmp2 = sTmp2 + sMid(sTmp1, nTmp1, 1);
        } else {
         sTmpFirst = sTmp2;
         sTmp2 = "";
         if ( nDQ == 2 ) nDQ = 0;
        }
        break;
       case ",":
        if ( nDQ == 2 || nDP == 2 ) { // 04.07.2018
         nKomma = 1; nDP = 0; nSP = 0; sTmp2 = "";
        }
        break;
       default:
    	if ( nDQ == 2 ) nDQ = 0; // 04.07.2018

    	if ( nDP == 2 || nDQ == 1 ) { // 04.07.2018
    	 sTmp2 = sTmp2 + sMid(sTmp1, nTmp1, 1);
    	} else if ( (nDP == 1) && (nDQ == 0) ) {
    	 if ( (nDP == 1) && (nSP == 0) && (sMid(sTmp1, nTmp1, 1).equals(" ")) ) {
    	  nSP = 1; nDP = 2;
    	 } else if ( (nDP == 1) && (nSP == 0) && (!sMid(sTmp1, nTmp1, 1).equals(" ")) ) {
    	  sTmp2 = sTmp2 + sMid(sTmp1, nTmp1, 1);
    	  nSP = 0; nDP = 2;
         }
      	}
        break;
      }
     }

     sTmpLast = sTmp2;

     // 26.05.2011
     if ( sTmpFirst.toLowerCase().equals(v5name.toLowerCase()) ) {
      v5value = sTmpLast.trim(); nRet = 1;
     }

     if ( nKomma == 1 ) nKomma = 0;

    }

   }

   if ( nRet == 1 ) sRetv5value = v5value; else sRetv5value = "";
   return nRet;

  }

  public static String sQueryLuaAllsRetValue() 
  {
   return sRetv5value;
  }

  public static String Value() 
  {
   return sRetv5value;
  }

}
