package com.beinet.resourcecapture.captureTask.services;

import com.beinet.resourcecapture.captureTask.utils.DownHelper;
import com.beinet.resourcecapture.captureTask.utils.FileHelper;
import com.beinet.resourcecapture.captureTask.utils.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * DownImgs
 *
 * @author youbl
 * @version 1.0
 * @date 2021/1/6 22:17
 */
@Slf4j
public class DownImgs {
    private String dir;

    public DownImgs(String dir) {
        this.dir = dir;
    }

    public void execute() throws IOException {
        File dirObj = new File(dir);
        if (!dirObj.exists()) {
            return;
        }
        if (!dirObj.isDirectory()) {
            return;
        }
        for (File file : dirObj.listFiles()) {
            if (!file.isFile() || file.length() <= 0) {
                continue;
            }
            String str = FileHelper.readFile(file);
            if (StringUtils.isEmpty(str)) {
                continue;
            }
            for (String item : str.split("\\s")) {
                if (item.isEmpty()) {
                    continue;
                }
                downFile(item, file);
            }
        }
    }

    private void downFile(String url, File file) throws IOException {
        String subdir = file.getAbsolutePath().replace(".html.txt", "");
        File subdirObj = new File(subdir);
        if (!subdirObj.exists()) {
            subdirObj.mkdirs();
        }
        String imgName = subdir + "\\" + url.substring(url.lastIndexOf('/') + 1);
        File imgObj = new File(imgName);
        if (imgObj.exists() && imgObj.length() > 1000) {
            return;
        }
        byte[] arr = DownHelper.GetBinary(url);
        if (arr == null || arr.length <= 0)
            return;
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(imgObj))) {
            out.write(arr);
        }
        log.info(url + " saved to " + imgName);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        is.r
//        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), defaultEncoding))) {
//            writer.write(content);
//            writer.flush();
//        }
    }
}
