package com.zkcm.hydrobiologicasinica.common.utils;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zkcm.hydrobiologicasinica.common.exception.HsException;
import com.zkcm.hydrobiologicasinica.system.enums.ContentTypeEnum;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @description： Minio工具类
 * @author     ：lwl
 * @date       ：2020/8/14 9:35
 * @version:     1.0.0
 */
@Slf4j
public class MinIOUtil {

    /**
     *  创建minio的桶
     * @param minioClient
     * @param bucketName
     * @param foreign
     * result:
     * author: lwl
     * date: 2020/8/14 9:34
     */
    public static void createBucket(MinioClient minioClient, String bucketName, Boolean foreign) throws Exception{
        if (StringUtils.isBlank(bucketName)||minioClient == null ) {
            throw new Exception("桶不能为空");
        }
        bucketName = bucketName.toLowerCase();
        // 检查存储桶是否已经存在
        if (!minioClient.bucketExists(bucketName)) {
            // 创建一个名为的存储桶
            minioClient.makeBucket(bucketName);
        }
        // 设置对外访问
        if (foreign == null || foreign) {
            minioClient.setBucketPolicy(bucketName, getPolicy(bucketName));
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName  minio桶
     * @param bucketNext  桶下面某个目录
     * @param minioClient minio实体类
     * @param inputStream 上传文件流
     * @param objectName  上传文件的名称
     * @param contentTypeEnum 上传文件的类型【取值参考MinioUtil 静态属性】
     *                    result:
     *                    creater: lwl
     *                    time: 2020/3/13 9:30
     */
    public static String upload(String bucketName,String bucketNext, MinioClient minioClient, InputStream inputStream, String objectName, ContentTypeEnum contentTypeEnum, Boolean foreign) throws Exception {
        if (minioClient == null || inputStream == null || contentTypeEnum == null) {
            throw new Exception("对象不能为空");
        }
        createBucket(minioClient,bucketName,foreign);
        minioClient.putObject(bucketName, bucketNext+StringPool.SLASH+objectName, inputStream, null, null, null, contentTypeEnum.getName());
        return minioClient.getObjectUrl(bucketName, bucketNext+StringPool.SLASH+objectName);
    }

    /**
     *  上传到桶里
     * result:
     * author: lwl
     * date: 2020/8/14 9:48
     */
    public static String upload(String bucketName, MinioClient minioClient, InputStream inputStream, String objectName, ContentTypeEnum contentTypeEnum, Boolean foreign) throws Exception {
        if (minioClient == null || inputStream == null || contentTypeEnum == null) {
            throw new Exception("对象不能为空");
        }
        createBucket(minioClient,bucketName,foreign);
        minioClient.putObject(bucketName, objectName, inputStream, null, null, null, contentTypeEnum.getName());
        return minioClient.getObjectUrl(bucketName, StringPool.SLASH+objectName);
    }


    /**
     * 下载minio上的文件
     *
     * @param bucketName  minio桶
     * @param objectName  需要下载的文件名称 （包含他的全部路径）
     * @param minioClient minio实体类
     *                    result:
     *                    creater: lwl
     *                    time: 2020/3/13 10:59
     */
    public static InputStream getObject(String bucketName, String objectName, MinioClient minioClient) throws Exception {
        if(StringUtils.isBlank(bucketName)){
            throw new HsException("桶名不能为空");
        }
        return minioClient.getObject(bucketName, objectName);
    }

    /**
     * 设置桶的策略 公共访问
     *
     * @param bucketName result:
     *                   creater: lwl
     *                   time: 2020/3/13 10:45
     */
    public static String getPolicy(String bucketName) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");
        builder.append("                \"s3:GetBucketLocation\",\n");
        builder.append("                \"s3:ListBucket\"\n");
        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::" + bucketName + "\"\n");
        builder.append("        },\n");
        builder.append("        {\n");
        builder.append("            \"Action\": \"s3:GetObject\",\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n");
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2012-10-17\"\n");
        builder.append("}\n");
        return builder.toString();
    }





    /**
     * 获取某个桶下的某个文件下的文件
     *result:
     *creater: lwl
     *time: 2020/4/1 16:27
     */
    public static List<Item> getBucketFile(String bucketName, String fileName,MinioClient minioClient, String prefix, boolean recursive) throws Exception{
        List<Item> result = new ArrayList<>();
        if(minioClient.bucketExists(bucketName)){
            Iterable<Result<Item>> test = minioClient.listObjects(bucketName,prefix,recursive);
            test.forEach( res -> {
                try {
                    if(StringUtils.isBlank(fileName)){
                        result.add(res.get());
                    }else if(res.get().objectName().contains(fileName)){
                        result.add(res.get());
                    }
                } catch (Exception e) {
                    log.error("获取桶下的文件异常："+bucketName,e);
                }
            });
        }
        return result;
    }
    /**
     * 将某个桶下的文件夹拷贝到另一个桶下
     * @param minioClient
     * @param bucketName 来源桶
     * @param destBucketName 目标桶
     * @param fileName 文件名称
     *result:
     *creater: lwl
     *time: 2020/4/1 16:32
     */
    public static void copyBucketFileToBucket(MinioClient minioClient,String bucketName, String destBucketName,String fileName,String currentPrefix,String filePrefix) throws Exception {
        if(minioClient.bucketExists(bucketName)&&minioClient.bucketExists(destBucketName)){
            Iterable<Result<Item>> test = minioClient.listObjects(bucketName,null,true);
            test.forEach( res -> {
                String objectName = "";
                InputStream stream = null;
                try {
                    if(res.get().objectName().split(StringPool.SLASH)[1].equals(fileName)&&res.get().objectName().split(StringPool.SLASH)[0].equals(currentPrefix)){
                        objectName =  res.get().objectName();
                        stream = minioClient.getObject(bucketName, objectName);
                        objectName = objectName.replace(currentPrefix, filePrefix);
                        minioClient.putObject(destBucketName,objectName,stream,null,null,null,null);
                    }
                } catch (Exception e) {
                    log.error("拷贝桶下的文件异常："+bucketName+" ,文件名为："+objectName,e);
                } finally {
                    if(stream !=null){
                        try {
                            stream.close();
                        } catch (IOException e) {
                            log.error("拷贝桶下的文件关闭流异常："+bucketName+" ,文件名为："+objectName,e);
                        }
                    }
                }
            });
        }
    }

    /**
     * 查询某个桶下某个文件的存储桶里的对象名称
     *   使用场景：
     *      知道桶某个目录下的文件名称，但是不知道后缀，可以通过此方法，精确拿到桶中的对象名称
     * @param minioClient
     * @param bucketName
     * @param fileName 如果有结构，先定义好结构
     *result:
     *creater: lwl
     *time: 2020/4/1 17:40
     */
    public static String getBucketObjectName(MinioClient minioClient,String bucketName, String fileName) throws Exception {
        StringBuffer objectName = new StringBuffer();
        if(minioClient.bucketExists(bucketName)){
            Iterable<Result<Item>> test = minioClient.listObjects(bucketName,null,true);
            test.forEach( res -> {
                try {
                    if(res.get().objectName().contains(fileName)){
                        objectName.append( res.get().objectName());
                        return;
                    }
                } catch (Exception e) {
                }
            });
        }
        return objectName.toString();
    }
}