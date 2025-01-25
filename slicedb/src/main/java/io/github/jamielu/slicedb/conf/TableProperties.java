package io.github.jamielu.slicedb.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

/**
 * @author jamieLu
 * @create 2025-01-25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableProperties {
    private String actualDataNodes;
    private Properties dataBaseStrategy;
    private Properties tableStrategy;
}
