package com.reige.developer.module.base.mapper;

import com.reige.developer.common.mybatis.Page;
import com.reige.developer.module.base.pojo.Test;

import java.util.List;
import java.util.Map;

public interface TestMapper {
    List<Test> select();

    List<Map<String, Object>> page(Page page);
}
