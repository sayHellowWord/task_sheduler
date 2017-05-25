package com.yd.config.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * netty 服务端
 */
@Component
public class NettyServer implements ApplicationContextAware {

    @Autowired
    private Environment env;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;


    @Value("${rpcServer.backlog:1024}")
    int backlog;

    @PostConstruct
    public void start() {
        System.err.println("================   netty server start begin   ========================");
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    //注意是childOption
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new NettyServerInitializer());

            channel = serverBootstrap.bind(Integer.parseInt(env.getProperty("netty.server.port"))).sync().channel();
        } catch (InterruptedException e) {
            System.err.println("服务端异常");
            e.printStackTrace();
        }
        System.err.println("================   netty server start end    ========================");
    }

    @PreDestroy
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        bossGroup = null;
        workerGroup = null;
        System.err.println("================   netty server close   ========================");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

}
