package com.wangqin.globalshop.usercenter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author biscuit
 * @data 2018/07/25
 */
@Controller
@ResponseBody
@RequestMapping("/forward")
public class WechatTranspondController {

    private static final String testUrl = "http://test.buyer007.cn";
    private static final String devUrl = "http://dev.buyer007.cn";

    @RequestMapping("/test/login")
    public void loginTest(String code, HttpServletResponse response) {
        String trueUrl = testUrl + "/#/LoginTest?code=" + code;
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/test/authorized")
    public void authorizedTest(String code,HttpServletResponse response) {
        String trueUrl = testUrl + "/#/permission/test?code=" + code;
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/dev/login")
    public void loginDev(String code, HttpServletResponse response) {
        String trueUrl = devUrl + "/#/LoginTest?code=" + code;
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/dev/authorized")
    public void authorizedDev(String code,HttpServletResponse response) {
        String trueUrl = devUrl + "/#/permission/test?code=" + code;
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/devGetCode/{url}")
    public void dev(@PathVariable String url, String code, HttpServletResponse response) {
        String trueUrl = devUrl +url+ "?code=" + code;
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/testGetCode/{url}")
    public void test(@PathVariable String url, String code, HttpServletResponse response) {
        String trueUrl = testUrl +url+ "?code=" + code;
        try {
            response.sendRedirect(trueUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
