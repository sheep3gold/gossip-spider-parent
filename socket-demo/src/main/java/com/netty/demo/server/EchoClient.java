package com.netty.demo.server;

import io.netty.channel.EventLoopGroup;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        EventLoopGroup nioEventLoopGroup = null;

        //创建Bootstrap对象来引导启动客户端
    }
}
