package com.demo.kfjira.controller;

import com.demo.kfjira.service.WebhookService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping(value = "/webhook")
public class KfWebHookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KfWebHookController.class);

    @Autowired
    private WebhookService webhookService;

    @PostMapping(value = "/test")
    @ResponseBody
    public void test(@RequestParam Map<String, Object> params) {
        LOGGER.info("access my controller with ticketDTO:" + params.toString());
        webhookService.handleWebhook(params);
    }

    @RequestMapping(value = "/login")
    @ResponseBody
    public void login(HttpServletRequest request,
                      HttpServletResponse response) {
        LOGGER.info("access my controller===========");
        redirectTemporarily(response, "http://5b2b562a54aa5704.kf5.com/user/remote?username=niko.zheng@linkflowtech.com&time=1584585481&token=9f7fafde8d34c5ac\n" +
                "&name=郑志坚&phone=18855097641&return_to=https://5b2b562a54aa5704.kf5.com/agent/#/dashboard/overview/me");
    }

    @RequestMapping(value = "/logout")
    @ResponseBody
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {
        LOGGER.info("access logout===========");
    }

    private void redirectTemporarily(HttpServletResponse response, String url) {
        response.addHeader(HttpHeaders.LOCATION, url);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    public static void main(String[] args) {
        String s = DigestUtils.md5Hex("hui.zhang@linkflowtech.com+1584603133+3f23009c3dddb6c79527384c67f462".getBytes());
        System.out.println("1:" + s);
        String s2 = DigestUtils.md5Hex("hui.zhang@linkflowtech.com15846031333f23009c3dddb6c79527384c67f462".getBytes());
        System.out.println("2:" + s2);
        System.out.println("base64=========" + new String(Base64.getEncoder().encode("success:Initial0".getBytes())));
        String base = new String(Base64.getEncoder().encode("hui.zhang@linkflowtech.com/token:3f23009c3dddb6c79527384c67f462".getBytes()));
        System.out.println("3:" + base);
    }
}
