package com.zkcm.hydrobiologicasinica.common.utils;

import com.zkcm.hydrobiologicasinica.system.domain.Image.StartParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author: 玲
 * @Description: 图片加密&解密
 * @create 2020-07-29 16:17
 * @Modified By:
 */
public class PicUtil {

    /**
     * 加密
     */
    public static void encode(String remote, FTPClient ftpClient) {
        InputStream in = null;
        OutputStream out = null;
        ByteArrayOutputStream newOut = null;
        try {
            in = ftpClient.retrieveFileStream(remote);
            newOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int c;
            while ((c = in.read(buffer)) != -1) {
                for (int i = 0; i < c; i++) {
                    buffer[i] = (byte) (buffer[i] ^ 5);
                }
                newOut.write(buffer, 0, c);
            }
            byte[] bytes = newOut.toByteArray();
            in.close();
            ftpClient.completePendingCommand();
            out = ftpClient.storeFileStream(remote);
            out.write(bytes, 0, bytes.length);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (newOut != null) {
                try {
                    newOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                ftpClient.completePendingCommand();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解密
     */
    public static void decode(HttpServletRequest request, HttpServletResponse response) {
        InputStream in = null;
        ServletOutputStream out = null;
        try {
            String url = request.getRequestURI();
            url =  url.replace("/data", StartParam.imgPath );
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                out = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    //字节数组进行修改
                    for (int i = 0; i < len; i++) {
                        buffer[i] = (byte) (buffer[i] ^ 5);
                    }
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void getPdf(HttpServletRequest request, HttpServletResponse response) {
        InputStream in = null;
        ServletOutputStream out = null;
        try {
            String url = request.getRequestURI();
            url = StartParam.imgPath + url;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
                out = response.getOutputStream();
                IOUtils.copy(in,out);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
