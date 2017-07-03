package sagib.edu.tickcheck;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowDataSource {


    public interface OnShowArrivedListener {
        void onShowArrived(ArrayList<Show> data, Exception e);
    }

    public static void getShows(final OnShowArrivedListener listener, Context context) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        final ArrayList<Show> shows = new ArrayList<>();
        service.execute(new Runnable() {
            @Override
            public void run() {
                String html = "";
                try {
                    URL url = new URL("https://www.zappa-club.co.il/%D7%AA%D7%92%D7%99%D7%95%D7%AA/%D7%A9%D7%9C%D7%9E%D7%94-%D7%90%D7%A8%D7%A6%D7%99/");
                    URLConnection con = url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2.1; en-ca; LG-P505R Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
                    InputStream in = con.getInputStream();
                    html = StreamIO.read(in);
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onShowArrived(null, e);
                }
                Document parse = Jsoup.parse(html);
                Elements select = parse.select("#content > div > div.loader_wrap > article");
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                for (int i = 0; i < select.size(); i++) {
                    Elements elementArena = select.get(i).select(String.format("a > div > div.event_data > div:nth-child(%1$d) > meta:nth-child(%1$d)", 1));
                    Elements elementHour = select.get(i).select(String.format("a > div > div.event_data > div:nth-child(%1$d) > meta:nth-child(%1$d)", 2));
                    Elements elementDay = select.get(i).select(String.format("a > div > div.event_data > div:nth-child(%1$d) > span", 2));
                    Elements elementPerformer = select.get(i).select(String.format("article:nth-child(%d) > div > h2", i + 1));
                    Elements elementLink = select.get(i).select(String.format("article:nth-child(%d) > div > a", i + 1));
                    Element elementImage = select.get(i).select("a > div > div.event_img > img").first();
                    Date time = null;
                    String hour = elementHour.attr("content");
                    try {
                        time = formatter1.parse(hour);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        listener.onShowArrived(null, e);
                    }
                    String link = elementLink.toString().replace("<a class=\"buyTicket\" href=\"", "").replace("\">לרכישת כרטיסים ← </a>", "").replace("\">לפרטים ורכישה ← </a>", "");
                    String parsedlink = "";
                    try {
                        URL url = new URL(StreamIO.encodeZappaFromEncode(link));
                        link = url.toString();
                        URLConnection con = url.openConnection();
                        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2.1; en-ca; LG-P505R Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
                        InputStream in = con.getInputStream();
                        parsedlink = StreamIO.read(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                        listener.onShowArrived(null, e);
                    }
                    String image = elementImage.absUrl("src");
                    String performer = elementPerformer.toString().replace("<h2 itemprop=\"name\">", "").replace("</h2>", "");
                    String arena = elementArena.attr("content");
                    String day = elementDay.toString().replace("<span class=\"day\">", "").replace("</span>", "");
                    String dateTime = formatter.format(time);
                    boolean ticketsAvailable;
                    if (parsedlink.contains("כל הכרטיסים לארוע נמכרו."))
                        ticketsAvailable = false;
                    else {
                        ticketsAvailable = true;
                    }
                    ArrayList<Zone> zones = new ArrayList<>();
                    int idx1 = parsedlink.indexOf("areas': [");
                    String subs = parsedlink.substring(idx1 + 9);
                    int idx2 = subs.lastIndexOf(", \"soldOut");
                    String substring = subs.substring(0, idx2);
                    int sectors = substring.split("name").length - 1;
                    for (int j = 0; j < sectors; j++) {
                        int index = StreamIO.ordinalIndexOf(substring, "\"name\":\"", j + 1);
                        String temp = substring.substring(index);
                        index = StreamIO.ordinalIndexOf(temp, "\"", 13);
                        String zoneSubstring = temp.substring(0, index);
                        if (zoneSubstring.contains("capacity")) {
                            int areaidx = zoneSubstring.indexOf("\"name\":\"") + 8;
                            int areaendidx = zoneSubstring.indexOf("\"", areaidx);
                            String area = zoneSubstring.substring(areaidx, areaendidx);
                            int capaidx = zoneSubstring.indexOf("\"capacity\":", areaendidx) + 11;
                            int capaendidx = zoneSubstring.indexOf("\"", capaidx);
                            String capacity = zoneSubstring.substring(capaidx, capaendidx - 2);
                            int freeidx = zoneSubstring.indexOf("\"free\":", capaendidx) + 7;
                            int freeendidx = zoneSubstring.indexOf(",", freeidx);
                            String free = zoneSubstring.substring(freeidx, freeendidx);
                            Zone zone = new Zone(area, Integer.valueOf(capacity), Integer.valueOf(free));
                            zones.add(zone);
                        }
                    }
                    Show show = new Show(image, performer, arena, day, dateTime, link, ticketsAvailable, zones);
                    shows.add(show);
                }
                listener.onShowArrived(shows, null);
            }
        });
        service.shutdown();
    }
}
