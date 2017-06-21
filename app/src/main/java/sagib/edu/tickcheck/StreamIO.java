package sagib.edu.tickcheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by sagib on 12/05/2017.
 */
public class StreamIO {

    private static final String lineSeperator = "\n";

    public static void write(String fileName, String data) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(data);
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    public static String read(String fileName) throws IOException {
        InputStream in = new FileInputStream(fileName);
        return read(in);
    }

    public static void copy(String srcFileName, String destFileName) throws IOException {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFileName);
            out = new FileOutputStream(destFileName);
            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer);
            }
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    //Read from the web.

    public static String readWebSite(String address) throws IOException {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new IOException("Failed at parsing the url.", e);
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            throw new IOException("Failed at opening the connection", e);
        }
        InputStream in = null;
        in = con.getInputStream();
        return read(in);
    }

    public static String readWebSiteMobile(String address) throws IOException {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            throw new IOException("Failed at parsing the url.", e);
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1");
        } catch (IOException e) {
            throw new IOException("Failed at opening the connection", e);
        }
        InputStream in = null;
        in = con.getInputStream();
        return read(in);
    }

    public static String read(InputStream in) throws IOException {
        return read(in, "utf-8");
    }

    public static String read(InputStream in, String charset) throws IOException {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, charset));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line).append(lineSeperator);
            }
        } finally {
            if (reader != null)
                reader.close();
        }
        return data.toString();
    }

    public static boolean checkForTickets(String url) {
        try {
            String site = StreamIO.readWebSite(url);
            if (site.contains("כל הכרטיסים לארוע נמכרו."))
                return false;
            else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String encodeZappa(String url) {
        String string = "https://www.zappa-club.co.il/";
        int originalindex = string.length();
        String encode = url.substring(originalindex);
        String finalUrl = "";
        try {
            string = string + URLEncoder.encode(encode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            String site = StreamIO.readWebSite(string);
            int index = site.indexOf("event%3D") + 8;
            String id = site.substring(index, index + 5);
            finalUrl = "https://tickets.zappa-club.co.il/loader.aspx/?target=hall.aspx?event%3D" + id;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalUrl;
    }

    public static boolean checkTicketsEncode(String url) {
        return StreamIO.checkForTickets(StreamIO.encodeZappa(url));
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public static String encodeZappaFromEncode(String url) {
        String string = "https://www.zappa-club.co.il/";
        int originalindex = string.length();
        String encode = url.substring(originalindex);
        String finalUrl = "";
        string += encode;
        try {
            String site = StreamIO.readWebSiteMobile(string);
            int index = site.indexOf("event%3D") + 8;
            String id = site.substring(index, index + 5);
            finalUrl = "https://tickets.zappa-club.co.il/loader.aspx/?target=hall.aspx?event%3D" + id;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalUrl;
    }

    public static boolean checkTicketsWhEncode(String url) {
        return StreamIO.checkForTickets(StreamIO.encodeZappaFromEncode(url));
    }
}