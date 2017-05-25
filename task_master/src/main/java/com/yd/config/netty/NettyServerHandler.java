package com.yd.config.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    //客户端超时次数
    private Map<ChannelHandlerContext, Integer> clientOvertimeMap = new ConcurrentHashMap<>();
    private final int MAX_OVERTIME = 3;  //超时次数超过该值则注销连接


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 收到消息直接打印输出
        System.err.println(ctx.channel().remoteAddress() + " Say : " + msg);


        // 返回客户端消息 - 我已经接收到了你的消息
        ctx.writeAndFlush("Received your message !\n");

        clientOvertimeMap.remove(ctx);//只要接受到数据包，则清空超时次数
    }

    /*
     *
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     *
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        super.channelActive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    System.err.println("客户端读超时");
                    break;
                case WRITER_IDLE:
                    System.err.println("客户端写超时");
                    break;
                case ALL_IDLE:
                    System.err.println("客户端读写超时");
                    int overtimeTimes = clientOvertimeMap.getOrDefault(ctx, 0);
                    if (overtimeTimes < MAX_OVERTIME) {
                        ctx.writeAndFlush("1111\n");
                        addUserOvertime(ctx);
                    } else {
                        ctx.close();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void addUserOvertime(ChannelHandlerContext ctx) {
        int oldTimes = 0;
        if (clientOvertimeMap.containsKey(ctx)) {
            oldTimes = clientOvertimeMap.get(ctx);
        }
        clientOvertimeMap.put(ctx, (int) (oldTimes + 1));
    }

}