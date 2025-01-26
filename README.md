>[https://github.com/jamienlu/slicedb.git](https://github.com/jamienlu/slicedb.git)

# 相关功能

```plain
io/github/jamielu/slicedb/conf 分库分表规则配置和初始化
io/github/jamielu/slicedb/datasource 数据源路由druid连接池AbstractRoutingDataSource实现 
io/github/jamielu/slicedb/engine 分片规则解析 获取真实库表名
io/github/jamielu/slicedb/model 线程绑定真实库表记录器和规则对象获取真实sql
io/github/jamielu/slicedb/mybatis mybatis实现 
  MapperFactoryBean代理前创建分库分表代理
  InterceptorSQL执行前替换sql
io/github/jamielu/slicedb/startegy 分片策略和sql解析器
```


使用例子

```plain
<dependency>
  <groupId>io.github.jamielu</groupId>
  <artifactId>slicedb</artifactId>
  <version>0.0.1-SNAPSHOT</version>
<dependency>


yaml配置


spring:
  sharding:
    datasources:
      ds0:
        url: jdbc:mysql://192.168.0.100:3316/sharding?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
        username: root
        password: root
      ds1:
        url: jdbc:mysql://192.168.0.100:3326/sharding?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
        username: root
        password: root
      ds2:
        url: jdbc:mysql://192.168.0.100:3336/sharding?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
        username: root
        password: root
    tables:
      user:
        actualDataNodes: ds${0..2}.user${0..4}
        dataBaseStrategy:
          shardingColumn: user_id
          algorithmExpression: ds${user_id % 3}
        tableStrategy:
          shardingColumn: user_id
          algorithmExpression: user${user_id % 5}


```


版本说明：

v1 - 简单实现单表insert select update delete

vtodo  复杂sql解析  sql改写 sql结果合并

