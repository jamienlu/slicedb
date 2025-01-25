package io.github.jamielu.slicedb.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author jamieLu
 * @create 2025-01-25
 */

@ConfigurationProperties(prefix = "spring.sharding")
@Data
public class ShardingProperties {
    private Map<String, Properties> dataSources = new LinkedHashMap<>();
    private Map<String, TableProperties> tables = new LinkedHashMap<>();
}
