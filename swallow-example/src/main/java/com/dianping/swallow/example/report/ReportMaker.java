package com.dianping.swallow.example.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class ReportMaker {

    static long totalIn = 0;
    static long totalOut = 0;

    static String url = "http://cat.dianpingoa.com/cat/r/t";
    static Compare c = new Compare();
    static TreeSet<String> inTopics = new TreeSet<String>(c);
    static TreeSet<String> inApps = new TreeSet<String>(c);
    static TreeSet<String> outTopics = new TreeSet<String>(c);
    static TreeSet<String> outApps = new TreeSet<String>(c);

    static List<NameValuePair> initParams() {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("op", "history"));
        nvps.add(new BasicNameValuePair("domain", "Swallow"));
        nvps.add(new BasicNameValuePair("date", "20130501"));
        nvps.add(new BasicNameValuePair("startDate", "20130501"));
        nvps.add(new BasicNameValuePair("endDate", "20130601"));
        nvps.add(new BasicNameValuePair("ip", "All"));
        nvps.add(new BasicNameValuePair("sort", "type"));
        nvps.add(new BasicNameValuePair("reportType", "month"));
        return nvps;
    }

    static void parseIndexPage(String html) {
        Document doc = Jsoup.parse(html);
        Elements els = doc.select("tr.odd,tr.even");
        if (els != null) {
            Iterator<Element> it = els.iterator();
            while (it.hasNext()) {
                Element trEle = it.next();
                //[:: show ::] In:TGMobile_PassbookPush
                Element td1 = trEle.child(0);
                //344,454
                Element td2 = trEle.child(1);

                Elements as = td1.select("a:eq(1)");
                String typeName = as.text().trim();
                if (typeName.startsWith("In:")) {
                    String inTopic = typeName.substring(3);
                    inTopics.add(inTopic);
                    String numStr = td2.text().replace(",", "");
                    long num = Long.parseLong(numStr);
                    totalIn += num;
                } else if (typeName.startsWith("Out:")) {
                    String outTopic = typeName.substring(4);
                    outTopics.add(outTopic);
                    String numStr = td2.text().replace(",", "");
                    long num = Long.parseLong(numStr);
                    totalOut += num;
                }
            }
        }
    }

    static void parseDetailPage(String html, Set<String> apps) {
        Document doc = Jsoup.parse(html);
        Elements els = doc.select("tr.odd > td:eq(0),tr.even > td:eq(0)");
        if (els != null) {
            Iterator<Element> it = els.iterator();
            while (it.hasNext()) {
                Element el = it.next();
                List<TextNode> nodes = el.textNodes();
                if (nodes.size() > 1) {
                    String typeName = nodes.get(1).text().trim();
                    int index = typeName.indexOf(':');
                    if (index != -1) {
                        String app = typeName.substring(0, index);
                        if (!isLinshi(app)) {
                            apps.add(app);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //        String str = "08eddbde-d6b8-4edb-86e9-fda36109cb31";
        //        System.out.println(isLinshi(str));

        //inTopics和outTopics
        List<NameValuePair> nvps = initParams();
        String html = HttpClientUtil.get(url, nvps);
        parseIndexPage(html);
        System.out.println("total message in:" + totalIn);
        System.out.println("total message out:" + totalOut);

        //inApps
        System.out.println("InTopics: " + inTopics.size() + ", " + StringUtils.join(inTopics, ','));
        for (String inTopic : inTopics) {
            List<NameValuePair> nvps2 = initParams();
            nvps2.add(new BasicNameValuePair("type", "In:" + inTopic));
            html = HttpClientUtil.get(url, nvps2);
            parseDetailPage(html, inApps);
        }
        System.out.println("InApps: " + inApps.size() + ", " + inApps);

        //outApps
        System.out.println("OutTopics: " + outTopics.size() + ", " + outTopics);
        for (String outTopic : outTopics) {
            List<NameValuePair> nvps2 = initParams();
            nvps2.add(new BasicNameValuePair("type", "Out:" + outTopic));
            html = HttpClientUtil.get(url, nvps2);
            parseDetailPage(html, outApps);
        }
        System.out.println("OutApps: " + outApps.size() + ", " + outApps);

    }

    static class Compare implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }

    private static final Pattern LINSHI_NAME = Pattern.compile("^\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}$");

    static boolean isLinshi(String str) {
        //08eddbde-d6b8-4edb-86e9-fda36109cb31
        Matcher nameMatch = LINSHI_NAME.matcher(str);
        return nameMatch.matches();
    }
}
