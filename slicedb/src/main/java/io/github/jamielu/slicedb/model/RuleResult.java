package io.github.jamielu.slicedb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jamieLu
 * @create 2025-01-26
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuleResult {
    private String preDatasourceName;
    private String preTableName;
    private List<Integer> datasourceIndexes = new ArrayList<>();
    private List<Integer> tableIndexes = new ArrayList<>();;
}
