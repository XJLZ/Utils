package com.zkcm.hydrobiologicasinica.common.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zkcm.hydrobiologicasinica.system.config.FtpConfig;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.net.ftp.*;


@Slf4j
public class FtpUtil {
    private FTPClient ftpClient;
    private String ip;
    private int port;
    private String user;
    private String password;

    //本地字符编码
    private static String LOCAL_CHARSET = "GBK";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";


    /* *
     * Ftp构造函数
     */
    public FtpUtil(String ip, int port, String user, String Password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();
    }

    public FtpUtil(FtpConfig ftpConfig) {
        this.ip = ftpConfig.getHost();
        this.port = ftpConfig.getPort();
        this.user = ftpConfig.getUser();
        this.password = ftpConfig.getPassword();
        this.ftpClient = new FTPClient();
    }

    /**
     * @return 判断是否登入成功
     */
    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("GBK");
        this.ftpClient.configure(ftpClientConfig);
        try {
            if (this.port > 0) {
                this.ftpClient.connect(this.ip, this.port);
            } else {
                this.ftpClient.connect(this.ip);
            }
            // FTP服务器连接回答  
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                log.error("登录FTP服务失败！");
                return isLogin;
            }
            this.ftpClient.login(this.user, this.password);
            // 设置传输协议  
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
                    "OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }
            ftpClient.setControlEncoding(LOCAL_CHARSET);
            log.info("恭喜" + this.user + "成功登陆FTP服务器");
            isLogin = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(this.user + "登录FTP服务失败！" + e.getMessage());
        }
        this.ftpClient.setBufferSize(1024 * 2);
        this.ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }

    /**
     * @退出关闭服务器链接
     */
    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器  
                if (reuslt) {
                    log.info("成功退出服务器");
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.warn("退出FTP服务器异常！" + e.getMessage());
            } finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接  
                } catch (IOException e) {
                    e.printStackTrace();
                    log.warn("关闭FTP服务器的连接异常！");
                }
            }
        }
    }

    /*** 
     * 上传Ftp文件 
     * @param localFile 当地文件 
     * @param romotUpLoadePath  - 上传服务器路径 应该以/结束 
     * */
    public boolean uploadFile(File localFile, String romotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径  
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            log.info(localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            if (success == true) {
                log.info(localFile.getName() + "上传成功");
                return success;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error(localFile + "未找到");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /*** 
     * 下载文件 
     * @param remoteFileName   待下载文件名称 
     * @param localDires 下载到当地那个路径下 
     * @param remoteDownLoadPath remoteFileName所在的路径 
     * */

    public boolean downloadFile(String remoteFileName, String localDires,
                                String remoteDownLoadPath) {
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(
                    strFilePath));
            log.info(remoteFileName + "开始下载....");
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            if (success == true) {
                log.info(remoteFileName + "成功下载到" + strFilePath);
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(remoteFileName + "下载失败");
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (success == false) {
            log.error(remoteFileName + "下载失败!!!");
        }
        return success;
    }

    /*** 
     * @上传文件夹
     * @param localDirectory
     *            当地文件夹 
     * @param remoteDirectoryPath
     *            Ftp 服务器路径 以目录"/"结束 
     * */
    public boolean uploadDirectory(String localDirectory,
                                   String remoteDirectoryPath) {
        File src = new File(localDirectory);
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            boolean makeDirFlag = this.ftpClient.makeDirectory(remoteDirectoryPath);
            System.out.println("localDirectory : " + localDirectory);
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("src.getName() : " + src.getName());
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("makeDirFlag : " + makeDirFlag);
            // ftpClient.listDirectories();
        } catch (IOException e) {
            e.printStackTrace();
            log.info(remoteDirectoryPath + "目录创建失败");
        }
        File[] allFile = src.listFiles();
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
            if (!allFile[currentFile].isDirectory()) {
                String srcName = allFile[currentFile].getPath().toString();
                uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
            if (allFile[currentFile].isDirectory()) {
                // 递归  
                uploadDirectory(allFile[currentFile].getPath().toString(),
                        remoteDirectoryPath);
            }
        }
        return true;
    }

    /*** 
     * @下载文件夹
     * @param localDirectoryPath 本地地址
     * @param remoteDirectory 远程文件夹 
     * */
    public boolean downLoadDirectory(String localDirectoryPath, String remoteDirectory) {
        try {
            String fileName = new File(remoteDirectory).getName();
            localDirectoryPath = localDirectoryPath + fileName + "//";
            new File(localDirectoryPath).mkdirs();
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    downloadFile(allFile[currentFile].getName(), localDirectoryPath, remoteDirectory);
                }
            }
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/" + allFile[currentFile].getName();
                    downLoadDirectory(localDirectoryPath, strremoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载文件夹失败");
            return false;
        }
        return true;
    }

    /**
     * 仅下载文件夹里面的文件
     *
     * @param localDirectoryPath
     * @param remoteDirectory
     * @return
     */
    public boolean downLoadDirectoryWithoutFolderName(String localDirectoryPath, String remoteDirectory) {
        try {
            new File(localDirectoryPath).mkdirs();
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    downloadFile(allFile[currentFile].getName(), localDirectoryPath, remoteDirectory);
                }
            }
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/" + allFile[currentFile].getName();
                    downLoadDirectoryWithoutFolderName(localDirectoryPath, strremoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("下载文件夹失败");
            return false;
        }
        return true;
    }

    public boolean createFolder(String FolderName) {
        try {
            this.ftpClient.makeDirectory(StringPool.SLASH + FolderName + StringPool.SLASH);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("创建文件夹失败");
            return false;
        }
        return true;
    }

    /**
     * 返回FTP目录下的文件列表
     *
     * @param ftpDirectory
     * @return
     */
    public List<String> getFileNameList(String ftpDirectory) {
        List<String> list = new ArrayList<String>();
        try {
            String[] fileNames = ftpClient.listNames(ftpDirectory);
            list = Arrays.asList(fileNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取ftp目录下文件
     *
     * @param ftpDirectory result:
     *                     author: lwl
     *                     date: 2020/6/29 13:37
     */
    public List<FTPFile> getFileList(String ftpDirectory) {
        List<FTPFile> list = new ArrayList<FTPFile>();
        try {
            if (this.ftpLogin()) {
                FTPFile[] ftpFiles = ftpClient.listDirectories(ftpDirectory);
                list = Arrays.asList(ftpFiles);
            }
        } catch (IOException e) {
            log.error("获取ftp目录下文件异常", e);
        } finally {
            try {
                this.getFtpClient().disconnect();
            } catch (Exception e) {
                log.error("关闭FTP异常", e);
            }
        }
        return list;
    }

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public void listFiles(String pathName, FTPClient ftp, List<String> arFiles) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            ftp.changeWorkingDirectory(pathName);
            FTPFile[] files = ftp.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    arFiles.add(pathName + file.getName());
                } else if (file.isDirectory()) {
                    // 需要加此判断。否则，ftp默认将‘项目文件所在目录之下的目录（./）’与‘项目文件所在目录向上一级目录下的目录（../）’都纳入递归，这样下去就陷入一个死循环了。需将其过滤掉。
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        listFiles(pathName + file.getName() + "/", ftp, arFiles);
                    }
                }
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourceFileName 文件名称
     * @param sourceDir      源文件夹 "/"开头
     * @param targetDir      目标文件夹 "/"开头
     * @throws IOException
     */
    public void copyFile(String sourceFileName, String sourceDir, String targetDir) throws IOException {
        ByteArrayInputStream in = null;
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        try {
            if (!this.ftpClient.changeWorkingDirectory(targetDir)) {
                this.ftpClient.makeDirectory(targetDir);
            }
            ftpClient.setBufferSize(1024 * 2);
            // 变更工作路径
            ftpClient.changeWorkingDirectory(sourceDir);
            // 设置以二进制流的方式传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 将文件读到内存中
            ftpClient.retrieveFile(new String(sourceFileName.getBytes("GBK"), "iso-8859-1"), fos);
            in = new ByteArrayInputStream(fos.toByteArray());
            if (in != null) {
                ftpClient.changeWorkingDirectory(targetDir);
                ftpClient.storeFile(new String(sourceFileName.getBytes("GBK"), "iso-8859-1"), in);
            }
        } finally {
            // 关闭流
            if (in != null) {
                in.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 复制文件夹
     *
     * @param sourceDir 源文件夹 "/"开头
     * @param targetDir 目标文件夹 "/"开头
     * @throws IOException
     */
    public void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        if (!this.ftpClient.changeWorkingDirectory(targetDir)) {
            this.ftpClient.makeDirectory(targetDir);
        }
        // 获取源文件夹当前下的文件或目录
        // File[] file = (new File(sourceDir)).listFiles();
        FTPFile[] ftpFiles = ftpClient.listFiles(sourceDir);
        for (int i = 0; i < ftpFiles.length; i++) {
            if (ftpFiles[i].isFile()) {
                copyFile(ftpFiles[i].getName(), sourceDir, targetDir);
            } else if (ftpFiles[i].isDirectory()) {
                copyDirectiory(sourceDir + StringPool.SLASH + ftpFiles[i].getName(), targetDir + StringPool.SLASH + ftpFiles[i].getName());
            }
        }
    }

    /**
     * 读取文件内容
     */
    public String readFile(String remote) {
        InputStream in = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            in = this.ftpClient.retrieveFileStream(remote);
            if (in == null) {
                return null;
            }
            String line; // 用来保存每行读取的内容
            reader = new BufferedReader(new InputStreamReader(in));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ftpClient.completePendingCommand();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串写入文件
     */
    public boolean writeFile(String text, String remote) {
        PrintWriter pw = null;
        try {
            OutputStream outputStream = ftpClient.storeFileStream(remote);
            pw = new PrintWriter(new OutputStreamWriter(outputStream,
                    "UTF-8"));
            pw.write(text);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (pw != null) {
                pw.close();
            }
            try {
                ftpClient.completePendingCommand();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    // FtpClient的Set 和 Get 函数
    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }


    /**
     * 删除ftp上的文件
     * 借助于minio
     *
     * @param pathName 桶的名称或者站点路径
     * <p>
     * result:
     *  creater: lwl
     *  time: 2020/6/15 16:42
     */
    public void deleteDirOrFile(String pathName, String fileName, MinioClient minioClient) throws Exception {
        if (StringUtils.isEmpty(pathName)) {
            return;
        }
        String path = StringPool.SLASH + pathName + StringPool.SLASH;
        if (StringUtils.isNotEmpty(fileName)) {
            path += fileName + StringPool.SLASH;
        }
        List<String> files = new ArrayList<>();
        listFiles(path, getFtpClient(), files);

        if (CollectionUtils.isNotEmpty(files)) {
            for (String file : files) {
                String replace = file.replace(StringPool.SLASH + pathName + StringPool.SLASH, "");
                minioClient.removeObject(pathName, replace);
            }
        }
    }

    /**
     * 删除ftp上的文件夹
     * result:
     * creater: lwl
     * time: 2020/6/15 16:42
     */
    public void deleteDir(String pathName, String fileName) throws IOException {
        if (StringUtils.isEmpty(pathName)) {
            return;
        }
        String path = pathName + StringPool.SLASH + fileName;
        FTPFile[] files = ftpClient.listFiles(path);
        if (files == null || files.length == 0) {
            return;
        }
        for (FTPFile f : files) {
            String currentPath = path + StringPool.SLASH + f.getName();
            if (f.isFile()) {
                getFtpClient().dele(currentPath);
            }
            if (f.isDirectory()) {
                deleteDir(path, f.getName());
                getFtpClient().removeDirectory(currentPath);
            }
        }
        getFtpClient().removeDirectory(path);
    }

    public String findContains(List<String> covers,String fileName){
        StringBuffer coverPath = new StringBuffer();
        covers.forEach(t->{
            if(null!=t){
                if(t.contains(fileName)){
                    coverPath.append(t);
                    return;
                }
            }
        });
        return coverPath.toString();
    }
}
