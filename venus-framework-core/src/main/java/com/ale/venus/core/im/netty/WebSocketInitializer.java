package com.ale.venus.core.im.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * WebSocket初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 鉴权处理器
     */
    private final AuthHandler authHandler;

    /**
     * WebSocket处理器
     */
    private final WebSocketHandler webSocketHandler;

    public WebSocketInitializer(AuthHandler authHandler, WebSocketHandler webSocketHandler) {
        this.authHandler = authHandler;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
            new HttpServerCodec(),
            new ChunkedWriteHandler(),
            new HttpObjectAggregator(65536),
            this.authHandler,
            new IdleStateHandler(60, 0, 0),
            new WebSocketServerProtocolHandler("/ws", true),
            this.webSocketHandler
        );
    }
}
