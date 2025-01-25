package io.github.jamielu.slicedb.model;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
public class ShardingContext {
    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static ShardingResult get() {
        return LOCAL.get();
    }

    public static void set(ShardingResult shardingResult) {
        LOCAL.set(shardingResult);
    }
}
