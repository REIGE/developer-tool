package com.reige.developer.common.mybatis;

import lombok.Data;

@Data
public class Page {
    private long size;
    private long current;
    private long total;
}
