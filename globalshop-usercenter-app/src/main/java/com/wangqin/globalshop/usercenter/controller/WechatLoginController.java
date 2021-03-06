package com.wangqin.globalshop.usercenter.controller;

import com.alibaba.fastjson.JSON;
import com.wangqin.globalshop.biz1.app.aop.annotation.Authenticated;
import com.wangqin.globalshop.biz1.app.bean.dataVo.AuthUserVO;
import com.wangqin.globalshop.biz1.app.bean.dataVo.JsonResult;
import com.wangqin.globalshop.biz1.app.dal.dataObject.AuthUserDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.CompanyDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.WxUserDO;
import com.wangqin.globalshop.common.exception.ErpCommonException;
import com.wangqin.globalshop.common.redis.Cache;
import com.wangqin.globalshop.common.utils.*;
import com.wangqin.globalshop.common.utils.czh.ParseObj2Obj;
import com.wangqin.globalshop.usercenter.service.IUserCompanyService;
import com.wangqin.globalshop.usercenter.service.IUserService;
import com.wangqin.globalshop.usercenter.service.UserUploadFileService;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author biscuit
 * @data 2018/06/20
 */
@Controller
@RequestMapping("/wechatLogin")
@ResponseBody
public class WechatLoginController {
    @Value("#{sys.SESSION_ID}")
    private String SESSION_ID;
    @Value("#{sys.LOGIN_REDIRECT}")
    private String login_redirect;
    @Value("#{sys.PROXY_REDIRECT}")
    private String proxy_redirect;
    @Value("#{sys.AUTH_REDIRECT}")
    private String auth_redirect;
    @Value("#{sys.COMPANY_NO}")
    private String COMPANY_NO;
    @Value("#{sys.APPSECRET}")
    private String APPSECRET;
    @Value("#{sys.appid}")
    private String appid;
    @Value("#{sys.sysurl}")
    private String sysurl;
    @Autowired
    private UserUploadFileService uploadFileService;
    private static long TIMEOUT = 30 * 60 * 1000;
    private static Logger log = LoggerFactory.getLogger("WechatLoginController");

    @Autowired
    private IUserService userService;
    @Autowired
    private Cache loginCache;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IUserCompanyService companyService;


