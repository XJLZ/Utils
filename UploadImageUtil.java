package com.zkcm.hydrobiologicasinica.common.utils;

import com.zkcm.hydrobiologicasinica.system.constant.MinioConstant;
import com.zkcm.hydrobiologicasinica.system.enums.ContentTypeEnum;
import io.minio.MinioClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description： 上传图片工具类
 * @author     ：lwl
 * @date       ：2019/10/23 15:24
 * @version:
 */
public class UploadImageUtil {




    /**
     * 上传图片
     * @param file 上传文件对象
     * @param savePath 上传文件对象
     * @param urlPath 拼接URL
     * @param sitePath
     */
    public static Map<String,String> uploadImg(MultipartFile file, HttpServletRequest request, String savePath, String urlPath, String sitePath,MinioClient minioClient){
        Map<String,String> map = new HashMap<>();
        map.put("code","NO");
        if (StringUtils.isBlank(sitePath)){
            map.put("code","NO");
            return map;
        }

        if(file!=null){
            String fileName=file.getOriginalFilename();//获取文件名加后缀
            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +urlPath;//存储路径
            //文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            //新的文件名
            fileName = new Date().getTime()+"_"+new Random().nextInt(1000)+suffix;
            //先判断文件是否存在
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fileAdd = sdf.format(new Date());
            //获取文件夹路径
            File file1 =new File(savePath+"/"+fileAdd);
            //如果文件夹不存在则创建
            if(!file1 .exists()  && !file1 .isDirectory()){
                file1 .mkdirs();
            }
            //将图片存入文件夹
            File targetFile = new File(file1, fileName);
            try {
                //将上传的文件写到服务器上指定的文件。
                // file.transferTo(targetFile);
                String url = MinIOUtil.upload(sitePath, MinioConstant.BUCKET_OTHER_INFO, minioClient, file.getInputStream(), fileName, ContentTypeEnum.IMAGE_CONTENT_TYPE, true);
                // url显示地址
                // String url = returnUrl+fileAdd+"/"+fileName.substring(0,fileName.indexOf("."))+"/"+fileName.substring(fileName.indexOf(".")+1);
                String localPath =  fileAdd + "/" + fileName;
                map.put("url",url);
                map.put("localPath",localPath);
                map.put("code","YES");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;

    }



}
