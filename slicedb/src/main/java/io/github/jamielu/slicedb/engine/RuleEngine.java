package io.github.jamielu.slicedb.engine;

import io.github.jamielu.slicedb.model.RuleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * ds{0,1,2}.user{0,1} RuleResult
 * ds{0..4}.user{0..5} RuleResult
 *
 * @author jamieLu
 * @create 2025-01-26
 */
public class RuleEngine {
    public RuleResult analysisRuleResult(String el) {
        RuleResult result = new RuleResult();
        if (el.contains("..")) {
            el = el.replaceAll("\\.\\.", "@");
        }
        String[] el2 = el.split("\\.");
        buildRuleResult(result, el2[0], false);
        buildRuleResult(result, el2[1], true);
        return result;
    }

    /**
     * ds{0,1,2} ds{0$2}
     *
     * @param result 结果
     * @param el 参数
     * @param isTable 是否解析表
     */
    public void buildRuleResult(RuleResult result, String el, boolean isTable) {
        List<Integer> values = new ArrayList<>();
        int p1 = el.indexOf("{");
        int p2 = el.indexOf("}");
        System.out.println(el);
        String pre = el.substring(0, p1);
        String valueEl = el.substring(p1 + 1, p2);
        if (valueEl.contains("@")) {
            String[] temps = valueEl.split("@", -1);
            int start = Integer.parseInt(temps[0]);
            int end = Integer.parseInt(temps[1]);
            for (int j = start; j <= end; j++) {
                values.add(j);
            }
        } else {
            String[] temps = valueEl.split(",");
            for (String s : temps) {
                values.add(Integer.parseInt(s));
            }
        }
        if (isTable) {
            result.setPreTableName(pre);
            result.setTableIndexes(values);
        } else {
            result.setPreDatasourceName(pre);
            result.setDatasourceIndexes(values);
        }
    }
}
