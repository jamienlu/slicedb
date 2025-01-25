package io.github.jamielu.slicedb;

import io.github.jamielu.slicedb.conf.ShardingProperties;
import io.github.jamielu.slicedb.mybatis.ShardingMapperFactoryBean;
import io.github.jamielu.slicedb.domain.user.reposity.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ShardingProperties.class)
@MapperScan(value = "io.github.jamielu.slicedb.domain",
        factoryBean = ShardingMapperFactoryBean.class)
public class SlicedbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlicedbApplication.class, args);
    }
    @Autowired
    UserMapper userMapper;

}
