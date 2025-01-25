package io.github.jamielu.slicedb.mybatis;

import io.github.jamielu.slicedb.engine.ShardingEngine;
import io.github.jamielu.slicedb.model.ShardingContext;
import io.github.jamielu.slicedb.model.ShardingResult;
import io.github.jamielu.slicedb.domain.user.entity.User;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {
    @Setter
    ShardingEngine engine;
    public ShardingMapperFactoryBean() {
    }
    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }
    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        Class<?> clazz = getMapperInterface();
        SqlSession session = getSqlSession();
        Configuration configuration = session.getConfiguration();
        // mybatis代理之前创建分库分表代理避免被mybatis修改sql语句
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {
            String mapperId = clazz.getName() + "." + method.getName();
            MappedStatement statement = configuration.getMappedStatement(mapperId);
            BoundSql boundSql = statement.getBoundSql(args);
            Object[] params = getParams(boundSql,args);
            // 分库分表规则解析
            ShardingResult result = engine.sharding(boundSql.getSql(), params);
            ShardingContext.set(result);
            return method.invoke(proxy, args);
        });
    }
    @SneakyThrows
    private static Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] params = args;
        if(args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).toList();
            Object[] newParams = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                newParams[i] = getFieldValue(arg, cols.get(i));
            }
            params = newParams;
        }
        return params;
    }

    private static Object getFieldValue(Object o, String f) throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(f);
        field.setAccessible(true);
        return field.get(o);
    }
}
