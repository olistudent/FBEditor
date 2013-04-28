package de.FBEditor.struct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpPost {

 private static boolean isok;

 public HttpPost() {
  isok = false;
 }

 public String Post(String urlstr, String postdata) {
  String sRet = "";
  try {

   if (urlstr == null) {
    urlstr = "";
   }
   if (postdata == null) {
    postdata = "";
   }

   String encoding = "ISO-8859-1";

   URL url = new URL(urlstr);
   HttpURLConnection connection = (HttpURLConnection) url.openConnection();

   connection.setConnectTimeout(5000);
   connection.setReadTimeout(15000);

   connection.setDoInput(true);
   connection.setDoOutput(true);
   connection.setUseCaches(false);

   if (!"".equals(postdata)) {
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");
    connection.setRequestProperty("Content-Length", String.valueOf(postdata.length()));

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.write(postdata);
    writer.flush();
    writer.close();

//    System.out.println( "POST" );
   } else {
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");
//    System.out.println( "GET" );
   }

   String contentType = connection.getContentType();
   int encodingStart = contentType.indexOf("charset=");
   if (encodingStart != -1) {
    encoding = contentType.substring(encodingStart + 8);
   }
//   System.out.println( encoding );

   if (connection.getResponseCode() == 200) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));

    //   for ( String line; (line = reader.readLine()) != null; )
    //   {
    ////   System.out.println( line );
    ////    sRet += line + '\n';
    //    sRet += line;
    //   }

    int c;
    while ((c = reader.read()) != -1) {
     //     System.out.print((char) c);
     sRet += (char) c;
    }

    reader.close();

    isok = true;

   }

   connection.disconnect();
//System.out.println( connection.getResponseCode() );
//System.out.println( connection.getResponseMessage() );
  } catch (IOException ex) {
   Logger.getLogger(HttpPost.class.getName()).log(Level.SEVERE, null, ex);
  }
  return sRet;
 }

 public String Post2(String urlstr, String postdata) {
  String sRet = "";
  try {

   if (urlstr == null) {
    urlstr = "";
   }
   if (postdata == null) {
    postdata = "";
   }

   String encoding = "ISO-8859-1";

   URL url = new URL(urlstr);
   HttpURLConnection connection = (HttpURLConnection) url.openConnection();

   connection.setConnectTimeout(5000);
   connection.setReadTimeout(15000);

   connection.setDoInput(true);
   connection.setDoOutput(true);
   connection.setUseCaches(false);

   if (!"".equals(postdata)) {
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");
    connection.setRequestProperty("Content-Length", String.valueOf(postdata.length()));

    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.write(postdata);
    writer.flush();
    writer.close();

//    System.out.println( "POST" );
   } else {
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");
//    System.out.println( "GET" );
   }

   String contentType = connection.getContentType();
   int encodingStart = contentType.indexOf("charset=");
   if (encodingStart != -1) {
    encoding = contentType.substring(encodingStart + 8);
   }
//   System.out.println( encoding );

   if (connection.getResponseCode() == 200) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));

    for (String line; (line = reader.readLine()) != null;) {
////   System.out.println( line );
////    sRet += line + '\n';
     sRet += line;
    }

//    int c;
//    while ((c = reader.read()) != -1) {
////     System.out.print((char) c);
//     sRet += (char) c;
//    }

    reader.close();

    isok = true;

   }

   connection.disconnect();
//System.out.println( connection.getResponseCode() );
//System.out.println( connection.getResponseMessage() );

  } catch (IOException ex) {
   Logger.getLogger(HttpPost.class.getName()).log(Level.SEVERE, null, ex);
  }
  return sRet;
 }

 public boolean isOK() {
  return isok;
 }
}
