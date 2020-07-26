package com.beinet.resourcecapture.captureTask.utils;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;

public final class FileHelper {
    private static final Charset defaultEncoding = StandardCharsets.UTF_8;

    /**
     * 把内容写入文件，存在时覆盖
     *
     * @param filePath 文件路径
     * @param content  内容
     * @throws IOException 写入异常
     */
    public static void saveFile(String filePath, String content) throws IOException {
        saveFile(new File(filePath), content);
    }

    /**
     * 把内容写入文件，存在时覆盖
     *
     * @param file    文件路径
     * @param content 内容
     * @throws IOException 写入异常
     */
    public static void saveFile(File file, String content) throws IOException {
        if (file.exists()) {
            throw new FileAlreadyExistsException("文件已存在： " + file.getAbsolutePath());
        }
        ensureDirectory(file.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), defaultEncoding))) {
            writer.write(content);
            writer.flush();
        }
    }

    /**
     * 返回文本文件的全部内容
     *
     * @param file 文件
     * @return 文本内容
     * @throws IOException 可能的IO异常
     */
    public static String readFile(String file) throws IOException {
        if (StringUtils.isEmpty(file)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        return readFile(new File(file));
    }

    /**
     * 返回文本文件的全部内容
     *
     * @param file 文件
     * @return 文本内容
     * @throws IOException 可能的IO异常
     */
    public static String readFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("文件不存在: " + file.getAbsolutePath());
        }
        //byte[] fileContent = new byte[(int) file.length()];
        //int readLen;
        // BufferedReader reader = new BufferedReader(
        try (FileInputStream fi = new FileInputStream(file)) {
            return read(fi);
            // readLen = fi.read(fileContent);
        }
//        if (readLen < fileContent.length)
//            throw new IOException("文件长度：" + fileContent.length + ", 实际读取长度：" + readLen);
//        return new String(fileContent, defaultEncoding);
    }


    /**
     * 读取流的内容返回
     *
     * @param is 流
     * @return 流的内容
     * @throws IOException 异常
     */
    public static String read(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(is, defaultEncoding)) {
            // readLine不合适，无法确定换行符是 \n还是\r\n
            // BufferedReader bufferedReader = new BufferedReader(reader)
            // while ((line = bufferedReader.readLine()) != null)
            int c;
            while ((c = reader.read()) != -1)
                sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * 获取当前项目资源所在目录
     *
     * @return 目录
     */
    public static String getResourceBasePath() {
        // 获取当前根目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            // nothing to do
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }

        String pathStr = path.getAbsolutePath();
        // 如果是在eclipse中运行，则和target同级目录,如果是jar部署到服务器，则默认和jar包同级
        pathStr = pathStr.replace("\\target\\classes", "");

        return pathStr;
    }

    /**
     * 确认文件的上级目录存在，不存在时创建
     *
     * @param filePath 文件路径
     */
    private static void ensureDirectory(String filePath) throws IOException {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        filePath = replaceSeparator(filePath);
        if (filePath.charAt(filePath.length() - 1) == '/')
            filePath = filePath.substring(0, filePath.length() - 1);// 去除最后一个斜杠

        int idx = filePath.lastIndexOf('/');
        if (idx <= 0)
            return;

        String dirPath = filePath.substring(0, idx);
        File dir = new File(dirPath);
        if (dir.exists()) {
            return;
        }

        if (!dir.mkdirs()) {
            throw new IOException("目录创建失败：" + dir.getAbsolutePath());
        }
    }

    private static String replaceSeparator(String str) {
        return str.replace("\\", "/").replace("\\\\", "/");
    }

}