    @RequestMapping("/login")
    public Object login(HttpServletRequest request, HttpServletResponse response, String code) {
        JsonResult<List<AuthUserDO>> result = new JsonResult<>();
        try {
            /**获取openId和token*/
            JSONObject o = HttpClientUtil.post("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + APPSECRET + "&code=" + code + "&grant_type=authorization_code", null, null, "2");
            /**如果相应包含errcode  表示请求失败*/
            if (o.containsKey("errcode")) {
                return result.buildIsSuccess(false).buildMsg(o.getString("errmsg"));
            }
            String unionid = o.getString("unionid");
            List<AuthUserDO> list = userService.selectByUnionid(unionid);
            if (list.size() > 1) {
                return result.buildIsSuccess(true).buildData(list).buildMsg("请选择您要登陆的账户");

            }
            if (list.size() == 1) {
                AuthUserDO user = list.get(0);
                String sessionId = (String) request.getAttribute(SESSION_ID);
                if (StringUtils.isBlank(sessionId)) {
                    sessionId = CookieUtil.getCookieValue(request, SESSION_ID);
                }

                loginCache.putEx(sessionId, user.getLoginName(), TIMEOUT);
                loginCache.putEx(COMPANY_NO + sessionId, user.getCompanyNo(), TIMEOUT);
                AppUtil.setLoginUser(user.getLoginName(), user.getCompanyNo());
                response.sendRedirect(sysurl);
                return result.buildIsSuccess(true).buildMsg("登陆成功");
            }
        } catch (IOException e) {
            return result.buildIsSuccess(false).buildMsg("跳转地址错误：" + sysurl);
//            e.printStackTrace();
        }
        return result.buildIsSuccess(false).buildMsg("您还不是本平台的用户,请联系公司管理员进行授权后登陆");

    }

    @RequestMapping("/authorized")
    public Object authorized(String code, String state, HttpServletResponse response) {
        JsonResult<List<AuthUserDO>> result = new JsonResult<>();
        try {
            /**获取openId和token*/
            JSONObject o = HttpClientUtil.post("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + APPSECRET + "&code=" + code + "&grant_type=authorization_code", null, null, "2");
            /**如果相应包含errcode  表示请求失败*/
            if (o.containsKey("errcode")) {
                return result.buildIsSuccess(false).buildMsg(o.getString("errmsg"));
            }
            String unionid = o.getString("unionid");
            List<AuthUserDO> list = userService.selectByUnionidAndCompanyNo(unionid, state);
            if (hasAuthUser(list, state)) {
                return result.buildIsSuccess(false).buildMsg("您已存在当前公司的账户，不允许重复绑定");
            }

            String openid = o.getString("openid");
            String accessToken = o.getString("access_token");
            /**根据token获取用户的信息*/
            JSONObject object = HttpClientUtil.post("https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid, null, null, "2");
            WxUserDO user = new WxUserDO();
            user.setCompanyNo(state);
            user.setGender(Integer.valueOf(object.getString("sex")));
            user.setNickName(object.getString("nickname"));
            user.setProvince(object.getString("province"));
            user.setCity(object.getString("city"));
            user.setCountry(object.getString("country"));
            user.setOpenId(openid);
            user.setUnionId(unionid);
            String headImgUrl = uploadFileService.uploadImg(new URL(object.getString("headimgurl")).openStream(), unionid);
            user.setAvatarUrl(headImgUrl);

            userService.addUserByqrcode(state, user);
        } catch (ErpCommonException e) {
            return result.buildIsSuccess(false).buildMsg(e.getErrorMsg());
        } catch (IOException e) {
            return result.buildIsSuccess(false).buildMsg(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return result.buildIsSuccess(false).buildMsg("未知错误");
        }
        return result.buildIsSuccess(true).buildMsg("授权成功");
    }

    private boolean hasAuthUser(List<AuthUserDO> list, String state) {
        for (AuthUserDO authUserDO : list) {
            if (state.equals(authUserDO.getCompanyNo())) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/setProxy")
    public Object setProxy(String code, String state, HttpServletResponse response) {
        JsonResult<List<AuthUserDO>> result = new JsonResult<>();
        try {
            /**获取openId和token*/
            JSONObject o = HttpClientUtil.post("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + APPSECRET + "&code=" + code + "&grant_type=authorization_code", null, null, "2");
            /**如果相应包含errcode  表示请求失败*/
            if (o.containsKey("errcode")) {
                return result.buildIsSuccess(false).buildMsg(o.getString("errmsg"));
            }
            String unionid = o.getString("unionid");
            String companyNo = AppUtil.getLoginUserCompanyNo();
            List<AuthUserDO> list = userService.selectByUnionidAndCompanyNo(unionid, companyNo);
            if (hasAuthUser(list, companyNo)) {
                return result.buildIsSuccess(false).buildMsg("您已存在当前公司的账户，不允许重复绑定");
            }

            String openid = o.getString("openid");
            String accessToken = o.getString("access_token");
            /**根据token获取用户的信息*/
            JSONObject object = HttpClientUtil.post("https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid, null, null, "2");
            WxUserDO user = new WxUserDO();
            user.setCompanyNo(companyNo);
            user.setGender(Integer.valueOf(object.getString("sex")));
            user.setNickName(object.getString("nickname"));
            user.setProvince(object.getString("province"));
            user.setCity(object.getString("city"));
            user.setCountry(object.getString("country"));
            user.setOpenId(openid);
            user.setUnionId(unionid);
            String headImgUrl = uploadFileService.uploadImg(new URL(object.getString("headimgurl")).openStream(), unionid);
            user.setAvatarUrl(headImgUrl);
            userService.addProxy(state, user);
        } catch (ErpCommonException e) {
            return result.buildIsSuccess(false).buildMsg(e.getErrorMsg());
        } catch (IOException e) {
            return result.buildIsSuccess(false).buildMsg(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return result.buildIsSuccess(false).buildMsg("未知错误");
        }
        return result.buildIsSuccess(true).buildMsg("新增代理成功");
    }


    /**
     * 获取微信登陆二维码的链接
     *
     * @return
     */
    @RequestMapping("/getUrl")
    public Object getUrl() {
        JsonResult<Object> result = new JsonResult<>();
        try {
            String baseUrl = sysurl + login_redirect;
            baseUrl = URLEncoder.encode(baseUrl, "UTF-8");
            String url = "https://open.weixin.qq.com/connect/qrconnect?appid=wxfcdeefc831b3e8c4&redirect_uri=" + baseUrl + "&response_type=code&scope=snsapi_login";
            return result.buildData(url).buildIsSuccess(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.buildIsSuccess(false).buildMsg("获取失败");

    }


    /**
     * 获取微信授权二维码的图片链接
     *
     * @return
     */
    @RequestMapping("/getImgUrl")
    public Object getImgUrl() {
        JsonResult<Object> result = new JsonResult<>();
        try {
            String baseUrl = sysurl + "/wechatLogin/authorized";
            baseUrl = URLEncoder.encode(baseUrl, "UTF-8");
            String url = "https://open.weixin.qq.com/connect/qrconnect?appid=wxfcdeefc831b3e8c4&redirect_uri=" + baseUrl + "&response_type=code&scope=snsapi_login";
            String state = AppUtil.getLoginUserCompanyNo();
            url = url + "&state=" + state;
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("img");
            if (elements.size() != 1) {
                return result.buildIsSuccess(false).buildMsg("二维码链接有误");
            }
            String img = "";
            for (Element element : elements) {
                img = element.attr("src");
            }

            return result.buildData("https://open.weixin.qq.com" + img).buildIsSuccess(true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return result.buildIsSuccess(false).buildMsg("获取失败");
        } catch (IOException e) {
            e.printStackTrace();
            return result.buildIsSuccess(false).buildMsg("获取失败");
        }
    }

    /**
     * 获取微信授权二维码的网页
     *
     * @return
     */
    @RequestMapping("/getHtml")
    @Authenticated
    public void getImgHtml(HttpServletResponse response) {
        String baseUrl = sysurl + auth_redirect;
        log.info("回调地址：" + baseUrl);
        try {
            String companyNo = AppUtil.getLoginUserCompanyNo();
            if (StringUtils.isBlank(companyNo)) {
                response.setStatus(302);
                return;
            }
            baseUrl = URLEncoder.encode(baseUrl, "UTF-8");

            String str = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "  <meta charset=\"UTF-8\">\n" + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + "  <title></title>\n" + "</head>\n" + "<body>\n" + "    <script src=\"https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js\"></script>\n" + "    <div id=\"login_container\"></div>\n" + "    <script>\n" + "      var obj = new WxLogin\n" + "      ({\n" + "          id:\"login_container\",//div的id\n" + "          appid: \"" + appid + "\",\n" + "          scope: \"snsapi_login\",//写死\n" + "          redirect_uri: '" + baseUrl + "',\n" + "state: \"" + companyNo + "\",\n" + "style: \"black\",\n" + "});\n" + "</script>\n" + "</body>\n" + "</html>\n";
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(str);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取微信授权二维码的网页
     *
     * @return
     */
    @RequestMapping("/getLoginHtml")
    public void getLoginHtml(HttpServletResponse response) {
        String baseUrl = sysurl + login_redirect;
        try {
            baseUrl = URLEncoder.encode(baseUrl, "UTF-8");

            String str = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "  <meta charset=\"UTF-8\">\n" + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + "  <title></title>\n" + "</head>\n" + "<body>\n" + "    <script src=\"https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js\"></script>\n" + "    <div id=\"login_container\"></div>\n" + "    <script>\n" + "      var obj = new WxLogin\n" + "      ({\n" + "          id:\"login_container\",//div的id\n" + "          appid: \"" + appid + "\",\n" + "          scope: \"snsapi_login\",//写死\n" + "          redirect_uri: '" + baseUrl + "',\n" + "          state: \"\",\n" + "          style: \"black\",\n" + "          href: \"data:text/css;base64,LmltcG93ZXJCb3ggLnFyY29kZXsKICB3aWR0aDogMTgwcHghaW1wb3J0YW50Owp9Ci5pbXBvd2VyQm94IC5pbmZvIHsKICB3aWR0aDogMjgwcHg7CiAgbWFyZ2luOiAwIGF1dG87CiAgbWFyZ2luLWxlZnQ6IC0zMHB4IWltcG9ydGFudDsKICBwYWRkaW5nLWxlZnQ6IDEwcHg7Cn0KLndycF9jb2RlIHsKICBtYXJnaW4tbGVmdDogLTYwcHg7Cn0KLmltcG93ZXJCb3ggLnRpdGxlewogIG1hcmdpbi1sZWZ0OiAtNjBweDsKfQ==\",\n" + "      });\n" + "    </script>\n" + "</body>\n" + "</html>\n";
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(str);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取代理二维码的网页
     *
     * @return
     */
    @RequestMapping("/getProxyHtml")
    public void getProxyHtml(HttpServletResponse response, String parentAgent) {
        log.info("获取代理二维码");
        String baseUrl = sysurl + proxy_redirect;
        log.info("baseUrl" + baseUrl);
        parentAgent = StringUtil.isBlank(parentAgent) ? "" : parentAgent;
        log.info("parentAgent" + parentAgent);
        try {
            baseUrl = URLEncoder.encode(baseUrl, "UTF-8");
            String str = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "  <meta charset=\"UTF-8\">\n" + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + "  <title></title>\n" + "</head>\n" + "<body>\n" + "    <script src=\"https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js\"></script>\n" + "    <div id=\"login_container\"></div>\n" + "    <script>\n" + "      var obj = new WxLogin\n" + "      ({\n" + "          id:\"login_container\",//div的id\n" + "          appid: \"" + appid + "\",\n" + "          scope: \"snsapi_login\",//写死\n" + "          redirect_uri: '" + baseUrl + "',\n" + "          state: \"" + parentAgent + "\",\n" + "          style: \"black\",\n" + "          href: \"data:text/css;base64,LmltcG93ZXJCb3ggLnFyY29kZXsKICB3aWR0aDogMTgwcHghaW1wb3J0YW50Owp9Ci5pbXBvd2VyQm94IC5pbmZvIHsKICB3aWR0aDogMjgwcHg7CiAgbWFyZ2luOiAwIGF1dG87CiAgbWFyZ2luLWxlZnQ6IC0zMHB4IWltcG9ydGFudDsKICBwYWRkaW5nLWxlZnQ6IDEwcHg7Cn0KLndycF9jb2RlIHsKICBtYXJnaW4tbGVmdDogLTYwcHg7Cn0KLmltcG93ZXJCb3ggLnRpdGxlewogIG1hcmdpbi1sZWZ0OiAtNjBweDsKfQ==\",\n" + "      });\n" + "    </script>\n" + "</body>\n" + "</html>\n";
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            log.info("html" + str);
            PrintWriter writer = response.getWriter();
            writer.print(str);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }


    @PostMapping("/loginByUserNo")
    public Object loginByUserNo(String userNo, String companyNo, String code, String loginToken) {
        JsonResult<Object> result = new JsonResult<>();
        AuthUserDO user = userService.selectByUserNoAndCompanyNo(userNo, companyNo);
        if (user == null) {
            return result.buildIsSuccess(false).buildMsg("登陆失败");
        }
        String sessionId = (String) request.getAttribute(SESSION_ID);
        if (StringUtils.isBlank(sessionId)) {
            sessionId = CookieUtil.getCookieValue(request, SESSION_ID);
        }
        loginCache.putEx(sessionId, user.getLoginName(), TIMEOUT);
        loginCache.putEx(COMPANY_NO + sessionId, user.getCompanyNo(), TIMEOUT);
        AppUtil.setLoginUser(user.getLoginName(), user.getCompanyNo());
        Map<String, String> map = new HashMap<>();
        map.put("userName", user.getName());
        map.put("loginName", user.getLoginName());
        return result.buildIsSuccess(true).buildMsg("登陆成功").buildData(map);
    }

    @PostMapping("/getUserInfo")
    public Object getLoginHtml(String code) {
        log.info("开始登陆" + code);
        JsonResult<Object> result = new JsonResult<>();
        Map<String, String> map = new HashMap<>();
        /**获取openId和token*/
        JSONObject o = HttpClientUtil.post("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + APPSECRET + "&code=" + code + "&grant_type=authorization_code", null, null, "2");
        log.info("获取openId和token的结果" + o.toString());
        /**如果相应包含errcode  表示请求失败*/
        if (o.containsKey("errcode")) {
           // return result.buildIsSuccess(false).buildMsg(o.getString("errmsg"));
        	return result.buildIsSuccess(false).buildMsg("请勿刷新，否则需要重新扫码");
        }
        String unionid = o.getString("unionid");
        List<AuthUserDO> list = userService.selectByUnionid(unionid);
        List<AuthUserVO> vos = new ArrayList();

        if (list.size() > 1) {
            log.info("有多个账户" + list);
            for (AuthUserDO authUser : list) {
                AuthUserVO authUserVO = new AuthUserVO();
                ParseObj2Obj.parseObj2Obj(authUser, authUserVO);
                String companyNo = authUser.getCompanyNo();
                log.info(companyNo);
                CompanyDO companyDO = companyService.selectByCompanyNo(companyNo);
                if (companyDO == null) {
                    return result.buildIsSuccess(false).buildMsg("找不到对应的公司");
                }
                authUserVO.setCompanyName(companyDO.getCompanyName());
                vos.add(authUserVO);
                log.info("用户信息=====" + authUserVO);
            }
            map.put("status", "2");
            map.put("userInfo", JSON.toJSONString(vos));
            map.put("code", code);
            map.put("loginToken", "123");
            log.info("响应=====" + map);
            //result.buildIsSuccess(true).buildData(map);
            result.buildIsSuccess(true).buildData(map).buildMsg("扫码成功，请选择商家");
            log.info("响应=====" + result);
            return result;
        }
        if (list.size() == 1) {
            log.info("有一个账户" + list);
            AuthUserDO user = list.get(0);
            String sessionId = (String) request.getAttribute(SESSION_ID);
            if (StringUtils.isBlank(sessionId)) {
                sessionId = CookieUtil.getCookieValue(request, SESSION_ID);
            }

            loginCache.putEx(sessionId, user.getLoginName(), TIMEOUT);
            loginCache.putEx(COMPANY_NO + sessionId, user.getCompanyNo(), TIMEOUT);
            AppUtil.setLoginUser(user.getLoginName(), user.getCompanyNo());
            map.put("status", "1");
            map.put("userName", user.getName());
            map.put("loginName", user.getLoginName());
            result.buildIsSuccess(true).buildMsg("登陆成功").buildData(map);
            log.info("响应++++" + result);
            return result;
        }
        map.put("status", "0");
        log.info("响应=====" + map);
        return result.buildIsSuccess(true).buildMsg("您还不是本平台的用户,请联系公司管理员进行授权后登陆").buildData(map);


    }

}
