package com.yd.config.netty;


import com.google.common.base.Strings;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Autowired
    private NettyClient nettyClient;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.err.println("Server say : " + msg);
        if (!Strings.isNullOrEmpty(msg) && "1111".equals(msg)) {
            ctx.writeAndFlush("心跳信息收到\n");
        }
   }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Client active ");
        super.channelActive(ctx);
        ctx.writeAndFlush("netty注册成功，上报任务节点信息\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("Client close ");
        super.channelInactive(ctx);
        nettyClient.doConnect();
    }

}