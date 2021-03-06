package com.wangqin.globalshop.web.controller.item;

import com.wangqin.globalshop.biz1.app.dal.dataObject.AppletConfigDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.CompanyDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemQrcodeShareDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.ItemQrcodeShareDOMapperExt;
import com.wangqin.globalshop.common.base.BaseDto;
import com.wangqin.globalshop.common.exception.ErpCommonException;
import com.wangqin.globalshop.common.utils.DimensionCodeUtil;
import com.wangqin.globalshop.common.utils.EasyUtil;
import com.wangqin.globalshop.common.utils.JsonResult;
import com.wangqin.globalshop.common.utils.StringUtil;
import com.wangqin.globalshop.item.app.service.IAppletConfigService;
import com.wangqin.globalshop.item.app.service.IItemCompanyService;
import com.wangqin.globalshop.item.app.service.IItemService;
import com.wangqin.globalshop.item.app.service.impl.entity.ShareTokenEntity;
import com.wangqin.globalshop.web.dto.api.ItemShareEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ShareApiController {
    private static Logger log = LoggerFactory.getLogger("Wechat3rdPartyAuthorization");
    @Autowired
    private IItemService itemService;

    @Autowired
    private IAppletConfigService appletConfigService;
    @Autowired
    private IItemCompanyService companyService;

    @Autowired
    private ItemQrcodeShareDOMapperExt qrcodeShareDOMapperExt;

    private static final String TYPE_OF_MALL_APPLET = "2";
    public static final String ACCESS_TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";
    //public static final String ACCESS_TOKENPARAM = "grant_type=client_credential&appid=wxdef3e972a4a93e91&secret=fef11f402f8e8f3c1442163155aeb65a";
    private static final String ACCESS_TOKENPARAM_PART = "grant_type=client_credential&appid=";

    @RequestMapping("/api/items/share/token")
    @ResponseBody
    public String token(@RequestParam("uuid") String uuid) {

        ItemQrcodeShareDO qrcodeShareDO = qrcodeShareDOMapperExt.selectByShareNo(uuid);
        ShareTokenEntity tokenEntity = null;
        if (qrcodeShareDO != null) {
            tokenEntity = ShareTokenEntity.buildShareToken(qrcodeShareDO.getUserNo(), qrcodeShareDO.getCompanyNo(), qrcodeShareDO.getItemCode(), uuid);
        }
        JsonResult<String> jsonResult = new JsonResult<>();
        if (tokenEntity == null) {
            tokenEntity = new ShareTokenEntity();
        }
        jsonResult.buildIsSuccess(true).buildData(BaseDto.toString(tokenEntity));
        return BaseDto.toString(jsonResult);
    }

    @RequestMapping("/api/items/share")
    @ResponseBody
    public String detail(@RequestParam("itemCode") String itemCode,
                         @RequestParam("companyNo") String companyNo,
                         @RequestParam("userId") String userId) {

        JsonResult<ItemShareEntity> jsonResult = new JsonResult<>();


        String picUrl = null;
        log.info("当前公司的companyNo="+companyNo);
        CompanyDO companyDO = companyService.selectByCompanyNo(companyNo);
        log.info("company="+companyDO);
        log.info("companyGroup="+companyDO.getCompanyGroup());
        String baseCompanyNo = companyNo;
        if (StringUtil.isNotEmpty(companyDO.getCompanyGroup())) {
            baseCompanyNo = companyDO.getCompanyGroup();
        }
        picUrl = qrcodeShareDOMapperExt.selectPicUrl(userId, companyNo, itemCode);
        if (EasyUtil.isStringEmpty(picUrl)) {
            //获取商城小程序的appid和screat
            log.info("生成二维码用的companyNo="+companyNo);
            AppletConfigDO appletConfigDO = appletConfigService.queryWxMallConfigInfoByCompanyNo(baseCompanyNo, TYPE_OF_MALL_APPLET);
            String accessToken = appletConfigDO.getAuthorizerAccessToken();
            String token;
            if (StringUtil.isBlank(accessToken)) {
                String accessTokenParam = ACCESS_TOKENPARAM_PART + appletConfigDO.getAppid() + "&secret=" + appletConfigDO.getSecret();
                System.out.println("config:" + accessTokenParam);
                //TODO refactor
                //生成分享
                String reponse = DimensionCodeUtil.sendGet(ACCESS_TOKENURL, accessTokenParam);
                JSONObject myJson = JSONObject.fromObject(reponse);
                token = (String) myJson.get("access_token");
            } else {
                token = accessToken;
            }

            try {
                picUrl = itemService.generateItemShareUrl(userId, companyNo, itemCode, "pages/item/detail", token);
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                    picUrl = itemService.generateItemShareUrl(userId, companyNo, itemCode, "pages/item/detail", token);
                } catch (InterruptedException e1) {
                    jsonResult.buildIsSuccess(false).buildMsg("服务器繁忙，请稍后重试");
                    return BaseDto.toString(jsonResult);
                } catch (Exception ex) {
                    jsonResult.buildIsSuccess(false).buildMsg("服务器繁忙，请稍后重试");
                    return BaseDto.toString(jsonResult);
                }

            }
        }


        ItemShareEntity itemShareEntity = new ItemShareEntity();

        ItemDO item = itemService.itemDetailByItemCode(itemCode, companyNo);
        if(item == null){
            jsonResult.buildIsSuccess(false).buildMsg("找不到对应商品");
        }
        itemShareEntity.setItemDesc(item.getItemName());

        String mainPic = item.getMainPic();

        JSONObject jsonObject = JSONObject.fromObject(mainPic);
        JSONArray array = jsonObject.getJSONArray("picList");
        //JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString("picList"));
        List<String> picList = new ArrayList<>();

        int maxSize = array.size() > 8 ? 8 : array.size();
        for (int i = 0; i < maxSize; i++) {
            String pic = array.getJSONObject(i).getString("url");
            pic = formImg(pic);
            picList.add(pic);
        }

        picUrl = formImg(picUrl);
        //插入分享码图片
        if (picList.size() < 5) {
            picList.add(picUrl);
        } else {
            List<String> tmp = new ArrayList<>();
            int i = 0;
            for (String purl : picList) {
                if (i == 4) { //TODO refactor
                    tmp.add(picUrl);
                }
                tmp.add(purl);
                i++;
            }
            picList = tmp;
        }
        itemShareEntity.setItemImgList(picList);
        jsonResult.buildIsSuccess(true).buildData(itemShareEntity);
        return BaseDto.toString(jsonResult);
    }

    public String formImg(String url) {
        if (url.indexOf("?") == -1) {
            return url;
        }
        int len = url.indexOf("?");
        url = url.substring(0, len);
        url = url + "@1c_1e_480w.webp";
        return url;
    }

}
