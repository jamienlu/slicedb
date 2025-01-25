package io.github.jamielu.slicedb.engine;

import io.github.jamielu.slicedb.model.ShardingResult;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
public interface ShardingEngine {
    ShardingResult sharding(String sql, Object[] parameters);
}
