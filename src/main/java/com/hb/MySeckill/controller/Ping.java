package com.hb.MySeckill.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ping {

    @RequestMapping(value = {"/ping"})
    public String ping() {
        return "pong";
    }
}
