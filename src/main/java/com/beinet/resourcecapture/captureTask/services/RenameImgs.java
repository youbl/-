package com.beinet.resourcecapture.captureTask.services;

import com.beinet.resourcecapture.captureTask.utils.DownHelper;
import com.beinet.resourcecapture.captureTask.utils.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * RenameImgs
 *
 * @author youbl
 * @version 1.0
 * @date 2021/1/6 22:17
 */
@Slf4j
public class RenameImgs {
    private String dir;

    public RenameImgs(String dir) {
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
            int idx = 1;
            for (String item : str.split("\\s")) {
                if (item.isEmpty()) {
                    continue;
                }
                renameByFile(item, file, idx);
                idx++;
            }
        }
    }

    private void renameByFile(String url, File file, int idx) throws IOException {
        String subdir = file.getAbsolutePath().replace(".html.txt", "");
        File subdirObj = new File(subdir);
        if (!subdirObj.exists()) {
            return;
        }
        String imgName = subdir + "\\" + url.substring(url.lastIndexOf('/') + 1);
        File imgObj = new File(imgName);
        if (!imgObj.exists()) {
            return;
        }
        String strIdx = ("00" + idx);
        strIdx = strIdx.substring(strIdx.length() - 3);
        File newName = new File(imgObj.getParent() + "\\" + strIdx + "-" + imgObj.getName());
        imgObj.renameTo(newName);
    }
}
