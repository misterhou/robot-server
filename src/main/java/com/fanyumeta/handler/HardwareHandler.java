package com.fanyumeta.handler;

import com.fanyumeta.utils.HardwareControlCommandUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 中控服务消息处理器
 */
@Slf4j
public class HardwareHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("接收到的【处理前】消息：{}", msg);
        if (msg instanceof ByteBuf) {
            ByteBuf message = (ByteBuf) msg;
            String str = message.toString(CharsetUtil.UTF_8);
            log.info("接收到的【处理后】消息：{}, 对应的描述：{}", str, HardwareControlCommandUtil.getReceiveCommandDescription(str));
        }
    }
}
