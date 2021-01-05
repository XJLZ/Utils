package com.zkcm.hydrobiologicasinica.system.util;

import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.*;

public class FileUtils {


    /**
     * MultipartFile文件流  转 File 文件流
     * @param multipartFile
     * @throws IOException
     */
    public static File MultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = null;
        if (multipartFile.equals("") || multipartFile.getSize() <= 0) {
            multipartFile = null;
        } else {
            InputStream ins = null;
            ins = multipartFile.getInputStream();
            file = new File(multipartFile.getOriginalFilename());
            inputStreamToFile(ins, file);
            ins.close();
        }
        return file;

    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static File getImageFile(byte[] bytes, String path) {
        File localFile = null;
        try {
            // 根据绝对路径初始化文件
            localFile = new File(path+"临时图品.jpg");
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            // 输出流
            OutputStream os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localFile;
    }


    /**
     * 将 图片数据 转成 base64 字符串
     * @param originalBytes
     * @return
     */
    public static String byteToString(byte[] originalBytes) throws Exception {
        byte[] bytes = originalBytes;
        return new BASE64Encoder().encode(bytes);
    }
}
