package com.yd.web;

import com.yd.config.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试 controller
 */
@Controller
public class IndexController {

    @Autowired
    NettyClient nettyClient;

    @Autowired
    private DiscoveryClient client;

    @RequestMapping("/index")
    @ResponseBody
    public String index() {
        nettyClient.writeAndFlush("测试数据");
        return null;
    }

    @RequestMapping(value = "/add" ,method = RequestMethod.GET)
    @ResponseBody
    public Integer add(@RequestParam Integer a, @RequestParam Integer b) {
        ServiceInstance instance = client.getLocalServiceInstance();
        Integer r = a + b;
        System.err.println("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
        return r;
    }


}
