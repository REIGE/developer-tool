package com.reige.developer.common.mybatis;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * 分页 interceptor
 * </p>
 *
 * @since 2021-1-24
 */
@Intercepts(@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class}))
public class PaginationInterceptor implements Interceptor {
    public static final String SEMICOLON = ";";

    private int maxPageLimit = Integer.MAX_VALUE;

    public PaginationInterceptor() {
    }

    public PaginationInterceptor(int maxPageLimit) {
        this.maxPageLimit = maxPageLimit;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取StatementHandler
        StatementHandler statementHandler = getTarget((StatementHandler) invocation.getTarget());
        //我们这里使用的SystemMetaObject是mybatis帮我们封装好了的，点进去看就知道，是一个默认的MetaObject构造方法。可以省去我们的一些操作
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // 先判断是不是SELECT操作  (2019-04-10 00:37:31 跳过存储过程)
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()
                || StatementType.CALLABLE == mappedStatement.getStatementType()) {
            return invocation.proceed();
        }
        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();
        Optional<Page> pageOptional = getPage(parameterObject);
        if (!pageOptional.isPresent()) {
            return invocation.proceed();
        }
        Page page = pageOptional.get();
        Connection connection = (Connection) invocation.getArgs()[0];
        // 获取原始sql
        String originalSql = getOriginalSql(statementHandler);
        // 获取count sql
        String countSql = buildCountSql(originalSql);
        // 查询total
        long total = selectTotal(countSql, connection, mappedStatement, boundSql);
        page.setTotal(total);

        System.out.println("获取page:" + page);
        System.out.println("获取原始sql:" + originalSql);
        //注意拼接时limit左右两边的空格，不然就是连在一起了，会有语法错误。
        String limitSql = buildLimitSql(page, originalSql);
        //将拼装后的sql复制给BoundSQL
        metaObject.setValue("delegate.boundSql.sql", limitSql);
        //最后将处理权限返回给mybatis，让它继续执行。
        return invocation.proceed();
    }

    private String buildLimitSql(Page page, String originalSql) {
        //当前页数
        long currPage = page.getCurrent();
        //每页条数
        long pageSize = page.getSize();
        return originalSql + " limit " + (currPage - 1) * pageSize + "," + pageSize;
    }

    private String getOriginalSql(StatementHandler statementHandler) {
        String originalSql = statementHandler.getBoundSql().getSql().trim();
        if (originalSql.endsWith(SEMICOLON)) {
            originalSql = originalSql.substring(0, originalSql.length() - 1);
        }
        return originalSql;
    }

    private String buildCountSql(String originalSql) {
        return String.format(Locale.ENGLISH, "select count(*) from (%s) total", originalSql);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private Optional<Page> getPage(Object parameterObject) {
        if (parameterObject == null) {
            return Optional.empty();
        }
        if (parameterObject instanceof Page) {
            return Optional.of((Page) parameterObject);
        } else if (parameterObject instanceof Map) {
            Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
            for (Object param : parameterMap.values()) {
                if (param instanceof Page) {
                    return Optional.of((Page) param);
                }
            }
        }
        return Optional.empty();
    }

    private StatementHandler getTarget(StatementHandler target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return getTarget((StatementHandler) metaObject.getValue("h.target"));
        }
        return target;
    }

    private long selectTotal(String countSql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(countSql);
        DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
        parameterHandler.setParameters(statement);
        long total = 0;
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                total = resultSet.getLong(1);
            }
        }
        return total;
    }
}
