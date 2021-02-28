package com.reige.developer.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateRenderUtil {
    /**
     * 生成静态文件
     *
     * @param template
     * @param model
     * @return
     */
    public static String generateHtml(String template, Map model) {
        // 定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 使用模板加载器变为模板
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", template);
        // 在配置中设置模板加速器
        configuration.setTemplateLoader(stringTemplateLoader);
        try {
            // 获取模板的内容
            Template template1 = configuration.getTemplate("template");
            // 静态化
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        HashMap hashMap = new HashMap();
        hashMap.put("a", "1234567");
        String s = generateHtml("${a}", hashMap);
        System.out.println(s);
    }
}
