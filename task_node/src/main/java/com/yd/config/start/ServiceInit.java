package com.yd.config.start;

import com.yd.config.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 服务启动完成初始化操作
 */
//@Component
public class ServiceInit implements CommandLineRunner {

    @Autowired
    NettyClient nettyClient;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动，开始执行加载数据等操作<<<<<<<<<<<<<");
        //netty客户端
//        nettyClient.writeAndFlush("服务启动，开始执行加载数据等操作");
        System.out.println(">>>>>>>>>>>>>>>服务启动，结束执行加载数据等操作<<<<<<<<<<<<<");
    }

}
