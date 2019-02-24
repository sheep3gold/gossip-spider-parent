package com.netty.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception{
        EventLoopGroup eventLoopGroup = null;

        try {
            //创建ServerBootstrap实例来引导绑定和启动服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //创建NioEventLoopGroup对象来处理事件，如接收新连接、接收数据、写数据等
            eventLoopGroup = new NioEventLoopGroup();
            //指定通道类型为NioServerSocketChannel,设置InetSocketAddress让服务器监听某个端口以等待客户端连接
            serverBootstrap.group(eventLoopGroup).channel(NioServerSocketChannel.class)
                    .localAddress("localhost", port).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(new EchoServerHandler());
                }
            });
            //最后绑定服务器等待知道绑定完成，调用sync()方法会阻塞知道服务器完成绑定。然后服务
            // 器等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println("开始监听，端口为：" + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{
        new EchoServer(20000).start();
    }


}
