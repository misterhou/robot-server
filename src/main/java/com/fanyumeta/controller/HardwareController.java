package com.fanyumeta.controller;

import com.fanyumeta.client.HardwareControlClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test/hardware")
public class HardwareController {

    @Resource
    private HardwareControlClient hardwareControlClient;

    @GetMapping("/send/message")
    public String sendMessage(String message) {
        this.hardwareControlClient.sendMessage(message);
        return "success";
    }

    @GetMapping("/send/command")
    public String sendCommon(String message) {
        this.hardwareControlClient.sendCommand(message);
        return "success";
    }

}
