package com.yd.config.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 服务启动完成初始化操作
 */
//@Component
public class ServiceInit implements CommandLineRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动，开始执行加载数据等操作<<<<<<<<<<<<<");
        //netty服务接口
//        new NettyServerDemo().init();
        System.out.println(">>>>>>>>>>>>>>>服务启动，结束执行加载数据等操作<<<<<<<<<<<<<");
    }

}
