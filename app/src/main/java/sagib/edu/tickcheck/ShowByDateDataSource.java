package sagib.edu.tickcheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowByDateDataSource {

    public interface OnShowArrivedListener {
        void onShowArrived(ArrayList<Show> data, Exception e);
    }

    public static void getShows(final OnShowArrivedListener listener, Context context) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        final ArrayList<Show> shows = new ArrayList<>();
        final SharedPreferences prefs = context.getSharedPreferences("ShowsDate", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();
        String lastDate1 = prefs.getString("LastDate", null);
        MyDate lastDate = new MyDate(2017, 1, 1);
        if (lastDate1 != null) {
            lastDate = gson.fromJson(lastDate1, MyDate.class);
        }
        MyDate nowDate = new MyDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        String prefsString = prefs.getString("Date", "");
        MyDate chosenDate = nowDate;
        if (!prefsString.equals("")) {
            chosenDate = gson.fromJson(prefsString, MyDate.class);
        }
        LocalDate before = new LocalDate(chosenDate.getYear(), chosenDate.getMonth(), chosenDate.getDay());
        MyDate date;
        if (chosenDate.getYear() == 0 || before.isBefore(now) || nowDate.equals(chosenDate)) {
            date = nowDate;
        } else {
            date = chosenDate;
        }
        SharedPreferences prefsBoolean = context.getSharedPreferences("BandSwitchBoolean", Context.MODE_PRIVATE);
        boolean isLimited = prefsBoolean.getBoolean("islimited", true);
        boolean showLastList = false;
        if (isLimited) {
            String lastTime = prefs.getString("showslisttime", "");
            int limitMinutes = prefsBoolean.getInt("Minutes", 5);
            if (date.getDay() != lastDate.getDay() || date.getMonth() != lastDate.getMonth() || date.getYear() != lastDate.getYear()) {
                showLastList = false;
            } else {
                if (lastTime.length() > 3) {
                    if (nowTime.minusMinutes(limitMinutes).isBefore(LocalDateTime.parse(lastTime))) {
                        showLastList = true;
                    }
                    if (nowTime.minusMinutes(limitMinutes).isEqual(LocalDateTime.parse(lastTime))) {
                        showLastList = true;
                    }
                }
            }
        }
        final MyDate parsedDate = date;
        if (!showLastList || !isLimited) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    String html = "";
                    try {
                        URL url = new URL("https://www.zappa-club.co.il/sresults/?ed=" + parsedDate.getDay() + "&em=" + parsedDate.getMonth() + "&ey=" + parsedDate.getYear());
                        URLConnection con = url.openConnection();
                        con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                        con.setRequestProperty("Connection", "Keep-Alive");
                        InputStream in = con.getInputStream();
                        html = StreamIO.read(in);
                    } catch (IOException e) {
                        listener.onShowArrived(null, e);
                    }
                    Document parse = Jsoup.parse(html);
                    Elements select = parse.select("#content > section > div > div.loader_wrap > article");
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
                            listener.onShowArrived(null, e);
                            continue;
                        }
                        String link = elementLink.toString().replace("<a class=\"buyTicket\" href=\"", "").replace("\">לרכישת כרטיסים ← </a>", "").replace("\">לפרטים ורכישה ← </a>", "").replace("\">הזמנת כרטיסים ← </a>", "");
                        String parsedlinkfirst = "";
                        try {
                            URL url = new URL(StreamIO.encodeZappaFromEncode(link));
                            link = url.toString();
                            URLConnection con = url.openConnection();
                            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2.1; en-ca; LG-P505R Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
                            InputStream in = con.getInputStream();
                            parsedlinkfirst = StreamIO.read(in);
                        } catch (IOException e) {
                            listener.onShowArrived(null, e);
                            continue;
                        }
                        String parsedlink = parsedlinkfirst.replace("&#039;", "׳");
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
                        String substring = subs.substring(0, idx2 + 2);
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
                    String showsJson = gson.toJson(shows);
                    LocalDateTime currentNow = LocalDateTime.now();
                    prefs.edit().putString("showslisttime", currentNow.toString()).commit();
                    prefs.edit().putString("LastShows", showsJson).commit();
//                    String date1 = prefs.getString("Date", "");
//                    MyDate myDate = gson.fromJson(date1, MyDate.class);
                    prefs.edit().putString("LastDate", gson.toJson(parsedDate)).commit();
                    listener.onShowArrived(shows, null);
                }
            });

            service.shutdown();
        } else {
            Type arrayListType = new TypeToken<ArrayList<Show>>() {
            }.getType();
            ArrayList<Show> lastShows = gson.fromJson(prefs.getString("LastShows", ""), arrayListType);
            listener.onShowArrived(lastShows, null);
            Toast.makeText(context, "בקרת ריענון רשימת הופעות פעילה!", Toast.LENGTH_SHORT).show();
        }
    }
}