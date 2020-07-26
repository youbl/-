package com.beinet.resourcecapture.captureTask.services;

import com.beinet.resourcecapture.captureTask.CaptureTask;
import com.beinet.resourcecapture.captureTask.utils.FileHelper;
import com.beinet.resourcecapture.captureTask.utils.HttpHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public abstract class CaptureBase implements CaptureInterface {
    /**
     * 当前项目的根目录
     */
    protected final String basePath = FileHelper.getResourceBasePath();

    protected abstract String getHomePage();

    protected abstract void analyze(String homeHtml) throws IOException;

    @Override
    public void begin() {
        try {
            String homeUrl = checkUrl(getHomePage());

            String html = getAndSaveUrlContent(homeUrl);

            analyze(html);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private String checkUrl(String url) {
        if (StringUtils.isEmpty(url))
            throw new IllegalArgumentException("url不能为空");
        if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils.startsWithIgnoreCase(url, "https://"))
            url = "http://" + url;
        return url;
    }

    protected String getAndSaveUrlContent(String url) throws IOException {
        File file = new File(basePath, getFileName(url));
        if (file.exists() && file.length() > 1000) {
            return FileHelper.readFile(file);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String fileName = file.getAbsolutePath();
        String html = HttpHelper.GetPage(url);
        if (html.length() < 1000) {
            throw new IOException("Http请求出错：" + html);
        }
        FileHelper.saveFile(fileName, html);
        CaptureTask.println(url + " 已存入 " + fileName);
        return html;
    }

    protected String getFileName(String url) {
        int idx = url.indexOf('?');
        if (idx > 0)
            url = url.substring(0, idx);
        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);// 去除最后一个斜杠

        idx = url.lastIndexOf('/');
        if (idx > 0) {
            url = url.substring(idx + 1);
        }
        return "cache/" + url + ".html";
    }
}
