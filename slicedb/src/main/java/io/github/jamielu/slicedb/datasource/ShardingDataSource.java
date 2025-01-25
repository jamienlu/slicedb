package io.github.jamielu.slicedb.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.github.jamielu.slicedb.conf.ShardingProperties;
import io.github.jamielu.slicedb.model.ShardingContext;
import io.github.jamielu.slicedb.model.ShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@Slf4j
public class ShardingDataSource extends AbstractRoutingDataSource {
    public ShardingDataSource(ShardingProperties properties) {
        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
        properties.getDataSources().forEach((k, v) -> {
            try {
                dataSourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
            } catch (Exception e) {
                log.error("### createDataSource error!", e);
                throw new RuntimeException(e);
            }
        });
        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        // 分库规则替换
        ShardingResult shardingResult = ShardingContext.get();
        Object key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();
        log.info("### determineCurrentLookupKey = " + key);
        return key;
    }
}
