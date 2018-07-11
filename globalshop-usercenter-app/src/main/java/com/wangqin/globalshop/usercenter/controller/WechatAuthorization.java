package com.wangqin.globalshop.usercenter.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.InventoryOnWarehouseMapperExt;
import com.wangqin.globalshop.common.redis.Cache;
import com.wangqin.globalshop.common.utils.EasyUtil;
import com.wangqin.globalshop.common.utils.WxPay.PayUtil;
import com.wangqin.globalshop.usercenter.wechat_sdk.AesException;
import com.wangqin.globalshop.usercenter.wechat_sdk.WXBizMsgCrypt;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * @author biscuit
 * @data 2018/07/10
 * 第三方平台授权controller
 */
@RestController
@RequestMapping("/account")
public class WechatAuthorization {
    @Resource
    private Cache loginCache;
//    @Autowired
//    private RedisTemplate redisTemplate;

    @RequestMapping("/authorization")
    public void getComponentVerifyTicket(@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
                                         @RequestParam("msg_signature") String msgSignature, @RequestBody String postData) {
        System.out.println("微信轮询开始");
        WXBizMsgCrypt pc;
        InputStream in = null;
        try {
            pc = new WXBizMsgCrypt("FBDctXZiRWQYjrXMm5txJPAFWsEdkYnc", "FBDctXZiRWQYjrXMm5txJPAFWsEdkYnc7wud647ryu1", componentAppid);

            postData = postData.replace("AppId", "ToUserName");
            String result = pc.DecryptMsg(msgSignature, timestamp, nonce, postData);
            System.out.println("解密后明文:" + result);
            in = new ByteArrayInputStream(result.getBytes());
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            HashMap<String, String> resultMap = new HashMap<>();
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            for (Element element : elementList) {
                resultMap.put(element.getName(), element.getText());
            }
            System.out.println("resultMap+++++++++++");
            System.out.println(resultMap.toString());
            String componentVerifyTicket = resultMap.get("ComponentVerifyTicket");
            System.out.println("componentVerifyTicket+++++++++++++++");
            System.out.println(componentVerifyTicket);
            loginCache.put("componentVerifyTicket", componentVerifyTicket);
        } catch (AesException | DocumentException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    //todo
    private String componentAppid = "wxe25c15397f0ec710";
    private String componentAppsecret = "8eb667a448cb3226d57878acfaca84a0";

    @RequestMapping("/getHtml")
    public String getHtml() {
        String re_url = null;
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + getToken();
            //language=JSON
            String param = "{\"component_appid\":\"" + componentAppid + "\"}";
            String post = PayUtil.httpRequest(url, "POST", param);
            System.out.println("获取预授权码回调:"+post);
            JSONObject object = JSON.parseObject(post);
            /**预授权码*/
            String preAuthCode = object.getString("pre_auth_code");
            System.out.println("预授权码:"+preAuthCode);
            re_url = URLEncoder.encode("http://test.buyer007.cn/account/queryAuth", "UTF-8");

            String reUrl = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=" + componentAppid + "&pre_auth_code=" + preAuthCode + "&redirect_uri=" + re_url + "&auth_type=2";
            System.out.println("回调地址:"+reUrl);
            return "<html><head><title>Title</title></head><body><a href=\"" + reUrl + "\">授权小程序</a></body>\n" +
                    "</html>";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //todo
        return "";


    }

    /**
     * 根据auth_code查询授权信息
     *
     * @param authCode  授权成功时获得的授权码
     * @param expiresIn 存活时间
     * @return
     */
    @RequestMapping(value = "/queryAuth")
    public String queryAuth(@RequestParam("auth_code") String authCode, @RequestParam("expires_in") String expiresIn) {
        System.out.println("进入授权回调");
        System.out.println("auth_code=" + authCode);
        System.out.println("expires_in=" + expiresIn);
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + getToken();
        String param = "{\"component_appid\":\"" + componentAppid + "\",\"authorization_code\":\"" + authCode + "\"}";
        String s = PayUtil.httpRequest(url, "POST", param);
        System.out.println("----------------授权回调接口-------------");
        System.out.println(s);
        System.out.println("----------------授权回调接口-------------");
        return "success";
    }
//    {"authorization_info":{"authorizer_appid":"wx43020a9d04042b56","authorizer_access_token":"11_ENumAbXuzu28zKh-Hf8WJ1dCG3UGJwYhT9fjFASwRpfHDxioituzWxN4TG5NT6tODvXplVTSEqyjGIpQXEfVcF2dUNvL4hdjkTPrh1ALjuC7xIdqMl1zjC0_cOhwn7hvYMeCaIWITysYpHi5HYHiAEDQHQ","expires_in":7200,"authorizer_refresh_token":"refreshtoken@@@pHG_ZxB_wknyZNjSERpeL5Crc5fOlCn098pmRARTiQg","func_info":[{"funcscope_category":{"id":17}},{"funcscope_category":{"id":18},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":19}},{"funcscope_category":{"id":25},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":30},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":31},"confirm_info":{"need_confirm":0,"already_confirm":0,"can_confirm":0}},{"funcscope_category":{"id":36}},{"funcscope_category":{"id":37}},{"funcscope_category":{"id":40}}]}}


    private String getToken() {
        String componentAccessToken = (String) loginCache.get("component_access_token");
        /**判断数据库中是否存在component_access_token*/
        if (!EasyUtil.isStringEmpty(componentAccessToken)) {
            System.out.println("token============"+componentAccessToken);
            /**如果存在，直接返回token的值*/
            return componentAccessToken;
        }
        String componentVerifyTicket = (String) loginCache.get("componentVerifyTicket");
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
        String param = "{\"component_appid\":\"" + componentAppid + "\",\"component_appsecret\": \"" + componentAppsecret + "\",\"component_verify_ticket\":\"" + componentVerifyTicket + "\"}";
        String s = PayUtil.httpRequest(url, "POST", param);
        JSONObject obj = JSON.parseObject(s);
        componentAccessToken = obj.getString("component_access_token");
        /**在返回结果中获取token*/
        /**保存token，并设置有效时间*/
        System.out.println("token============"+componentAccessToken);
        loginCache.putEx("component_access_token", componentAccessToken, 7200L);
        return componentAccessToken;

    }

    public static void main(String[] args) {
        String k = "ticket@@@2J33clY2wz9SA8iQPPHUmO9r2BXVmilvhhhhiZZiI7j_o2wNSrg0wamwqadIiwyVayS5vfGM-6fFSC-r5Oge7A";
        String componentAppid = "wxe25c15397f0ec710";
        String componentAppsecret = "8eb667a448cb3226d57878acfaca84a0";
        String url = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
        String param = "{\"component_appid\":\"" + componentAppid + "\",\"component_appsecret\": \"" + componentAppsecret + "\",\"component_verify_ticket\":\"" + k + "\"}";
        String s = PayUtil.httpRequest(url, "POST", param);
        JSONObject obj = JSON.parseObject(s);
        String component_access_token = obj.getString("component_access_token");


        String url1 = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + component_access_token;
        //language=JSON
        String param1 = "{\"component_appid\":\"" + componentAppid + "\"}";
        String post = PayUtil.httpRequest(url1, "POST", param1);
        System.out.println("获取预授权码回调:"+post);

    }
//    public static void main(String[] args) {
//        ByteArrayInputStream in = new ByteArrayInputStream(result.getBytes());
//        // 读取输入流
//        SAXReader reader = new SAXReader();
//        Document document = reader.read(in);
//        HashMap<String, String> resultMap = new HashMap<>();
//        // 得到xml根元素
//        Element root = document.getRootElement();
//        // 得到根元素的所有子节点
//        List<Element> elementList = root.elements();
//        for (Element element : elementList) {
//            resultMap.put(element.getName(), element.getText());
//        }
//        System.out.println("resultMap");
//        System.out.println(resultMap);
//        String componentVerifyTicket = resultMap.get("ComponentVerifyTicket");
//        System.out.println("componentVerifyTicket");
//    }


}
