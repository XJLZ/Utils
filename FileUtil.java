package com.zkcm.hydrobiologicasinica.common.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * @description： 文件类操作
 * @author     ：lwl
 * @date       ：2019/10/23 16:44
 * @version:
 */
public class FileUtil {

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile()){
                return deleteFile(fileName);
            }else{
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
           return file.delete();
        }
        return true;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录

        return dirFile.delete();
    }


    /**
     * 拷贝文件
     * @param source 源文件
     * @param dest 目标文件
     *result:
     *creater: lwl
     *time: 2019/10/23 16:48
     */
    private static void copyFileUsingApacheCommonsIO(File source, File dest)
            throws IOException {
        FileUtils.copyFile(source, dest);
    }

    /**
     * 拷贝文件
     * @param oldPath 源文件
     * @param newPath 新文件
     *result:
     *creater: lwl
     *time: 2019/10/23 16:52
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                File file1 = new File(newPath.substring(0,newPath.lastIndexOf("/")));
                if(!file1 .exists()  && !file1 .isDirectory()){
                    file1 .mkdirs();
                }
                File newfile = new File(newPath);
                copyFileUsingApacheCommonsIO(oldfile,newfile);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    /**
     * 读取文件内容
     * @param sb
     * @param path
     */
    public static void readFile(StringBuffer sb, String path) {
        FileInputStream is = null;
        BufferedReader reader = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            is = new FileInputStream(file);
            String line; // 用来保存每行读取的内容
            reader = new BufferedReader(new InputStreamReader(is));
            line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        copyFolder(new File("D:\\com\\imageResources\\dataSource"), new File("D:\\com\\imageResources\\imageData\\111"));
    }
}
