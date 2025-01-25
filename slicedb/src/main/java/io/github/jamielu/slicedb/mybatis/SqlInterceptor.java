package io.github.jamielu.slicedb.mybatis;

import io.github.jamielu.slicedb.model.ShardingContext;
import io.github.jamielu.slicedb.model.ShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@Component
@Intercepts(
        @org.apache.ibatis.plugin.Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {java.sql.Connection.class, Integer.class}
        )
)
@Slf4j
public class SqlInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ShardingResult result = ShardingContext.get();
        if(result != null) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            log.info("### before sql statement: " + boundSql.getSql());
            String targetSqlStatement = result.getTargetSqlStatement();
            log.info("### replace sql statement: " + targetSqlStatement);
            // 分表规则替换
            if(!sql.equalsIgnoreCase(targetSqlStatement)) {
                Field field = boundSql.getClass().getDeclaredField("sql");
                Unsafe unsafe = UnsafeUtils.getUnsafe();
                long fieldOffset = unsafe.objectFieldOffset(field);
                unsafe.putObject(boundSql, fieldOffset, targetSqlStatement);
            }

            log.info("### after sql statement: " + boundSql.getSql());
        }
        return invocation.proceed();
    }

}
