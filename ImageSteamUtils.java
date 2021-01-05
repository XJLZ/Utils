package com.zkcm.hydrobiologicasinica.system.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageSteamUtils {



    /**
     * 获取 文件 流
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static byte[] getImageStreamByHttp(String url) throws IOException {
        URL urlConet = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlConet.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(4 * 1000);
        InputStream inStream = con.getInputStream();    //通过输入流获取图片数据
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        byte[] data = outStream.toByteArray();
        return data;
    }


    /**
     * 读取 本地文件，转为字节数组
     *
     * @param path 本地文件路径
     * @return
     * @throws IOException
     */
    public static byte[] getImageStreamByLocal(String path) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

        byte[] temp = new byte[2048];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] content = out.toByteArray();
        return content;
    }



}
