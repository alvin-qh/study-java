package alvin.study.se.jdbc.mptt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MPTT {
    private long id;
    private String name;
    private long pid;
    private long lft;
    private long rht;

    public MPTT(String name) {
        this.name = name;
    }
}
