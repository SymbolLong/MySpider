package com.zhang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 致远 on 2017/6/21 0021.
 */
public class MySpider implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
        Document document = page.getHtml().getDocument();
        Element name = document.getElementsByClass("ProfileHeader-name").first();
        System.out.println(name.text());
        // 部分三：从页面发现后续的url地址来抓取
        List<String> requests = new ArrayList<String>();
        Elements elements = document.getElementsByClass("FollowshipCard-counts").first().children();
        for (Element element : elements ) {
            requests.add(element.attr("abs:href"));
        }
        System.out.println(requests);
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
        String url = "https://www.zhihu.com/people/wang-la-ma-12/answers";//启动URL
        Spider spider = Spider.create(new MySpider());
        spider.addUrl(url);//从指定开始抓
        spider.thread(5);//开启5个线程抓取
        spider.run();//启动爬虫
    }
}
