package com.yd.config.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * netty 客户端
 */
@Component
public class NettyClient implements ApplicationContextAware {

    @Autowired
    private Environment env;

    @Autowired
    private NettyClientInitializer nettyClientInitializer;


    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private Channel channel;


    @PostConstruct
    public void start() throws InterruptedException {
        System.err.println("================    netty client start  begin    ========================");
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(nettyClientInitializer);
        // channel = bootstrap.connect(env.getProperty("netty.server.host"), Integer.parseInt(env.getProperty("netty.server.port"))).sync().channel();
        doConnect();
        System.err.println("================    netty client start  end    ========================");
    }


    /**
     * 链接
     */
    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = bootstrap.connect(env.getProperty("netty.server.host"), Integer.parseInt(env.getProperty("netty.server.port")));

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println("Connect to server successfully!");
                } else {
                    //断线重连
                    System.out.println("Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });

    }

    @PreDestroy
    public void stop() {
        eventLoopGroup.shutdownGracefully();
        eventLoopGroup = null;
        System.err.println("================    netty client close  end    ========================");
    }


    /**
     * 写数据
     *
     * @param data
     * @return
     */
    public ChannelFuture writeAndFlush(String data) {
          /*
            * 向服务端发送在控制台输入的文本 并用"\r\n"结尾
            * 之所以用\r\n结尾 是因为我们在handler中添加了 DelimiterBasedFrameDecoder 帧解码。
            * 这个解码器是一个根据\n符号位分隔符的解码器。所以每条消息的最后必须加上\n否则无法识别和解码
             * */
        return channel.writeAndFlush(data + "\r\n");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
