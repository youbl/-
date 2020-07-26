package com.beinet.resourcecapture.captureTask.services;

import com.beinet.resourcecapture.captureTask.utils.FileHelper;
import com.beinet.resourcecapture.captureTask.utils.HttpHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            // 下载当前页图片

            url = getNextPage(html);
        } while (url.length() > 0);
    }

    List<String> getAllListUrl(String homeHtml) throws IOException {
        Map<String, Integer> allListUrl = getPerPageListUrl(homeHtml);
        String nextPage = getNextPage(homeHtml);

        while (nextPage.length() > 0) {
            String html = getAndSaveUrlContent(getHomePage() + nextPage);
            allListUrl.putAll(getPerPageListUrl(html));

            nextPage = getNextPage(html);
        }
        return allListUrl.keySet().stream().collect(Collectors.toList());
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
