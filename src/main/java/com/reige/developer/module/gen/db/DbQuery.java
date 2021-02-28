package com.reige.developer.module.gen.db;

public interface DbQuery {
    String getTablesSql(String schema);
    String getTableFieldsSql(String schema, String tablename);
}
