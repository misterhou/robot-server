package com.fanyumeta.client;

import com.fanyumeta.handler.HardwareHandler;
import com.fanyumeta.socket.TCPClient;
import com.fanyumeta.utils.HardwareControlCommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 中控服务客户端
 */
@Slf4j
@Component
@EnableConfigurationProperties(HardwareControlProperties.class)
public class HardwareControlClient {
    private HardwareControlProperties hardwareControlProperties;

    @Autowired
    public HardwareControlClient(HardwareControlProperties hardwareControlProperties) {
        this.hardwareControlProperties = hardwareControlProperties;
    }

    /**
     * 发送消息给中控服务
     *
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        List<String> commandList = getHardwareCommand(message);
        for (String command : commandList) {
            this.sendCommand(command);
        }
    }

    /**
     * 发送指定数据到中控系统
     * @param command 中控指令
     */
    @Deprecated
    public void sendCommand(String command) {
        try {
            TCPClient client = new TCPClient(this.hardwareControlProperties.getHost(),
                    this.hardwareControlProperties.getPort(), new HardwareHandler());
            client.sendMessage(command);
            Thread.sleep(this.hardwareControlProperties.getTimeout() * 1000);
            client.close();
        } catch (Exception e) {
            String errMsg = "发送指令到中控系统出错";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg);
        }
    }

    /**
     * 通过文字获取中控指令集合
     * @param message 文字信息
     * @return 中控指令集合
     */
    private List<String> getHardwareCommand(String message) {
        List<String> command = HardwareControlCommandUtil.parse(message);
        if (log.isDebugEnabled()) {
            log.debug("将【{}】通过系统解析后得到的中控指令【{}】", message, command);
        }
        return command;
    }
}
