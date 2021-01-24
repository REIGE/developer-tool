package com.reige.developer.common.mybatis;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * <p>
 * 分页 interceptor
 * </p>
 *
 * @since 2021-1-24
 */
@Intercepts(@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class}))
public class PaginationInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //我们这里使用的SystemMetaObject是mybatis帮我们封装好了的，点进去看就知道，是一个默认的MetaObject构造方法。可以省去我们的一些操作
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环
        // 可以分离出最原始的的目标类)
        while (metaObject.hasGetter("h")) {
            Object object = metaObject.getValue("h");
            System.out.println(object.toString());
            metaObject = SystemMetaObject.forObject(object);

        }
        // 分离最后一个代理对象的目标类
        while (metaObject.hasGetter("target")) {
            Object object = metaObject.getValue("target");
            System.out.println(object.toString());
            metaObject = SystemMetaObject.forObject(object);

        }
        //获取查询接口映射
        // 只重写需要分页的sql语句。通过MappedStatement的ID匹配，默认重写以Page结尾的
        //  MappedStatement的sql
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String mapId = mappedStatement.getId();

        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();
        Optional<Page> pageOptional = getPage(parameterObject);
        if (!pageOptional.isPresent()) {
            return invocation.proceed();
        }
        Page page = pageOptional.get();
        //当前页数
        long currPage = page.getCurrent();
        //每页条数
        long pageSize = page.getSize();

        //管理参数的handler
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
        //获取请求时的参数，这里我们查询sql使用的是map来封装查询参数，当然，优雅一点我们可以创建一个对象来封装。

        //原始sql
        String originalSql = statementHandler.getBoundSql().getSql().trim();
        System.out.println("获取原始sql:" + originalSql);
        if (originalSql.endsWith(";")) {
            //去掉原始sql末尾的分号，以便我们对其进行拼接。
            originalSql = originalSql.substring(0, originalSql.length() - 1);
        }
        //注意拼接时limit左右两边的空格，不然就是连在一起了，会有语法错误。
        String limitSql = originalSql + " limit " + (currPage - 1) * pageSize + "," + pageSize;
        //将拼装后的sql复制给BoundSQL
        metaObject.setValue("delegate.boundSql.sql", limitSql);

        //最后将处理权限返回给mybatis，让它继续执行。
        return invocation.proceed();
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
}
