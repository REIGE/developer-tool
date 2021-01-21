package com.reige.developer.module.base.controller;

import com.reige.developer.common.web.Resp;
import com.reige.developer.module.base.mapper.TestMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestController {

    @Resource
    private TestMapper testMapper;

    @GetMapping("/test")
    public Resp test() {
        return Resp.ok(testMapper.select());
    }

}
