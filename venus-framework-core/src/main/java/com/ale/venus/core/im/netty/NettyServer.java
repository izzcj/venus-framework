package com.ale.venus.core.im.netty;

import com.ale.venus.core.im.InstantMessageProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * netty服务端
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class NettyServer implements InitializingBean {

    /**
     * 即时消息配置属性
     */
    private final InstantMessageProperties instantMessageProperties;

    /**
     * WebSocket初始化器
     */
    private final WebSocketInitializer webSocketInitializer;

    public NettyServer(InstantMessageProperties instantMessageProperties, WebSocketInitializer webSocketInitializer) {
        this.instantMessageProperties = instantMessageProperties;
        this.webSocketInitializer = webSocketInitializer;
    }

    @Override
    public void afterPropertiesSet() {
        new Thread(this::start).start();
    }

    /**
     * 启动
     */
    private void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        int port = this.instantMessageProperties.getPort();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.webSocketInitializer);

            b.bind(port).sync();
            log.info("Netty WebSocket服务启动成功！端口：[{}]", port);
        } catch (Exception e) {
            log.error("Netty WebSocket启动失败！", e);
        }
    }

}
