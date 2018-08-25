package com.wangqin.globalshop.item.app.feign;

import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by BG235729 on 2018/8/25.
 */

@FeignClient(name = "itemApi")
public interface ItemBrandFeignService extends ItemBrandApi {

}
