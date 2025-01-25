package io.github.jamielu.slicedb.conf;

import io.github.jamielu.slicedb.datasource.ShardingDataSource;
import io.github.jamielu.slicedb.engine.JmShardingEngine;
import io.github.jamielu.slicedb.engine.ShardingEngine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingConfiguration {
    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }
    @Bean
    public ShardingEngine shardingEngine(ShardingProperties shardingProperties) {
        return new JmShardingEngine(shardingProperties);
    }
}
