package com.beinet.resourcecapture.captureTask.services;

import com.beinet.resourcecapture.captureTask.utils.FileHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://xher.net/
 */
@Service
public class CaptureXHerNet extends CaptureBase {

    @Override
    protected String getHomePage() {
        return "https://xher.net/";
    }

    @Override
    protected void analyze(String homeHtml) throws IOException {
        List<String> arrList = getAllListUrl(homeHtml);
//        arrList = new ArrayList<>();
//        arrList.add("https://xher.net/index.php?/category/298");
        for (String item : arrList) {
            downloadImgFromList(item);
        }
    }

    @Override
    protected String getFileName(String url) {
        String category = url.substring(url.lastIndexOf('/') + 1);
        if (url.contains(getHomePage() + "picture.php")) {
            String item = url.replace(getHomePage() + "picture.php?/", "");
            item = item.substring(0, item.indexOf('/'));
            return "xher/" + category + "-" + item + ".html";
        } else if (url.contains(getHomePage() + "index.php?/category/")) {
            String item = url.replace(getHomePage() + "index.php?/category/", "");
            int idx = item.indexOf("/");
            if (idx < 0) {
                return "xher/" + item + ".html";
            }
            return "xher/" + item.substring(0,idx) + "_" + category + ".html";
        }
        return "xher/" + category + ".html";
    }

    void downloadImgFromList(String url) throws IOException {
        String fileName = getFileName(url);
        HashSet<String> imgUrls = new HashSet<>();
        do {
            String html = getAndSaveUrlContent(url);

            // 获取当前页的图片清单
            getImgFromHtml(html, imgUrls);

            url = getNextPage(html);
        } while (url.length() > 0);

        // 存入txt文件，后面再慢慢下载
        saveImgUrls(imgUrls, "txt/" + fileName + ".txt");
    }

    void getImgFromHtml(String html, HashSet<String> imgUrls) throws IOException {
        Pattern regex = Pattern.compile("<a[^>]+href=\"(picture\\.php\\?[^\"]+)\"");
        Matcher matcher = regex.matcher(html);
        while (matcher.find()) {
            if (matcher.group(1).contains("slideshow"))
                continue;
            String itemUrl = getHomePage() + matcher.group(1);
            getItemUrl(itemUrl, imgUrls);
        }
    }

    void getItemUrl(String url, HashSet<String> arr) throws IOException {
        String html = getAndSaveUrlContent(url);
        Pattern regex = Pattern.compile("url:'(_data/[^']+)',\\s*type:'xxlarge'");
        Matcher matcher = regex.matcher(html);
        boolean finded = false;
        while (matcher.find()) {
            finded = true;
            arr.add(getHomePage() + matcher.group(1));
        }
        if (finded)
            return;
        regex = Pattern.compile("url:'(_data/[^']+)',\\s*type:'xlarge'");
        matcher = regex.matcher(html);
        if (matcher.find()) {
            arr.add(getHomePage() + matcher.group(1));
        }
    }

    void saveImgUrls(Iterable<String> imgUrls, String imgListFile) throws IOException {
        if (new File(imgListFile).exists()) {
            return;
        }
        String str = String.join("\n", imgUrls);
        FileHelper.saveFile(imgListFile, str);
    }

    List<String> getAllListUrl(String homeHtml) throws IOException {
        List<String> ret = new ArrayList<>();
        for (int i = 1; i <= 301; i++) {
            ret.add("https://xher.net/index.php?/category/" + i);
        }
        return ret;
    }

    String getNextPage(String html) {
        //<a target="_self" href="index_2.html">下一页</a>
        Pattern regex = Pattern.compile("<a href=\"([^\"]+)\" rel=\"next\">");
        Matcher matcher = regex.matcher(html);
        if (matcher.find()) {
            return getHomePage() + matcher.group(1);
        }
        return "";
    }
}
