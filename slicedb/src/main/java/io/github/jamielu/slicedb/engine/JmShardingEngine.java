package io.github.jamielu.slicedb.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.fastjson.JSONObject;
import io.github.jamielu.slicedb.conf.ShardingProperties;

import io.github.jamielu.slicedb.model.RuleResult;
import io.github.jamielu.slicedb.model.ShardingResult;
import io.github.jamielu.slicedb.strategy.HashShardingStrategy;
import io.github.jamielu.slicedb.strategy.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@Slf4j
public class JmShardingEngine implements ShardingEngine {
    private final MultiValueMap<String, String> actualDataNodes = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, String> actualTables = new LinkedMultiValueMap<>();
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategies    = new HashMap<>();
    public JmShardingEngine(ShardingProperties shardingProperties) {
        shardingProperties.getTables().forEach((tableName, tableProperties) -> {
            // 解析真实库名表明
            RuleResult result = new RuleEngine().analysisRuleResult(tableProperties.getActualDataNodes());
            result.getDatasourceIndexes().forEach(index -> {
                actualDataNodes.add(tableName, result.getPreDatasourceName() + index);
            });
            result.getTableIndexes().forEach(index -> {
                actualTables.add(tableName, result.getPreTableName() + index);
            });
            // 配置分库分表策略
            databaseStrategies.put(tableName, new HashShardingStrategy(tableProperties.getDataBaseStrategy()));
            tableStrategies.put(tableName, new HashShardingStrategy(tableProperties.getTableStrategy()));
        });
    }
    @Override
    public ShardingResult sharding(String sql, Object[] parameters) {
        log.info("##### sql = " + sql);
        log.info("##### parameters = " + JSONObject.toJSONString(parameters));
        String table;
        Map<String, Object> shardingColumnsMap = new HashMap<>();
        // druid sql解析
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        if(sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            // 获取sql表名
            table = sqlInsertStatement.getTableName().getSimpleName();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                // 获取sql字段名
                String columnName = columnExpr.getSimpleName();
                // 分表字段
                shardingColumnsMap.put(columnName, parameters[i]);
            }
            log.info("### sql = " + sqlStatement + ", tableName = " + table);
        } else {
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(parameters));
            sqlStatement.accept(visitor);
            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if(sqlNames.size() > 1) {
                throw new RuntimeException("not support multi tables sharding: " + sqlNames);
            }
            table = sqlNames.iterator().next().getSimpleName();
            shardingColumnsMap = visitor.getConditions().stream()
                .collect(Collectors.toMap(c -> c.getColumn().getName(), c -> c.getValues().get(0)));
            log.info("### sql = " + sqlStatement + ", tableName = " + table);
        }
        // 解析真实库
        ShardingStrategy databaseStrategy = databaseStrategies.get(table);
        String targetDatabase = databaseStrategy.doSharding(actualDataNodes.get(table), table, shardingColumnsMap);
        // 解析真实表
        ShardingStrategy tableStrategy = tableStrategies.get(table);
        String targetTable = tableStrategy.doSharding(actualTables.get(table), table, shardingColumnsMap);
        log.info("##### target db.table = " + targetDatabase + "." + targetTable);
        return new ShardingResult(targetDatabase, sql.replace(" " +table + " ", " " +targetTable+ " "));
    }

}
