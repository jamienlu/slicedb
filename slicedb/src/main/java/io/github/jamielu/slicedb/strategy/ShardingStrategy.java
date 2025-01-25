package io.github.jamielu.slicedb.strategy;

import java.util.List;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2025-01-26
 */
public interface ShardingStrategy {
    List<String> getShardingColums();

    /**
     * 分库分表策略
     *
     * @param availableTargetNames 真实库表集
     * @param logicTableName 逻辑库表
     * @param shardingParams 分片参数
     * @return 真实库表
     */
    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);
}
