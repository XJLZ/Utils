package com.zkcm.hydrobiologicasinica.system.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zkcm.hydrobiologicasinica.common.exception.RedisConnectException;
import com.zkcm.hydrobiologicasinica.common.service.RedisService;
import com.zkcm.hydrobiologicasinica.system.domain.cms.PublishInfo;
import com.zkcm.hydrobiologicasinica.system.model.cms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WechatUtils {

    @Resource
    private RedisService redisService;

    @Resource
    private RestTemplate restTemplate;

    @Value("${spring.ftp.generateFile}")
    private String generateFile;

    /**
     * 获取 access_token 令牌
     *
     * @param appID
     * @param appsecret
     * @param isInit    是否初始化 1：初始化
     */
    public String getWechatAccessToken(String appID, String appsecret, Boolean isInit) throws RedisConnectException {
        String access_token = null;
        if (isInit) {
            String getAccessTokenURL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appID + "&secret=" + appsecret;
            String accessTokenJson = restTemplate.getForObject(getAccessTokenURL, String.class);
            JSONObject accessTokenJsonObject = JSONObject.parseObject(accessTokenJson);
            access_token = (String) accessTokenJsonObject.get("access_token");
            Integer expires_in = (Integer) accessTokenJsonObject.get("expires_in");
            log.info("access_token: {}", access_token);
            // access_token 存入redis 缓存
            if (null != expires_in){
                redisService.set("access_token_" + appID + "_" + appsecret, access_token, (expires_in - 600) * 1000L);
            }
            return access_token;
        } else {
            access_token = redisService.get("access_token_" + appID + "_" + appsecret);
            if (access_token != null) {
                return access_token;
            } else {
                this.getWechatAccessToken(appID, appsecret, true);
            }
        }
        return access_token;
    }


    /**
     * 新增其他类型永久素材
     */
    public String uploadOtherMaterial(MultipartFile multipartFile, String appID, String appsecret, Integer multipartFileType, byte[] coverImage) throws IOException, RedisConnectException {

        // 从缓存中获取 微信的token
        String accessToken = this.getWechatAccessToken(appID, appsecret, false);

        String uploadOtherMaterialURL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken + "&type=image";

        File file = null;
        if (multipartFileType == 0){
            file = FileUtils.getImageFile(coverImage,generateFile);
        }else if (multipartFileType == 1){
            file = FileUtils.MultipartFileToFile(multipartFile);
        }

        String type = "image";
        String title = null;
        String introduction = null;

        //设置请求体，注意是LinkedMultiValueMap
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        if ("vedio".equalsIgnoreCase(type)) {
            if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(introduction)) {
                data.add("title", title);
                data.add("introduction", introduction);
            }
        }

        //设置上传文件
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        data.add("media", fileSystemResource);

        //上传文件,设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(fileSystemResource.getFile().length());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(data, httpHeaders);

        //这里RestTemplate请求返回的字符串直接转换成JSONObject会报异常,后续深入找一下原因
        String result = restTemplate.postForObject(uploadOtherMaterialURL, requestEntity, String.class);
        JSONObject resultJson = JSON.parseObject(result);
        String media_id = (String) resultJson.get("media_id");
        if (media_id == null){
            log.error(""+result);
        }
        return media_id;
    }


    /**
     * 上传图文消息内的图片获取URL
     */
    public String getImageURL(String imgPath, String appID, String appsecret) throws RedisConnectException, IOException {
        // File file = new File("C:\\Users\\QJH\\Desktop\\1595323422(1).jpg");
        // 正文中得图片转为 FileIO File
        byte[] imageStream = null;
        if (0 == imgPath.indexOf("http")) {
            imageStream = ImageSteamUtils.getImageStreamByHttp(imgPath);  // 网络图片转换为IO
        } else {
            imageStream = ImageSteamUtils.getImageStreamByLocal(imgPath); // 本地图片转换为IO
        }
        // 字节流转File
        File file = FileUtils.getImageFile(imageStream, generateFile);

        // 从缓存中获取 微信的token
        String accessToken = this.getWechatAccessToken(appID, appsecret, false);

        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + accessToken;

        //设置请求体，注意是LinkedMultiValueMap
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();

        //设置上传文件
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        data.add("media", fileSystemResource);

        //上传文件,设置请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(fileSystemResource.getFile().length());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(data, httpHeaders);

        // restTemplate 发起POST 请求
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JSONObject resultJson = JSON.parseObject(result);
        String imgUrl = (String) resultJson.get("url");
        // System.out.println(resultJson);
        return imgUrl;
    }


    /**
     * 新增永久图文素材
     *
     * @param thumb_media_id 永久图片素材ID
     * @param contentPattern
     * @param param
     * @param appID
     * @param appsecret
     * @throws RedisConnectException
     */
    public String addNews(String thumb_media_id, String contentPattern, PublishInfo param, String appID, String appsecret) throws RedisConnectException {
        // 从缓存中获取 微信的token
        String accessToken = this.getWechatAccessToken(appID, appsecret, false);

        Articles articles = this.createArticles(thumb_media_id, contentPattern, param);
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=" + accessToken;

        //将菜单对象转换成JSON字符串
        String jsonNews = JSONObject.toJSONString(articles);

        //发起POST请求创建菜单
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String result = restTemplate.postForObject(url, jsonNews, String.class);
        JSONObject resultJson = JSONObject.parseObject(result);
        String media_id = (String) resultJson.get("media_id");
        log.info("media_id: {}", media_id);
        return media_id;
    }

    private Articles createArticles(String thumb_media_id, String contentPattern, PublishInfo param) {
        Articles articles = new Articles();
        List<News> dataList = new ArrayList<News>();
        News news1 = new News();
        news1.setTitle(param.getTitle());
        news1.setThumb_media_id(thumb_media_id);
        news1.setAuthor(param.getAuthorName());
        news1.setDigest(param.getSummary());
        news1.setShow_cover_pic(1);//显示封面
        // news1.setContent("图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 \"http://mmbiz.qpic.cn/mmbiz_png/yCOFTsicLQWBHdTGdNDv2OibJXvv1laXIb8mfdiax1DyicpCU6ETZbW5ibhSUdkxujkc1HZGopRU8UoyvXkibevN3miaw/0?wx_fmt=png\"接口获取。外部图片url将被过滤。");
        news1.setContent(contentPattern);
        news1.setContent_source_url(param.getContentUrl());  //图文消息的原文地址，即点击“阅读原文”后的URL
        news1.setNeed_open_comment(0);   //Uint32  是否打开评论，0不打开，1打开
        news1.setOnly_fans_can_comment(1);    //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
        dataList.add(news1);

        articles.setArticles(dataList);
        return articles;
    }


    /**
     * 发布图文信息
     *
     * @param media_id
     * @param appID
     * @param appsecret
     * @return
     * @throws RedisConnectException
     */
    public Integer publish(String media_id, String appID, String appsecret) throws RedisConnectException {
        // 从缓存中获取 微信的token
        String accessToken = this.getWechatAccessToken(appID, appsecret, false);

        Wrap wrap = new Wrap();
        Mpnews mpnews = new Mpnews();
        mpnews.setMedia_id(media_id);
        wrap.setMpnews(mpnews);
        wrap.setFilter(new Filter());

        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + accessToken;

        //将菜单对象转换成JSON字符串
        String jsonNews = JSONObject.toJSONString(wrap);

        //发起POST请求创建菜单
        String result = restTemplate.postForObject(url, jsonNews, String.class);
        JSONObject resultjson = JSONObject.parseObject(result);
        Integer errCode = (Integer) resultjson.get("errcode");
        return errCode;
    }


    public Integer preview(String media_id, String appID, String appsecret) throws RedisConnectException {
        // String media_id = "_9NXjcIs5UrMtGrNIRqBFimq62bukLEHWESa1-CeNFI";
        String accessToken = this.getWechatAccessToken(appID, appsecret, false);
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=" + accessToken;

        Wrap wrap = new Wrap();
        wrap.setTouser("oLqU_xPEIWzisih00HAuNcxn3epU");
        Mpnews mpnews = new Mpnews();
        mpnews.setMedia_id(media_id);
        wrap.setMpnews(mpnews);
        //将菜单对象转换成JSON字符串
        String jsonNews = JSONObject.toJSONString(wrap);

        //发起POST请求创建菜单
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(url, jsonNews, String.class);
        JSONObject resultjson = JSONObject.parseObject(result);
        Integer errCode = (Integer) resultjson.get("errcode");
        return errCode;
    }


}
