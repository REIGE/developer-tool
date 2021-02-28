package com.reige.developer.module.gen.controller;

import com.reige.developer.freemarker.TemplateRenderUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gen")
public class TemplateController {

    @GetMapping("/template")
    public String template() {
        Map<String, Object> map = new HashMap<>();

        map.put("a", "1234567");

        Map<String, String> b = new HashMap<>();
        b.put("name", "name123");


                map.put("b", Collections.singletonList(b));
        return TemplateRenderUtil.generateHtml("${a}\nasdf" +
                "adf" +
                "asdf" +
                "af" +
                "asdfasdf\n\r\n\r " +
                "asdf\n" +
                "<#list b as item>${item.name}</#list><br>", map);
    }

}
