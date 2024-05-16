package alvin.study.guava.eventbus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实体对象, 作为事件对象的负载
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private long id;
    private String name;
}
