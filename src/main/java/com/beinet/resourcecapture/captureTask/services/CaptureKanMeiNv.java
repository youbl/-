package com.beinet.resourcecapture.captureTask.services;

import com.beinet.resourcecapture.captureTask.utils.FileHelper;
import com.beinet.resourcecapture.captureTask.utils.HttpHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://网红精选.com
 * https://xn--vusp20e10f.xyz/
 */
@Service
public class CaptureKanMeiNv extends CaptureBase {

    @Override
    protected String getHomePage() {
        return "https://xn--vusp20e10f.xyz/";
    }

    @Override
    protected void analyze(String homeHtml) throws IOException {
        List<String> arrList = getAllListUrl(homeHtml);

        for (String item : arrList) {
            downloadImgFromList(item);
        }
    }

    @Override
    protected String getFileName(String url) {
        return "kanMN/" + super.getFileName(url);
    }

    void downloadImgFromList(String url) throws IOException {
        do {
            String html = getAndSaveUrlContent(getHomePage() + url);

            // 获取当前页的图片清单
            List<String> imgUrls = getImgFromHtml(html);
            // 存入txt文件，后面再慢慢下载
            saveImgUrls(imgUrls, "txt/" + getFileName(url) + ".txt");

            url = getNextPage(html);
        } while (url.length() > 0);
    }

    List<String> getImgFromHtml(String html) {
        List<String> ret = new ArrayList<>();

        Pattern regex = Pattern.compile("<img\\s[^>]*src=\"([^\"]+\\.jpg)\"[^>]*/>");
        Matcher matcher = regex.matcher(html);
        while (matcher.find()) {
            ret.add(matcher.group(1));
        }
        return ret;
    }

    void saveImgUrls(List<String> imgUrls, String imgListFile) throws IOException {
        if (new File(imgListFile).exists()) {
            return;
        }
        String str = String.join("\n", imgUrls);
        FileHelper.saveFile(imgListFile, str);
    }

    List<String> getAllListUrl(String homeHtml) throws IOException {
        Map<String, Integer> allListUrl = getPerPageListUrl(homeHtml);
        String nextPage = getNextPage(homeHtml);

        while (nextPage.length() > 0) {
            String html = getAndSaveUrlContent(getHomePage() + nextPage);
            allListUrl.putAll(getPerPageListUrl(html));

            nextPage = getNextPage(html);
        }
        return new ArrayList<>(allListUrl.keySet());
    }

    /**
     * 获取当前页面的列表url
     *
     * @param html 页面html
     * @return 列表
     */
    Map<String, Integer> getPerPageListUrl(String html) {
        Map<String, Integer> ret = new HashMap<>();

        Pattern regex = Pattern.compile("<a\\s[^>]*href=\"(list_[^>]+\\.html)\">");
        Matcher matcher = regex.matcher(html);
        while (matcher.find()) {
            ret.put(matcher.group(1), 1);
        }
        return ret;
    }

    String getNextPage(String html) {
        //<a target="_self" href="index_2.html">下一页</a>
        Pattern regex = Pattern.compile("<a\\s[^>]*href=\"([^\"]+\\.html)\">下一页</a>");
        Matcher matcher = regex.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
