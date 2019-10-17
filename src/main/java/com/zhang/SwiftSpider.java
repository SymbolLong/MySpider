package com.zhang;

import com.zhang.util.DownLoadUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 尝试爬取网站数据
 * <p>
 * Created by 致远 on 2019/10/17 0021.
 */
public class SwiftSpider implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private static String website = "https://www.hackingwithswift.com/";

    public void process(Page page) {
        //部分一：获取网页源码
        Html html = page.getHtml();
        Document document = html.getDocument();
        //head link href
        try {
            Element head = document.head();
            for (Element child : head.children()) {
                String href = child.attr("href");
                if (href.startsWith("/")) {
                    DownLoadUtil.downLoadFromUrl(website + href.replaceFirst("/", ""), new File("/Users/power/工作/swift" + href));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //body img
        List<String> images = html.$("img").all();
        for (String image : images) {
            String[] attrs = image.replace("\"", "").split(" ");
            for (String attr : attrs) {
                if (attr.startsWith("src=") || attr.startsWith("srcset=")) {
                    String src = attr.replace("src=", "").replace("srcset=", "").replaceFirst("/", "");
                    DownLoadUtil.downLoadFromUrl(website + src, new File("/Users/power/工作/swift/" + src));
                }
            }
        }

        // 部分二：定义如何抽取页面信息，并保存下来
        String uri = page.getUrl().toString().replace(website, "");
        try {
            if (StringUtils.isBlank(uri)) {
                uri = "/index";
            }
            if (uri.startsWith("files")) {
                DownLoadUtil.downLoadFromUrl(website + uri, new File("/Users/power/工作/swift/" + uri));
            } else {
                File file = new File("/Users/power/工作/swift/" + uri + ".html");
                FileUtils.writeStringToFile(file, page.getRawText(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 部分三：从页面发现后续的url地址来抓取
        List<String> requests = new ArrayList<String>();
        Selectable links = html.links();
        for (String link : links.all()) {
            if (!link.equals(website) && link.startsWith(website) && !link.endsWith("#")) {
                requests.add(link);
            }
        }
        page.addTargetRequests(requests);
    }

    public Site getSite() {
        return site;
    }

    /**
     * 起点方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String url = website;//启动URL
        Spider spider = Spider.create(new SwiftSpider());
        spider.addUrl(url);//从指定开始抓
        spider.thread(10);//开启5个线程抓取
        spider.run();//启动爬虫
    }

}
