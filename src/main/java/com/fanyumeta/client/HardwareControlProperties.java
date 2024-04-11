package com.fanyumeta.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("fan-yu.hardware-control")
public class HardwareControlProperties {

    /**
     * 中控服务地址
     */
    private String host;

    /**
     * 中控服务端口号
     */
    private Integer port;

    /**
     * 等待响应超时时间（单位：秒）
     */
    private Long timeout;
}
