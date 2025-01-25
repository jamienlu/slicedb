package io.github.jamielu.slicedb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShardingResult {
    private String targetDataSourceName;
    private String targetSqlStatement;
}
