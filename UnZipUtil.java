package com.zkcm.hydrobiologicasinica.common.utils;

import com.zkcm.hydrobiologicasinica.api.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * @author ：lwl
 * @description： 解压ZIP
 * @date ：2019/10/28 9:01
 * @version:
 */
@Slf4j
public class UnZipUtil {

    public ResultResponse uploadZip(String filePath) {
        ResultResponse rs = new ResultResponse();

        File file = new File(filePath);

        if (!file.exists()) {
            return rs.setErrorMessage("导入的文件无效!");
        }

        //以时间戳为文件名保存文件
        String headZipName = Calendar.getInstance().getTimeInMillis() + file.getName();


        return rs;
    }

    public static void unZip(String path, String savePath) throws IOException {
        ZipFile zipFile = null;
        InputStream is = null;
        File file = null;
        try {
            zipFile = new ZipFile(path, "GBK");

            Enumeration<?> entries = zipFile.getEntries();
            // 遍历ZIP 文件
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                //文件名称
                String fileName = savePath + entry.getName();
                file = new File(fileName);
                // 如果是文件夹就创建，如果是文件则新增
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    is = zipFile.getInputStream(entry);
                    writeToLocal(fileName, is);
                }
            }
            zipFile.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }
    }

    /**
     * 将InputStream写入本地文件
     *
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException
     */
    private static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = null;
        try {
            downloadFile = new FileOutputStream(destination);
            while ((index = input.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (downloadFile != null) {
                downloadFile.close();
            }
            if (input != null) {
                input.close();
            }
        }

    }

    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * 解压后的文件名，和之前一致
     *
     * @param zipFile 待解压的zip文件
     * @param descDir 指定目录
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir) throws IOException {
        //解决中文文件夹乱码
        java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipFile, Charset.forName("GBK"));
        // TODO
        // Linux --> '/'
        // Windows --> '\\'
        String name = zip.getName().substring(zip.getName().lastIndexOf('/') + 1, zip.getName().lastIndexOf('.'));

        File pathFile = new File(descDir + name);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        for (Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
            java.util.zip.ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + name + "/" + zipEntryName).replaceAll("\\*", "/");

            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
        return;
    }


    public static void main(String[] args) throws IOException {
//        UnZipUtil zip = new UnZipUtil();
//        String savePath = "C:\\Users\\lwl\\Desktop\\";
//        zip.unZip("C:\\Users\\lwl\\Desktop\\7030153790.zip",savePath);
//        unZipFiles(new File("D:\\com\\zip\\1.zip"), "D:\\com\\test\\");
        unZipFiles("D:\\com\\zip\\cs.zip", "D:\\com\\test\\");
    }

}
