package com.reige.developer.module.base.controller;

import com.reige.developer.common.mybatis.Page;
import com.reige.developer.common.web.Resp;
import com.reige.developer.module.base.mapper.TestMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Resource
    private TestMapper testMapper;

    @GetMapping("/test")
    public Resp test() {
        return Resp.ok(testMapper.select());
    }

    @GetMapping("/page")
    public Resp page() {
        Page page = new Page();
        page.setCurrent(1);
        page.setSize(10);
        List<Map<String, Object>> list = testMapper.page(page);
        System.out.println(page);
        return Resp.ok(list);
    }

}
